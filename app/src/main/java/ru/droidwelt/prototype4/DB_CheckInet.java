package ru.droidwelt.prototype4;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class DB_CheckInet extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... sss) {
        String result = "";
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection con = null;
            Statement st = null;
            ResultSet rs = null;

            try {
                con = DriverManager.getConnection(Appl.MSSQL_DB, Appl.MSSQL_LOGIN, Appl.MSSQL_PASS);
                if (con != null) {
                    st = con.createStatement();
                    rs = st.executeQuery("select 1 as ID");
                    if (rs != null) result = rs.toString();
                }
            } catch (SQLException e) {
                //e.printStackTrace();
                result = "######1 " + e.getMessage();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                  //  throw new RuntimeException(e.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            result = "######3 " + e.getMessage();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

}