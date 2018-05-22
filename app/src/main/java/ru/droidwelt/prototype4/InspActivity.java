package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

public class InspActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static InspActivity instance;
    private static ListView lv;
    private  InspCursorAdapter myAdapter;
    public static String filter = "";
    private int find_id = -1;

    public static synchronized InspActivity getInstance() {
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesNoActionBar(this);
        instance = this;
        setContentView(R.layout.activity_inspect);

        Toolbar tb_insp = findViewById(R.id.tb_insp);
        setSupportActionBar(tb_insp);
        ImageButton ib_insp_mainmenu = findViewById(R.id.ib_insp_mainmenu);
        ib_insp_mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewMenu = new Intent(InspActivity.this, ChoiceMemuActivity.class);
                viewMenu.putExtra("MODE", "INS");
                startActivity(viewMenu);
            }
        });

        displayLoadDateTime();

        String[] from = new String[]{"RVV_NAME", "RVV_KVO1", "RVV_KVO2"};
        int[] to = new int[]{R.id.textView_item_RVV_NAME, R.id.textView_item_RVV_KVO1, R.id.textView_item_RVV_KVO2};

        setmyAdapter(new InspCursorAdapter(this, R.layout.activity_inspect_item, null, from, to));
        lv = findViewById(R.id.lv_inspect);
        lv.setOnItemClickListener(viewInspectListener);
        lv.setOnItemLongClickListener(viewInspestLongListener);
        lv.setAdapter(getInspectAdapter());

        getLoaderManager().initLoader(0, null, this);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        FloatingActionButton ins_add_fab = findViewById(R.id.ins_add_fab);
        ins_add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editrec = new Intent(InspActivity.this, InspInputActivity.class);
                editrec.putExtra("_id", -1);
                startActivityForResult(editrec, 999);
            }
        });

        FloatingActionButton ins_scanner_fab = findViewById(R.id.ins_scanner_fab);
        ins_scanner_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                // This flag clears the called app from the activity stack, so users arrive in the expected
                // place next time this application is restarted.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                // intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });

        EditText editFilter = findViewById(R.id.editText_inspect_Filter);
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
                getInspectAdapter().changeCursor(getInspectRecords(filter));
                getInspectAdapter().notifyDataSetChanged();
            }
        });

    }

    OnItemClickListener viewInspectListener = new OnItemClickListener() {
        // посмотр записи
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            Intent editrec = new Intent(InspActivity.this, InspInputActivity.class);
            editrec.putExtra("_id", (int) (id));
            startActivityForResult(editrec, 999);
        }
    };

    OnItemLongClickListener viewInspestLongListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v,
                                       int position, long id) {
            int indexst = getInspectAdapter().getCursor().getColumnIndex("RVV_STATE");
            String state = getInspectAdapter().getCursor().getString(indexst);

            if (state.contains("N")) {
                final long id_delete = id;
                new AlertDialog.Builder(InspActivity.this)
                        .setTitle(getString(R.string.dlg_confirm_req))
                        .setMessage(
                                getString(R.string.s_inspest_delete_confirm))
                        .setNegativeButton((getString(R.string.dlg_prg_no)), null)
                        .setPositiveButton((getString(R.string.dlg_prg_yes)),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String sLite = "DELETE FROM RVV WHERE _id=" + id_delete;
                                        Appl.getDatabase().execSQL(sLite);
                                        refreshRecords();
                                    }

                                }).create().show();
            } else {
                int index = getInspectAdapter().getCursor().getColumnIndex("RVV_KVO2");
                String kvo2str = getInspectAdapter().getCursor().getString(index);
                if (kvo2str.isEmpty()) {
                    String sLite = "UPDATE RVV set RVV_KVO2=RVV_KVO1,RVV_STATE='V' where _id= " + id;
                    //Log.i("LOG", "onItemClick sLite=" + sLite);
                    Appl.getDatabase().execSQL(sLite);
                    refreshRecords();
                }
            }
            return true;
        }
    };


    public  void refreshRecords() {
        getInspectAdapter().changeCursor(getInspectRecords(filter));
        getInspectAdapter().notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inspect, menu);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Appl.setLast_scanned_code(contents);
                findRecordByScanCode(Appl.getLast_scanned_code());
                if (find_id == -1)
                    Appl.DisplayToastInfo(getString(R.string.s_tovar_not_found), R.mipmap.ic_fab_scanner, Toast.LENGTH_LONG);
            } /*else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }*/
        }
    }



