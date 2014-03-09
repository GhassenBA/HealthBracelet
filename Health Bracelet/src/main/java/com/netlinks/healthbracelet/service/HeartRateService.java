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


package com.netlinks.healthbracelet.service;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.netlinks.healthbracelet.MainActivity;
import com.netlinks.healthbracelet.R;


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
public class HeartRateService extends Service {

    private static final String TAG = "HealthRateService"; // Service name used for debug
    private static final String ACTION_MAIN = "com.netlinks.healthbracelet.action.MAIN"; //Main Activity Action
    private final IBinder binder = new HeartRateServiceBinder();
    private BluetoothAdapter btAdapter;
    private ConnectThread connectThread;

    private void showOnGoingNotification() {
        final int NOTIFICATION_ID = 1;

// Create an Intent that will open the main Activity
// if the notification is clicked.


        // Set the Notification UI parameters
        final String name = getString(R.string.app_name);
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);
        //TODO replace with implicit intent
        final Intent intent =
                new Intent(this, MainActivity.class);

        final PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Heart Rate Monitor")
                .setTicker(name)
                .setContentIntent(pi).setOngoing(true);
        final Notification notification = builder.build();


        // Move the Service to the Foreground
        startForeground(NOTIFICATION_ID, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        BluetoothConfig();

        showOnGoingNotification();

        //restart the service ASAP in case it was killed by the system
        // ( resource critical situations)
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Bluetooth Configuration
     * 1- Establishing the connection with the bracelet
     * 2- Create the socket
     */
    public void BluetoothConfig() {
        final String address = "7A:79:46:66:28:31"; //Health Bracelet MAC address

        btAdapter = BluetoothAdapter.getDefaultAdapter();        // Get the default Bluetooth adapter
        // Check for Bluetooth support and then check to make sure it is turned on
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth is not supported");
            stopSelf();
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth is ON...");
            } else {
                Log.d(TAG, "...Bluetooth is OFF...");
                //TODO Requesting to open bluetooth (in a proper way)
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //int REQUEST_ENABLE_BT = 404;
                getApplication().startActivity(enableBtIntent);

            }
        }

        Log.d(TAG, "...trying to connect...");


        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.d(TAG, "Fatal Error : Incorrect MAC-address");
        } else {
            // Set up a pointer to the remote node using it's address.
            final BluetoothDevice device = btAdapter.getRemoteDevice(address);

            connectThread = new ConnectThread(device, btAdapter);
            connectThread.start();

        }
    }

    public class HeartRateServiceBinder extends Binder {
        public HeartRateService getService() {
            return HeartRateService.this;
        }
    }


}