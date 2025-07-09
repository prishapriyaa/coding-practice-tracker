import db from '../db/db.js';
export const upsertTopicStats = (req, res) => {
   //console.log('Headers:', req.headers);
  //console.log('Body:', req.body);
    // res.sendStatus(200);

  const { user_id, platform, topic, total_attempted, last_updated } = req.body;
  const mysqlDate= new Date(last_updated);

  const checkQuery = `
    SELECT * FROM topic_stats WHERE user_id = ? AND platform = ? AND topic = ?
  `;

  db.query(checkQuery, [user_id, platform, topic], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });

    const query = results.length > 0
      ? `UPDATE topic_stats SET total_attempted = ?, last_updated = ? WHERE user_id = ? AND platform = ? AND topic = ?`
      : `INSERT INTO topic_stats (total_attempted, last_updated, user_id, platform, topic) VALUES (?, ?, ?, ?, ?)`;

    const values = results.length > 0
      ? [total_attempted, mysqlDate, user_id, platform, topic]
      : [total_attempted, mysqlDate, user_id, platform, topic];

    db.query(query, values, (err2) => {
      if (err2) {
        console.error("DB error on insert/update:", err2);
        return res.status(500).json({ error: err2.message });
      }
      res.status(200).json({ message: 'Topic stats updated' });
    });

  });
};

export const getTopicStats = (req, res) => {
  const { userId } = req.params;

  const query = `SELECT topic, total_attempted FROM topic_stats WHERE user_id = ?`;

  db.query(query, [userId], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });

    res.status(200).json(results); // already in correct format
  });
};


// export const upsertTopicStats = (req, res) => {
//   console.log("Received payload for /topic-stats:", req.body);  // <-- Debug log

//   const { user_id, platform, topic, total_attempted, last_updated } = req.body;

//   const checkQuery = `
//     SELECT * FROM topic_stats WHERE user_id = ? AND platform = ? AND topic = ?
//   `;

//   db.query(checkQuery, [user_id, platform, topic], (err, results) => {
//     if (err) {
//       console.error("Error during SELECT:", err);
//       return res.status(500).json({ error: err.message });
//     }

//     const query = results.length > 0
//       ? `UPDATE topic_stats SET total_attempted = ?, last_updated = ? WHERE user_id = ? AND platform = ? AND topic = ?`
//       : `INSERT INTO topic_stats (total_attempted, last_updated, user_id, platform, topic) VALUES (?, ?, ?, ?, ?)`;

//     const values = results.length > 0
//       ? [total_attempted, last_updated, user_id, platform, topic]
//       : [total_attempted, last_updated, user_id, platform, topic];

//     db.query(query, values, (err2) => {
//       if (err2) {
//         console.error("Error during INSERT/UPDATE:", err2);  // <-- Debug log
//         return res.status(500).json({ error: err2.message });
//       }

//       res.status(200).json({ message: 'Topic stats updated' });
//     });
//   });
// };
