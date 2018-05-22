package ru.droidwelt.prototype4;

import android.os.AsyncTask;

import org.json.JSONArray;

class DB_Query extends AsyncTask<String, Void, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... query) {
        return  Appl.query_MSSQL_Exec (query);
    }

    /*   protected void onProgressUpdate() {
        Appl.ShowProgressIndicatior(Appl.getContext());
    }


    @Override
    protected void onPostExecute(JSONArray result) {
        super.onPostExecute(result);
     //   Appl.Indicator.dismiss();
    }
*/
}