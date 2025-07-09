import express from 'express';
import { upsertTopicStats, getTopicStats } from '../controllers/topicStatsController.js';

const router = express.Router();

// Route: POST /api/topic-stats
router.post('/', upsertTopicStats);
router.get('/:userId', getTopicStats);

export default router;
