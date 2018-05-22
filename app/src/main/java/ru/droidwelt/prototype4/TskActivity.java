package ru.droidwelt.prototype4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TskActivity extends AppCompatActivity implements OnClickListener {

    private static final String TSK_OK = "TSK_OK";
    private static final String TSK_SROK = "TSK_SROK";
    private static final String TSK_READY = "TSK_READY";
    private static final String TSK_CONTENT = "TSK_CONTENT";
    private static final String TSK_COMMENT = "TSK_COMMENT";
    private static final String TSK_ID = "TSK_ID";
    private static String TSK_ViewMode = "1";
    public ListView lv;
    private JSONArray urlsPos;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesNoActionBar(this);
        setContentView(R.layout.activity_tsk);

        Toolbar tb_tsk = findViewById(R.id.tb_tsk);
        setSupportActionBar(tb_tsk);
        ImageButton ib_tsk_mainmenu = findViewById(R.id.ib_tsk_mainmenu);
        ib_tsk_mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewMenu = new Intent(TskActivity.this, ChoiceMemuActivity.class);
                viewMenu.putExtra("MODE", "TSK");
                startActivity(viewMenu);
            }
        });

        Button btn_Exec0 = findViewById(R.id.bt_tsk0);
        btn_Exec0.setOnClickListener(this);
        Button btn_Exec1 = findViewById(R.id.bt_tsk1);
        btn_Exec1.setOnClickListener(this);
        Button btn_Exec2 = findViewById(R.id.bt_tsk2);
        btn_Exec2.setOnClickListener(this);

        OnItemClickListener tskViewListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

               JSONObject u = null;
                try {
                    u = new JSONObject(urlsPos.getString(position));
                } catch (JSONException ignored) {
                }
                int id_rec = -1;
                try {
                    if (u != null) id_rec = Integer.parseInt(u.getString(TSK_ID));
                } catch (JSONException ignored) {
                }

                if (id_rec >= 0) {
                    Intent viewTsk = new Intent(TskActivity.this, TskViewActivity.class);
                    viewTsk.putExtra(TSK_ID, id_rec);
                    startActivityForResult(viewTsk, 0);
                    Appl.animateStart(TskActivity.this);
                }
            }
        };

        lv = findViewById(R.id.lv_tsk);
        lv.setOnItemClickListener(tskViewListener);

        if (Appl.getExecOnStart())
            Tsk_Exec_Querty("1");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 0) | (resultCode >= 0)) {
            Tsk_Exec_Querty(TSK_ViewMode);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_tsk0:
                Tsk_Exec_Querty("0");
                TSK_ViewMode = "0";
                break;
            case R.id.bt_tsk1:
                Tsk_Exec_Querty("1");
                TSK_ViewMode = "1";
                break;
            case R.id.bt_tsk2:
                Tsk_Exec_Querty("2");
                TSK_ViewMode = "2";
                break;

            default:
                break;
        }
    }

    public void Tsk_Exec_Querty(String mode) {
        if (Appl.checkConnectToServer(true)) {
            Appl.getMSSQLConnectionSettings();
            Appl.ShowProgressIndicatior(this);
            Appl.SExec = "TSK_LISTFLTR " + Appl.FIO_ID + "," + mode;
            urlsPos = Appl.query_MSSQL_Background(Appl.SExec);
            JSONURL(urlsPos);
            try {
                Appl.Indicator.dismiss();
            } catch (Exception ignored) {
            }
        }
    }


    public void JSONURL(JSONArray urls) {
        try {
            ArrayList<HashMap<String, Object>> myBooks = new ArrayList<>();
            for (int i = 0; i < urls.length(); i++) {
                HashMap<String, Object> hm;
                hm = new HashMap<>();
                hm.put(TSK_OK, urls.getJSONObject(i).getString(TSK_OK));
                hm.put(TSK_SROK, urls.getJSONObject(i).getString(TSK_SROK));
                hm.put(TSK_READY, urls.getJSONObject(i).getString(TSK_READY));
                hm.put(TSK_CONTENT, urls.getJSONObject(i).getString(TSK_CONTENT));
                hm.put(TSK_COMMENT, urls.getJSONObject(i).getString(TSK_COMMENT));
                myBooks.add(hm);
            }

            String[] from = new String[]{TSK_OK, TSK_SROK, TSK_READY, TSK_CONTENT, TSK_COMMENT};
            int[] to = new int[]{R.id.text_TSK_OK, R.id.text_TSK_SROK, R.id.text_TSK_READY, R.id.text_TSK_CONTENT, R.id.text_TSK_COMMENT};

            TskSimpleAdapter adapter = new TskSimpleAdapter(TskActivity.this, myBooks, R.layout.activity_tsk_item, from, to);

            lv.setAdapter(adapter);
            lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        } catch (JSONException ignored) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // домой
                finish();
                Appl.animateFinish(TskActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
