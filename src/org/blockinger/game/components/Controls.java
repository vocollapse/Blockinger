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

package org.blockinger.game.components;

import org.blockinger.game.R;
import org.blockinger.game.activities.GameActivity;
import org.blockinger.game.pieces.*;


import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class Controls extends Component {

	// Constants
	
	// Stuff
	private Board board;
	//private boolean initialized;
	private Vibrator v;
	private int vibrationOffset;
	private long shortVibeTime;
	private int[] lineThresholds;
	
	// Player Controls
	private boolean playerSoftDrop;
	private boolean clearPlayerSoftDrop;
	private boolean playerHardDrop;
	private boolean leftMove;
	private boolean rightMove;
	private boolean continuousSoftDrop;
	private boolean continuousLeftMove;
	private boolean continuousRightMove;
	private boolean clearLeftMove;
	private boolean clearRightMove;
	private boolean leftRotation;
	private boolean rightRotation;
	private boolean buttonVibrationEnabled;
	private boolean eventVibrationEnabled;
	private int initialHIntervalFactor;
	private int initialVIntervalFactor;
	
	public Controls(GameActivity ga) {
		super(ga);
		
		lineThresholds = host.getResources().getIntArray(R.array.line_thresholds);
		shortVibeTime = 0;
		
		v = (Vibrator) host.getSystemService(Context.VIBRATOR_SERVICE);
		
		buttonVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_vibration_button", false);
		eventVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_vibration_events", false);
		try {
			vibrationOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(host).getString("pref_vibDurOffset", "0"));
		} catch(NumberFormatException e) {
			vibrationOffset = 0;
		}
		if(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_accelerationH", true))
			initialHIntervalFactor = 2;
		else
			initialHIntervalFactor = 1;
		if(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_accelerationV", true))
			initialVIntervalFactor = 2;
		else
			initialVIntervalFactor = 1;
		playerSoftDrop = false;
		leftMove = false;
		rightMove = false;
		leftRotation = false;
		rightRotation = false;
		clearLeftMove = false;
		clearRightMove = false;
		clearPlayerSoftDrop = false;
		continuousSoftDrop = false;
		continuousLeftMove = false;
		continuousRightMove = false;
	}
	
	public void vibrateWall() {
		if (v == null)
			return;
		if (!eventVibrationEnabled)
			return;
		if(((AudioManager)host.getSystemService(Context.AUDIO_SERVICE)).getRingerMode() == AudioManager.RINGER_MODE_SILENT)
			return;
		v.vibrate(host.game.getMoveInterval() + vibrationOffset);
	}
	
	public void cancelVibration() {
		v.cancel();
	}
	
	public void vibrateBottom() {
		if (v == null)
			return;
		if (!eventVibrationEnabled)
			return;
		if(((AudioManager)host.getSystemService(Context.AUDIO_SERVICE)).getRingerMode() == AudioManager.RINGER_MODE_SILENT)
			return;
		v.cancel();
		v.vibrate(new long[] {0, 5 + vibrationOffset, 30 + vibrationOffset, 20 + vibrationOffset}, -1);
	}
	
	public void vibrateShort() {
		if (v == null)
			return;
		if (!buttonVibrationEnabled)
			return;
		if(((AudioManager)host.getSystemService(Context.AUDIO_SERVICE)).getRingerMode() == AudioManager.RINGER_MODE_SILENT)
			return;
		if((host.game.getTime() - shortVibeTime) > (host.getResources().getInteger(R.integer.shortVibeInterval) + vibrationOffset)) {
			shortVibeTime = host.game.getTime();
			v.vibrate(vibrationOffset);
		}
	}


	public void rotateLeftPressed() {
		leftRotation = true;
		host.game.action();
		vibrateShort();
		host.sound.buttonSound(2);
    	//Thread.yield();
	}

	public void rotateLeftReleased() {
    	//Thread.yield();
	}

	public void rotateRightPressed() {
		rightRotation = true;
		host.game.action();
		vibrateShort();
		host.sound.buttonSound(2);
	    //Thread.yield();
	}

	public void rotateRightReleased() {
    	//Thread.yield();
	}

	public void downButtonReleased() {
		clearPlayerSoftDrop = true;
		vibrateShort();
	    //Thread.yield();
	}

	public void downButtonPressed() {
		host.game.action();
		playerSoftDrop = true;
		clearPlayerSoftDrop = false;
		vibrateShort();
		host.game.setNextPlayerDropTime(host.game.getTime());
		host.sound.buttonSound(3);
	}

	public void dropButtonReleased() {
		
	}

	public void dropButtonPressed() {
		if(!host.game.getActivePiece().isActive())
			return;
		host.game.action();
		playerHardDrop = true;
		if(buttonVibrationEnabled & !eventVibrationEnabled)
			vibrateShort();
	}

	public void leftButtonReleased() {
		clearLeftMove = true;
		cancelVibration();
	}

	public void leftButtonPressed() {
		host.game.action();
		clearLeftMove = false;
		leftMove = true;
		rightMove = false;
		host.game.setNextPlayerMoveTime(host.game.getTime());
		host.sound.buttonSound(1);
	}

	public void rightButtonReleased() {
		clearRightMove = true;
		cancelVibration();
	}

	public void rightButtonPressed() {
		host.game.action();
		clearRightMove = false;
		rightMove = true;
		leftMove = false;
		host.game.setNextPlayerMoveTime(host.game.getTime());
		host.sound.buttonSound(1);
	}

	public void cycle(long tempTime) {
		long gameTime = host.game.getTime();
		Piece active = host.game.getActivePiece();
		Board board = host.game.getBoard();
		int maxLevel = host.game.getMaxLevel();

		
		// Left Rotation
		if(leftRotation) {
			leftRotation = false;
			active.turnLeft(board);
			host.display.invalidatePhantom();
		}
		
		// Right Rotation
		if(rightRotation) {
			rightRotation = false;
			active.turnRight(board);
			host.display.invalidatePhantom();
		}
		
		// Reset Move Time
		if((!leftMove && !rightMove) && (!continuousLeftMove && !continuousRightMove))
			host.game.setNextPlayerMoveTime(gameTime);
		
		// Left Move
		if(leftMove) {
			continuousLeftMove = true;
			leftMove = false;
			if(active.moveLeft(board)) { // successful move
				vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
				host.display.invalidatePhantom();
				host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + initialHIntervalFactor*host.game.getMoveInterval());
			} else { // failed move
				vibrateWall();
				host.game.setNextPlayerMoveTime(gameTime);
			}
			
		} else if(continuousLeftMove) {
			if(gameTime >= host.game.getNextPlayerMoveTime()) {
				if(active.moveLeft(board)) { // successful move
					vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
					host.display.invalidatePhantom();
					host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + host.game.getMoveInterval());
				} else { // failed move
					vibrateWall();
					host.game.setNextPlayerMoveTime(gameTime);
				}
			}

			if(clearLeftMove) {
				continuousLeftMove = false;
				clearLeftMove = false;
			}
		}
		
		// Right Move
		if(rightMove) {
			continuousRightMove = true;
			rightMove = false;
			if(active.moveRight(board)) { // successful move
				vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
				host.display.invalidatePhantom();
				host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + initialHIntervalFactor*host.game.getMoveInterval());
			} else { // failed move
				vibrateWall();
				host.game.setNextPlayerMoveTime(gameTime); // first interval is doubled!
			}
			
		} else if(continuousRightMove) {
			if(gameTime >= host.game.getNextPlayerMoveTime()) {
				if(active.moveRight(board)) { // successful move
					vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
					host.display.invalidatePhantom();
					host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + host.game.getMoveInterval());
				} else { // failed move
					vibrateWall();
					host.game.setNextPlayerMoveTime(gameTime);
				}
			}

			if(clearRightMove) {
				continuousRightMove = false;
				clearRightMove = false;
			}
		}
		
		
		// Hard Drop
		if(playerHardDrop) {
			board.interruptClearAnimation();
			int hardDropDistance = active.hardDrop(false, board);
			vibrateBottom();
			host.game.clearLines(true, hardDropDistance);
			host.game.pieceTransition(eventVibrationEnabled);
			board.invalidate();
			playerHardDrop = false;
			
			if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
				host.game.nextLevel();
			host.game.setNextDropTime(gameTime + host.game.getAutoDropInterval());
			host.game.setNextPlayerDropTime(gameTime);

		// Initial Soft Drop
		} else if(playerSoftDrop) {
			playerSoftDrop = false;
			continuousSoftDrop = true;
			if(!active.drop(board)) {
				// piece finished
				vibrateBottom();
				host.game.clearLines(false, 0);
				host.game.pieceTransition(eventVibrationEnabled);
				board.invalidate();
			} else {
				host.game.incSoftDropCounter();
			}
			if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
				host.game.nextLevel();
			host.game.setNextDropTime(host.game.getNextPlayerDropTime() + host.game.getAutoDropInterval());
			host.game.setNextPlayerDropTime(host.game.getNextPlayerDropTime() + initialVIntervalFactor*host.game.getSoftDropInterval());
			
		// Continuous Soft Drop
		} else if(continuousSoftDrop) {
			if(gameTime >= host.game.getNextPlayerDropTime()) {
				if(!active.drop(board)) {
					// piece finished
					vibrateBottom();
					host.game.clearLines(false, 0);
					host.game.pieceTransition(eventVibrationEnabled);
					board.invalidate();
				} else {
					host.game.incSoftDropCounter();
				}
				if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
					host.game.nextLevel();
				host.game.setNextDropTime(host.game.getNextPlayerDropTime() + host.game.getAutoDropInterval());
				host.game.setNextPlayerDropTime(host.game.getNextPlayerDropTime() + host.game.getSoftDropInterval());
				
			// Autodrop if faster than playerDrop
			} else if(gameTime >= host.game.getNextDropTime()) {
				if(!active.drop(board)) {
					// piece finished
					vibrateBottom();
					host.game.clearLines(false, 0);
					host.game.pieceTransition(eventVibrationEnabled);
					board.invalidate();
				}
				if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
					host.game.nextLevel();
				host.game.setNextDropTime(host.game.getNextDropTime() + host.game.getAutoDropInterval());
				host.game.setNextPlayerDropTime(host.game.getNextDropTime() + host.game.getSoftDropInterval());
			}

			/* Cancel continuous SoftDrop */
			if(clearPlayerSoftDrop) {
				continuousSoftDrop = false;
				clearPlayerSoftDrop = false;
			}
			
		// Autodrop if no playerDrop
		} else if(gameTime >= host.game.getNextDropTime()) {
			if(!active.drop(board)) {
				// piece finished
				vibrateBottom();
				host.game.clearLines(false, 0);
				host.game.pieceTransition(eventVibrationEnabled);
				board.invalidate();
			}
			if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
				host.game.nextLevel();
			host.game.setNextDropTime(host.game.getNextDropTime() + host.game.getAutoDropInterval());
			host.game.setNextPlayerDropTime(host.game.getNextDropTime());
			
		} else
			host.game.setNextPlayerDropTime(gameTime);
	}

	public void setBoard(Board instance2) {
		this.board = instance2;
	}

	public Board getBoard() {
		return this.board;
	}
	
	/**
	 * unused!
	 */
	@Override
	public void reconnect(GameActivity cont) {
		super.reconnect(cont);
		v = (Vibrator) cont.getSystemService(Context.VIBRATOR_SERVICE);
		
		buttonVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(cont).getBoolean("pref_vibration_button", false);
		eventVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(cont).getBoolean("pref_vibration_events", false);
		try {
			vibrationOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(cont).getString("pref_vibDurOffset", "0"));
		} catch(NumberFormatException e) {
			vibrationOffset = 0;
		}
	}

	/**
	 * unused!
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		v = null;
	}
	
}
