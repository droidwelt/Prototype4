package ru.droidwelt.prototype4;

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

class DB_SaveAvatar extends AsyncTask<URL, Integer, Long> {

    @Override
    protected Long doInBackground(URL... params) {
        String msql = "update FIO set FIO_NAME='" + Appl.FIO_NAME
                + "',FIO_SUBNAME='" + Appl.FIO_SUBNAME + "' where FIO_ID="
                + Appl.FIO_ID;
        Appl.query_MSSQL_Exec(msql);

        if (Appl.FIO_IMAGE != null) {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                Connection con;

                try {
                    con = DriverManager.getConnection(Appl.MSSQL_DB,
                            Appl.MSSQL_LOGIN, Appl.MSSQL_PASS);
                    if (con != null) {
                        PreparedStatement pStmt = con
                                .prepareStatement("update dbo.FIO set FIO_IMAGE= (?) where FIO_ID= (?) ");

                        pStmt.setBytes(1,
                                Appl.getByteArrayfromBitmap(Appl.FIO_IMAGE));
                        pStmt.setInt(2, Appl.FIO_ID);
                        pStmt.executeUpdate();
                    }
                } catch (SQLException ignored) {
                }

            } catch (ClassNotFoundException ignored) {
            }
        } else {
            msql = "update FIO set FIO_IMAGE=NULL where FIO_ID=" + Appl.FIO_ID;
            Appl.query_MSSQL_Exec(msql);
        }

        return (long) 1;
    }


    protected void onPostExecute(Long result) {
        Appl.Indicator.dismiss();
    }

}
