package ru.droidwelt.prototype4;

import android.database.Cursor;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class DB_SendAll extends AsyncTask<URL, Integer, Long> {

    private static byte[] blob_MSA_IMAGE;
    private static String blob_MSA_ID;


    private void save_Blob() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection con;

            try {
                con = DriverManager.getConnection(Appl.MSSQL_DB, Appl.MSSQL_LOGIN, Appl.MSSQL_PASS);
                if (con != null) {
                    PreparedStatement pStmt = con.prepareStatement("update dbo.MSA set MSA_IMAGE= (?) where MSA_ID=CONVERT(uniqueidentifier, (?) )");
                    pStmt.setBytes(1, blob_MSA_IMAGE);
                    pStmt.setString(2, blob_MSA_ID);
                    pStmt.executeUpdate();
                }
            } catch (SQLException e) {
              //  resultSet = "######1 " + e.getMessage(); // "Нет подключения к сети Интернет");
            }

        } catch (ClassNotFoundException e) {
          //  resultSet = "######3 " + e.getMessage(); // "Неопределенная ошибка совместимости");
        }
    }


    private void send_one_record_to_MSSSQL(String xmsa_id) {
        String sSQL = "select MSA_ID,MSA_CLR,MSA_LBL,MSA_TITLE,MSA_TEXT,FIO_ID," +
                "MSA_FILETYPE,MSA_FILENAME,length(MSA_IMAGE) as IMAGESIZE " +
                "from MSA where MSA_ID='" + xmsa_id + "'";
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
        c.moveToFirst();
        int index = c.getColumnIndex("MSA_TITLE");
        String MSA_TITLE = Appl.strnormalize(c.getString(index));
        index = c.getColumnIndex("MSA_CLR");
        String MSA_CLR = Appl.strnormalize(c.getString(index));
        index = c.getColumnIndex("MSA_LBL");
        String MSA_LBL = Appl.strnormalize(c.getString(index));
        index = c.getColumnIndex("MSA_TEXT");
        String MSA_TEXT = Appl.strnormalize(c.getString(index));
        index = c.getColumnIndex("FIO_ID");
        int FIO_ID = Integer.parseInt(Appl.strnormalize(c.getString(index)));
        index = c.getColumnIndex("MSA_FILETYPE");
        String MSA_FILETYPE = Appl.strnormalize(c.getString(index));
        index = c.getColumnIndex("MSA_FILENAME");
        String MSA_FILENAME = Appl.strnormalize(c.getString(index));
        index = c.getColumnIndex("IMAGESIZE");
        String simagesize = Appl.strnormalize(c.getString(index));
        int imagesize = 0;
        if (!simagesize.equals("")) imagesize = Integer.parseInt(simagesize);
        c.close();

        int safe_msa_tp = 2;
        if (imagesize > 0) safe_msa_tp = 22;

        StringBuilder msql =
                new StringBuilder(" delete from dbo.MSB where MSA_ID=CONVERT(uniqueidentifier,'" + xmsa_id + "') " +
                        " delete from dbo.MSA where MSA_ID=CONVERT(uniqueidentifier,'" + xmsa_id + "') " +
                        " insert into dbo.MSA " +
                        " (MSA_ID,MSA_CLR,MSA_LBL,FIO_ID,MSA_STATE,MSA_DATE,MSA_GRP," +
                        "MSA_TITLE,MSA_TEXT,MSA_FILETYPE,MSA_FILENAME) " +
                        " values " +
                        "(CONVERT(uniqueidentifier,'" + xmsa_id + "'),'" + MSA_CLR + "','" + MSA_LBL + "'," +
                        FIO_ID + "," + safe_msa_tp + ",GETDATE(),'" + Appl.APP_PREFIX + "','" +
                        MSA_TITLE + "','" + MSA_TEXT + "','" + MSA_FILETYPE + "','" + MSA_FILENAME + "')    ");

        StringBuilder sFio_list = new StringBuilder();
        sSQL = "select distinct X.* from " +
                "(" +
                "  select  F.FIO_ID " +
                "  from MSB B " +
                "  left join FIO F on B.FIO_ID=F.FIO_ID " +
                "  where F.FIO_TP=2 and MSA_ID='" + xmsa_id + "' " +
                "     union all  " +
                "  select  W.FIO_IDMB " +
                "  from FIO W " +
                "  where W.FIO_IDGR in " +
                "  (select  B.FIO_ID " +
                "  from MSB B " +
                "  left join FIO F on B.FIO_ID=F.FIO_ID " +
                "  where F.FIO_TP=1 and MSA_ID='" + xmsa_id + "') " +
                "  ) X ";

        c = Appl.getDatabase().rawQuery(sSQL, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int xfio_id = c.getInt(c.getColumnIndex("FIO_ID"));
            if (xfio_id != Appl.FIO_ID) {
                if (sFio_list.toString().equals("")) sFio_list = new StringBuilder("" + xfio_id);
                else sFio_list.append(",").append(xfio_id);
            }
            msql.append("insert into MSB (MSA_ID,MSB_ID,FIO_ID) " + " values (CONVERT(uniqueidentifier,'")
                    .append(xmsa_id).append("'),").append("NewID(),").append(xfio_id).append(") ");
            c.moveToNext();
        }
        c.close();
        msql.append("select 1 as RES");
        Appl.query_MSSQL_Exec(msql.toString());

        // пишем в MSSQL изображение
        if (imagesize > 0) {
            int i = 0;
            int theImagePos = 0;
            int rdbytes = 200000;
            byte[] resall = null;

            while (rdbytes == 200000 & i < 300) {
                Cursor cursor = Appl.getDatabase().rawQuery(
                        "select substr(MSA_IMAGE,1+" + String.valueOf(i) + "*200000,200000) from MSA  where MSA_ID='" + xmsa_id + "'", null);
                i = i + 1;
                cursor.moveToFirst();
                byte[] res;
                rdbytes = cursor.getBlob(0).length;
                if (rdbytes > 0) {
                    res = cursor.getBlob(0);
                    resall = Appl.concatArray(resall, res);
                    theImagePos = theImagePos + res.length;
                }
                cursor.close();
            }
            blob_MSA_IMAGE = resall;
            blob_MSA_ID = xmsa_id;
            save_Blob();
        }

        if (!sFio_list.toString().equals("")) {
            List<String> FCM_Send_Codes = new ArrayList<>();
            sSQL = "select DEV_FCM from DEV D where DEV_FCM<>'' and D.FIO_ID in (" + sFio_list + ") ;";
            c = Appl.getDatabase().rawQuery(sSQL, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String sDEV_FCM = c.getString(c.getColumnIndex("DEV_FCM"));
                FCM_Send_Codes.add(sDEV_FCM);
                c.moveToNext();
            }
            c.close();
            PushNotification.send_FCM_message(MSA_TITLE, MSA_TEXT, FCM_Send_Codes);
        }

        if (safe_msa_tp == 22) {
            msql = new StringBuilder("update MSA set MSA_STATE=2 where MSA_ID=CONVERT(uniqueidentifier,'" + xmsa_id + "') select 1 as RES");
            Appl.query_MSSQL_Exec(msql.toString());
        }

        sSQL = "update MSA set MSA_STATE=3 where MSA_ID='" + xmsa_id + "'";
        Appl.getDatabase().execSQL(sSQL);
    }

    @Override
    protected Long doInBackground(URL... params) {
        int res = 0;
        String sSQL = "select MSA_ID from MSA where MSA_STATE=4 order by MSA_DATE;";
        Cursor cc = Appl.getDatabase().rawQuery(sSQL, null);
        cc.moveToFirst();
        while (!cc.isAfterLast()) {
            res++;
            String xmsa_id = cc.getString(cc.getColumnIndex("MSA_ID"));
            send_one_record_to_MSSSQL(xmsa_id);
            cc.moveToNext();
            publishProgress(res);
        }
        cc.close();
        return (long) res;
    }


    protected void onProgressUpdate(Integer... progress) {
        if (progress[0] != 0) {
            if (Appl.MSA_MODEVIEW == 4)
                try {
                    MsaMainActivity.refreshRecords();
                } catch (Exception ignored) {
                }
        }
    }

    protected void onPostExecute(Long result) {
        try {
            Appl.Indicator.dismiss();
        } catch (Exception ignored) {
        }

        if ((result > 0) & (Appl.MSA_MODEVIEW == 4)) {
            try {
                MainActivity.getInstance().getBoxCount();
                MainActivity.getInstance().refreshMainMenu();
                MsaMainActivity.refreshRecords();
            } catch (Exception ignored) {
            }

        }
    }


}
