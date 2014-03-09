package com.netlinks.healthbracelet.service;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;


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


public class SyncThread extends Thread {
    private final InputStream mmInStream;
    private Handler h;
    private BluetoothSocket mmSocket;

    public SyncThread(BluetoothSocket socket) {
        h = new MsgHandler();
        mmSocket = socket;
        InputStream tmpIn = null;

        // Get the input stream, using temp objects because
        // member stream are final
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            //TODO handle exception
        }

        mmInStream = tmpIn;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                h.obtainMessage(MsgHandler.RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
            } catch (IOException e) {
                break;
            }
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            //TODO handle exception
        }
    }
}
