package ru.droidwelt.prototype4;

import android.content.ContentValues;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class DB_ReceiveBlobFormMSSQL extends AsyncTask<URL, Integer, Long> {

    public String MSA_FILETYPE = "";
    public String MSA_ID = "?";
    private int imgsize = 0;
    TouchImageView tiv_msa_view;


    @Override
    protected Long doInBackground(URL... params) {
        JSONArray resultImg = Appl.query_MSSQL_Exec("MSAIMAGEGET_STR '" + MSA_ID + "'");
        if (!resultImg.toString().contains("######")) {
            try {
                String resstr = resultImg.getJSONObject(0).getString("MSA_IMAGE");
                byte[] resall = Appl.hexToBytes(resstr);
                imgsize = resall.length;
                ContentValues ediMessage = new ContentValues();
                ediMessage.put("MSA_IMAGE", resall);
                Appl.getDatabase().update("MSA", ediMessage, "MSA_ID='" + MSA_ID + "'", null);
            } catch (JSONException ignored) {
            }
        }
        return null;
    }

    protected void onPostExecute(Long result) {
        Appl.Indicator.dismiss();
        MsaMainActivity.getInstance().setImageSize(MSA_ID, imgsize);
        if (tiv_msa_view != null) {
            try {
                MsaMainRecyclerAdapter.load_image_from_SQLite(tiv_msa_view, MSA_ID);
                MsaMainActivity.mRecyclerView.getAdapter().notifyDataSetChanged();
            } catch (Exception e) {
              //  Appl.DisplayToastError(e.toString());
            }
        }
    }

}
