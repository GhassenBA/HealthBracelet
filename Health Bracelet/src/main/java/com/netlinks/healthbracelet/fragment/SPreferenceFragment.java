
/*
 * Copyright (C) 2014 Health Bracelet Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.netlinks.healthbracelet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.netlinks.healthbracelet.R;

/**
 * Created by Saif Chaouachi on 1/25/14.
 */
public class SPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String CALL_MESSAGE = "call_message";
    public static final String CALL_ENABLED = "call_enabled";
    public static final String MESSAGE_TEXT = "message_text";
    public static final String MESSAGE_ENABLED = "message_enabled";
    public static final int TIME_OUT = 500;
    private static int ContactsRequestCode = 21;
    private EditText savior;

    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.savior_pref);
        final EditTextPreference customContact = (EditTextPreference) findPreference("savior_number");
        savior = customContact.getEditText();
        savior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchContactPicker();
            }
        });
        prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        findPreference(MESSAGE_TEXT).setEnabled(
                prefs.getBoolean(MESSAGE_ENABLED, true)

        );
        findPreference(CALL_MESSAGE).setEnabled(prefs.getBoolean(CALL_ENABLED, false));


    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void launchContactPicker() {
        final Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);


        startActivityForResult(contactPickerIntent, SPreferenceFragment.ContactsRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == SPreferenceFragment.ContactsRequestCode) && (resultCode == Activity.RESULT_OK)) {
            final String[] id = {data.getData().getLastPathSegment()};
            final String[] columns = {ContactsContract.CommonDataKinds.Phone.DATA};
            final Cursor c = getActivity().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            columns,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", id,
                            null);


            if (c != null && c.moveToFirst()) {

                final String number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DATA));
                savior.setText(number);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case MESSAGE_ENABLED:
                //if the two will be disabled ignore the command
                boolean isActivated = sharedPreferences.getBoolean(MESSAGE_ENABLED, true);
                if (!sharedPreferences.getBoolean(CALL_ENABLED, false) && !isActivated) {
                    //Reverse last disable and show error message
                    Toast.makeText(getActivity(), "Can't disable both", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((SwitchPreference) findPreference(MESSAGE_ENABLED)).setChecked(true);
                        }
                    }, TIME_OUT);


                } else {
                    //Disable the concerned field
                    findPreference(MESSAGE_TEXT).setEnabled(isActivated);
                }

                break;
            case CALL_ENABLED:
                boolean isActivated2 = sharedPreferences.getBoolean(CALL_ENABLED, false);
                if (!sharedPreferences.getBoolean("message_enabled", true) && !isActivated2) {
                    Toast.makeText(getActivity(), "Can't disable both", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((SwitchPreference) findPreference(CALL_ENABLED)).setChecked(true);
                        }
                    }, TIME_OUT);


                } else {
                    findPreference(CALL_MESSAGE).setEnabled(isActivated2);
                }


                break;
        }
    }


}




