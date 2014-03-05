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
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.netlinks.healthbracelet.MainActivity;
import com.netlinks.healthbracelet.R;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by Saif Chaouachi on 1/28/14.
 */
public class HeartRateService extends Service {
    private static final String TAG = "HealthBracelet"; // Application name

    private String address;    // MAC-address

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Set a UUID for my device

    @Override
    public void onCreate() {
        BluetoothConfig();
    }


    private void showOnGoingNotification() {
        final int NOTIFICATION_ID = 1;

// Create an Intent that will open the main Activity
// if the notification is clicked.
        //TODO replace explicit intent with implicit one
        //TODO resolve multiple MainActivity launch
        final Intent intent =
                new Intent(this, MainActivity.class);
        final PendingIntent pi = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, 0);
        // Set the Notification UI parameters
        final String name = getString(R.string.app_name);
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setTicker(name).setWhen(System.currentTimeMillis())
                //.setSubText("Heart Rate Monitor")
                .setContentIntent(pi).setOngoing(true);
        final Notification notification = builder.build();


        // Move the Service to the Foreground
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class HeartRateServiceBinder extends Binder {
        public HeartRateService getService() {
            return HeartRateService.this;
        }
    }

    private final IBinder binder = new HeartRateServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showOnGoingNotification();


        //restart the service ASAP in case it was killed by the system
        // ( resource critical situations)
        return START_STICKY;
    }

    /**
     * Bluetooth Congiguration
     * 1- Establishing the connection with the bracelet
     * 2- Create the socket
     */
    public void BluetoothConfig() {
        address = "BRACELET-MAC-ADDRESS"; //(String) getResources().getText(R.string.default_MAC);

        btAdapter = BluetoothAdapter.getDefaultAdapter();        // Get the default Bluetooth adapter
        // Check for Bluetooth support and then check to make sure it is turned on
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth is not supported");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth is ON...");
            } else {
                Log.d(TAG, "...Bluetooth is OFF...");
            }
        }

        Log.d(TAG, "...try connect...");


        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.d(TAG, "Fatal Error : Incorrect MAC-address");
        } else {
            // Set up a pointer to the remote node using it's address.
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // Two things are needed to make a connection:
            //   A MAC address AND a Service UUID.
            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "Fatal Error : socket create failed : " + e.getMessage() + ".");
            }

            // Discovery is resource intensive.  We should deactivate it
            btAdapter.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            Log.d(TAG, "...Connecting...");
            try {
                btSocket.connect();
                Log.d(TAG, "...Connection is OK...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Log.d(TAG, "Fatal Error : unable to close socket during connection failure" + e2.getMessage() + ".");
                }
            }
        }
    }
}