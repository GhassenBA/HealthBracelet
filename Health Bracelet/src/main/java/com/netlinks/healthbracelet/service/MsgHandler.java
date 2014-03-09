package com.netlinks.healthbracelet.service;


import android.os.Handler;

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

public class MsgHandler extends Handler {
    static final int RECIEVE_MESSAGE = 1;        // Status for Handler
    private float heartRate;
    private StringBuilder sb = new StringBuilder();

    @Override
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case RECIEVE_MESSAGE:                                                   // if receive massage
                byte[] readBuf = (byte[]) msg.obj;
                String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                sb.append(strIncom);                                                // append string
                int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                if (endOfLineIndex > 0) {                                            // if end-of-line,
                    String receivedNumber = sb.substring(0, endOfLineIndex);               // extract string
                    sb.delete(0, sb.length());
                    heartRate = Integer.parseInt(receivedNumber);

                }
                break;
        }
    }

    public float getHeartRate() {
        return heartRate;
    }
}
