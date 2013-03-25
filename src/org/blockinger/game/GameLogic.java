/*
 * Copyright 2013 Simon Willeke
 * contact: hamstercount@hotmail.com
 */

/*
    This file is part of Blockinger.

    Blockinger is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Blockinger is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Blockinger.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Blockinger.

    Blockinger ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Blockinger wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package org.blockinger.game;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import org.blockinger.game.pieces.*;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;

public class GameLogic {

	// Constants
	
	// Stuff
	private static GameLogic instance;
	private BlockBoard board;
	private int activeIndex, previewIndex;
	private Piece[] activePieces;
	private Piece[] previewPieces;
	//private Context c;
	private boolean initialized;
	private PieceGenerator rng;
	private DefeatDialogFragment dialog;
	private GregorianCalendar date;
	private SimpleDateFormat formatter;
	public int hourOffset;
	private SoundComponent sound;
	
	// Player Controls
	//private boolean finishPiece;
	private boolean playerSoftDrop;
	private boolean clearPlayerSoftDrop;
	private boolean playerHardDrop;
	private boolean leftMove;
	private boolean rightMove;
	private boolean clearLeftMove;
	private boolean clearRightMove;
	private boolean leftRotation;
	private boolean rightRotation;
	private boolean multitetris;
	private boolean buttonVibrationEnabled;
	private boolean eventVibrationEnabled;
	private int hardDropDistance;
	
	// Game State
	private boolean scheduleSpawn;
	private long spawnTime;
	private long actions;
	private boolean paused;
	private boolean restartMe;
	private long score;
	private long consecutiveBonusScore;
	private int clearedLines;
	private int level;
	private int maxLevel;
	private long gameTime;     // += (systemtime - currenttime) at start of cycle
	private long currentTime;  // = systemtime at start of cycle
	private int[] dropIntervals; // =(1/gamespeed)
	private int[] lineThresholds; // =(1/gamespeed)
	private long nextDropTime;
	private long playerDropInterval;
	private long nextPlayerDropTime;
	private long playerMoveInterval;
	private long nextPlayerMoveTime;
	//private android.support.v4.app.FragmentManager fragmentManager;
	private int prevPhantomY;
	private boolean dropPhantom;
	private FragmentManager fragmentManager;
	//TO INITIALIZE WITH CONTEXT
	private Vibrator v;
	private int singleLineScore;
	private int doubleLineScore;
	private int trippleLineScore;
	private int multiTetrisScore;
	private int quadLineScore;
	private int hardDropBonusFactor;
	private int spawn_delay;
	private int piece_start_x;
	private int vibrationOffset;
	private long softdroppresstime;
	
	private GameLogic() {
		date = new GregorianCalendar();
		formatter = new SimpleDateFormat("HH:mm:ss",Locale.US);
		date.setTimeInMillis(60000);
		if(formatter.format(date.getTime()).startsWith("23"))
			hourOffset = 1;
		else if(formatter.format(date.getTime()).startsWith("01"))
			hourOffset = -1;
		else
			hourOffset = 0;
		buttonVibrationEnabled = false;
		eventVibrationEnabled = false;
		multitetris = false;
		initialized = false;
		paused = true;
		restartMe = true;
		actions = 0;
		score = 0;
		consecutiveBonusScore = 0;
		level = 0;
		maxLevel = 0;
		clearedLines = 0;
		gameTime = 0;
		nextDropTime = 0;
		playerDropInterval = 0;
		nextPlayerDropTime = 0;
		playerMoveInterval = 0;
		nextPlayerMoveTime = 0;
		softdroppresstime = 0;
		playerSoftDrop = false;
		leftMove = false;
		rightMove = false;
		leftRotation = false;
		rightRotation = false;
		hardDropDistance = 0;
		scheduleSpawn = false;
		spawnTime = 0;
		clearLeftMove = false;
		clearRightMove = false;
		clearPlayerSoftDrop = false;
		dropPhantom = true;
		prevPhantomY = 0;
		vibrationOffset = 0;
	}
	
	public int getCurrentDropinterval() {
		return dropIntervals[Math.min(level,maxLevel)];
	}
	
	public void init(Context cont) {
		//this.c = cont;
		sound = new SoundComponent(cont);
		dialog = new DefeatDialogFragment();
		dialog.setCancelable(false);
		dropIntervals = cont.getResources().getIntArray(R.array.intervals);
		lineThresholds = cont.getResources().getIntArray(R.array.line_thresholds);
		singleLineScore = cont.getResources().getInteger(R.integer.singleLineScore);
		doubleLineScore = cont.getResources().getInteger(R.integer.doubleLineScore);
		trippleLineScore = cont.getResources().getInteger(R.integer.trippleLineScore);
		multiTetrisScore = cont.getResources().getInteger(R.integer.multiTetrisScore);
		quadLineScore = cont.getResources().getInteger(R.integer.quadLineScore);
		hardDropBonusFactor = cont.getResources().getInteger(R.integer.hardDropBonusFactor);
		spawn_delay = cont.getResources().getInteger(R.integer.spawn_delay);
		piece_start_x = cont.getResources().getInteger(R.integer.piece_start_x);
		clearedLines = 0;
		gameTime = 0;
		actions = 0;
		level = 0;
		score = 0;
		consecutiveBonusScore = 0;
		softdroppresstime = 0;
		multitetris = false;
		maxLevel = cont.getResources().getInteger(R.integer.levels);
		nextDropTime = dropIntervals[level];
		
		v = (Vibrator) cont.getSystemService(Context.VIBRATOR_SERVICE);
		
		playerDropInterval = (int)(1000.0f / PreferenceManager.getDefaultSharedPreferences(cont).getInt("pref_softdropspeed", 60));
		nextPlayerDropTime = (int)(1000.0f / PreferenceManager.getDefaultSharedPreferences(cont).getInt("pref_softdropspeed", 60));
		playerMoveInterval = (int)(1000.0f / PreferenceManager.getDefaultSharedPreferences(cont).getInt("pref_movespeed", 60));
		nextPlayerMoveTime = (int)(1000.0f / PreferenceManager.getDefaultSharedPreferences(cont).getInt("pref_movespeed", 60));
		gameTime = 0;
		if(PreferenceManager.getDefaultSharedPreferences(cont).getString("pref_rng", "sevenbag").equals("sevenbag") ||
				PreferenceManager.getDefaultSharedPreferences(cont).getString("pref_rng", "7-Bag-Randomization (default)").equals("7-Bag-Randomization (default)"))
			rng = new PieceGenerator(PieceGenerator.STRAT_7BAG);
		else
			rng = new PieceGenerator(PieceGenerator.STRAT_RANDOM);
		
		buttonVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(cont).getBoolean("pref_vibration_button", false);
		eventVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(cont).getBoolean("pref_vibration_events", false);
		try {
			vibrationOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(cont).getString("pref_vibDurOffset", "0"));
		} catch(NumberFormatException e) {
			vibrationOffset = 0;
		}
		
		// Initialize Pieces
		activePieces  = new Piece[7];
		previewPieces = new Piece[7];
		
		activePieces[0] = new IPiece(cont);
		activePieces[1] = new JPiece(cont);
		activePieces[2] = new LPiece(cont);
		activePieces[3] = new OPiece(cont);
		activePieces[4] = new SPiece(cont);
		activePieces[5] = new TPiece(cont);
		activePieces[6] = new ZPiece(cont);
		
		previewPieces[0] = new IPiece(cont);
		previewPieces[1] = new JPiece(cont);
		previewPieces[2] = new LPiece(cont);
		previewPieces[3] = new OPiece(cont);
		previewPieces[4] = new SPiece(cont);
		previewPieces[5] = new TPiece(cont);
		previewPieces[6] = new ZPiece(cont);
		
		// starting pieces
		activeIndex  = rng.next();
		previewIndex = rng.next();
		activePieces[activeIndex].setActive(true);

		restartMe = false;
		initialized = true;
		hardDropDistance = 0;
		playerSoftDrop = false;
		leftMove = false;
		rightMove = false;
		leftRotation = false;
		rightRotation = false;
		scheduleSpawn = false;
		spawnTime = 0;
		clearLeftMove = false;
		clearRightMove = false;
		clearPlayerSoftDrop = false;
		dropPhantom = true;
		prevPhantomY = 0;
	}
	
	public void setRunning(boolean b) {
		if(!initialized)
			throw new RuntimeException("GameLogic has not been initialized!");
		if(b)
			currentTime = System.currentTimeMillis();
		
		paused = !b;
	}
	
	public void clearLines() {
		activePieces[activeIndex].place();
		int cleared = board.clearLines(activePieces[activeIndex].getDim());
		clearedLines += cleared;
		long addScore;
		
		switch(cleared) {
			case 1:
				addScore = singleLineScore;
				multitetris = false;
				sound.clearSound();
				break;
			case 2:
				addScore = doubleLineScore;
				multitetris = false;
				sound.clearSound();
				break;
			case 3:
				addScore = trippleLineScore;
				multitetris = false;
				sound.clearSound();
				break;
			case 4:
				if(multitetris)
					addScore = multiTetrisScore;
				else
					addScore = quadLineScore;
				multitetris = true;
				sound.tetrisSound();
				break;
			default:
				addScore = 0;
				consecutiveBonusScore = 0;
				sound.dropSound();
				break;
		}
		long tempBonus = consecutiveBonusScore;
		consecutiveBonusScore += addScore;
		if(playerHardDrop)
			addScore = (int)((float)addScore* (1.0f + ((float)hardDropDistance/(float)hardDropBonusFactor)));
		score += addScore + tempBonus;
		BlockBoard.getInstance().popupScore(addScore);
	}
	
	public void pieceTransition() {
		if (!initialized)
			throw new RuntimeException("GameLogic has not been initialized!");
		
		scheduleSpawn = true;
		
		//Delay Piece Transition only, while vibration is playing
		if(eventVibrationEnabled)
			spawnTime = gameTime + spawn_delay;
		else
			spawnTime = gameTime;
		
		activePieces[activeIndex].reset();
		activeIndex  = previewIndex;
		previewIndex = rng.next();
		activePieces[activeIndex].reset();
	}
	
	public void finishTransition() {
		scheduleSpawn = false;
		dropPhantom = true;
		activePieces[activeIndex].setActive(true);
		nextDropTime = gameTime + dropIntervals[Math.min(level,maxLevel)];
		nextPlayerDropTime = gameTime;
		nextPlayerMoveTime = gameTime;
		if(!activePieces[activeIndex].setPosition(piece_start_x, 0, false)) {
			setRunning(false);
			//DEFEAT HERE
			dialog.setData(score, gameTime, (int)((float)actions*(60000.0f / gameTime)));
			dialog.show(fragmentManager, "hamster");
			restartMe = true;
		}
	}
	
	public boolean isResumable() {
		return !restartMe;
	}

	public String getScoreString() {
		return "" + score;
	}
	
	public void vibrateWall() {
		if (!eventVibrationEnabled || v == null)
			return;
		//v.cancel();
		//v.vibrate(20);
		v.vibrate(playerMoveInterval + vibrationOffset);
		//v.vibrate(new long[] {0, 10 , 10, 10}, -1);
	}
	
	public void cancelVibration() {
		if ((!eventVibrationEnabled && !buttonVibrationEnabled) || v == null)
			return;
		v.cancel();
	}
	
	public void vibrateBottom() {
		if (!eventVibrationEnabled || v == null)
			return;
		v.cancel();
		v.vibrate(new long[] {0, 5 + vibrationOffset, 30 + vibrationOffset, 20 + vibrationOffset}, -1);
	}
	
	public void vibrateShort() {
		if (!buttonVibrationEnabled || v == null)
			return;
		v.cancel();
		v.vibrate(5 + vibrationOffset);
	}


	public void rotateLeftPressed() {
		leftRotation = true;
		actions++;
		vibrateShort();
    	//Thread.yield();
	}

	public void rotateLeftReleased() {
    	//Thread.yield();
	}

	public void rotateRightPressed() {
		rightRotation = true;
		actions++;
		vibrateShort();
	    //Thread.yield();
	}

	public void rotateRightReleased() {
    	//Thread.yield();
	}

	public void downButtonReleased() {
		clearPlayerSoftDrop = true;
		if((gameTime - softdroppresstime) > 200)
			vibrateShort();
	    //Thread.yield();
	}

	public void downButtonPressed() {
		softdroppresstime = gameTime;
		actions++;
		playerSoftDrop = true;
		clearPlayerSoftDrop = false;
		vibrateShort();
		nextPlayerDropTime = gameTime;
    	//Thread.yield();
	}

	public void dropButtonReleased() {
    	//Thread.yield();
	}

	public void dropButtonPressed() {
		if(!activePieces[activeIndex].isActive())
			return;
		actions++;
		playerHardDrop = true;
		if(buttonVibrationEnabled & !eventVibrationEnabled)
			vibrateShort();
	}

	public void leftButtonReleased() {
		clearLeftMove = true;
		cancelVibration();
    	//Thread.yield();
	}

	public void leftButtonPressed() {
		actions++;
		clearLeftMove = false;
		leftMove = true;
		rightMove = false;
		nextPlayerMoveTime = gameTime;
		//vibrateShort(); wird schon unten gemacht (weil in jedem tick)
		
    	//Thread.yield();
	}

	public void rightButtonReleased() {
		clearRightMove = true;
		cancelVibration();
    	//Thread.yield();
	}

	public void rightButtonPressed() {
		actions++;
		clearRightMove = false;
		rightMove = true;
		leftMove = false;
		nextPlayerMoveTime = gameTime;
		//vibrateShort(); wird schon unten gemacht (weil in jedem tick)
		
    	//Thread.yield();
	}

	public void drawActive(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
			Canvas c, BlockBoardView blockBoardView) {
		activePieces[activeIndex].drawOnBoard(spaltenOffset, zeilenOffset, spaltenAbstand, c, blockBoardView);
	}

	public void drawPhantom(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
			Canvas c, BlockBoardView blockBoardView) {
		int y = activePieces[activeIndex].getY();
		int x = activePieces[activeIndex].getX();
		activePieces[activeIndex].setPhantom(true);
		
		if(dropPhantom) {
			int backup__currentRowIndex = BlockBoard.getInstance().getCurrentRowIndex();
			Row backup__currentRow = BlockBoard.getInstance().getCurrentRow();
			int cnt = y+1;
			
			while(activePieces[activeIndex].setPositionSimpleCollision(x, cnt)) {
				cnt++;
			}
			
			 BlockBoard.getInstance().setCurrentRowIndex(backup__currentRowIndex);
			 BlockBoard.getInstance().setCurrentRow(backup__currentRow);
		} else
			activePieces[activeIndex].setPositionSimple(x, prevPhantomY);
		
		prevPhantomY = activePieces[activeIndex].getY();
		activePieces[activeIndex].drawOnBoard(spaltenOffset, zeilenOffset, spaltenAbstand, c, blockBoardView);
		activePieces[activeIndex].setPositionSimple(x, y);
		activePieces[activeIndex].setPhantom(false);
		dropPhantom = false;
	}

	public void drawPreview(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
			Canvas c, BlockBoardView blockBoardView) {
		previewPieces[previewIndex].drawOnPreview(spaltenOffset, zeilenOffset, spaltenAbstand, c, blockBoardView);
	}

	public void cycle(long tempTime) {
		if (!initialized)
			throw new RuntimeException("GameLogic has not been initialized!");
		if(paused)
			return;
		
		gameTime += (tempTime - currentTime);
		currentTime = tempTime;
		
		// Instant Placement
		if(scheduleSpawn) {
			if(gameTime >= spawnTime) 
				finishTransition();
		
		// Hard Drop
		} else if(playerHardDrop) {
			BlockBoard.getInstance().interruptClearAnimation();
			hardDropDistance = activePieces[activeIndex].hardDrop(false);
			vibrateBottom();
			clearLines();
			pieceTransition();
			BlockBoard.getInstance().invalidate();
			playerHardDrop = false;
			
			if((level < maxLevel) && (clearedLines > lineThresholds[Math.min(level,maxLevel - 1)]))
				level++;
			nextDropTime = gameTime + dropIntervals[Math.min(level,maxLevel)];
			nextPlayerDropTime = gameTime;
			
		// Soft Drop
		} else if(playerSoftDrop) {
			if(gameTime >= nextPlayerDropTime) {
				if(!activePieces[activeIndex].drop()) {
					// piece finished
					vibrateBottom();
					clearLines();
					pieceTransition();
					BlockBoard.getInstance().invalidate();
				} else {
					//vibrateShort(); DAS WUERDE HIER BEI JEDEM TICK VIBRIEREN UND DAS MUSS DOCH NICHT SEIN. BEIM BESTEN WILLEN NICHT MEIN LIEBER FREUND.
				}
				if((level < maxLevel) && (clearedLines > lineThresholds[Math.min(level,maxLevel - 1)]))
					level++;
				nextDropTime = nextPlayerDropTime + dropIntervals[Math.min(level,maxLevel)];
				nextPlayerDropTime = nextPlayerDropTime + playerDropInterval;
				
			// Autodrop if faster than playerDrop
			} else if(gameTime >= nextDropTime) {
				if(!activePieces[activeIndex].drop()) {
					// piece finished
					vibrateBottom();
					clearLines();
					pieceTransition();
					BlockBoard.getInstance().invalidate();
				}
				if((level < maxLevel) && (clearedLines > lineThresholds[Math.min(level,maxLevel - 1)]))
					level++;
				nextDropTime = nextDropTime + dropIntervals[Math.min(level,maxLevel)];
				nextPlayerDropTime = nextDropTime + playerDropInterval;
			}

			if(clearPlayerSoftDrop) {
				playerSoftDrop = false;
				clearPlayerSoftDrop = false;
			}
			
		// Autodrop if no playerDrop
		} else if(gameTime >= nextDropTime) {
			if(!activePieces[activeIndex].drop()) {
				// piece finished
				vibrateBottom();
				clearLines();
				pieceTransition();
				BlockBoard.getInstance().invalidate();
			}
			if((level < maxLevel) && (clearedLines > lineThresholds[Math.min(level,maxLevel - 1)]))
				level++;
			nextDropTime = nextDropTime + dropIntervals[Math.min(level,maxLevel)];
			nextPlayerDropTime = nextDropTime;
			
		} else
			nextPlayerDropTime = gameTime;

		
		// Reset Move Time
		if(!leftMove && !rightMove)
			nextPlayerMoveTime = gameTime;
		
		// Left Move
		if(leftMove) {
			if(gameTime >= nextPlayerMoveTime) {
				if(activePieces[activeIndex].moveLeft()) {
					vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
					dropPhantom = true;
				} else
					vibrateWall();
				nextPlayerMoveTime = nextPlayerMoveTime + playerMoveInterval;
			}

			if(clearLeftMove) {
				leftMove = false;
				clearLeftMove = false;
			}
		}
		
		// Right Move
		if(rightMove) {
			if(gameTime >= nextPlayerMoveTime) {
				if(activePieces[activeIndex].moveRight()) {
					vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
					dropPhantom = true;
				} else
					vibrateWall();
				nextPlayerMoveTime = nextPlayerMoveTime + playerMoveInterval;
			}

			if(clearRightMove) {
				rightMove = false;
				clearRightMove = false;
			}
		}
		
		// Left Rotation
		if(leftRotation) {
			leftRotation = false;
			activePieces[activeIndex].turnLeft();
			dropPhantom = true;
		}
		
		// Right Rotation
		if(rightRotation) {
			rightRotation = false;
			activePieces[activeIndex].turnRight();
			dropPhantom = true;
		}
	}

	public void setBoard(BlockBoard instance2) {
		this.board = instance2;
	}

	public BlockBoard getBoard() {
		return this.board;
	}

	public String getLevelString() {
		return "" + level;
	}

	public void setFragmentManager(android.support.v4.app.FragmentManager fm) {
		fragmentManager = fm;
	}

	public String getTimeString() {
		date.setTimeInMillis(gameTime + hourOffset*(3600000));
		//date.setTime(gameTime - (1000*60*60));
		return formatter.format(date.getTime());
	}

	public String getAPMString() {
		return String.valueOf((int)((float)actions*(60000.0f / gameTime)));
	}

	public void invalidatePhantom() {
		dropPhantom = true;
	}


	public static GameLogic getInstance() {
		if(instance == null)
			instance = new GameLogic();
		return instance;
	}

	public static void setInstance(GameLogic gl) {
		instance = gl;
	}
	
	
	private class PieceGenerator {

		private static final int STRAT_RANDOM = 0;
		private static final int STRAT_7BAG = 1;
		
		int strategy;
		int bag[];
		int bagPointer;
		private Random rndgen;
		
		public PieceGenerator(int strat) {
			bag = new int[7];
			for(int i = 0; i < 7; i++) //initial Permutation
				bag[i] = i;
			
			rndgen = new Random(System.currentTimeMillis());
			if(strat==STRAT_RANDOM)
				this.strategy = STRAT_RANDOM;
			else
				this.strategy = STRAT_7BAG;
			
			// Fill initial Bag
			for(int i = 0; i < 6; i++) {
				int c = rndgen.nextInt(7-i);
				int t = bag[i]; bag[i] = bag[i+c]; bag[i+c] = t;	/* swap */
			}
			bagPointer = 0;
		}

		public int next() {
			if(strategy== STRAT_RANDOM)
				return rndgen.nextInt(7);
			else {
				if(bagPointer < 7) {
					bagPointer++;
					return bag[bagPointer - 1];
				} else {
					// Randomize Bag
					for(int i = 0; i < 6; i++) {
						int c = rndgen.nextInt(7-i);
						int t = bag[i]; bag[i] = bag[i+c]; bag[i+c] = t;	/* swap */
					}
					bagPointer = 1;
					return bag[bagPointer - 1];
				}
			}
		}
	}


	public void reconnect(Context cont, FragmentManager supportFragmentManager) {
		v = (Vibrator) cont.getSystemService(Context.VIBRATOR_SERVICE);
		fragmentManager = supportFragmentManager;
		
		playerDropInterval = (int)(1000.0f / PreferenceManager.getDefaultSharedPreferences(cont).getInt("pref_softdropspeed", 60));
		nextPlayerDropTime = (int)(1000.0f / PreferenceManager.getDefaultSharedPreferences(cont).getInt("pref_softdropspeed", 60));
		playerMoveInterval = (int)(1000.0f / PreferenceManager.getDefaultSharedPreferences(cont).getInt("pref_movespeed", 60));
		nextPlayerMoveTime = (int)(1000.0f / PreferenceManager.getDefaultSharedPreferences(cont).getInt("pref_movespeed", 60));
		
		if(PreferenceManager.getDefaultSharedPreferences(cont).getString("pref_rng", "sevenbag").equals("sevenbag") ||
				PreferenceManager.getDefaultSharedPreferences(cont).getString("pref_rng", "7-Bag-Randomization (default)").equals("7-Bag-Randomization (default)"))
			rng = new PieceGenerator(PieceGenerator.STRAT_7BAG);
		else
			rng = new PieceGenerator(PieceGenerator.STRAT_RANDOM);
		
		buttonVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(cont).getBoolean("pref_vibration_button", false);
		eventVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(cont).getBoolean("pref_vibration_events", false);
		try {
			vibrationOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(cont).getString("pref_vibDurOffset", "0"));
		} catch(NumberFormatException e) {
			vibrationOffset = 0;
		}
		sound = new SoundComponent(cont);
	}

	public void disconnect() {
		setFragmentManager(null);
		v = null;
		sound.release();
	}
	
}
