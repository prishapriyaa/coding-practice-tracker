import streamlit as st
import requests
import subprocess
import time
import os
import pandas as pd
import plotly.express as px

# API base
BASE_URL = "http://localhost:5000/api"

def fix_empty(arg):
    return arg if arg.strip() else "__EMPTY__"

def get_user_id(username):
    try:
        res = requests.get(f"{BASE_URL}/users/{username}")
        print(f"{BASE_URL}/users/{username}")
        print(res)
        if res.status_code==200:
            user_id=res.json().get("userId")
            print(f"userID: {user_id}")
            return res.json().get("userId")
    except Exception as e:
        st.error(f"Error fetching user ID: {e}")
    return None

def get_platform_stats(user_id):
    try:
        res=requests.get(f"{BASE_URL}/platform-stats/{user_id}")
        if res.status_code==200:
            return res.json()
        else:
            st.error("Could not fetch platform stats")
            return None
    except Exception as e:
        st.error(f"Error: {e}")
        return None
    

def get_topic_stats(user_id):
    try:
        res=requests.get(f"{BASE_URL}/topic-stats/{user_id}")
        if res.status_code==200:
            return res.json()
        else:
            st.error("Could not fetch topic stats")
            return None
    except Exception as e:
        st.error(f"Error: {e}")
        return None
    

def get_language_stats(user_id):
    try:
        res=requests.get(f"{BASE_URL}/language-stats/{user_id}")
        if res.status_code==200:
            return res.json()
        else:
            st.error("Could not fetch language stats")
            return None
    except Exception as e:
        st.error(f"Error: {e}")
        return None

# Session state setup
if "submitted" not in st.session_state:
    st.session_state.submitted = False
if "dashboard_data" not in st.session_state:
    st.session_state.dashboard_data = {}
if "platform_usernames" not in st.session_state:
    st.session_state.platform_usernames = {}

# 1️⃣ Ask for user's name
if not st.session_state.submitted:
    username = st.text_input("Hey, what should we call you? Your username should be UNIQUE")
    if username:
        st.write(f"Hi **{username}**, enter coding platform usernames which you want to be considered:")
        
        # 2️⃣ Platform usernames
        leetcode = st.text_input("LeetCode Username")
        gfg = st.text_input("GeeksforGeeks Username")
        codeforces = st.text_input("Codeforces Username")
        hackerrank = st.text_input("HackerRank Username")

        # 3️⃣ Submit button
        if st.button("Submit"):
            # Save usernames
            st.session_state.platform_usernames = {
                "username": username,
                "leetcode": leetcode,
                "gfg": gfg,
                "codeforces": codeforces,
                "hackerrank": hackerrank
            }

            # 4️⃣ Call Java scraper
            try:
                scraper_path = os.path.join("java-scraper", "target", "java-scraper-1.0-SNAPSHOT.jar")
                
                print("Launching scraper with: ", username, leetcode, gfg, codeforces, hackerrank)
                print("Using scraper path:", scraper_path)

                print("👉 Running subprocess with command:", ["java", "-jar", scraper_path, username, leetcode, gfg, codeforces, hackerrank])

                # subprocess.run(
                #     ["java", "-jar", scraper_path, username, leetcode, gfg, codeforces, hackerrank],
                #     check=True
                # )
                # subprocess.run([
                #     "java", "-jar", scraper_path,
                #     username or "", leetcode or "", gfg or "", codeforces or "", hackerrank or ""
                # ], check=True)

                args = [
                    "java", "-jar", scraper_path,
                    fix_empty(username),
                    fix_empty(leetcode),
                    fix_empty(gfg),
                    fix_empty(codeforces),
                    fix_empty(hackerrank)
                ]

                subprocess.run(args, check=True)

                st.success("Scraper executed successfully! Fetching data...")

                time.sleep(2)  # small wait before fetching data

                # 5️⃣ Fetch user_id first
                user_id = get_user_id(username)
                print("userId: ", user_id)
                if user_id is None:
                    st.error("Couldn't retrieve user ID. Please ensure the scraper added it.")
                else:
                    # Fetch dashboard data
                    stats = {
                        "language_stats": get_language_stats(user_id),
                        "platform_stats": get_platform_stats(user_id),
                        "topic_stats": get_topic_stats(user_id)
                    }

                    print("📦 Retrieved Stats:")
                    print("Languages:", stats["language_stats"])
                    print("Platform:", stats["platform_stats"])
                    print("Topics:", stats["topic_stats"])


                    st.session_state.dashboard_data = stats
                    st.session_state.submitted = True
                    st.rerun()


            except subprocess.CalledProcessError as e:
                st.error(f"Scraper failed: {e}")
            except Exception as e:
                st.error(f"Error: {e}")
else:
    # Sidebar with usernames
    username = st.session_state.platform_usernames.get("username", "User")
    st.sidebar.title("Platform Usernames")
    for plat, user in st.session_state.platform_usernames.items():
        st.sidebar.write(f"**{plat.capitalize()}**: {user}")

    # 6️⃣ Show charts
    st.title(f"📊 {username}'s Coding Practice Dashboard")

    stats = st.session_state.dashboard_data

    if "language_stats" in stats and stats["language_stats"]:
        st.subheader("Languages Used")
        lang_df = pd.DataFrame(stats["language_stats"])
        fig_lang = px.bar(lang_df, x="language", y="count", color="language", title="Languages Used")
        st.plotly_chart(fig_lang)
    else:
        st.info("No language data available.")

    if "platform_stats" in stats and stats["platform_stats"]:
        st.subheader("Problems Solved per Platform")
        plat_df = pd.DataFrame(stats["platform_stats"])
        fig_plat = px.bar(plat_df, x="platform", y="total_solved", color="platform", title="Problems Solved")
        st.plotly_chart(fig_plat)

        # Optional donut pie chart
        fig_pie = px.pie(plat_df, values="total_solved", names="platform", hole=0.4,
                        title="Distribution of Problems Solved by Platform")
        st.plotly_chart(fig_pie)
    else:
        st.info("No platform data available.")

    if "topic_stats" in stats and stats["topic_stats"]:
        st.subheader("Topic-wise Distribution (LeetCode)")
        topic_df = pd.DataFrame(stats["topic_stats"])
        fig_topic = px.bar(topic_df, y="topic", x="total_attempted", orientation="h",
                        title="Topic-wise Problems Attempted", color="topic")
        st.plotly_chart(fig_topic)
    else:
        st.info("No topic data available.")
