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

import org.blockinger.game.db.HighscoreOpenHelper;
import org.blockinger.game.db.ScoreDataSource;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ListActivity {

	public static ScoreDataSource datasource;
	public Cursor mc;
	public static SimpleCursorAdapter adapter;
	public MediaPlayer mainMenuMusicPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		// Make Hyperlink in this textview clickable (obsolete now)
		TextView t2 = (TextView) findViewById(R.id.TextView1);
	    t2.setMovementMethod(LinkMovementMethod.getInstance());
		
	    datasource = new ScoreDataSource(this);
	    datasource.open();
	    
	    mc = datasource.getCursor();

	    // Use the SimpleCursorAdapter to show the
	    // elements in a ListView
	    adapter = new SimpleCursorAdapter(
	    	(Context)this,
	        R.layout.blockinger_list_item,
	        mc,
	        new String[] {HighscoreOpenHelper.COLUMN_SCORE, HighscoreOpenHelper.COLUMN_PLAYERNAME},
	        new int[] {R.id.text1, R.id.text2},
	        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

	    setListAdapter(adapter);
	    
	    try{
		    if(mainMenuMusicPlayer == null) {
			    mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.lemmings03);
			    mainMenuMusicPlayer.setLooping(true);
			    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    mainMenuMusicPlayer.start();
		    } else if (!mainMenuMusicPlayer.isPlaying()) {
			    mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.lemmings03);
			    mainMenuMusicPlayer.setLooping(true);
			    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    mainMenuMusicPlayer.start();
		    }
	    } catch(IllegalStateException e) {
	    	mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.lemmings03);
		    mainMenuMusicPlayer.setLooping(true);
		    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
		    mainMenuMusicPlayer.start();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_about:
				Intent intent1 = new Intent(this, AboutActivity.class);
				startActivity(intent1);
				return true;
			case R.id.action_donate:
				String url = getResources().getString(R.string.donation_url);
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


    public void onClickStart(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		Bundle b = new Bundle();
		b.putInt("mode", GameActivity.start_new_game); //Your id
		b.putString("playername", ((TextView)findViewById(R.id.nicknameEditView)).getText().toString()); //Your id
		intent.putExtras(b); //Put your id to your next Intent
		startActivity(intent);
	    mainMenuMusicPlayer.release();
	    try{
		    if(mainMenuMusicPlayer == null) {
			    mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
			    mainMenuMusicPlayer.setLooping(true);
			    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    mainMenuMusicPlayer.start();
		    } else if (!mainMenuMusicPlayer.isPlaying()) {
			    mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
			    mainMenuMusicPlayer.setLooping(true);
			    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    mainMenuMusicPlayer.start();
		    }
	    } catch(IllegalStateException e) {
	    	mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
		    mainMenuMusicPlayer.setLooping(true);
		    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
		    mainMenuMusicPlayer.start();
	    }
    }

    public void onClickResume(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		Bundle b = new Bundle();
		b.putInt("mode", GameActivity.resume_old_game); //Your id
		b.putString("playername", ((TextView)findViewById(R.id.nicknameEditView)).getText().toString()); //Your id
		intent.putExtras(b); //Put your id to your next Intent
		startActivity(intent);
	    mainMenuMusicPlayer.release();
	    try{
		    if(mainMenuMusicPlayer == null) {
			    mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
			    mainMenuMusicPlayer.setLooping(true);
			    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    mainMenuMusicPlayer.start();
		    } else if (!mainMenuMusicPlayer.isPlaying()) {
			    mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
			    mainMenuMusicPlayer.setLooping(true);
			    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    mainMenuMusicPlayer.start();
		    }
	    } catch(IllegalStateException e) {
	    	mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
		    mainMenuMusicPlayer.setLooping(true);
		    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
		    mainMenuMusicPlayer.start();
	    }
    }

    public void onClickQuit(View view) {
		this.finish();
	    mainMenuMusicPlayer.release();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    };
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
	    mainMenuMusicPlayer.release();
    };
    
    @Override
    protected void onResume() {
    	super.onResume();
    	datasource.open();
	    Cursor cursor = datasource.getCursor();
	    adapter.changeCursor(cursor);
	    
	    /*// Music is running
	    try {
	    	if(mainMenuMusicPlayer.isPlaying())
		    	return;
	    } catch (Exception e) {
		}
	    
	    // no music is running
*/	    mainMenuMusicPlayer.release();
	    try{
		    if(mainMenuMusicPlayer == null) {
			    mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.lemmings03);
			    mainMenuMusicPlayer.setLooping(true);
			    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    mainMenuMusicPlayer.start();
		    } else if (!mainMenuMusicPlayer.isPlaying()) {
			    mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.lemmings03);
			    mainMenuMusicPlayer.setLooping(true);
			    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    mainMenuMusicPlayer.start();
		    }
	    } catch(IllegalStateException e) {
	    	mainMenuMusicPlayer = MediaPlayer.create(this, R.raw.lemmings03);
		    mainMenuMusicPlayer.setLooping(true);
		    mainMenuMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
		    mainMenuMusicPlayer.start();
	    }
    };

	public static ScoreDataSource getDS() {
		return datasource;
	}

	public static SimpleCursorAdapter getAdapter() {
		return adapter;
	}

}
