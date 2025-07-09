import express from 'express';
import { addUser, getUserByUsername } from '../controllers/userController.js';

const router = express.Router();

router.post('/', addUser);
router.get('/:username', getUserByUsername);

export default router;
