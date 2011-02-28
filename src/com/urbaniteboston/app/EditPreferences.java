package com.urbaniteboston.app;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by IntelliJ IDEA.
 * User: mchang
 * Date: 2/27/11
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditPreferences extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

    }
}