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

import org.blockinger.game.components.Board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Row {

	private Row below; // positive x direction
	private Row above; // negative x direction
	private Square[] elements;
	private Square emptySquare;
	private int width;
	private Animator animator;
	private int fillStatus;
	
	public Row(int width, Context c) {
		emptySquare = new Square(Square.type_empty, c);
		animator = new Animator(c,this);
		this.width = width;
		below = null;
		above = null;
		fillStatus = 0;
		elements = new Square[width];
		for(int i = 0; i < width; i++) {
			elements[i] = emptySquare;
		}
	}
	
	public void set(Square s, int i) {
		if(s.isEmpty())
			return;
		if((i >= 0) && (i < width)) {
			fillStatus++;
			elements[i] = s;
		}
	}
	
	public Square get(int i) {
		if((i >= 0) && (i < width))
			return elements[i];
		return null;
	}

	public void set(Square[] squares) {
		elements = squares;
		fillStatus = 0;
		
		if(elements!=null)
			for(int i = 0; i < width; i++) {
				if(elements[i]!=null)
					if(!elements[i].isEmpty())
						fillStatus++;
			}
	}

	public void setAbove(Row row) {
		this.above = row;
	}

	public void setBelow(Row row) {
		this.below = row;
	}
	
	public Row below() {
		return this.below;
	}
	
	public Row above() {
		return this.above;
	}
	
	public Row delete() {
		Row result = this.below;
		
		if(above!=null)
			above.setBelow(below);
		if(below!=null)
			below.setAbove(above);
		
		above = null;
		below = null;
		
		return result;
	}

	public void draw(int x, int y, int squareSize, Canvas c) { // top left corner of Row
		animator.draw(x, y, squareSize, c);
	}

	public Bitmap drawBitmap(int squareSize) { // top left corner of Row
		Bitmap bm = Bitmap.createBitmap(width*squareSize, squareSize, Bitmap.Config.ARGB_8888);
		Canvas tamp = new Canvas(bm);
		for(int i = 0; i < width; i++) {
			if(elements[i] != null)
				elements[i].draw(i*squareSize,0,squareSize,tamp,false);
		}
		return bm;
	}
	
	public boolean isFull() {
		if(fillStatus >= width)
			return true;
		else
			return false;
	}
	
	public void cycle(long time, Board board) {
		animator.cycle(time, board);
	}
	
	public void clear(Board board, int currentDropInterval) {
		animator.start(board, currentDropInterval);
	}
	
	public void finishClear(Board board) {
		// clear this Row
		fillStatus = 0;
		for(int i = 0; i < width; i++) {
			elements[i] = emptySquare;
		}
		
		Row topRow = board.getTopRow();

		// disconnect tempRow
		above().setBelow(below());
		below().setAbove(above());
		
		// insert tempRow on top
		setBelow(topRow);
		setAbove(topRow.above());
		topRow.above().setBelow(this);
		topRow.setAbove(this);
		
		board.finishClear(this);
	}

	public boolean interrupt(Board board) {
		return animator.finish(board);
	}
}
