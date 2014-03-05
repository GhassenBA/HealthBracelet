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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.netlinks.healthbracelet.R;

/**
 * Created by Saif Chaouachi on 1/26/14.
 */
public class UrgencyDialogFragment extends DialogFragment {

    private Context mContext;
    private SharedPreferences prefs;
    private AlertDialog dialog;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    private ProgressBar progressBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Health Urgency");
        alertDialogBuilder.setMessage("Are you fine?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", null);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        progressBar = (ProgressBar) inflater.inflate(R.layout.timeout_pb, null);
        alertDialogBuilder.setView(progressBar);

        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        dialog = alertDialogBuilder.create();
        return dialog;

    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        urgency();
    }

    public void cancel() {
        dialog.cancel();
    }

    public void urgency() {
        String phoneNo = prefs.getString("savior_number", "911");
        if (prefs.getBoolean(SPreferenceFragment.MESSAGE_ENABLED, true)) {

            String sms = prefs.getString(SPreferenceFragment.MESSAGE_TEXT, getString(R.string.help));
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(mContext, "SMS Sent!", Toast.LENGTH_LONG).show();
        }

        if (prefs.getBoolean(SPreferenceFragment.CALL_ENABLED, false)) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNo));
            startActivity(callIntent);
            Toast.makeText(mContext, "Call started!", Toast.LENGTH_LONG).show();
        }
    }

    public void countDown(final long timeout) {
        new CountDownTimer(timeout, 1000) {

            public void onTick(long millisUntilFinished) {

                int sec =Math.round((100* millisUntilFinished / timeout));

                Toast.makeText(mContext,""+sec,Toast.LENGTH_SHORT).show();
                progressBar.setProgress(sec);
            }

            public void onFinish() {
                dialog.cancel();
            }
        }.start();
    }

    //public void timerDelayRemoveDialog(long time, final UrgencyDialogFragment d) {
      //  new Handler().postDelayed(new Runnable() {
      //      public void run() {
        //        d.cancel();
          //  }
        //}, time);
    //}
}
