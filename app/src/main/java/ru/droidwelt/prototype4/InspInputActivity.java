package ru.droidwelt.prototype4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


public class InspInputActivity extends AppCompatActivity {


    private EditText et_name, et_kvo2, et_bar, et_comment;
    private static String s_name = "", s_kvo1 = "", s_kvo2 = "", s_bar = "", s_comment = "";
    private static int _id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemes(this);
        setContentView(R.layout.activity_inspinput);
        ImageButton ib_ok, ib_cancel, ib_scanner, ib_setkvo2;

        ib_ok = findViewById(R.id.ib_ok);
        ib_cancel = findViewById(R.id.ib_cancel);
        ib_scanner = findViewById(R.id.ib_scanner);
        ib_setkvo2 = findViewById(R.id.ib_setkvo2);
        et_name = findViewById(R.id.et_name);
        EditText et_kvo1 = findViewById(R.id.et_kvo1);
        et_kvo2 = findViewById(R.id.et_kvo2);
        et_bar = findViewById(R.id.et_bar);
        et_comment = findViewById(R.id.et_comment);
        Bundle extras = getIntent().getExtras();
        _id = extras != null ? extras.getInt("_id") : 0;

        if (_id >= 0) {
            setTitle(getString(R.string.header_inspect));
            int i_name = InspActivity.getInstance().getInspectAdapter().getCursor().getColumnIndex("RVV_NAME");
            int i_kvo1 = InspActivity.getInstance().getInspectAdapter().getCursor().getColumnIndex("RVV_KVO1");
            int i_kvo2 = InspActivity.getInstance().getInspectAdapter().getCursor().getColumnIndex("RVV_KVO2");
            int i_bar = InspActivity.getInstance().getInspectAdapter().getCursor().getColumnIndex("RVV_BAR");
            int i_comment = InspActivity.getInstance().getInspectAdapter().getCursor().getColumnIndex("RVV_COMMENT");
            s_name = InspActivity.getInstance().getInspectAdapter().getCursor().getString(i_name);
            s_kvo1 = InspActivity.getInstance().getInspectAdapter().getCursor().getString(i_kvo1);
            s_kvo2 = InspActivity.getInstance().getInspectAdapter().getCursor().getString(i_kvo2);
            s_bar = InspActivity.getInstance().getInspectAdapter().getCursor().getString(i_bar);
            s_comment = InspActivity.getInstance().getInspectAdapter().getCursor().getString(i_comment);
            et_name.setText(s_name);
            et_kvo1.setText(s_kvo1);
            et_kvo2.setText(s_kvo2);
            et_bar.setText(s_bar);
            et_comment.setText(s_comment);
        } else {
            //	Log.i("LOG", "InspInputActivity NEW");
            setTitle(getString(R.string.add_new));
            et_name.setTextColor(getResources().getColor(R.color.text_edit));
            et_bar.setTextColor(getResources().getColor(R.color.text_edit));
            ib_setkvo2.setEnabled(false);
        }

        ib_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        ib_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().isEmpty()) {
                    Appl.DisplayToastError(getString(R.string.s_insp_noname));
                } else {
                    s_kvo2 = et_kvo2.getText().toString();
                    s_comment = et_comment.getText().toString();
                    s_bar = et_bar.getText().toString();
                    s_name = et_name.getText().toString();
                    s_kvo2 = Appl.strnormalize(et_kvo2.getText().toString());
                    s_comment = Appl.strnormalize(et_comment.getText().toString());
                    s_bar = Appl.strnormalize(et_bar.getText().toString());
                    s_name = Appl.strnormalize(et_name.getText().toString());
                    if (_id >= 0) { // UPDATE
                        String sLite = "UPDATE RVV SET RVV_KVO2='" + s_kvo2
                                + "',RVV_COMMENT='" + s_comment
                                + "',RVV_BAR='" + s_bar
                                + "',RVV_STATE='V' where _id="
                                + _id;
                        Appl.getDatabase().execSQL(sLite);

                    } else {// INSERT
                        String sLite =
                                "INSERT INTO RVV  ( RVV_NAME,RVV_KVO2,RVV_COMMENT,RVV_BAR,RVV_STATE) " +
                                        "VALUES ('" + s_name + "','" + s_kvo2 + "','" + s_comment + "','" + s_bar + "','N');";
                        Appl.getDatabase().execSQL(sLite);
                    }
                    InspActivity.getInstance().refreshRecords();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        ib_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                // This flag clears the called app from the activity stack, so users arrive in the expected
                // place next time this application is restarted.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                // intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });

        ib_setkvo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_kvo2.setText(s_kvo1);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Appl.setLast_scanned_code(contents);
                if (contents != null) et_bar.setText(contents);
            }
        }
    }
}
