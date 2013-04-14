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
import org.blockinger.game.activities.GameActivity;

import android.media.MediaPlayer;
import android.preference.PreferenceManager;

public class Sound extends Component {

	private MediaPlayer tetrisSoundPlayer;
	private MediaPlayer dropSoundPlayer;
	private MediaPlayer clearSoundPlayer;
	private MediaPlayer gameOverPlayer;
	
	public Sound(GameActivity c) {
		super(c);
		tetrisSoundPlayer = MediaPlayer.create(c,R.raw.seqlong);
		tetrisSoundPlayer.setLooping(false);
		tetrisSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));
		
		dropSoundPlayer = MediaPlayer.create(c,R.raw.drop2);
		dropSoundPlayer.setLooping(false);
		dropSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));
	    
		clearSoundPlayer = MediaPlayer.create(c,R.raw.synthaccord);
		clearSoundPlayer.setLooping(false);
		clearSoundPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));
		
		gameOverPlayer = MediaPlayer.create(c,R.raw.gameover);
		gameOverPlayer.setLooping(false);
		gameOverPlayer.setVolume(0.015f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(c).getInt("pref_soundvolume", 60));
	}
	
	public void clearSound() {
		//if(clearSoundPlayer.isPlaying()) 
		//	clearSoundPlayer.stop();
		clearSoundPlayer.seekTo(0);
		clearSoundPlayer.start();
	}
	
	public void dropSound() {
		//if(dropSoundPlayer.isPlaying())
		//	dropSoundPlayer.stop();
		dropSoundPlayer.seekTo(0);
		dropSoundPlayer.start();
	}
	
	public void release() {
		dropSoundPlayer.release();
		clearSoundPlayer.release();
		tetrisSoundPlayer.release();
		gameOverPlayer.release();
	}

	public void tetrisSound() {
		//if(tetrisSoundPlayer.isPlaying())
		//	tetrisSoundPlayer.stop();
		tetrisSoundPlayer.seekTo(0);
		tetrisSoundPlayer.start();
	}

	public void gameOverSound() {
		//if(tetrisSoundPlayer.isPlaying())
		//	tetrisSoundPlayer.stop();
		gameOverPlayer.seekTo(0);
		gameOverPlayer.start();
	}
}
