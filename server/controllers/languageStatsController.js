import db from '../db/db.js';

export const upsertLanguageStats = (req, res) => {
  const { user_id, platform, language, total_attempted } = req.body;

  const checkQuery = `
    SELECT * FROM language_stats WHERE user_id = ? AND platform = ? AND language = ?
  `;

  db.query(checkQuery, [user_id, platform, language], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });

    const query = results.length > 0
      ? `UPDATE language_stats SET total_attempted = ? WHERE user_id = ? AND platform = ? AND language = ?`
      : `INSERT INTO language_stats (total_attempted, user_id, platform, language) VALUES (?, ?, ?, ?)`;

    const values = results.length > 0
      ? [total_attempted, user_id, platform, language]
      : [total_attempted, user_id, platform, language];

    db.query(query, values, (err2) => {
      if (err2) return res.status(500).json({ error: err2.message });
      res.status(200).json({ message: 'Language stats updated' });
    });
  });
};

export const getLanguageStats = (req, res) => {
  const { userId } = req.params;

  const query = `SELECT language, total_attempted FROM language_stats WHERE user_id = ?`;

  db.query(query, [userId], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });

    const formatted = results.map(row => ({
      language: row.language,
      count: row.total_attempted
    }));

    res.status(200).json(formatted);
  });
};
