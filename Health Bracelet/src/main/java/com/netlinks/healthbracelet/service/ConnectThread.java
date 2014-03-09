package com.netlinks.healthbracelet.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

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
 *         3/9/14
 */


public class ConnectThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private SyncThread syncThread;
    private BluetoothAdapter mmAdapter;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter btAdapter) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        mmAdapter = btAdapter;
        mmDevice = device;
        BluetoothSocket btSocket = null;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            btSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            //TODO exception handling
        }
        mmSocket = btSocket;

    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mmAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
//TODO handle exception
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        syncThread = new SyncThread(mmSocket);
        syncThread.start();
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            //TODO handle exception
        }
    }

}