/*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Appl.CONT_SCANNER: // из сканера
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (scanResult != null) {
                        Appl.setLast_scanned_code(scanResult.getContents());
                        findRecordByScanCode(Appl.getLast_scanned_code());
                        if (find_id == -1)
                            Appl.DisplayToastInfo(getString(R.string.s_tovar_not_found), R.mipmap.ic_scanner, Toast.LENGTH_LONG);
                    }
                    break;
            }
        }
    }*/

    @SuppressLint("ResourceAsColor")
    public void findRecordByScanCode(String scan_code) {
        //  Appl.DisplayToastOk(scan_code);
        int index = getInspectAdapter().getCursor().getColumnIndex("RVV_BAR");
        find_id = -1;
        boolean b_find = false;
        int pos = 0;

        if (getInspectAdapter().getCursor().moveToFirst()) {
            String str = getInspectAdapter().getCursor().getString(index);
            if ((str != null) & (scan_code.equals(str))) {
                b_find = true;
                find_id = getInspectAdapter().getCursor().getInt(0);

            } else {
                while (getInspectAdapter().getCursor().moveToNext() & !b_find) {
                    pos = pos + 1;
                    str = getInspectAdapter().getCursor().getString(index);
                    if ((str != null) & (scan_code.equals(str))) {
                        b_find = true;
                        find_id = getInspectAdapter().getCursor().getInt(0);
                    }
                }
            }
        }
        if (b_find) {
            //   Appl.DisplayToastOk(""+pos);
            lv.setItemChecked(pos, true);
            lv.smoothScrollToPosition(pos);
            getInspectAdapter().getItemId(pos);
            getInspectAdapter().getCursor().moveToPosition(pos);
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
            return getInspectRecords(filter);
        }
    }


    public static Cursor getInspectRecords(String filter) {
        String sSQL =
                "select  _id, RVV_ID,RVV_NAME,RVV_CENA,RVV_KVO1,RVV_KVO1,RVV_KVO2,RVV_COMMENT,RVV_BAR,RVV_DATEREV,RVV_STATE "
                        + "from RVV "
                        + "where RVV_NAME like '%" + filter + "%' "
                        + "order by RVV_NAME ";
        return Appl.getDatabase().rawQuery(sSQL, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        getInspectAdapter().swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public  InspCursorAdapter getInspectAdapter() {
        return myAdapter;
    }

    public  void setmyAdapter(InspCursorAdapter inspectAdapter) {
        myAdapter = inspectAdapter;
    }


    private void displayLoadDateTime() {
        TextView tv = findViewById(R.id.textView_InspectLastLoad);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sdate = getString(R.string.s_lastload) + sp.getString("ins_dateload", getString(R.string.s_time_not_defined));
        tv.setText(sdate);
    }

    private void setLoadDateTime() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sdate = DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString();
        Editor editor = sp.edit();
        editor.putString("ins_dateload", sdate);
        editor.apply();
    }


    public void saveRvv() {
        String sSQL = "select  RVV_ID,RVV_NAME,RVV_CENA,RVV_KVO1,RVV_KVO1,RVV_KVO2,RVV_COMMENT,RVV_BAR,RVV_DATEREV,RVV_STATE "
                + "from RVV where RVV_STATE='N' or RVV_STATE='V' ";

        Cursor mc = Appl.getDatabase().rawQuery(sSQL, null);
        int i_name = mc.getColumnIndex("RVV_NAME");
        int i_kvo2 = mc.getColumnIndex("RVV_KVO2");
        int i_comment = mc.getColumnIndex("RVV_COMMENT");
        int i_bar = mc.getColumnIndex("RVV_BAR");
        int i_rvvid = mc.getColumnIndex("RVV_ID");

        String eExec = "";  // INSERT
        int n_rec_processed = 0;
        mc.moveToFirst();
        for (int i = 0; i < mc.getCount(); i++) {
            if (mc.getString(mc.getColumnIndex("RVV_STATE")).equals("N")) {
                n_rec_processed++;
                eExec = eExec + " insert into dbo.RVV (RVV_NAME,RVV_KVO1,RVV_COMMENT,RVV_BAR) values " +
                        "('" + mc.getString(i_name) + "','" + mc.getString(i_kvo2) + "','" + mc.getString(i_comment) + "','" + mc.getString(i_bar) + "')  ";
            }
            mc.moveToNext();
        }
        if (n_rec_processed > 0) {
            eExec = eExec + " select 1 as RES";
            Appl.query_MSSQL_Background(eExec);
        }

        eExec = "";   // UPDATE
        n_rec_processed = 0;
        mc.moveToFirst();
        for (int i = 0; i < mc.getCount(); i++) {
            if (mc.getString(mc.getColumnIndex("RVV_STATE")).equals("V")) {
                n_rec_processed++;
                eExec = eExec + " update dbo.RVV set RVV_KVO1='" + mc.getString(i_kvo2) +
                        "',RVV_COMMENT='" + mc.getString(i_comment) +
                        "',RVV_BAR='" + mc.getString(i_bar) +
                        "' where RVV_ID=" + mc.getString(i_rvvid);
            }
            mc.moveToNext();
        }
        if (n_rec_processed > 0) {
            eExec = eExec + " select 1 as RES";
            Appl.query_MSSQL_Background(eExec);
        }

        mc.close();
    }


    public void loadRvv() {
        JSONArray urls = Appl.query_MSSQL_Background("RVV_LIST");
        if (!urls.toString().contains("######")) {
            try {
                for (int i = 0; i < urls.length(); i++) {
                    String RVV_ID = urls.getJSONObject(i).getString("RVV_ID");
                    String RVV_NAME = Appl.strnormalize(urls.getJSONObject(i).getString("RVV_NAME"));
                    String RVV_CENA = Appl.strnormalize(urls.getJSONObject(i).getString("RVV_CENA"));
                    String RVV_KVO1 = Appl.strnormalize(urls.getJSONObject(i).getString("RVV_KVO1"));
                    String RVV_KVO2 = Appl.strnormalize(urls.getJSONObject(i).getString("RVV_KVO2"));
                    String RVV_COMMENT = Appl.strnormalize(urls.getJSONObject(i).getString("RVV_COMMENT"));
                    String RVV_BAR = Appl.strnormalize(urls.getJSONObject(i).getString("RVV_BAR"));
                    String RVV_DATEREV = Appl.strnormalize(urls.getJSONObject(i).getString("RVV_DATEREV"));
                    String sLite =
                            "INSERT INTO RVV  (RVV_ID, RVV_NAME, RVV_CENA, RVV_KVO1,RVV_KVO2,RVV_COMMENT,RVV_BAR,RVV_DATEREV,RVV_STATE) " +
                                    "VALUES ('" + RVV_ID + "','" + RVV_NAME + "','" + RVV_CENA + "','" + RVV_KVO1 + "','" + RVV_KVO2 +
                                    "','" + RVV_COMMENT + "','" + RVV_BAR + "','" + RVV_DATEREV + "','');";
                    Appl.getDatabase().execSQL(sLite);
                }

            } catch (JSONException e) {
                Appl.DisplayToastError(R.string.s_bad_data);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: // домой
                finish();
                Appl.animateFinish(InspActivity.this);
                return true;

            case R.id.mi_inspect_transfer:
                if (Appl.checkConnectToServer(true)) {
                    Appl.Indicator = new ProgressDialog(this);
                    Appl.Indicator.setMessage(this.getResources().getString(R.string.s_wait));
                    Appl.Indicator.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // STYLE_HORIZONTAL
                    Appl.Indicator.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            saveRvv();
                            Appl.getDatabase().execSQL("delete from RVV;");
                            loadRvv();
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                            Appl.Indicator.dismiss();
                        }
                    }).start();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getInspectAdapter().changeCursor(getInspectRecords(filter));
            setLoadDateTime();
            displayLoadDateTime();
            MainActivity.getInstance().refreshMainMenu();
        }
    };



}
