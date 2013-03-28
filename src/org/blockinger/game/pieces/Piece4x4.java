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

package org.blockinger.game.pieces;

import org.blockinger.game.Square;
import org.blockinger.game.components.Board;

import android.content.Context;

public abstract class Piece4x4 extends Piece {
	
	/**
	 * Always call super(); first.
	 * @param width
	 * @param height
	 */
	protected Piece4x4(Context c) {
		super(c,4);
	}
	
	/**
	 * @return true if rotation was successfull.
	 */
	@Override
	public boolean turnLeft(Board board) {
		int maxLeftOffset = -4;
		int maxRightOffset = -4;
		int maxBottomOffset = -4;
		int leftOffset = 0;
		int rightOffset = 0;
		int bottomOffset = 0;
		Square backup[][] = pattern;
		// [0][0] ... [0][3]
		//  ....       ....
		// [3][0] ... [3][3]
		rotated[0][0] = pattern[0][3];
		rotated[0][3] = pattern[3][3];
		rotated[3][3] = pattern[3][0];
		rotated[3][0] = pattern[0][0];
		
		rotated[0][1] = pattern[1][3];
		rotated[1][3] = pattern[3][2];
		rotated[3][2] = pattern[2][0];
		rotated[2][0] = pattern[0][1];
		
		rotated[0][2] = pattern[2][3];
		rotated[2][3] = pattern[3][1];
		rotated[3][1] = pattern[1][0];
		rotated[1][0] = pattern[0][2];
		
		rotated[1][1] = pattern[1][2];
		rotated[1][2] = pattern[2][2];
		rotated[2][2] = pattern[2][1];
		rotated[2][1] = pattern[1][1];
		// check for border violations and collisions
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				if(rotated[i][j] != null) {
					leftOffset = - (x+j);
					rightOffset = (x+j) - (board.getWidth() - 1);
					bottomOffset = (y+i) - (board.getHeight() - 1);
					if(!rotated[i][j].isEmpty()) // left border violation
						if (maxLeftOffset < leftOffset)
							maxLeftOffset = leftOffset;
					if(!rotated[i][j].isEmpty()) // right border violation
						if (maxRightOffset < rightOffset)
							maxRightOffset = rightOffset;
					if(!rotated[i][j].isEmpty()) // bottom border violation
						if (maxBottomOffset < bottomOffset)
							maxBottomOffset = bottomOffset;
					if(board.get(x+j,y+i) != null)
						if(!rotated[i][j].isEmpty() && !board.get(x+j,y+i).isEmpty()) // collision
							return false;
				}
			}
		}
		
		backup = pattern;
		pattern = rotated;
		rotated = backup;
		
		// try to correct border violations
		if(maxBottomOffset < 1) {
			if(maxLeftOffset < 1)  {
				if(maxRightOffset < 1) {
					reDraw();
					return true;
				} else {
					if(setPosition(x - maxRightOffset, y, false, board)) {
						reDraw();
						return true;
					} else {
						rotated = pattern;
						pattern = backup;
						return false;
					}
				}
			} else {
				if(setPosition(x + maxLeftOffset, y, false, board)) {
					reDraw();
					return true;
				} else {
					rotated = pattern;
					pattern = backup;
					return false;
				}
			}
		} else {
			if(setPosition(x, y - maxBottomOffset, false, board)) {
				reDraw();
				return true;
			} else {
				rotated = pattern;
				pattern = backup;
				return false;
			}
		}
	}

	/**
	 * @return true if rotation was successfull.
	 */
	@Override
	public boolean turnRight(Board board) {
		int maxLeftOffset = -4;
		int maxRightOffset = -4;
		int maxBottomOffset = -4;
		int leftOffset = 0;
		int rightOffset = 0;
		int bottomOffset = 0;
		Square backup[][] = pattern;
		// [0][0] ... [0][3]
		//  ....       ....
		// [3][0] ... [3][3]
		rotated[0][3] = pattern[0][0];
		rotated[3][3] = pattern[0][3];
		rotated[3][0] = pattern[3][3];
		rotated[0][0] = pattern[3][0];
		
		rotated[1][3] = pattern[0][1];
		rotated[3][2] = pattern[1][3];
		rotated[2][0] = pattern[3][2];
		rotated[0][1] = pattern[2][0];
		
		rotated[2][3] = pattern[0][2];
		rotated[3][1] = pattern[2][3];
		rotated[1][0] = pattern[3][1];
		rotated[0][2] = pattern[1][0];
		
		rotated[1][2] = pattern[1][1];
		rotated[2][2] = pattern[1][2];
		rotated[2][1] = pattern[2][2];
		rotated[1][1] = pattern[2][1];

		// check for border violations and collisions
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				if(rotated[i][j] != null) {
					leftOffset = - (x+j);
					rightOffset = (x+j) - (board.getWidth() - 1);
					bottomOffset = (y+i) - (board.getHeight() - 1);
					if(!rotated[i][j].isEmpty()) // left border violation
						if (maxLeftOffset < leftOffset)
							maxLeftOffset = leftOffset;
					if(!rotated[i][j].isEmpty()) // right border violation
						if (maxRightOffset < rightOffset)
							maxRightOffset = rightOffset;
					if(!rotated[i][j].isEmpty()) // bottom border violation
						if (maxBottomOffset < bottomOffset)
							maxBottomOffset = bottomOffset;
					if(board.get(x+j,y+i) != null)
						if(!rotated[i][j].isEmpty() && !board.get(x+j,y+i).isEmpty()) // collision
							return false;
				}
			}
		}
		
		backup = pattern;
		pattern = rotated;
		rotated = backup;
		
		// try to correct border violations
		if(maxBottomOffset < 1) {
			if(maxLeftOffset < 1)  {
				if(maxRightOffset < 1) {
					reDraw();
					return true;
				} else {
					if(setPosition(x - maxRightOffset, y, false, board)) {
						reDraw();
						return true;
					} else {
						rotated = pattern;
						pattern = backup;
						return false;
					}
				}
			} else {
				if(setPosition(x + maxLeftOffset, y, false, board)) {
					reDraw();
					return true;
				} else {
					rotated = pattern;
					pattern = backup;
					return false;
				}
			}
		} else {
			if(setPosition(x, y - maxBottomOffset, false, board)) {
				reDraw();
				return true;
			} else {
				rotated = pattern;
				pattern = backup;
				return false;
			}
		}
	}
}
