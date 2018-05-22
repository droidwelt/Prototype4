package ru.droidwelt.prototype4;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChoiceFioActivity extends AppCompatActivity {

    private static TextView tv_choicefio_sendlist;
    static List<ChoiceFioStructure> list_fio;
    LinearLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    static RecyclerView.Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choicefio);
        Appl.setMyThemesDlg(this);
        setTitle(getString(R.string.s_message_choice_template_fio));
        ImageButton ib_choicefio_exit = findViewById(R.id.ib_choicefio_exit);
        ib_choicefio_exit.setOnClickListener(oclBtnOk);
        ImageButton ib_choicefio_refresh = findViewById(R.id.ib_choicefio_refresh);
        ib_choicefio_refresh.setOnClickListener(oclBtnOk);
        tv_choicefio_sendlist = findViewById(R.id.tv_choicefio_sendlist);
        tv_choicefio_sendlist.setText(MsaUtils.message_get_send_list());

        list_fio = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_choicefio);
        mRecyclerView.setHapticFeedbackEnabled(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getFioRecords();
        mAdapter = new ChoiceFioRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }


    public static void setFilChecked(String FIO_ID, String FIO_CH) {
        String sSQL;
        if (FIO_CH.equals(""))
            FIO_CH = "1";
        else
            FIO_CH = "";
        if (FIO_CH.equals("1"))
            sSQL = "insert into MSB (MSA_ID,FIO_ID) values ('" + Appl.MSA_ID + "'," + FIO_ID + ");";
        else
            sSQL = "delete from MSB  where MSA_ID='" + Appl.MSA_ID + "' and FIO_ID=" + FIO_ID + " ;";
        Appl.getDatabase().execSQL(sSQL);
        getFioRecords();
        mAdapter.notifyDataSetChanged();
        MsaEditActivity.display_send_list();
        tv_choicefio_sendlist.setText(MsaUtils.message_get_send_list());
    }


    OnClickListener oclBtnOk = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.ib_choicefio_exit:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;

                case R.id.ib_choicefio_refresh:
                    if ((Appl.checkConnectToServer(true)) && (MsaUtils.loadFIO()) && (MsaUtils.loadDEV())) {
                        Appl.loadFioArray();
                        getFioRecords();
                        mAdapter.notifyDataSetChanged();
                    }
                    break;

                default:
                    break;
            }
        }
    };


    public static void getFioRecords() {
        String sSQL = "select F._id as _id,F.FIO_ID,F.FIO_TP," +
                " FIO_NAME,FIO_IMAGE,FIO_TP, FIO_SUBNAME, " +
                " case when (select count(*) from MSB B " +
                " where B.MSA_ID='" + Appl.MSA_ID + "' and B.FIO_ID=F.FIO_ID)>0 " +
                " then '1' else '' end as FIO_CH " +
                " from FIO F" +
                " where FIO_TP in (1,2)" +
                "order by FIO_TP,FIO_NAME";
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);

        list_fio.clear();
        c.moveToFirst();
        int index_FIO_ID = c.getColumnIndex("FIO_ID");
        int index_FIO_NAME = c.getColumnIndex("FIO_NAME");
        int index_FIO_SUBNAME = c.getColumnIndex("FIO_SUBNAME");
        int index_FIO_CH = c.getColumnIndex("FIO_CH");
        int index_FIO_TP = c.getColumnIndex("FIO_TP");
        int index_FIO_IMAGE = c.getColumnIndex("FIO_IMAGE");

        while (!c.isAfterLast()) {
            ChoiceFioStructure mes = new ChoiceFioStructure();
            mes.fio_id = c.getString(index_FIO_ID);
            mes.fio_name = c.getString(index_FIO_NAME);
            mes.fio_subname = c.getString(index_FIO_SUBNAME);
            mes.fio_tp = c.getInt(index_FIO_TP);
            mes.fio_choice = c.getString(index_FIO_CH);
            byte[] fio_img = c.getBlob(index_FIO_IMAGE);
            if (fio_img != null)
                mes.fio_img = BitmapFactory.decodeByteArray(fio_img, 0, fio_img.length);

            list_fio.add(mes);
            c.moveToNext();
        }
        c.close();

    }


}