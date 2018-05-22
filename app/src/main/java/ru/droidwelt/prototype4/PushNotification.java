package ru.droidwelt.prototype4;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

class PushNotification {


    static void send_FCM_message(final String MSA_TITLE, final String MSA_TEXT, final List<String> FCM_Send_DevCodes) {
        class WorkingThread extends Thread {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < FCM_Send_DevCodes.size(); i++) {

                    String my = "{ \"to\" : \"" + FCM_Send_DevCodes.get(i) + "\", \"data\" : { \"TEXT\" : \"" + MSA_TEXT + "\",\"TITLE\" : \"" + MSA_TITLE + "\" } }";
                 //   Log.e("EEEEEEEE", "my="+my);

                    try {
                        URL url = new URL("https://fcm.googleapis.com/fcm/send");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Authorization",
                                "key=AAAADZ9DXfg:APA91bFqZqGApxPdg0M0PMoLsXJE_Gcsy_44bqkqlshSzJ4_JKg-u2fiwU1f0GvM_IxXX-m0ZIBGsEvlXXk2BE5O1y9X7CC3kxY978YEkOGoilFHTbcGwwQMC7LeVQSEQSjOBYXp-liB");
                        //   https://console.firebase.google.com/project/prototype2-8f11f/settings/general/android:ru.droidwelt.prototype2
                        conn.setRequestProperty("Content-Length", "" + Integer.toString(my.getBytes().length));
                        OutputStream os = conn.getOutputStream();
                        os.write(my.getBytes("UTF-8"));
                        conn.connect();

                   //     int responseCode = conn.getResponseCode();
                   //     Log.e("EEEEEEEE", "responseCode=" + responseCode);

                        InputStream input = conn.getInputStream();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                            for (String line; (line = reader.readLine()) != null; ) {
                                // System.out.println(line);
                                Log.e("EEEEEEEE", "input=" + line);
                            }
                        }
                    } catch (IOException e) {
                        Log.e("EEEEEEEE", e.toString());
                    }
                }
            }
        }
        new WorkingThread().start();
    }
}
