package ru.droidwelt.prototype4;


import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static ClientActivity instance;
    private  ClientCursorAdapter myClientAdapter;
    public static String filter = "";
    public  ListView lv;
    public static List<Integer> LIST_CLT_ID;
    public static List<Integer> LIST_ID;

    public static synchronized ClientActivity getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesNoActionBar(this);
        instance = this;
        setContentView(R.layout.activity_client);
        LIST_CLT_ID = new ArrayList<>();
        LIST_ID = new ArrayList<>();

        Toolbar tb_clt = findViewById(R.id.tb_clt);
        setSupportActionBar(tb_clt);
        ImageButton ib_clt_mainmenu = findViewById(R.id.ib_clt_mainmenu);
        ib_clt_mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewMenu = new Intent(MainActivity.getInstance(), ChoiceMemuActivity.class);
                viewMenu.putExtra("MODE", "CLT");
                startActivity(viewMenu);
            }
        });

        displayLoadDateTime();

        String[] from = new String[]{"CLT_NAME", "CLT_DOLG", "CLT_AVANS"};
        int[] to = new int[]{R.id.text_client_item_CLT_NAME, R.id.text_item_client_CLT_DOLG, R.id.text_item_client_CLT_AVANS};

        setmyAdapter(new ClientCursorAdapter(this, R.layout.activity_client_item, null, from, to));
        lv = findViewById(R.id.lv_client);
        lv.setOnItemClickListener(viewContactListener);
        lv.setAdapter(getClientAdapter());

        getLoaderManager().initLoader(0, null, this);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        EditText editFilter = findViewById(R.id.client_edit_Filter);
        editFilter.setText(filter);
        editFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter = s.toString();
                getClientAdapter().changeCursor(getClientRecords(filter));
                getClientAdapter().notifyDataSetChanged();
            }
        });
    }

    // слушатель событий в ListView
    OnItemClickListener viewContactListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            Cursor cursor = Appl.getDatabase().rawQuery("select CLT_ID from CLT where _id = " + String.valueOf(id), null);
            cursor.moveToFirst();
            int CLT_ID = cursor.getInt(0);
            // Log.i("LOG", "onItemClick CLT_ID="+CLT_ID);
            cursor.close();
            Intent viewClient = new Intent(ClientActivity.this, ClientViewActivity.class);
            viewClient.putExtra("CLT_ID", CLT_ID);
            viewClient.putExtra("POS", position);
            startActivityForResult(viewClient,1);
            Appl.animateStart(ClientActivity.this);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 0) | (resultCode >= 0)) {
            int Pos = data.getIntExtra("POS", -1);
            if (Pos >= 0) {
                findByLIST_POS(Pos);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.mi_client_download:
                if (Appl.checkConnectToServer(true)) {
                    Appl.ShowProgressIndicatior (this);
                    new MyTaskClient(this).execute();
                }
                return true;

            case android.R.id.home:
                finish();
                Appl.animateFinish(ClientActivity.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class MyTaskClient extends AsyncTask<Void, Void, String> {
        private WeakReference<ClientActivity> activityReference;
        MyTaskClient(ClientActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Void... params) {
            ClientActivity activity = activityReference.get();
            Appl.getDatabase().execSQL("delete from CLT;");
            Appl.getDatabase().execSQL("delete from OPP;");
            activity.loadClt();
            activity.loadOpp();
            return "END";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Appl.Indicator.dismiss();
            } catch (Exception ignored) {
            }
            ClientActivity activity = activityReference.get();
            if (activity == null) return;

            activity.getClientAdapter().changeCursor(getClientRecords(filter));
            activity.setLoadDateTime();
            activity.displayLoadDateTime();
            MainActivity.getInstance().refreshMainMenu();
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this);
    }

    static class MyCursorLoader extends CursorLoader {
        MyCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return getClientRecords(filter);
        }
    }

    public static Cursor getClientRecords(String filter) {
        String sSQL = "select  _id, CLT_ID,CLT_NAME,CLT_DOLG,CLT_AVANS "
                + "from CLT "
                + "where CLT_NAME like '%" + filter + "%' "
                + "order by CLT_NAME ";
        getInstance().LIST_CLT_ID.clear();
        LIST_ID.clear();
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
        c.moveToFirst();
        int index_ID = c.getColumnIndex("_id");
        int index_CLT_ID = c.getColumnIndex("CLT_ID");
        while (!c.isAfterLast()) {
            getInstance().LIST_CLT_ID.add(c.getInt(index_CLT_ID));
            LIST_ID.add(c.getInt(index_ID));
            c.moveToNext();
        }
        return c;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        getClientAdapter().swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public ClientCursorAdapter getClientAdapter() {
        return myClientAdapter;
    }

    public void setmyAdapter(ClientCursorAdapter ClientatAdapter) {
        myClientAdapter = ClientatAdapter;
    }

    private void displayLoadDateTime() {
        TextView tv = findViewById(R.id.textView_ClientLastLoad);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sdate = getString(R.string.s_lastload) + sp.getString("clt_dateload", getString(R.string.s_time_not_defined));
        tv.setText(sdate);
    }

    private void setLoadDateTime() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sdate = DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString();
        Editor editor = sp.edit();
        editor.putString("clt_dateload", sdate);
        editor.apply();
    }

    public void loadClt() {
        JSONArray urls = Appl.query_MSSQL_Simple("CLT_LIST");
        if (!urls.toString().contains("######")) {
            try {
                for (int i = 0; i < urls.length(); i++) {
                    String CLT_ID = urls.getJSONObject(i).getString("CLT_ID");
                    String CLT_NAME = Appl.strnormalize(urls.getJSONObject(i).getString("CLT_NAME"));
                    String CLT_ADDR = Appl.strnormalize(urls.getJSONObject(i).getString("CLT_ADDR"));
                    String CLT_PHONE = Appl.strnormalize(urls.getJSONObject(i).getString("CLT_PHONE"));
                    String CLT_DOLG = Appl.strnormalize(urls.getJSONObject(i).getString("CLT_DOLG"));
                    String CLT_AVANS = Appl.strnormalize(urls.getJSONObject(i).getString("CLT_AVANS"));
                    String sLite = "INSERT INTO CLT  (CLT_ID, CLT_NAME, CLT_ADDR, CLT_PHONE,CLT_DOLG,CLT_AVANS) " +
                            "VALUES ('" + CLT_ID + "','" + CLT_NAME + "','" + CLT_ADDR + "','" + CLT_PHONE + "','" + CLT_DOLG + "','" + CLT_AVANS + "');";
                    Appl.getDatabase().execSQL(sLite);
                }
            } catch (JSONException e) {
                Appl.DisplayToastError(R.string.s_fail_data);
            }
        }
    }

    public void loadOpp() {
        JSONArray urls = Appl.query_MSSQL_Simple("OPP_LOAD");
        if (!urls.toString().contains("######")) {
            try {
                for (int i = 0; i < urls.length(); i++) {
                    String CLT_ID = urls.getJSONObject(i).getString("CLT_ID");
                    String DT = Appl.strnormalize(urls.getJSONObject(i).getString("DT"));
                    String OPP_OPER = Appl.strnormalize(urls.getJSONObject(i).getString("OPP_OPER"));
                    String OPP_DATE = Appl.strnormalize(urls.getJSONObject(i).getString("OPP_DATE"));
                    String OPP_NAME = Appl.strnormalize(urls.getJSONObject(i).getString("OPP_NAME"));
                    String OPP_SUM = Appl.strnormalize(urls.getJSONObject(i).getString("OPP_SUM"));
                    String sLite = "INSERT INTO OPP  (CLT_ID, DT, OPP_OPER, OPP_DATE,OPP_NAME,OPP_SUM) " +
                            "VALUES ('" + CLT_ID + "','" + DT + "','" + OPP_OPER + "','" + OPP_DATE + "','" + OPP_NAME + "','" + OPP_SUM + "');";
                    Appl.getDatabase().execSQL(sLite);
                }
            } catch (JSONException e) {
                Appl.DisplayToastError(R.string.s_fail_data);
            }
        }
    }


    public  void findByLIST_POS(int LIST_POS) {
        lv.setSelection(LIST_POS);
        lv.smoothScrollToPosition(LIST_POS);
    }


}
