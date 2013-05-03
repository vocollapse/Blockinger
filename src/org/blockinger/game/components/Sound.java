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
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.preference.PreferenceManager;

public class Sound implements OnAudioFocusChangeListener, OnLoadCompleteListener{

	private Activity host;
	private AudioManager audioCEO;
//	private MediaPlayer musicPlayer;
//	private MediaPlayer tetrisSoundPlayer;
//	private MediaPlayer dropSoundPlayer;
//	private MediaPlayer clearSoundPlayer;
//	private MediaPlayer gameOverPlayer;
//	private MediaPlayer buttonSoundPlayer;
	private int soundID_musicPlayer;
	private int soundID_tetrisSoundPlayer;
	private int soundID_dropSoundPlayer;
	private int soundID_clearSoundPlayer;
	private int soundID_gameOverPlayer;
	private int soundID_buttonSoundPlayer;
	private boolean noFocus;
	private IntentFilter intentFilter;
	private NoiseBroadcastReceiver noisyAudioStreamReceiver;
	private SoundPool soundPool;

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
		noisyAudioStreamReceiver = new NoiseBroadcastReceiver();
		c.registerReceiver(noisyAudioStreamReceiver, intentFilter);
		
		soundPool = new SoundPool(c.getResources().getInteger(R.integer.audio_streams),AudioManager.STREAM_MUSIC,0);
		soundPool.setOnLoadCompleteListener(this);

		soundID_tetrisSoundPlayer = soundPool.load(c, R.raw.seqlong, 1);
//		tetrisSoundPlayer = MediaPlayer.create(c,R.raw.seqlong);
//		tetrisSoundPlayer.setLooping(false);
//		tetrisSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));

		soundID_dropSoundPlayer = soundPool.load(c, R.raw.drop2, 1);
//		dropSoundPlayer = MediaPlayer.create(c,R.raw.drop2);
//		dropSoundPlayer.setLooping(false);
//		dropSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));

		soundID_buttonSoundPlayer = soundPool.load(c, R.raw.keypressstandard, 1);
//		buttonSoundPlayer = MediaPlayer.create(c,R.raw.keypressstandard);
//		buttonSoundPlayer.setLooping(false);
//		buttonSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));

		soundID_clearSoundPlayer = soundPool.load(c, R.raw.synthaccord, 1);
//		clearSoundPlayer = MediaPlayer.create(c,R.raw.synthaccord);
//		clearSoundPlayer.setLooping(false);
//		clearSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));

		soundID_gameOverPlayer = soundPool.load(c, R.raw.synthaccord, 1);
//		gameOverPlayer = MediaPlayer.create(c,R.raw.gameover);
//		gameOverPlayer.setLooping(false);
//		gameOverPlayer.setVolume(0.015f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));
		
		switch(musicChoice) {
			case MENU_MUSIC :
				soundID_musicPlayer = soundPool.load(c, R.raw.lemmings03, 2);
//				musicPlayer = MediaPlayer.create(c,R.raw.lemmings03);
//				musicPlayer.setLooping(true);
//				musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_musicvolume", 60));
//				musicPlayer.start();
				break;
			case GAME_MUSIC :
				soundID_musicPlayer = soundPool.load(c, R.raw.sadrobot01, 2);
//				musicPlayer = MediaPlayer.create(c,R.raw.sadrobot01);
//				musicPlayer.setLooping(true);
//				musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_musicvolume", 60));
//				musicPlayer.start();
				break;
			default :
				soundID_musicPlayer = 0;
//				musicPlayer = new MediaPlayer();
				break;
		}
	}
	
	public void clearSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_clearSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
//		clearSoundPlayer.seekTo(0);
//		clearSoundPlayer.start();
	}
	
	public void buttonSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		if(!PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_button_sound", true))
			return;
		soundPool.play(
			soundID_buttonSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
//		buttonSoundPlayer.seekTo(0);
//		buttonSoundPlayer.start();
	}
	
	public void dropSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_dropSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
//		dropSoundPlayer.seekTo(0);
//		dropSoundPlayer.start();
	}

	public void tetrisSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_tetrisSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
//		tetrisSoundPlayer.seekTo(0);
//		tetrisSoundPlayer.start();
	}

	public void gameOverSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		pause(); // pause music to make the end of the game feel more dramatic. hhheheh.
		soundPool.play(
			soundID_gameOverPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
//		gameOverPlayer.seekTo(0);
//		gameOverPlayer.start();
	}

	public void resume() {
		soundPool.autoResume();
//		try {
//			musicPlayer.start();
//		} catch(IllegalStateException e) {
//			return;
//		}
	}

	public void pause() {
		soundPool.autoPause();
//		try {
//			musicPlayer.pause();
//		} catch(IllegalStateException e) {
//			return;
//		}
	}
	
	public void release() {
		soundPool.release();
		soundPool = null;
//		buttonSoundPlayer.release();
//		dropSoundPlayer.release();
//		clearSoundPlayer.release();
//		tetrisSoundPlayer.release();
//		gameOverPlayer.release();
//		musicPlayer.release();

		host.unregisterReceiver(noisyAudioStreamReceiver);
		audioCEO.abandonAudioFocus(this);
		host = null;
		noFocus = true;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
        	soundPool.setVolume(soundID_musicPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
//        	buttonSoundPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	dropSoundPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	clearSoundPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	tetrisSoundPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	gameOverPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	musicPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
    	} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
        	soundPool.setVolume(soundID_musicPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
//        	buttonSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	dropSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	clearSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	tetrisSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	gameOverPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
//        	musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
    		resume();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
        	pause();
        }
    }

	@Override
	public void onLoadComplete(SoundPool sp, int soundID, int status) {
		if((status == 0) && (soundID == soundID_musicPlayer))
			soundID_musicPlayer = sp.play( // overwrite the old soundID with the new streamID
				soundID, // sound to play = music sound id
				0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 
				0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 
				2, 
				-1, 
				1.0f
			);
	}

}
