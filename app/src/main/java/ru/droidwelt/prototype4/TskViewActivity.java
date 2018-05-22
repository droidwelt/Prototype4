package ru.droidwelt.prototype4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class TskViewActivity extends AppCompatActivity implements OnClickListener {

    private static int ID_REC = -1;
    private static String _tsk_ok = "", _tsk_comment = "";
    private  Button tv_save;
    public  TextView tv_tsk_ok;
    private  TextView tv_tsk_srok;
    private  TextView tv_tsk_ready;
    private  TextView tv_tsk_content;
    private   EditText et_comment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemes(this);
        setContentView(R.layout.activity_tskview);
        setTitle(getString(R.string.header_tskview));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        ID_REC = extras != null ? extras.getInt("TSK_ID") : 0;

        ImageButton btn_choice_state = findViewById(R.id.button_tskview_choicestate);
        btn_choice_state.setOnClickListener(this);
        tv_save = findViewById(R.id.button_tskview_save);
        tv_save.setOnClickListener(this);

        tv_tsk_ok = findViewById(R.id.tskview_textView_tsk_ok);
        tv_tsk_srok = findViewById(R.id.tskview_textView_tsk_srok);
        tv_tsk_ready = findViewById(R.id.tskview_textView_tsk_ready);
        tv_tsk_content = findViewById(R.id.tskview_textView_tsk_content);

        et_comment = findViewById(R.id.tskview_textView_tsk_comment);
        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                control_btn_state();
            }
        });

        Appl.getMSSQLConnectionSettings();
        Appl.ShowProgressIndicatior(this);
        new MyTaskRead(this).execute();
    }


    private static class MyTaskRead extends AsyncTask<Void, Void, JSONArray> {
        private WeakReference<TskViewActivity> activityReference;
        MyTaskRead(TskViewActivity context) { activityReference = new WeakReference<>(context);
        }

        @Override
        protected JSONArray  doInBackground(Void... params) {
            String SExec = "TSK_ONE " + ID_REC;
            return Appl.query_MSSQL_Simple (SExec);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                Appl.Indicator.dismiss();
            } catch (Exception ignored) {
            }
            TskViewActivity activity = activityReference.get();
            if (activity == null) return;

            if (result.toString().contains("######")) {
                Appl.DisplayToastError(result.toString());
            } else {
                JSONObject u = null;
                try {
                    u = new JSONObject();
                    u = result.getJSONObject(0);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                try {
                    activity.tv_tsk_srok.setText(u.getString("TSK_SROK"));
                    activity.tv_tsk_ready.setText(u.getString("TSK_READY"));
                    activity.tv_tsk_content.setText(u.getString("TSK_CONTENT"));
                    _tsk_ok = u.getString("TSK_OK");
                    _tsk_comment = u.getString("TSK_COMMENT");
                    activity.tv_tsk_ok.setText(_tsk_ok);
                    activity.et_comment.setText(_tsk_comment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // домой
                Exit_Process();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_tskview_choicestate:
                Intent viewTsk = new Intent(TskViewActivity.this,
                        TskStateActivity.class);
                viewTsk.putExtra("outText", getTskState());
                startActivityForResult(viewTsk,1);
                break;

            case R.id.button_tskview_save:
                tsk_save_result();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String ans = data.getStringExtra("ANS");
                tv_tsk_ok.setText(ans);
                control_btn_state();
            }
        }
    }


    public  String getTskState() {
        return tv_tsk_ok.getText().toString();
    }

    public  boolean is_record_modified() {
        String s_ok = tv_tsk_ok.getText().toString();
        String s_co = et_comment.getText().toString();
        return ((s_ok.compareTo(_tsk_ok) != 0) | (s_co.compareTo(_tsk_comment) != 0));
    }

    public  void control_btn_state() {
       tv_save.setEnabled(is_record_modified());
    }

    public void Exit_Process() {
        if (is_record_modified()) {
            AlertDialog.Builder ad;
            ad = new android.app.AlertDialog.Builder(this);
            ad.setTitle(getString(R.string.dlg_confirm_req));
            ad.setMessage(getString(R.string.dlg_date_changed));

            android.content.DialogInterface.OnClickListener l_nos = new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(-1);
                    finish();
                }
            };
            ad.setNegativeButton(getString(R.string.dlg_prg_no), l_nos);

            android.content.DialogInterface.OnClickListener l_yes = new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tsk_save_result();
                }
            };
            ad.setPositiveButton(getString(R.string.dlg_prg_yes), l_yes);
            ad.create();
            ad.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Exit_Process();
    }


    public void tsk_save_result() {
        Appl.getMSSQLConnectionSettings();
        Appl.ShowProgressIndicatior(this);
        String x_OK = tv_tsk_ok.getText().toString();
        String x_COMMENT = et_comment.getText().toString();
        String x_SQL = String.valueOf(ID_REC) + ",'" + x_OK + "','" + x_COMMENT + "'";
        Appl.SExec = "TSK_UPDATE " + x_SQL;
        new MyTaskWrite(this).execute();
    }

    private static class MyTaskWrite extends AsyncTask<Void, Void, JSONArray> {
        private WeakReference<TskViewActivity> activityReference;
        MyTaskWrite(TskViewActivity context) { activityReference = new WeakReference<>(context);
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            return Appl.query_MSSQL_Simple (Appl.SExec);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                Appl.Indicator.dismiss();
            } catch (Exception ignored) {
            }
            TskViewActivity activity = activityReference.get();
            if (activity == null) return;

            if (result.toString().contains("######")) {
                Appl.DisplayToastError(result.toString());
            } else {
                _tsk_ok = activity.tv_tsk_ok.getText().toString();
                _tsk_comment = activity.et_comment.getText().toString();
                activity.control_btn_state();
                activity.setResult(ID_REC);
                activity.finish();
            }

        }
    }

}
