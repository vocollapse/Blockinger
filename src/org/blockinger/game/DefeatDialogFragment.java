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

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DefeatDialogFragment extends DialogFragment {

	private CharSequence scoreString;
	private CharSequence timeString;
	private CharSequence apmString;
	private long score;
	
	public DefeatDialogFragment() {
		super();
		scoreString = "unknown";
		timeString = "unknown";
		apmString = "unknown";
		score = -1;
	}
	
	public void setData(long scoreArg, long timeMillis, int apm) {
		GregorianCalendar date = new GregorianCalendar();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",Locale.US);

		date.setTimeInMillis(timeMillis + GameLogic.getInstance().hourOffset*(3600000));
		scoreString = String.valueOf(scoreArg);
		timeString = formatter.format(date.getTime());
		apmString = String.valueOf(apm);
		score = scoreArg;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.defeatDialogTitle);
		builder.setMessage(
				"Score:" + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + scoreString + "\n" +
				"Time:" + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + timeString + "\n" +
				"Total APM:" + getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab) + apmString
				);
		builder.setNeutralButton(R.string.defeatDialogReturn, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((GameActivity)getActivity()).putScore(score);
				getActivity().finish();
			}
		});
		return builder.create();
	}
}
