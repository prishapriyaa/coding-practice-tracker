import db from '../db/db.js';

export const upsertPlatformStats = (req, res) => {
  const { user_id, platform, total_attempted, last_updated } = req.body;

  const checkQuery = `
    SELECT * FROM platform_stats WHERE user_id = ? AND platform = ?
  `;

  db.query(checkQuery, [user_id, platform], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });

    const query = results.length > 0
      ? `UPDATE platform_stats SET total_attempted = ?, last_updated = ? WHERE user_id = ? AND platform = ?`
      : `INSERT INTO platform_stats (total_attempted, last_updated, user_id, platform) VALUES (?, ?, ?, ?)`;

    const values = results.length > 0
      ? [total_attempted, last_updated, user_id, platform]
      : [total_attempted, last_updated, user_id, platform];

    db.query(query, values, (err2) => {
      if (err2) return res.status(500).json({ error: err2.message });
      res.status(200).json({ message: 'Platform stats updated' });
    });
  });
};

export const getPlatformStats = (req, res) => {
  const { userId } = req.params;

  const query = `SELECT platform, total_attempted FROM platform_stats WHERE user_id = ?`;

  db.query(query, [userId], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });

    const formatted = results.map(row => ({
      platform: row.platform,
      total_solved: row.total_attempted
    }));

    res.status(200).json(formatted);
  });
};

