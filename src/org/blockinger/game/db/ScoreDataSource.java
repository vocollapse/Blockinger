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

package org.blockinger.game.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ScoreDataSource {

	// Database fields
	  private SQLiteDatabase database;
	  private HighscoreOpenHelper dbHelper;
	  private String[] allColumns = { HighscoreOpenHelper.COLUMN_ID,
			  HighscoreOpenHelper.COLUMN_SCORE,
			  HighscoreOpenHelper.COLUMN_PLAYERNAME};

	  public ScoreDataSource(Context context) {
	    dbHelper = new HighscoreOpenHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Score createScore(long score, String playerName) {
	    ContentValues values = new ContentValues();
	    values.put(HighscoreOpenHelper.COLUMN_SCORE, score);
	    values.put(HighscoreOpenHelper.COLUMN_PLAYERNAME, playerName);
	    long insertId = database.insert(HighscoreOpenHelper.TABLE_HIGHSCORES, null, values);
	    Cursor cursor = database.query(HighscoreOpenHelper.TABLE_HIGHSCORES,
	        allColumns, HighscoreOpenHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, HighscoreOpenHelper.COLUMN_SCORE + " DESC");
	    cursor.moveToFirst();
	    Score newScore = cursorToScore(cursor);
	    cursor.close();
	    return newScore;
	  }

	  public void deleteScore(Score score) {
	    long id = score.getId();
	    //System.out.println("Comment deleted with id: " + id);
	    database.delete(HighscoreOpenHelper.TABLE_HIGHSCORES, HighscoreOpenHelper.COLUMN_ID
	        + " = " + id, null);
	  }

	  private Score cursorToScore(Cursor cursor) {
		  Score score = new Score();
		  score.setId(cursor.getLong(0));
		  score.setScore(cursor.getLong(1));
		  score.setName(cursor.getString(2));
	    return score;
	  }

	public Cursor getCursor() {
		return database.query(HighscoreOpenHelper.TABLE_HIGHSCORES,
		        allColumns, null, null, null, null, HighscoreOpenHelper.COLUMN_SCORE + " DESC");
	}
	  
}
