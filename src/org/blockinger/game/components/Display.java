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
import org.blockinger.game.Row;
import org.blockinger.game.activities.GameActivity;
import org.blockinger.game.pieces.Piece;

import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;

public class Display extends Component {

	private int prevPhantomY;
	private boolean dropPhantom;
	private Paint paint;
	private int gridRowBorder;
	private int gridColumnBorder;
	private int squaresize;
	private int rowOffset;
	private int rows;
	private int columnOffset;
	private int columns;
	private boolean landscapeInitialized;
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
	private Paint popUptextPaint;
	
	public Display(GameActivity ga) {
		super(ga);
		invalidatePhantom();
		setPhantomY(0);
		landscapeInitialized = false;
	    paint = new Paint();
		rows = host.getResources().getInteger(R.integer.zeilen);
		columns = host.getResources().getInteger(R.integer.spalten);

		squaresize = 1; // unknown at this point!
		prev_top = 1; // unknown at this point!
		prev_bottom = 1; // unknown at this point!
		prev_left = 1; // unknown at this point!
		
		prev_right = 1; // unknown at this point!
		
		rowOffset = host.getResources().getInteger(R.integer.zeilenoffset);
		columnOffset = host.getResources().getInteger(R.integer.spaltenoffset);

		textPaint = new Paint();
		textRect = new Rect();
		textPaint.setColor(host.getResources().getColor(color.white));
		textPaint.setAntiAlias(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_antialiasing", true));
		popUptextPaint = new Paint();
		popUptextPaint.setColor(host.getResources().getColor(color.white));
		popUptextPaint.setAntiAlias(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_antialiasing", true));
		popUptextPaint.setTextSize(120);
		textSizeH = 1;
		textHeight = 2;
		if(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_fps", false))
			textLines = 10;
		else
			textLines = 8;
	}
	
	public void doDraw(Canvas c, int fps) {
		if(c==null)
			return;

		if (!landscapeInitialized){
			int fpsenabled = 0;
			if(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_fps", false))
				fpsenabled = 1;
			
			host.game.getBoard().invalidate();
			//portraitInitialized = false;
			landscapeInitialized = true;
			squaresize   = (int)(((c.getHeight()-1) - 2*rowOffset)/rows);
			columnOffset = (int)(((c.getWidth()-1) - squaresize*(host.getResources().getInteger(R.integer.padding_columns)+4+columns))/2);
			gridRowBorder = rowOffset + squaresize*rows;
			gridColumnBorder = columnOffset + squaresize*columns;
			prev_top = rowOffset;
			prev_bottom = rowOffset + 4*squaresize;
			prev_left = gridColumnBorder + host.getResources().getInteger(R.integer.padding_columns)*squaresize;
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
		//paint.setColor(host.getResources().getColor(color.background_dark));
		//c.drawRect(0, 0, c.getWidth()-1, c.getHeight()-1, paint);
		c.drawColor(Color.argb(0, 0, 0, 0), android.graphics.PorterDuff.Mode.CLEAR);
		
		host.game.getBoard().draw(columnOffset, rowOffset, squaresize, c);
		
		drawActive(columnOffset, rowOffset, squaresize, c);

		if(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_phantom", false))
			drawPhantom(columnOffset, rowOffset, squaresize, c);
		
	    drawGrid(columnOffset, rowOffset, gridColumnBorder, gridRowBorder, c);
		
	    drawPreview(prev_left, prev_top, prev_right, prev_bottom, c);

	    drawTextFillBox(c, fps);

		if(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_popup", true))
			drawPopupText(c);
	}

	private void drawGrid(int x, int y, int xBorder, int yBorder, Canvas c) {
		
		paint.setColor(host.getResources().getColor(color.holo_blue_dark));
        for (int zeilePixel = 0; zeilePixel <= rows; zeilePixel ++) {
            c.drawLine(x, y + zeilePixel*squaresize, xBorder, y + zeilePixel*squaresize, paint);
        }
        for (int spaltePixel = 0; spaltePixel <= columns; spaltePixel ++) {
            c.drawLine(x + spaltePixel*squaresize, y, x + spaltePixel*squaresize, yBorder, paint);
        }

		//draw Border
		paint.setColor(host.getResources().getColor(color.background_light));
		c.drawLine(x, y, x, yBorder, paint);
		c.drawLine(x, y, xBorder, y, paint);
		c.drawLine(xBorder, yBorder, xBorder, y, paint);
		c.drawLine(xBorder, yBorder, x, yBorder, paint);
	}

	private void drawPreview(int left, int top, int right, int bottom, Canvas c) {
		//Background
		//paint.setColor(host.getResources().getColor(color.background_dark));
		//c.drawRect(left, top, right, bottom, paint);
		
		// Piece
		drawPreview(left, top, squaresize, c);
		
		// Grid Lines
		paint.setColor(host.getResources().getColor(color.holo_blue_dark));
        for (int zeilePixel = 0; zeilePixel <= 4; zeilePixel ++) {
            c.drawLine(left, top + zeilePixel*squaresize, right, top + zeilePixel*squaresize, paint);
        }
        for (int spaltePixel = 0; spaltePixel <= 4; spaltePixel ++) {
            c.drawLine(left + spaltePixel*squaresize, top, left + spaltePixel*squaresize, bottom, paint);
        }
        
        // Border
		paint.setColor(host.getResources().getColor(color.background_light));
		c.drawLine(left, top, right, top, paint);
		c.drawLine(left, top, left, bottom, paint);
		c.drawLine(right, bottom, right, top, paint);
		c.drawLine(right, bottom, left, bottom, paint);
	}

	private void drawTextFillBox(Canvas c, int fps) {	
		
	    // draw Level Text
		c.drawText(host.getResources().getString(R.string.level_title), textLeft, textTop + textHeight, textPaint);
		c.drawText(host.game.getLevelString(), textLeft, textTop + 2*textHeight, textPaint);

	    // draw Score Text
		c.drawText(host.getResources().getString(R.string.score_title), textLeft, textTop + 3*textHeight + textEmptySpacing, textPaint);
		c.drawText(host.game.getScoreString(), textLeft, textTop + 4*textHeight + textEmptySpacing, textPaint);

		// draw Time Text
		c.drawText(host.getResources().getString(R.string.time_title), textLeft, textTop + 5*textHeight + 2*textEmptySpacing, textPaint);
		c.drawText(host.game.getTimeString(), textLeft, textTop + 6*textHeight + 2*textEmptySpacing, textPaint);

	    // draw APM Text
		c.drawText(host.getResources().getString(R.string.apm_title), textLeft, textTop + 7*textHeight + 3*textEmptySpacing, textPaint);
		c.drawText(host.game.getAPMString(), textLeft, textTop + 8*textHeight + 3*textEmptySpacing, textPaint);

	    // draw FPS Text
		if(!PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_fps", false))
			return;
		c.drawText(host.getResources().getString(R.string.fps_title), textLeft, textTop + 9*textHeight + 4*textEmptySpacing, textPaint);
		c.drawText("" + fps, textLeft, textTop + 10*textHeight + 4*textEmptySpacing, textPaint);
	}
	
	private void drawActive(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
			Canvas c) {
		host.game.getActivePiece().drawOnBoard(spaltenOffset, zeilenOffset, spaltenAbstand, c);
	}

	private void drawPhantom(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
			Canvas c) {
		Piece active = host.game.getActivePiece();
		int y = active.getY();
		int x = active.getX();
		active.setPhantom(true);
		
		if(dropPhantom) {
			int backup__currentRowIndex = host.game.getBoard().getCurrentRowIndex();
			Row backup__currentRow = host.game.getBoard().getCurrentRow();
			int cnt = y+1;
			
			while(active.setPositionSimpleCollision(x, cnt, host.game.getBoard())) {
				cnt++;
			}
			
			 host.game.getBoard().setCurrentRowIndex(backup__currentRowIndex);
			 host.game.getBoard().setCurrentRow(backup__currentRow);
		} else
			active.setPositionSimple(x, prevPhantomY);
		
		prevPhantomY = active.getY();
		active.drawOnBoard(spaltenOffset, zeilenOffset, spaltenAbstand, c);
		active.setPositionSimple(x, y);
		active.setPhantom(false);
		dropPhantom = false;
	}

	private void drawPreview(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
			Canvas c) {
		host.game.getPreviewPiece().drawOnPreview(spaltenOffset, zeilenOffset, spaltenAbstand, c);
	}

	private void drawPopupText(Canvas c) {
		
		final int offset = 6;
		final int diagonaloffset = 6;
		
		String text = host.game.getPopupString();
		popUptextPaint.setTextSize(host.game.getPopupSize());
		popUptextPaint.setColor(host.getResources().getColor(color.black));
		popUptextPaint.setAlpha(host.game.getPopupAlpha());

		int left = columnOffset + ((int)columns*squaresize/2) - ((int)popUptextPaint.measureText(text)/2); // middle minus half text width
		int top = c.getHeight()/2;
		
		c.drawText(text, offset+left, top, popUptextPaint); // right
		c.drawText(text, diagonaloffset+left, diagonaloffset+top, popUptextPaint); // bottom right
		c.drawText(text, left, offset+top, popUptextPaint); // bottom
		c.drawText(text, -diagonaloffset+left, diagonaloffset+top, popUptextPaint); // bottom left
		c.drawText(text, -offset+left, top, popUptextPaint); // left
		c.drawText(text, -diagonaloffset+left, -diagonaloffset+top, popUptextPaint); // top left
		c.drawText(text, left, -offset+top, popUptextPaint); // top
		c.drawText(text, diagonaloffset+left, -diagonaloffset+top, popUptextPaint); // top right

		popUptextPaint.setColor(host.game.getPopupColor());
		popUptextPaint.setAlpha(host.game.getPopupAlpha());
		c.drawText(text, left, top, popUptextPaint);
		
	}

	public void invalidatePhantom() {
		dropPhantom = true;
	}

	public void setPhantomY(int i) {
		prevPhantomY = i;
	}

}
