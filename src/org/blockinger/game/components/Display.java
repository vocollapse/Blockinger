package org.blockinger.game.components;

import org.blockinger.game.R;
import org.blockinger.game.Row;
import org.blockinger.game.activities.GameActivity;
import org.blockinger.game.pieces.Piece;

import android.R.color;
import android.graphics.Canvas;
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
		paint.setColor(host.getResources().getColor(color.background_dark));
		c.drawRect(0, 0, c.getWidth()-1, c.getHeight()-1, paint);
		
		host.game.getBoard().draw(columnOffset, rowOffset, squaresize, c);
		
		drawActive(columnOffset, rowOffset, squaresize, c);

		if(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_phantom", false))
			drawPhantom(columnOffset, rowOffset, squaresize, c);
		
	    drawGrid(columnOffset, rowOffset, gridColumnBorder, gridRowBorder, c);
		
	    drawPreview(prev_left, prev_top, prev_right, prev_bottom, c);

	    drawTextFillBox(c, fps);
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
		paint.setColor(host.getResources().getColor(color.background_dark));
		c.drawRect(left, top, right, bottom, paint);
		
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
	
	public void drawActive(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
			Canvas c) {
		host.game.getActivePiece().drawOnBoard(spaltenOffset, zeilenOffset, spaltenAbstand, c);
	}

	public void drawPhantom(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
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

	public void drawPreview(int spaltenOffset, int zeilenOffset, int spaltenAbstand,
			Canvas c) {
		host.game.getPreviewPiece().drawOnPreview(spaltenOffset, zeilenOffset, spaltenAbstand, c);
	}

	public void invalidatePhantom() {
		dropPhantom = true;
	}

	public void setPhantomY(int i) {
		prevPhantomY = i;
	}

}
