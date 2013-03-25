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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.view.View.OnTouchListener;


public class GameActivity extends FragmentActivity {

	public BlockBoard blockBoard;
	public GameLogic gameLogic;
	private String playerName;
	//private MediaPlayer gameMusicPlayer;
	private int songtime;

	public static final int start_new_game = 0;
	public static final int resume_old_game = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		playerName = "Anonymous";
		blockBoard = BlockBoard.getInstance();
		gameLogic = GameLogic.getInstance();
		blockBoard.setLogic(gameLogic);
		gameLogic.setBoard(blockBoard);
		gameLogic.reconnect(this,getSupportFragmentManager());
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game);
		
		
		((ImageButton)findViewById(R.id.rightButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	gameLogic.rightButtonPressed();
		        	((ImageButton)findViewById(R.id.rightButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	gameLogic.rightButtonReleased();
		        	((ImageButton)findViewById(R.id.rightButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.leftButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	gameLogic.leftButtonPressed();
		        	((ImageButton)findViewById(R.id.leftButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	gameLogic.leftButtonReleased();
		        	((ImageButton)findViewById(R.id.leftButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.softDropButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	gameLogic.downButtonPressed();
		        	((ImageButton)findViewById(R.id.softDropButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	gameLogic.downButtonReleased();
		        	((ImageButton)findViewById(R.id.softDropButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.hardDropButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	gameLogic.dropButtonPressed();
		        	((ImageButton)findViewById(R.id.hardDropButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	gameLogic.dropButtonReleased();
		        	((ImageButton)findViewById(R.id.hardDropButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.rotateRightButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	gameLogic.rotateRightPressed();
		        	((ImageButton)findViewById(R.id.rotateRightButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	gameLogic.rotateRightReleased();
		        	((ImageButton)findViewById(R.id.rotateRightButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.rotateLeftButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	gameLogic.rotateLeftPressed();
		        	((ImageButton)findViewById(R.id.rotateLeftButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	gameLogic.rotateLeftReleased();
		        	((ImageButton)findViewById(R.id.rotateLeftButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		
		// This activity was startet from the Main Menu
		int value = start_new_game;
		Bundle b = getIntent().getExtras();
		if(b!=null){ 
			value = b.getInt("mode");
			if(b.getString("playername") != null)
				playerName = b.getString("playername");
		}

		songtime = 0;
		// This activity was restarted after a configuration change
		if(value == start_new_game) {
			GameState recovery = (GameState)getLastCustomNonConfigurationInstance();
			if(recovery != null) {
				value = resume_old_game;
				GameLogic.setInstance(recovery.gl);
				recovery.gl.reconnect(this, getSupportFragmentManager());
				BlockBoard.setInstance(recovery.bb);
				songtime = recovery.tracktimemillis;
			}
		}
		
		((BlockBoardView)findViewById(R.id.boardView)).init(value);
	    MainActivity.getAdapter().notifyDataSetChanged();
	    
	   /* try{
		    if(gameMusicPlayer == null) {
			    gameMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
			    gameMusicPlayer.setLooping(true);
			    gameMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    gameMusicPlayer.seekTo(songtime);
			    gameMusicPlayer.start();
			    //gameMusicPlayer.seekTo(songtime);
		    } else if (!gameMusicPlayer.isPlaying()) {
			    gameMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
			    gameMusicPlayer.setLooping(true);
			    gameMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    gameMusicPlayer.seekTo(songtime);
			    gameMusicPlayer.start();
			    //gameMusicPlayer.seekTo(songtime);
		    }
	    } catch(IllegalStateException e) {
	    	gameMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
		    gameMusicPlayer.setLooping(true);
		    gameMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
		    gameMusicPlayer.seekTo(songtime);
		    gameMusicPlayer.start();
		    //gameMusicPlayer.seekTo(songtime);
	    }*/
	}
	
	public void putScore(long score) {
		if(playerName == null || playerName.equals(""))
			playerName = "Anonymous";
	    MainActivity.getDS().createScore(score, playerName);
	    MainActivity.getDS().open();
	    Cursor cursor = MainActivity.getDS().getCursor();
	    MainActivity.getAdapter().changeCursor(cursor);
	}
    
/*    @Override
    protected void onPause() {
    	try {
    		songtime = gameMusicPlayer.getCurrentPosition();
    	} catch (IllegalStateException e) {}
    	gameMusicPlayer.release();
		gameLogic.disconnect();
    	super.onPause();
    };*/
    
    @Override
    protected void onStop() {
    	super.onStop();
    	/*try {
    		songtime = gameMusicPlayer.getCurrentPosition();
    	} catch (IllegalStateException e) {}
    	gameMusicPlayer.release();*/
		gameLogic.disconnect();
    };
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	/*try {
    		songtime = gameMusicPlayer.getCurrentPosition();
    	} catch (IllegalStateException e) {}
    	gameMusicPlayer.release();*/
		gameLogic.disconnect();
    };
    
    @Override
    protected void onResume() {
    	super.onResume();
		gameLogic.reconnect(this,getSupportFragmentManager());

	    /*try{
		    if(gameMusicPlayer == null) {
			    gameMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
			    gameMusicPlayer.setLooping(true);
			    gameMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    gameMusicPlayer.start();
		    } else if (!gameMusicPlayer.isPlaying()) {
			    gameMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
			    gameMusicPlayer.setLooping(true);
			    gameMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
			    gameMusicPlayer.start();
		    }
	    } catch(IllegalStateException e) {
	    	gameMusicPlayer = MediaPlayer.create(this, R.raw.sadrobot01);
		    gameMusicPlayer.setLooping(true);
		    gameMusicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_musicvolume", 60));
		    gameMusicPlayer.start();
	    }*/
    };
    
    @Override
    public Object onRetainCustomNonConfigurationInstance () {
        final GameState data = new GameState(gameLogic, blockBoard, songtime);
        return data;
    }
    
    
    private class GameState {

    	public GameLogic gl;
    	public BlockBoard bb;
    	public int tracktimemillis;
    	
    	public GameState(GameLogic a, BlockBoard b, int i) {
    		gl = a;
    		gl.disconnect();
    		bb = b;
    		tracktimemillis = i;
    	}
    	
    }

}
