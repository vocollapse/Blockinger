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

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;


public class BlockBoardView extends SurfaceView implements Callback {

	private BlockBoard blockBoard;
	private GameLogic gameLogic;
	private MainThread mainThread;
	private Paint paint;
	private int gridRowBorder;
	private int gridColumnBorder;
	private int squaresize;
	private int rowOffset;
	private int rows;
	private int columnOffset;
	private int columns;
	private long lastDelay;
	private boolean landscapeInitialized;
	//private boolean portraitInitialized;
	private int prev_top;
	private int prev_bottom;
	private int prev_left;
	private int prev_right;
	private int textLeft;
	private int textTop;
	private int textRight;
	private int textBottom;
	private int textLines;
	private int textSizeH;
	private int textEmptySpacing;
	private Paint textPaint;
	private Rect textRect;
	private int textHeight;
	
	public BlockBoardView(Context context) {
		super(context);
		landscapeInitialized = false;
		//portraitInitialized = false;
		textPaint = new Paint();
		textRect = new Rect();
		textPaint.setColor(getResources().getColor(color.white));
		textPaint.setAntiAlias(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_antialiasing", true));//getResources().getBoolean(R.integer.enable_antialiasing));
		textSizeH = 1;
		textHeight = 2;
		if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_fps", false))
			textLines = 10;
		else
			textLines = 8;
	}

	public BlockBoardView(Context context, AttributeSet attrs) {
		super(context,attrs);
		landscapeInitialized = false;
		//portraitInitialized = false;
		textPaint = new Paint();
		textRect = new Rect();
		textPaint.setColor(getResources().getColor(color.white));
		textPaint.setAntiAlias(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_antialiasing", true));
		textSizeH = 1;
		textHeight = 2;
		if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_fps", false))
			textLines = 10;
		else
			textLines = 8;
	}

	public BlockBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
		landscapeInitialized = false;
		//portraitInitialized = false;
		textPaint = new Paint();
		textRect = new Rect();
		textPaint.setColor(getResources().getColor(color.white));
		textPaint.setAntiAlias(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_antialiasing", true));
		textSizeH = 1;
		textHeight = 2;
		if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_fps", false))
			textLines = 10;
		else
			textLines = 8;
	}
	
	public void init(int mode) {
		landscapeInitialized = false;
		//portraitInitialized = false;
		getHolder().addCallback(this);
        this.blockBoard = BlockBoard.getInstance();
        this.gameLogic = GameLogic.getInstance();
	    this.paint = new Paint();
		rows = getResources().getInteger(R.integer.zeilen);
		columns = getResources().getInteger(R.integer.spalten);

		squaresize = 1; // unknown at this point!
		prev_top = 1; // unknown at this point!
		prev_bottom = 1; // unknown at this point!
		prev_left = 1; // unknown at this point!
		
		lastDelay = 100;
		prev_right = 1; // unknown at this point!
		
		rowOffset = getResources().getInteger(R.integer.zeilenoffset);
		columnOffset = getResources().getInteger(R.integer.spaltenoffset);
		
		if((mode == GameActivity.start_new_game) || !gameLogic.isResumable()) {
			blockBoard.init(columns, rows,getContext());
			gameLogic.init(getContext());
		}
	}
	
	private void doDraw(Canvas c, int fps) {
		if(c==null)
			return;

		if (!landscapeInitialized){
			int fpsenabled = 0;
			if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_fps", false))
				fpsenabled = 1;
			
			BlockBoard.getInstance().invalidate();
			//portraitInitialized = false;
			landscapeInitialized = true;
			squaresize   = (int)(((c.getHeight()-1) - 2*rowOffset)/rows);
			columnOffset = (int)(((c.getWidth()-1) - squaresize*(getResources().getInteger(R.integer.padding_columns)+4+columns))/2);
			gridRowBorder = rowOffset + squaresize*rows;
			gridColumnBorder = columnOffset + squaresize*columns;
			prev_top = rowOffset;
			prev_bottom = rowOffset + 4*squaresize;
			prev_left = gridColumnBorder + getResources().getInteger(R.integer.padding_columns)*squaresize;
			prev_right = prev_left + 4*squaresize;
			textLeft = prev_left;
			textTop = prev_bottom + 2*squaresize;
			textRight = (c.getWidth()-1) - columnOffset;
			textBottom = (c.getHeight()-1) - rowOffset - squaresize;
			textSizeH = 1;

			// Adaptive Text Size Setup
			textPaint.setTextSize(textSizeH + 1);
			while(textPaint.measureText("00:00:00") < (textRight - textLeft)) {
				//stuff
				textPaint.getTextBounds((String)"Level:32", 0, 6, textRect);
				textHeight = textRect.height();
				textEmptySpacing = ((textBottom - textTop) - (textLines*(textHeight + 3))) / (3 + fpsenabled);
				if(textEmptySpacing < 10)
					break;
				
				textSizeH++;
				textPaint.setTextSize(textSizeH + 1);
			}
			textPaint.setTextSize(textSizeH);
			textPaint.getTextBounds((String)"Level:32", 0, 6, textRect);
			textHeight = textRect.height() + 3;
			textEmptySpacing = ((textBottom - textTop) - (textLines*(textHeight))) / (3 + fpsenabled);
		}

		// Background
		paint.setColor(getResources().getColor(color.background_dark));
		c.drawRect(0, 0, c.getWidth()-1, c.getHeight()-1, paint);
		
		blockBoard.draw(columnOffset, rowOffset, squaresize, c);
		
		gameLogic.drawActive(columnOffset, rowOffset, squaresize, c, this);

		if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_phantom", false))
			gameLogic.drawPhantom(columnOffset, rowOffset, squaresize, c, this);
		
	    drawGrid(columnOffset, rowOffset, gridColumnBorder, gridRowBorder, c);
		
	    drawPreview(prev_left, prev_top, prev_right, prev_bottom, c);

	    drawTextFillBox(c, fps);
	}

	private void drawGrid(int x, int y, int xBorder, int yBorder, Canvas c) {
		
		paint.setColor(getResources().getColor(color.holo_blue_dark));
        for (int zeilePixel = 0; zeilePixel <= rows; zeilePixel ++) {
            c.drawLine(x, y + zeilePixel*squaresize, xBorder, y + zeilePixel*squaresize, paint);
        }
        for (int spaltePixel = 0; spaltePixel <= columns; spaltePixel ++) {
            c.drawLine(x + spaltePixel*squaresize, y, x + spaltePixel*squaresize, yBorder, paint);
        }

		//draw Border
		paint.setColor(getResources().getColor(color.background_light));
		c.drawLine(x, y, x, yBorder, paint);
		c.drawLine(x, y, xBorder, y, paint);
		c.drawLine(xBorder, yBorder, xBorder, y, paint);
		c.drawLine(xBorder, yBorder, x, yBorder, paint);
	}

	private void drawPreview(int left, int top, int right, int bottom, Canvas c) {
		//Background
		paint.setColor(getResources().getColor(color.background_dark));
		c.drawRect(left, top, right, bottom, paint);
		
		// Piece
		gameLogic.drawPreview(left, top, squaresize, c, this);
		
		// Grid Lines
		paint.setColor(getResources().getColor(color.holo_blue_dark));
        for (int zeilePixel = 0; zeilePixel <= 4; zeilePixel ++) {
            c.drawLine(left, top + zeilePixel*squaresize, right, top + zeilePixel*squaresize, paint);
        }
        for (int spaltePixel = 0; spaltePixel <= 4; spaltePixel ++) {
            c.drawLine(left + spaltePixel*squaresize, top, left + spaltePixel*squaresize, bottom, paint);
        }
        
        // Border
		paint.setColor(getResources().getColor(color.background_light));
		c.drawLine(left, top, right, top, paint);
		c.drawLine(left, top, left, bottom, paint);
		c.drawLine(right, bottom, right, top, paint);
		c.drawLine(right, bottom, left, bottom, paint);
	}

	private void drawTextFillBox(Canvas c, int fps) {	
		
	    // draw Level Text
		c.drawText(getResources().getString(R.string.level_title), textLeft, textTop + textHeight, textPaint);
		c.drawText(gameLogic.getLevelString(), textLeft, textTop + 2*textHeight, textPaint);

	    // draw Score Text
		c.drawText(getResources().getString(R.string.score_title), textLeft, textTop + 3*textHeight + textEmptySpacing, textPaint);
		c.drawText(gameLogic.getScoreString(), textLeft, textTop + 4*textHeight + textEmptySpacing, textPaint);

		// draw Time Text
		c.drawText(getResources().getString(R.string.time_title), textLeft, textTop + 5*textHeight + 2*textEmptySpacing, textPaint);
		c.drawText(gameLogic.getTimeString(), textLeft, textTop + 6*textHeight + 2*textEmptySpacing, textPaint);

	    // draw APM Text
		c.drawText(getResources().getString(R.string.apm_title), textLeft, textTop + 7*textHeight + 3*textEmptySpacing, textPaint);
		c.drawText(gameLogic.getAPMString(), textLeft, textTop + 8*textHeight + 3*textEmptySpacing, textPaint);

	    // draw FPS Text
		if(!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_fps", false))
			return;
		c.drawText(getResources().getString(R.string.fps_title), textLeft, textTop + 9*textHeight + 4*textEmptySpacing, textPaint);
		c.drawText("" + fps, textLeft, textTop + 10*textHeight + 4*textEmptySpacing, textPaint);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {

		mainThread = new MainThread(getHolder()); 
     
		if(blockBoard == null) 
			this.blockBoard = BlockBoard.getInstance();
		else
			mainThread.firstTime = false;

        //drawInit();
		
		gameLogic.setRunning(true);
		mainThread.setRunning(true);
		mainThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

        boolean retry = true;
        mainThread.setRunning(false);
        while (retry) {
            try {
            	mainThread.join();
                retry = false;
            } catch (InterruptedException e) {
                
            }
        }
	}
	
	class MainThread extends Thread {
	     
        private SurfaceHolder surfaceHolder;
        private boolean runFlag = false;
        boolean firstTime = true;
		public long lastFrameDuration = 0;
		private long lastFrameStartingTime = 0;
		int fpslimit;
 
        public MainThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            try {
            	fpslimit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_fpslimittext", "35"));
            } catch(NumberFormatException e) {
            	fpslimit = 35;
            }
            if(fpslimit < 5)
            	fpslimit = 5;
        }
 
        public void setRunning(boolean run) {
            this.runFlag = run;
        }
        
        @Override
        public void run() {
            Canvas c;
            long tempTime = System.currentTimeMillis();

    		long fpsUpdateTime = tempTime + 200;
    		int frames = 0;
    		int frameCounter[] = {0, 0, 0, 0, 0};
    		int i = 0;
            
            while (this.runFlag) {
        		//synchronized(BlockBoard.getInstance()) {
		            if(firstTime){
		            	//drawInit();
		            	firstTime = false;
		            	continue;
		            }
		            
		            /* FPS CONTROL */
		            tempTime = System.currentTimeMillis();
		            
		            try {
		            	mainThread.fpslimit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_fpslimittext", "35"));
		            } catch(NumberFormatException e) {
		            	mainThread.fpslimit = 35;
		            }
		            if(mainThread.fpslimit < 5)
		            	mainThread.fpslimit = 5;
		            
		            if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_fpslimit", false)) {
			            lastFrameDuration = tempTime - lastFrameStartingTime;
			            if(lastFrameDuration > (1000.0f/fpslimit))
			            	lastDelay = Math.max(0, lastDelay - 25);
			            else
			            	lastDelay+= 25;
			            
			            if(lastDelay == 0) {} // no Sleep
			            else {
				            try {// do sleep!
								Thread.sleep(lastDelay);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
			            }
			            lastFrameStartingTime = tempTime;
		            }
		            
		            if(tempTime >= fpsUpdateTime) {
		            	i = (i + 1) % 5;
			    		fpsUpdateTime += 200;
			    		frames = frameCounter[0] + frameCounter[1] + frameCounter[2] + frameCounter[3] + frameCounter[4];
			            frameCounter[i] = 0;
		            }
		            frameCounter[i]++;
		            /* END OF FPS CONTROL*/
		            
		            gameLogic.cycle(tempTime);
		            blockBoard.cycle(tempTime);
		            
		            c = null;
		            try {
		               
		                c = this.surfaceHolder.lockCanvas(null);
		                synchronized (this.surfaceHolder) {                   
		                    doDraw(c, frames);
		                }
		            } finally {
		               
		                if (c != null) {
		                    this.surfaceHolder.unlockCanvasAndPost(c);
		                    
		                }
		            }
        		//}
            	//Thread.yield();
            }
        }

	}
}

