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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.preference.PreferenceManager;

public class Sound implements OnAudioFocusChangeListener{

	private Activity host;
	private AudioManager audioCEO;
	private MediaPlayer musicPlayer;
	private MediaPlayer tetrisSoundPlayer;
	private MediaPlayer dropSoundPlayer;
	private MediaPlayer clearSoundPlayer;
	private MediaPlayer gameOverPlayer;
	private MediaPlayer buttonSoundPlayer;
	private boolean noFocus;
	private IntentFilter intentFilter;
	private BroadcastReceiver noisyAudioStreamReceiver;

	public static final int NO_MUSIC = 0x0;
	public static final int MENU_MUSIC = 0x1;
	public static final int GAME_MUSIC = 0x2;
	
	public Sound(Activity c, int musicChoice) {
		host = c;
		
		audioCEO = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		c.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		int result = audioCEO.requestAudioFocus(this,
	                // Use the music stream.
	                AudioManager.STREAM_MUSIC,
	                // Request permanent focus.
	                AudioManager.AUDIOFOCUS_GAIN);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			noFocus = false;
		} else
			noFocus = true;

		intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		noisyAudioStreamReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context c, Intent i) {
				if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(i.getAction())) {
		            // Not implemented due to lack of reversibility.
		        }
			}
		};
		c.registerReceiver(noisyAudioStreamReceiver, intentFilter);
		
		tetrisSoundPlayer = MediaPlayer.create(c,R.raw.seqlong);
		tetrisSoundPlayer.setLooping(false);
		tetrisSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));

		dropSoundPlayer = MediaPlayer.create(c,R.raw.drop2);
		dropSoundPlayer.setLooping(false);
		dropSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));

		buttonSoundPlayer = MediaPlayer.create(c,R.raw.keypressstandard);
		buttonSoundPlayer.setLooping(false);
		buttonSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));
	    
		clearSoundPlayer = MediaPlayer.create(c,R.raw.synthaccord);
		clearSoundPlayer.setLooping(false);
		clearSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));
		
		gameOverPlayer = MediaPlayer.create(c,R.raw.gameover);
		gameOverPlayer.setLooping(false);
		gameOverPlayer.setVolume(0.015f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));
		
		switch(musicChoice) {
			case MENU_MUSIC :
				musicPlayer = MediaPlayer.create(host,R.raw.lemmings03);
				musicPlayer.setLooping(true);
				musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
				musicPlayer.start();
				break;
			case GAME_MUSIC :
				musicPlayer = MediaPlayer.create(host,R.raw.sadrobot01);
				musicPlayer.setLooping(true);
				musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
				musicPlayer.start();
				break;
			default :
				musicPlayer = new MediaPlayer();
				break;
		}
	}
	
	public void clearSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		clearSoundPlayer.seekTo(0);
		clearSoundPlayer.start();
	}
	
	public void buttonSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		if(!PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_button_sound", true))
			return;
		buttonSoundPlayer.seekTo(0);
		buttonSoundPlayer.start();
	}
	
	public void dropSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		dropSoundPlayer.seekTo(0);
		dropSoundPlayer.start();
	}

	public void tetrisSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		tetrisSoundPlayer.seekTo(0);
		tetrisSoundPlayer.start();
	}

	public void gameOverSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		pause(); // pause music to make the end of game feel more dramatic. hehe.
		gameOverPlayer.seekTo(0);
		gameOverPlayer.start();
	}

	public void resume() {
		try {
			musicPlayer.start();
		} catch(IllegalStateException e) {
			return;
		}
	}

	public void pause() {
		try {
			musicPlayer.pause();
		} catch(IllegalStateException e) {
			return;
		}
	}
	
	public void release() {
		buttonSoundPlayer.release();
		dropSoundPlayer.release();
		clearSoundPlayer.release();
		tetrisSoundPlayer.release();
		gameOverPlayer.release();
		musicPlayer.release();

		host.unregisterReceiver(noisyAudioStreamReceiver);
		audioCEO.abandonAudioFocus(this);
		host = null;
		noFocus = true;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
        	buttonSoundPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	dropSoundPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	clearSoundPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	tetrisSoundPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	gameOverPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	musicPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
    	} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
        	buttonSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	dropSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	clearSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	tetrisSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	gameOverPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
    		resume();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
        	pause();
        }
    }

}
