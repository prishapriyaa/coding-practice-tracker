import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';

import userRoutes from './routes/userRoutes.js';
import platformStatsRoutes from './routes/platformStatsRoutes.js';
import topicStatsRoutes from './routes/topicStatsRoutes.js';
import languageStatsRoutes from './routes/languageStatsRoutes.js'; 

dotenv.config();

const app = express();
app.use(cors());

app.use((req, res, next) => {
  if (req.method === 'POST' || req.method === 'PUT' || req.method === 'PATCH') {
    express.json()(req, res, next);
  } else {
    next();
  }
});

app.use('/api/users', userRoutes);
app.use('/api/platform-stats', platformStatsRoutes);
app.use('/api/topic-stats', topicStatsRoutes);
app.use('/api/language-stats', languageStatsRoutes); 

//process.env.PORT ||
const PORT = 5000;
app.listen(PORT, '0.0.0.0',()=> console.log(`Server running on http://localhost:${PORT}`));
