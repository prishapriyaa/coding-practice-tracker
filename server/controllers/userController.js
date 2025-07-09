// controllers/userController.js
import db from '../db/db.js';

export const addUser = (req, res) => {
  const {username, leetcode, gfg, codeforces, hackerrank, email} =req.body;
  console.log("username is: "+username)

  if (!username) {
    return res.status(400).json({ error: 'Username is required' });
  }

  const query = `
    INSERT INTO users (username, leetcode_handle, gfg_handle, codeforces_handle, hackerrank_handle, email)
    VALUES (?, ?, ?, ?, ?, ?)
  `;

  const values = [username, leetcode, gfg, codeforces, hackerrank, email];

  db.query(query, values, (err, result) => {
    if (err) return res.status(500).json({ error: err.message });

    res.status(201).json({
      message: 'User created',
      userId: result.insertId
    });
  });
};

export const getUserByUsername = (req, res) => {
  const { username } = req.params;

  const query = "SELECT id FROM users WHERE username = ?";
  db.query(query, [username], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });

    if (results.length > 0) {
      return res.status(200).json({ userId: results[0].id });
    } else {
      return res.status(404).json({ error: "User not found" });
    }
  });
};



