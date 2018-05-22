package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import static ru.droidwelt.prototype4.Appl.context;

public class ClientViewActivity extends AppCompatActivity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static int CLT_ID = -1;
    public ListView lv;
    private static int LIST_POS = -1;
    OnSwipeTouchListener onSwipeTouchListener;
    private  ClientViewCursorAdapter myAdapter;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemes(this);
        setContentView(R.layout.activity_cltview);
        setTitle(getString(R.string.header_cltview));
        Bundle extras = getIntent().getExtras();
        CLT_ID = extras != null ? extras.getInt("CLT_ID") : 0;
        LIST_POS = extras != null ? extras.getInt("POS") : 0;

        String[] from = new String[]{"OPP_OPER", "OPP_DATE", "OPP_NAME", "OPP_SUM"};
        int[] to = new int[]{R.id.text_cltview_OPP_OPER,
                R.id.text_cltview_OPP_DATE,
                R.id.text_cltview_OPP_NAME,
                R.id.text_cltview_OPP_SUM};


        setmyAdapter(new ClientViewCursorAdapter(this, R.layout.activity_cltview_item, null, from, to));
        lv = findViewById(R.id.list);
        lv.setAdapter(getClientViewAdapter());
        getLoaderManager().initLoader(0, null, this);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        operClientData();

        creareOnSwipeTouchListener();
        LinearLayout ly_cltView = findViewById(R.id.ly_cltView);
        ly_cltView.setOnTouchListener(onSwipeTouchListener);

        ly_cltView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //  Toast.makeText(ClientViewActivity.this, "Click", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void creareOnSwipeTouchListener() {
        onSwipeTouchListener = new OnSwipeTouchListener(ClientViewActivity.this) {
            public void onSwipeTop() {
                // Toast.makeText(ClientViewActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                //  Toast.makeText(ClientViewActivity.this, "right", Toast.LENGTH_SHORT).show();
                moveToPrev();
            }

            public void onSwipeLeft() {
                //   Toast.makeText(ClientViewActivity.this, "left", Toast.LENGTH_SHORT).show();
                moveToNext();
            }

            public void onSwipeBottom() {
                //  Toast.makeText(ClientViewActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

            public void onClick() {
              //  Toast.makeText(ClientViewActivity.this, "Click", Toast.LENGTH_SHORT).show();
            }
        };
    }


    public void moveToNext() {
      //  WeakReference<ClientActivity> activity = new WeakReference<ClientActivity>(new ClientActivity());
        if (LIST_POS < ClientActivity.LIST_CLT_ID.size()) {
            LIST_POS = LIST_POS + 1;
            CLT_ID = ClientActivity.LIST_CLT_ID.get(LIST_POS);
            operClientData();
        }
    }

    public void moveToPrev() {
        if (LIST_POS > 0) {
            LIST_POS = LIST_POS - 1;

            CLT_ID = ClientActivity.LIST_CLT_ID.get(LIST_POS);
         //   WeakReference<ClientActivity> activity = new WeakReference<ClientActivity>(new ClientActivity());
         //   CLT_ID = activity.get().LIST_CLT_ID.get(LIST_POS);
            operClientData();
        }
    }

    public void operClientData() {
        dipslay_client_Header();
        getClientViewAdapter().changeCursor(getClientViewRecords());
    }


    public void dipslay_client_Header() {
        String sSQL = "select * from CLT where CLT_ID=" + CLT_ID;
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            try {
                TextView tv_clt_name = findViewById(R.id.et_cltview_clt_name);
                tv_clt_name.setText(c.getString(c.getColumnIndex("CLT_NAME")));
                TextView tv_clt_addr = findViewById(R.id.et_cltview_clt_addr);
                tv_clt_addr.setText(c.getString(c.getColumnIndex("CLT_ADDR")));
                TextView tv_clt_phone = findViewById(R.id.et_cltview_clt_phone);
                tv_clt_phone.setText(c.getString(c.getColumnIndex("CLT_PHONE")));
                TextView tv_clt_saldo = findViewById(R.id.et_cltview_clt_saldo);
                double plus = c.getDouble(c.getColumnIndex("CLT_AVANS"));
                double minus = c.getDouble(c.getColumnIndex("CLT_DOLG"));
                String sSaldo = "" + (plus - minus);
                tv_clt_saldo.setText(sSaldo);
                setTitle(tv_clt_name.getText());
            } catch (Exception ignored) {
            }
        } else
            setTitle("??");
        c.close();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // домой
                ExitActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ExitActivity();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void ExitActivity() {
        Intent answerIntent = new Intent();
        answerIntent.putExtra("POS", LIST_POS);
        setResult(RESULT_OK, answerIntent);
        finish();
        Appl.animateFinish(ClientViewActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ClientViewActivity.MyCursorLoader(this);
    }


    private static class MyCursorLoader extends CursorLoader {
        MyCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return getClientViewRecords();
        }
    }

    public static Cursor getClientViewRecords() {
        String sSQL =
                "select  _id, OPP_OPER, OPP_DATE, OPP_NAME, OPP_SUM"
                        + " from OPP "
                        + " where CLT_ID=" + CLT_ID
                        + " order by OPP_DATE ";
        return Appl.getDatabase().rawQuery(sSQL, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        getClientViewAdapter().swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public  ClientViewCursorAdapter getClientViewAdapter() {
        return myAdapter;
    }

    public  void setmyAdapter(ClientViewCursorAdapter operAdapter) {
        myAdapter = operAdapter;
    }

}
