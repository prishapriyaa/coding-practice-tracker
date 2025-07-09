import express from 'express';
import { upsertLanguageStats, getLanguageStats } from '../controllers/languageStatsController.js';

const router = express.Router();
router.post('/', upsertLanguageStats);
router.get('/:userId', getLanguageStats);

export default router;
