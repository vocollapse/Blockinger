package org.blockinger.game.activities;

import org.blockinger.game.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class HelpActivity extends PreferenceActivity {

    private AlertDialog.Builder dialog;

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.help_menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        /* Initialize Dialog */
        dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setNeutralButton(R.string.action_backtomain, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /* Register on Click Callbacks */
        Preference pref = findPreference("pref_help_scoring");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialog.setTitle(R.string.pref_help_scoring_title);
                dialog.setMessage(R.string.pref_help_scoring_message);
                dialog.show();
                return true;
            }
        });

        pref = findPreference("pref_help_levels");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialog.setTitle(R.string.pref_help_levels_title);
                dialog.setMessage(R.string.pref_help_levels_message);
                dialog.show();
                return true;
            }
        });

        pref = findPreference("pref_help_vibration");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialog.setTitle(R.string.pref_help_vibration_title);
                dialog.setMessage(R.string.pref_help_vibration_message);
                dialog.show();
                return true;
            }
        });

        pref = findPreference("pref_help_apm");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialog.setTitle(R.string.pref_help_apm_title);
                dialog.setMessage(R.string.pref_help_apm_message);
                dialog.show();
                return true;
            }
        });

        pref = findPreference("pref_help_fps");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialog.setTitle(R.string.pref_help_fps_title);
                dialog.setMessage(R.string.pref_help_fps_message);
                dialog.show();
                return true;
            }
        });

        pref = findPreference("pref_help_randomizer");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialog.setTitle(R.string.pref_help_randomizer_title);
                dialog.setMessage(R.string.pref_help_randomizer_message);
                dialog.show();
                return true;
            }
        });

        pref = findPreference("pref_help_resumability");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialog.setTitle(R.string.pref_help_resumability_title);
                dialog.setMessage(R.string.pref_help_resumability_message);
                dialog.show();
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
