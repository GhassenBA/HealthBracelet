package com.netlinks.healthbracelet;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.netlinks.healthbracelet.fragment.MainFragment;
import com.netlinks.healthbracelet.service.HeartRateService;

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

/**
 * @author Saif Chaouachi(ch.saiff35@gmail.com)
 *         1/28/14
 */


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    public static final int HEARTRATE_RECEIVED = 1;

    private UpdateHeartRateThread updateThread;
    // Reference to the service
    private HeartRateService serviceRef;
    // Handles the connection between the service and activity
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // Called when the connection is made.
            serviceRef = ((HeartRateService.HeartRateServiceBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
// Received when the service unexpectedly disconnects.
            serviceRef = null;
        }
    };
    TextView heartRateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        //TODO move this
        startService(new Intent(this, HeartRateService.class));

        heartRateView = (TextView) findViewById(R.id.heartRateTextView);
        updateThread = new UpdateHeartRateThread();
        updateThread.start();
    }

    private final Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            heartRateView = (TextView) findViewById(R.id.heartRateTextView);

            float heartRate = (float) msg.obj;
                    heartRateView.setText(String.valueOf(heartRate));


        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Bind to the service
        Intent intent = new Intent(this, HeartRateService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        updateThread = new UpdateHeartRateThread();
        updateThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        updateThread.stop();

        // UnBind to the service
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SPreferenceActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class UpdateHeartRateThread extends Thread {
        public UpdateHeartRateThread() {

        }

        public void run(){
            while(true) {
                try {
                    this.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (serviceRef != null) {
                    mhandler.obtainMessage(1,serviceRef.getHeartRate()).sendToTarget();
                }
            }
        }
    }

}
