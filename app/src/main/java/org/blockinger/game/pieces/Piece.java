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

import org.blockinger.game.R;
import org.blockinger.game.Square;
import org.blockinger.game.components.Board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class Piece {

    public static final int type_J = 1; // blue
    public static final int type_L = 2; // orange
    public static final int type_O = 3; // yellow
    public static final int type_Z = 4; // red
    public static final int type_S = 5; // green
    public static final int type_T = 6; // magenta
    public static final int type_I = 7; // cyan

    protected boolean active;
    protected int x; // pattern position
    protected int y; // pattern position
    protected int dim;    // maximum dimensions for square matrix, so all rotations fit inside!
    protected int squareSize;
    protected Square pattern[][];   // square matrix
    protected Square rotated[][];   // square matrix
    private Square emptySquare;
    private Canvas cv;
    private Bitmap bm;
    private Canvas cvPhantom;
    private Bitmap bmPhantom;
    private boolean isPhantom;

    /**
     * Always call super(); first.
     * @param width
     * @param height
     */
    protected Piece(Context c, int dimension) {
        this.dim = dimension;
        squareSize = 1;
        x = c.getResources().getInteger(R.integer.piece_start_x);
        y = 0;
        active = false;
        isPhantom = false;

        emptySquare =  new Square(Square.type_empty,c);

        pattern = new Square[dim][dim]; // empty piece
        rotated = new Square[dim][dim];
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                pattern[i][j] = emptySquare;
                rotated[i][j] = emptySquare;
            }
        }
    }

    public void reset(Context c) {
        x = c.getResources().getInteger(R.integer.piece_start_x);
        y = 0;
        active = false;
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                pattern[i][j] = emptySquare;
            }
        }
    }

    public void setActive(boolean b) {
        active = b;
        reDraw();
    }

    public boolean isActive() {
        return active;
    }

    public void place(Board board) {
        active = false;
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                if(pattern[i][j] != null)
                    board.set(x+j,y+i,pattern[i][j]);
            }
        }
    }

    /**
     *
     * @return true if movement was successfull.
     */
    public boolean setPosition(int x_new, int y_new, boolean noInterrupt, Board board) {
        boolean collision = false;
        int leftOffset = 0;
        int rightOffset = 0;
        int bottomOffset = 0;
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                if(pattern[i][j] != null) {
                    leftOffset = - (x_new+j);
                    rightOffset = (x_new+j) - (board.getWidth() - 1);
                    bottomOffset = (y_new+i) - (board.getHeight() - 1);
                    if(!pattern[i][j].isEmpty() && (leftOffset > 0)) // left border violation
                        return false;
                    if(!pattern[i][j].isEmpty() && (rightOffset > 0)) // right border violation
                        return false;
                    if(!pattern[i][j].isEmpty() && (bottomOffset > 0)) // bottom border violation
                        return false;
                    if(board.get(x_new+j,y_new+i) != null) {
                        collision = (!pattern[i][j].isEmpty() && !board.get(x_new+j,y_new+i).isEmpty()); // collision
                        if(collision) {
                            if(noInterrupt)
                                return false;
                            // Try to avoid collision by interrupting all running clear animations.
                            board.interruptClearAnimation();
                            collision = !board.get(x_new+j,y_new+i).isEmpty(); // Still not empty?
                            if(collision)
                                return false; // All hope is lost.
                        }
                    }
                }
            }
        }
        x = x_new;
        y = y_new;
        return true;
    }

    /**
     * @return true if rotation was successfull.
     */
    public abstract boolean turnLeft(Board board);

    /**
     * @return true if rotation was successfull.
     */
    public abstract boolean turnRight(Board board);

    /**
     *
     * @return true if movement to the left was successfull.
     */
    public boolean moveLeft(Board board) {
        if(!active)
            return true;
        return setPosition(x - 1, y, false, board);
    }

    /**
     *
     * @return true if movement to the right was successfull.
     */
    public boolean moveRight(Board board) {
        if(!active)
            return true;
        return setPosition(x + 1, y, false, board);
    }

    /**
     *
     * @return true if drop was successfull. Otherwise the ground or other pieces was hit.
     */
    public boolean drop(Board board) {
        if(!active)
            return true;
        return setPosition(x, y + 1, false, board);
    }

    public int hardDrop(boolean noInterrupt, Board board) {
        int i=0;
        while(setPosition(x, y + 1, noInterrupt, board)){
            if(i >= board.getHeight())
                throw new RuntimeException("Hard Drop Error: dropped too far.");
            i++;
        }
        return i;
    }

    protected void reDraw() {

        bm = Bitmap.createBitmap(squareSize*dim, squareSize*dim, Bitmap.Config.ARGB_8888);
        cv = new Canvas(bm);
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                if(pattern[i][j] == null) {} else
                    if(!pattern[i][j].isEmpty())
                        pattern[i][j].draw(j*squareSize, i*squareSize, squareSize, cv, false);
            }
        }

        bmPhantom = Bitmap.createBitmap(squareSize*dim, squareSize*dim, Bitmap.Config.ARGB_8888);
        cvPhantom = new Canvas(bmPhantom);
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                if(pattern[i][j] == null) {} else
                    if(!pattern[i][j].isEmpty())
                        pattern[i][j].draw(j*squareSize, i*squareSize, squareSize, cvPhantom, true);
            }
        }
    }

    /** draw on actual position
     *
     * @param xOffset board x offset
     * @param yOffset board y offset
     * @param squareSize
     * @param c
     * @param view
     */
    public void drawOnBoard(int xOffset, int yOffset, int ss, Canvas c) {
        if(!active)
            return;
        if(ss != squareSize) {
            squareSize = ss;
            reDraw();
        }
        if(isPhantom)
            c.drawBitmap(bmPhantom, x*squareSize + xOffset, y*squareSize + yOffset, null);
        else
            c.drawBitmap(bm, x*squareSize + xOffset, y*squareSize + yOffset, null);
    }

    // draw on preview position
    public void drawOnPreview(int xpos, int ypos, int ss, Canvas c) {
        if(ss != squareSize) {
            squareSize = ss;
            reDraw();
        }
        c.drawBitmap(bm, xpos, ypos, null);
    }

    public int getDim() {
        return dim;
    }

    public void setPhantom(boolean b) {
        isPhantom = b;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPositionSimple(int x_new, int y_new) {
        x = x_new;
        y = y_new;
    }

    public boolean setPositionSimpleCollision(int x_new, int y_new, Board board) {
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                if(pattern[i][j] != null) {
                    if(board.get(x_new+j,y_new+i) == null) {
                        if(!pattern[i][j].isEmpty())
                            return false;
                    } else {
                        if(!pattern[i][j].isEmpty() && !board.get(x_new+j,y_new+i).isEmpty())
                            return false;
                    }

                }
            }
        }
        x = x_new;
        y = y_new;
        return true;
    }
}
