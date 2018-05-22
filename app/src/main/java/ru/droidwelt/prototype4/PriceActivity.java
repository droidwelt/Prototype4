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
import android.view.MenuInflater;
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
import java.util.Date;

public class PriceActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private PriceCursorAdapter myAdapter;
    public static String filter = "";


    private void displayLoadDateTime() {
        TextView tv = findViewById(R.id.tv_PriceLastLoad);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "обновлен " + sp.getString("prn_dateload", getString(R.string.s_time_not_defined));
        tv.setText(s);
    }

    private void setLoadDateTime() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sdate = DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString();
        Editor editor = sp.edit();
        editor.putString("prn_dateload", sdate);
        editor.apply();
    }

    public void loadPrn() {
        JSONArray urls = Appl.query_MSSQL_Simple("PRN_LIST");
        if (!urls.toString().contains("######")) {
            try {
                Appl.getDatabase().execSQL("DELETE FROM PRN");
                for (int i = 0; i < urls.length(); i++) {
                    String PRN_ID = urls.getJSONObject(i).getString("PRN_ID");
                    String PRN_NAME = Appl.strnormalize(urls.getJSONObject(i).getString("PRN_NAME"));
                    String PRN_CENA = Appl.strnormalize(urls.getJSONObject(i).getString("PRN_CENA"));
                    String PRN_RES = Appl.strnormalize(urls.getJSONObject(i).getString("PRN_RES"));
                    String PRN_OST = Appl.strnormalize(urls.getJSONObject(i).getString("PRN_OST"));
                    String sLite =
                            "INSERT INTO PRN  (PRN_ID, PRN_NAME, PRN_CENA, PRN_RES,PRN_OST) " +
                                    "VALUES ('" + PRN_ID + "','" + PRN_NAME + "','" + PRN_CENA + "','" + PRN_RES + "','" + PRN_OST + "');";
                    Appl.getDatabase().execSQL(sLite);
                }

            } catch (JSONException e) {
                Appl.DisplayToastError(R.string.s_bad_data);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesNoActionBar(this);
        setContentView(R.layout.activity_price);
        displayLoadDateTime();

        Toolbar tb_clt = findViewById(R.id.tb_clt);
        setSupportActionBar(tb_clt);
        ImageButton ib_clt_mainmenu = findViewById(R.id.ib_clt_mainmenu);
        ib_clt_mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewMenu = new Intent(PriceActivity.this, ChoiceMemuActivity.class);
                viewMenu.putExtra("MODE", "PRS");
                startActivity(viewMenu);
            }
        });

        String[] from = new String[]{"PRN_NAME", "PRN_CENA", "PRN_RES", "PRN_OST"};
        int[] to = new int[]{R.id.textView_item_PRN_NAME, R.id.textView_item_PRN_CENA, R.id.textView_item_PRN_RES, R.id.textView_item_PRN_OST};

        setmyAdapter(new PriceCursorAdapter(this, R.layout.activity_price_item, null, from, to));
        ListView lv = findViewById(R.id.lv_price);
        lv.setOnItemClickListener(viewPriceListener);
        lv.setAdapter(getPriceAdapter());

        getLoaderManager().initLoader(0, null, this);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        EditText editFilter = findViewById(R.id.editText_price_Filter);
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
                getPriceAdapter().changeCursor(getPriceRecords(filter));
                getPriceAdapter().notifyDataSetChanged();
            }
        });

    }

    // слушатель событий в ListView
    OnItemClickListener viewPriceListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_price, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                Appl.animateFinish(PriceActivity.this);
                return true;

            case R.id.mi_price_download:
                if (Appl.checkConnectToServer(true)) {
                    Appl.ShowProgressIndicatior(this);
                    new MyTaskPrice(this).execute();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
 

    private static class MyTaskPrice extends AsyncTask<Void, Void, String> {
        private WeakReference<PriceActivity> activityReference;
        MyTaskPrice(PriceActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Void... params) {
            PriceActivity activity = activityReference.get();
            activity.loadPrn();
            return "END";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Appl.Indicator.dismiss();
            } catch (Exception ignored) {
            }
            PriceActivity activity = activityReference.get();
            if (activity == null) return;

            activity.getPriceAdapter().changeCursor(getPriceRecords(filter));
            activity.setLoadDateTime();
            activity.displayLoadDateTime();
            MainActivity.getInstance().refreshMainMenu();
        }
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this);
    }


    private static class MyCursorLoader extends CursorLoader {
        MyCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return getPriceRecords(filter);
        }
    }


    public static Cursor getPriceRecords(String filter) {
        String sSQL =
                "select  _id, PRN_ID,PRN_NAME,PRN_CENA,PRN_OST,PRN_RES "
                        + "from PRN "
                        + "where PRN_NAME like '%" + filter + "%' "
                        + "order by PRN_NAME ";
        return Appl.getDatabase().rawQuery(sSQL, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        getPriceAdapter().swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public PriceCursorAdapter getPriceAdapter() {
        return myAdapter;
    }

    public void setmyAdapter(PriceCursorAdapter priceAdapter) {
        myAdapter = priceAdapter;
    }


}
