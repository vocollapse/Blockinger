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
	
	public void cycle(long time) {
		animator.cycle(time);
	}
	
	public void clear() {
		animator.start();
		
		// clear this Row
		fillStatus = 0;
		for(int i = 0; i < width; i++) {
			elements[i] = emptySquare;
		}
	}
	
	public void finishClear() {
		Row topRow = BlockBoard.getInstance().getTopRow();

		// disconnect tempRow
		above().setBelow(below());
		below().setAbove(above());
		
		// insert tempRow on top
		setBelow(topRow);
		setAbove(topRow.above());
		topRow.above().setBelow(this);
		topRow.setAbove(this);
		
		BlockBoard.getInstance().finishClear(this);
	}

	public boolean interrupt() {
		return animator.finish();
	}
	
	public class Animator {

		public static final int animationStageIdle = 0;
		public static final int animationStageFlash = 1;
		public static final int animationStageBurst = 2;
		
		// Config
		private long flashInterval;
		private long flashFinishTime;
		//private long burstFinishTime;
		//private boolean useFlash;
		//private boolean useBurst;
		private int squareSize;
		
		// State
		private long startTime;
		private int stage;
		//private long burstProgress;
		//private int brustWidth;
		//private int burstHeight;
		//private Paint transparentPaint;
		private boolean drawEnable;
		private long nextFlash;
		
		// Data
		//private Context context;
		private Row row;
		private Bitmap bitmapRow;
		private int flashCount;
		private int rawFlashInterval;
		
		// Constructor
		public Animator(Context c, Row r) {
			rawFlashInterval = c.getResources().getInteger(R.integer.clearAnimation_flashInterval);
			flashCount = c.getResources().getInteger(R.integer.clearAnimation_flashCount);
			stage = animationStageIdle;
			this.row = r;
			drawEnable = true;
			startTime = 0;
			flashFinishTime = 0;
			nextFlash = 0;
			flashInterval = 0;
			//useFlash = false;
			//useBurst = false;
			//burstFinishTime = 0;
			//burstProgress = 0;
			//brustWidth = 0;
			//burstHeight = 0;
			//transparentPaint = new Paint();
			squareSize = 0;
		}
		
		public void cycle(long time) {
			if(stage == animationStageIdle)
				return;
			
			if(time >= flashFinishTime)
				finish();
			else if (time >= nextFlash) {
				nextFlash += flashInterval;
				drawEnable = !drawEnable;
				BlockBoard.getInstance().invalidate();
			}
		}

		public void start() {
			bitmapRow = row.drawBitmap(squareSize);
			stage = animationStageFlash;
			startTime = System.currentTimeMillis();
			flashInterval = Math.min( // Choose base flash interval on slow levels and shorter interval on fast levels.
					rawFlashInterval,
					(int)((float)GameLogic.getInstance().getCurrentDropinterval() / (float)flashCount)
			);
			flashFinishTime = startTime + 2*flashInterval*flashCount;
			nextFlash = startTime + flashInterval;
			drawEnable = false;
			BlockBoard.getInstance().invalidate();
		}
		
		public boolean finish() {
			if(animationStageIdle == stage)
				return false;
			stage = animationStageIdle;
			row.finishClear();
			drawEnable = true;
			return true;
		}
		
		public void draw(int x, int y, int ss, Canvas c) {
			//float scaleFactor = flashFinishTime / (flashFinishTime-flashProgress);
			//Bitmap bm = Bitmap.createBitmap(brustWidth, burstHeight, Bitmap.Config.ARGB_8888);
			//Canvas tamp = new Canvas(bm);
			//transparentPaint.setAlpha(do{(shit.here())}while(you += shit));
			this.squareSize = ss;
			if(drawEnable) {
				if(stage == animationStageIdle)
					bitmapRow = row.drawBitmap(ss);
				//bitmapRow.scale(scaleFactor, scaleFactor, px, py);
				if (bitmapRow != null)
					c.drawBitmap(bitmapRow, x, y, null);
			}
		}
		
		public void startFlash() {
			
		}
		
		public void cancelBurst() {
			
		}
		
		public void startBurst() {
			
		}
	}
}
