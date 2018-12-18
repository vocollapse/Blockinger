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

import org.blockinger.game.activities.GameActivity;

import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;

public class WorkThread extends Thread {

    /**
     *
     */
    private SurfaceHolder surfaceHolder;
    private boolean runFlag = false;
    boolean firstTime = true;
    public long lastFrameDuration = 0;
    private long lastFrameStartingTime = 0;
    int fpslimit;
    long lastDelay;
    private GameActivity host;

    public WorkThread(GameActivity ga, SurfaceHolder sh) {
        host = ga;
        this.surfaceHolder = sh;
        try {
            fpslimit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(host).getString("pref_fpslimittext", "35"));
        } catch(NumberFormatException e) {
            fpslimit = 25;
        }
        if(fpslimit < 5)
            fpslimit = 5;

        lastDelay = 100;
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
                if(firstTime){
                    firstTime = false;
                    continue;
                }

                /* FPS CONTROL */
                tempTime = System.currentTimeMillis();

                try {
                    fpslimit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(host).getString("pref_fpslimittext", "35"));
                } catch(NumberFormatException e) {
                    fpslimit = 35;
                }
                if(fpslimit < 5)
                    fpslimit = 5;

                if(PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_fpslimit", false)) {
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
                            // e.printStackTrace(); ignore this shit
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

                if(host.game.cycle(tempTime))
                    host.controls.cycle(tempTime);
                host.game.getBoard().cycle(tempTime);

                c = null;
                try {

                    c = this.surfaceHolder.lockCanvas(null);
                    synchronized (this.surfaceHolder) {
                        host.display.doDraw(c, frames);
                    }
                } finally {

                    if (c != null) {
                        this.surfaceHolder.unlockCanvasAndPost(c);

                    }
                }
        }
    }

    public void setFirstTime(boolean b) {
        firstTime = b;
    }

}
