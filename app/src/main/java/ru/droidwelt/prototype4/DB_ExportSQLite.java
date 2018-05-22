

package ru.droidwelt.prototype4;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


class DB_ExportSQLite extends AsyncTask<URL, Integer, Long> {

    String sFileName = "";


    @Override
    protected Long doInBackground(URL... params) {
        int readAll = 0;
        File f = new File(Environment.getExternalStorageDirectory().toString() + "/Download/", sFileName);
        try {
          if (!f.createNewFile())
              return null;
        } catch (IOException ignored) {
        }

        int i = 0;
        int theImagePos = 0;
        int rdbytes = 200000;
        byte[] resall = null;

        while (rdbytes == 200000 & i < 300) {
            Cursor cursor = Appl.getDatabase().rawQuery(
                    "select substr(MSA_IMAGE,1+" + String.valueOf(i) + "*200000,200000) from MSA  where MSA_ID='" + Appl.MSA_ID + "'", null);
            i = i + 1;
            cursor.moveToFirst();
            byte[] res;

            rdbytes = cursor.getBlob(0).length;
            readAll = readAll + rdbytes;
            if (rdbytes > 0) {
                res = cursor.getBlob(0);
                resall = Appl.concatArray(resall, res);
                theImagePos = theImagePos + res.length;
            }
            cursor.close();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException ignored) {

        }
        try {
            assert resall != null;
            if (fos != null) {
                fos.write(resall);
                fos.flush();
                fos.close();
            }
        } catch (IOException ignored) {
        }
        return null;
    }


    protected void onPostExecute(Long result) {
        try {
            Appl.startIntentFromFile(sFileName);
        } catch (Exception ignored) {
        }

    }

}
