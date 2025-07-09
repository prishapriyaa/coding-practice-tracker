import express from 'express';
import { upsertPlatformStats, getPlatformStats } from '../controllers/platformStatsController.js';

const router = express.Router();

// Route: POST /api/platform-stats
router.post('/', upsertPlatformStats);
router.get('/:userId', getPlatformStats);

export default router;
