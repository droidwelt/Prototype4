package ru.droidwelt.prototype4;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MsaMainActivity extends AppCompatActivity {


    final static int EXIT_CODE_EDIT = 10001;
    final static int EXIT_CODE_AVATAR = 10002;

    private static MsaMainActivity instance;
    static TextView tv_mes_record_count;
    static String myfilter = "";
    static ImageButton ib_msa_filter_lbl, ib_msa_filter_clear;
    static EditText et_msa_text_filter;
    //   GridLayoutManager mLayoutManager;
    LinearLayoutManager mLayoutManager;
    static List<MsaMainDataStructure> list_msa;
    static RecyclerView mRecyclerView;
    static RecyclerView.Adapter mAdapter;
    static SwipeRefreshLayout mSwipeRefreshLayout;

    MenuItem mi_msa_new, mi_msa_receive_all, mi_msa_send_all, mi_msa_delete_all, mi_mes_move_all_to_inbox, mi_mes_move_all_to_newbox;

    public static synchronized MsaMainActivity getInstance() {
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Appl.DisplayToastOk("MsaMainActivity");
        instance = this;
        Appl.setMyThemesNoActionBar(this);
        Appl.download_onfirstrecord = Appl.getDownloadOnFirstrecord();
        setContentView(R.layout.activity_msamain);

        LinearLayout ly_msa_rv = findViewById(R.id.ly_msa_rv);
        ly_msa_rv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ly_msa_rv.setBackgroundResource(R.drawable.bg_main1);
        ly_msa_rv.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        Appl.getMSSQLConnectionSettings();
        list_msa = new ArrayList<>();

        Toolbar tb_mainmsa = findViewById(R.id.tb_mainmsa);
        setSupportActionBar(tb_mainmsa);
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(Appl.getMyBarDrawable());
        ImageButton ib_msa_mainmenu = findViewById(R.id.ib_msa_mainmenu);
        ib_msa_mainmenu.setOnClickListener(onClickButtonListtiner);

        tv_mes_record_count = findViewById(R.id.tv_msa_record_count);
        ib_msa_filter_lbl = findViewById(R.id.ib_msa_filter_lbl);
        ib_msa_filter_lbl.setOnClickListener(onClickButtonListtiner);
        ib_msa_filter_lbl.setImageDrawable(Appl.getLabelDrawableByNomer(Appl.FILTER_LBL, 1));
        ib_msa_filter_clear = findViewById(R.id.ib_msa_filter_clear);
        ib_msa_filter_clear.setOnClickListener(onClickButtonListtiner);

        FloatingActionButton fab_msa_add = findViewById(R.id.fab_msa_add);
        if (Appl.getMesFabKey()) {
            fab_msa_add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNewMessage();
                }
            });
        } else {
            fab_msa_add.setX(10000);
        }

        Appl.getIMSA_MODE();
        Appl.loadFioArray();

        mRecyclerView = findViewById(R.id.rv_msamain);
        mRecyclerView.setHapticFeedbackEnabled(true);
        mLayoutManager = new LinearLayoutManager(this);
        //   mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MsaMainRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = findViewById(R.id.msa_swipe_container);
     /*   mSwipeRefreshLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mSwipeRefreshLayout.setBackgroundResource(R.drawable.bg_main1);
        mSwipeRefreshLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);*/
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        displayTitleOnBar();
        refreshRecords();
        if ((Appl.MSA_MODEVIEW == 2) & (mAdapter.getItemCount() == 0))
            execLoadRecord(true);

        et_msa_text_filter = findViewById(R.id.et_msa_text_filter);
        et_msa_text_filter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myfilter = (s.toString()).replace(" ", "");
                refreshRecords();
            }
        });

        verifyOldMessages();
        displayFilerLayout();
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(false);
            execLoadRecord(true);
        }
    };


    public static void execLoadRecord(boolean recordlimit) {
            Appl.download_in_progress = true;
            Appl.ShowProgressIndicatior (MsaMainActivity.getInstance());
            DB_ReceiveAll ar = new DB_ReceiveAll();
            ar.RecordsLimit = recordlimit;
            ar.execute();

    }

    public static void refreshRecords() {
        getMesRecords(myfilter);
        mAdapter.notifyDataSetChanged();
        if (mRecyclerView.getAdapter().getItemCount() > 0) {
            if (Appl.MSA_POS >= 0) {
                if (mRecyclerView.getAdapter().getItemCount() > Appl.MSA_POS)
                    mRecyclerView.scrollToPosition(Appl.MSA_POS);
                else
                    mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        }
        String s = "" + mAdapter.getItemCount();
        tv_mes_record_count.setText(s);
    }


    public static void getMesRecords(String filter) {
        String sFilterLBL = "", sFilterCLR = "";
        if (!Appl.FILTER_LBL.equals("")) sFilterLBL = " and MSA_LBL='" + Appl.FILTER_LBL + "' ";
        if (!Appl.FILTER_CLR.equals("")) sFilterCLR = " and MSA_CLR='" + Appl.FILTER_CLR + "' ";
        String sSQL;
        //  String sMode = "" + Appl.MSA_MODEVIEW;
        //  if (Appl.MSA_MODEVIEW == 22) sMode = "2,22";
        if (Appl.MSA_MODEVIEW != 0) {
            sSQL = "select  MSA_ID,MSA_TITLE,MSA_CLR,MSA_LBL,"
                    + "MSA_TEXT,A.FIO_ID,MSA_FILETYPE,MSA_FILENAME,"
                    + "FIO_NAME,MSA_DATE,length(MSA_IMAGE) as IMAGESIZE "
                    + "from MSA A "
                    + "left join FIO F on A.FIO_ID=F.FIO_ID "
                    + "where MSA_STATE=" + Appl.MSA_MODEVIEW
                    + " and ((MSA_TITLE like '%" + filter + "%') or (FIO_NAME like '%" + filter + "%')) "
                    + sFilterLBL + sFilterCLR
                    + "order by MSA_DATE desc ";
        } else {
            sSQL = "select MSA_ID,MSA_TITLE,MSA_CLR,MSA_LBL,"
                    + " MSA_TEXT,A.FIO_ID,MSA_FILETYPE,MSA_FILENAME,"
                    + " FIO_NAME,MSA_DATE,length(MSA_IMAGE) as IMAGESIZE "
                    + " from MSA A "
                    + " left join FIO F on A.FIO_ID=F.FIO_ID "
                    + " where MSA_STATE in (" + Appl.MSA_MODEVIEW + ") "
                    + " order by MSA_DATE desc ";
        }
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);

        list_msa.clear();
        c.moveToFirst();
        int index_MSA_ID = c.getColumnIndex("MSA_ID");
        int index_MSA_TITLE = c.getColumnIndex("MSA_TITLE");
        int index_MSA_CLR = c.getColumnIndex("MSA_CLR");
        int index_MSA_LBL = c.getColumnIndex("MSA_LBL");
        int index_MSA_TEXT = c.getColumnIndex("MSA_TEXT");
        int index_FIO_ID = c.getColumnIndex("FIO_ID");
        int index_MSA_FILETYPE = c.getColumnIndex("MSA_FILETYPE");
        int index_MSA_FILENAME = c.getColumnIndex("MSA_FILENAME");
        int index_FIO_NAME = c.getColumnIndex("FIO_NAME");
        int index_MSA_DATE = c.getColumnIndex("MSA_DATE");
        int index_IMAGESIZE = c.getColumnIndex("IMAGESIZE");
        while (!c.isAfterLast()) {
            MsaMainDataStructure mes = new MsaMainDataStructure();
            mes.msa_id = c.getString(index_MSA_ID);
            mes.msa_title = c.getString(index_MSA_TITLE);
            mes.msa_clr = c.getString(index_MSA_CLR);
            mes.msa_lbl = c.getString(index_MSA_LBL);
            mes.msa_text = c.getString(index_MSA_TEXT);
            mes.fio_id = c.getInt(index_FIO_ID);
            mes.msa_filetype = c.getString(index_MSA_FILETYPE);
            mes.msa_filename = c.getString(index_MSA_FILENAME);
            mes.fio_name = c.getString(index_FIO_NAME);
            mes.msa_date = c.getString(index_MSA_DATE);
            mes.imagesize = c.getInt(index_IMAGESIZE);
            list_msa.add(mes);
            c.moveToNext();
        }
        c.close();
    }


    OnClickListener onClickButtonListtiner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.ib_msa_filter_lbl:
                    Intent intentvfy = new Intent(MsaMainActivity.this, MsaFilterLblActivity.class);
                    startActivity(intentvfy);
                    break;

                case R.id.ib_msa_filter_clear:
                    et_msa_text_filter.setText("");
                    Appl.FILTER_LBL = "";
                    ib_msa_filter_lbl.setImageDrawable(Appl.getLabelDrawableByNomer(Appl.FILTER_LBL, 1));
                    refreshRecords();
                    break;

                case R.id.ib_msa_mainmenu:
                    Intent viewMenu = new Intent(MainActivity.getInstance(), ChoiceMemuActivity.class);
                    viewMenu.putExtra("MODE", "MES");
                    startActivity(viewMenu);
                    break;

                default:
                    break;
            }
        }
    };


    // подключение меню----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_msa, menu);
        mi_msa_new = menu.findItem(R.id.mi_mes_new);
        if (Appl.getMesFabKey()) mi_msa_new.setVisible(false);
        mi_msa_receive_all = menu.findItem(R.id.mi_mes_receive_all);
        mi_msa_send_all = menu.findItem(R.id.mi_mes_send_all);
        mi_msa_delete_all = menu.findItem(R.id.mi_mes_delete_all);
        mi_mes_move_all_to_inbox = menu.findItem(R.id.mi_mes_move_all_to_inbox);
        mi_mes_move_all_to_newbox = menu.findItem(R.id.mi_mes_move_all_to_newbox);
        set_menu_item_enabled();
        return true;
    }

    public void set_menu_item_enabled() {
        mi_msa_receive_all.setVisible(Appl.MSA_MODEVIEW == 2);
        mi_msa_send_all.setVisible(Appl.MSA_MODEVIEW == 4);
        mi_msa_delete_all.setVisible(true);
        mi_mes_move_all_to_inbox.setVisible(Appl.MSA_MODEVIEW == 2);
        mi_mes_move_all_to_newbox.setVisible(Appl.MSA_MODEVIEW == 22);
    }

    protected void displayTitleOnBar() {
        TextView tv_mes_title = findViewById(R.id.tv_msa_title);
        if (Appl.MSA_MODEVIEW == 0) {
            tv_mes_title.setText(getString(R.string.s_message_draft));
        } else if (Appl.MSA_MODEVIEW == 1) {
            tv_mes_title.setText(getString(R.string.s_message_tmpl));
        } else if (Appl.MSA_MODEVIEW == 2) {
            tv_mes_title.setText(getString(R.string.s_message_newbox));
        } else if (Appl.MSA_MODEVIEW == 22) {
            tv_mes_title.setText(getString(R.string.s_message_inbox));
        } else if (Appl.MSA_MODEVIEW == 3) {
            tv_mes_title.setText(getString(R.string.s_message_sentbox));
        } else if (Appl.MSA_MODEVIEW == 4) {
            tv_mes_title.setText(getString(R.string.s_message_outbox));
        } else if (Appl.MSA_MODEVIEW == 10) {
            tv_mes_title.setText(getString(R.string.s_message_favorbox));
        } else {
            tv_mes_title.setText(getString(R.string.s_message_box));
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(Appl.getMyBarDrawable());

        if (Appl.MSA_MODEVIEW == 2) mSwipeRefreshLayout.setEnabled(true);
        else mSwipeRefreshLayout.setEnabled(false);
    }

    public void createNewMessage() {
            Appl.MSA_MODEVIEW = 0;
            Appl.setIMSA_MODE();
            displayTitleOnBar();
            Appl.MSA_ID = Appl.generate_GUID();
            Appl.MSA_MODEEDIT = 0;
            MsaUtils.create_new_draft();
            refreshRecords();
            set_menu_item_enabled();
            displayFilerLayout();
            Intent mesCreate = new Intent(this, MsaEditActivity.class);
            startActivityForResult(mesCreate, EXIT_CODE_EDIT);
            Appl.animateStart(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // новое сообщение
            case R.id.mi_mes_new:
                createNewMessage();
                return true;

		/*	Типы в MSA_STATE:   ( СОСТОЯНИЕ )
               0  - черновик
		       1 -  шаблон
		       2 -  новые
		       22 -  входящие
		       3 -  отправленные
		       4 -  исходящие
		       5 -  удаленные*/

            //  исходящие
            case R.id.mi_mes_outbox:
                Appl.MSA_MODEVIEW = 4;
                Appl.setIMSA_MODE();
                displayTitleOnBar();
                refreshRecords();
                set_menu_item_enabled();
                displayFilerLayout();
                return true;

            //  отправленные
            case R.id.mi_mes_sentbox:
                Appl.MSA_MODEVIEW = 3;
                Appl.setIMSA_MODE();
                displayTitleOnBar();
                refreshRecords();
                set_menu_item_enabled();
                displayFilerLayout();
                return true;

            // новые
            case R.id.mi_mes_newbox:
                Appl.MSA_MODEVIEW = 2;
                Appl.setIMSA_MODE();
                displayTitleOnBar();
                refreshRecords();
                set_menu_item_enabled();
                displayFilerLayout();
                return true;

            // входящие
            case R.id.mi_mes_inbox:
                Appl.MSA_MODEVIEW = 22;
                Appl.setIMSA_MODE();
                displayTitleOnBar();
                refreshRecords();
                set_menu_item_enabled();
                displayFilerLayout();
                return true;

            // черновики
            case R.id.mi_mes_draft:
                Appl.MSA_MODEVIEW = 0;
                Appl.setIMSA_MODE();
                displayTitleOnBar();
                refreshRecords();
                set_menu_item_enabled();
                displayFilerLayout();
                return true;

            // избранное
            case R.id.mi_mes_favbox:
                Appl.MSA_MODEVIEW = 10;
                Appl.setIMSA_MODE();
                displayTitleOnBar();
                refreshRecords();
                set_menu_item_enabled();
                displayFilerLayout();
                return true;

            // послать все записи
            case R.id.mi_mes_send_all: {
                if (Appl.checkConnectToServer(true)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
                    builder.setTitle(getString(R.string.dlg_confirm_req));
                    builder.setMessage(getString(R.string.s_message_send_confirm_all));
                    builder.setNegativeButton(getString(R.string.dlg_prg_no), null);
                    builder.setPositiveButton((getString(R.string.dlg_prg_yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (Appl.checkConnectToServer(true)) {
                                        Appl.ShowProgressIndicatior(MsaMainActivity.this);
                                        DB_SendAll ar = new DB_SendAll();
                                        ar.execute();
                                    }
                                }

                            });
                    final AlertDialog dlg = builder.create();
                    dlg.show();
                }
            }
            return true;

            // удалить все записи
            case R.id.mi_mes_delete_all: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
                    builder.setTitle(getString(R.string.dlg_confirm_req));
                    builder.setMessage(getString(R.string.s_message_delete_confirm_all));
                    builder.setNegativeButton(getString(R.string.dlg_prg_no), null);
                    builder.setPositiveButton((getString(R.string.dlg_prg_yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   /* DB_DeleteAll ar = new DB_DeleteAll();
                                    ar.execute(); */
                                    Appl.Indicator = new ProgressDialog(MsaMainActivity.this);
                                    Appl.Indicator.setMessage(MsaMainActivity.this.getResources().getString(R.string.s_wait));
                                    Appl.Indicator.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // STYLE_HORIZONTAL
                                    Appl.Indicator.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String sSQL = "delete from MSA where MSA_STATE=" + Appl.MSA_MODEVIEW + " and MSA_TITLE like '%" + Appl.message_filter + "%' ;";
                                            Appl.getDatabase().execSQL(sSQL);
                                            Message msg = handlerDeleteAll.obtainMessage();
                                            handlerDeleteAll.sendMessage(msg);
                                            Appl.Indicator.dismiss();
                                        }
                                    }).start();
                                }

                            });
                    final AlertDialog dlg = builder.create();
                    dlg.show();
            }
            return true;

            // Переместить все во входящие
            case R.id.mi_mes_move_all_to_inbox: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
                    builder.setTitle(getString(R.string.dlg_confirm_req));
                    builder.setMessage(getString(R.string.s_message_move_all_to_inbox_confirm));
                    builder.setNegativeButton(getString(R.string.dlg_prg_no), null);
                    builder.setPositiveButton((getString(R.string.dlg_prg_yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MsaUtils.change_all_record_state_SQLite(2, 22);
                                    refreshRecords();
                                }
                            });
                    final AlertDialog dlg = builder.create();
                    dlg.show();
            }
            return true;

            // Переместить все в новын
            case R.id.mi_mes_move_all_to_newbox: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
                    builder.setTitle(getString(R.string.dlg_confirm_req));
                    builder.setMessage(getString(R.string.s_message_move_all_to_newbox_confirm));
                    builder.setNegativeButton(getString(R.string.dlg_prg_no), null);
                    builder.setPositiveButton((getString(R.string.dlg_prg_yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MsaUtils.change_all_record_state_SQLite(22, 2);
                                    refreshRecords();
                                }
                            });
                    final AlertDialog dlg = builder.create();
                    dlg.show();
            }
            return true;

            // принять все
            case R.id.mi_mes_receive_all:
                execLoadRecord(false);
                return true;

            // Аватар
            case R.id.mi_mes_my_avatar:
                if (Appl.checkConnectToServer(true)) {
                    Intent intentava = new Intent(MsaMainActivity.this, MsaAvatarActivity.class);
                    startActivityForResult(intentava, EXIT_CODE_AVATAR);
                }
                return true;

            // Обслуживание базы
            case R.id.mi_mes_verifydb:
                    Intent intentvfy = new Intent(MsaMainActivity.this, VerifyDbActivity.class);
                    startActivity(intentvfy);
                return true;

            // Настройки
            case R.id.mi_mes_my_setting:
                Appl.SETTING_MODE_GLOBAL = false;
                Intent intentsetting = new Intent(MsaMainActivity.this, PrefActivity.class);
                startActivity(intentsetting);
                Appl.animateStart(MsaMainActivity.this);
                return true;

            // Список респондентов
            case R.id.mi_mes_fio_list_load:
                if ((Appl.checkConnectToServer(true)) && (MsaUtils.loadFIO()) && (MsaUtils.loadDEV())) {
                    Appl.DisplayToastOk(getString(R.string.s_respondent_list_reloaded));
                    Appl.loadFioArray();
                    refreshRecords();
                }
                return true;

            // домой
            case android.R.id.home:
                finish();
                Appl.animateFinish(MsaMainActivity.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Handler handlerDeleteAll = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshRecords();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        switch (requestCode) {
            case EXIT_CODE_EDIT:
                set_menu_item_enabled();
                refreshRecords();
                break;

            case EXIT_CODE_AVATAR:
                if ((Appl.checkConnectToServer(true)) && (MsaUtils.loadFIO()) && (MsaUtils.loadDEV())) {
                    Appl.DisplayToastOk(getString(R.string.s_respondent_list_reloaded));
                    Appl.loadFioArray();
                    refreshRecords();
                }
                break;
        }

    }


    public void verifyOldMessages() {
        int old = Appl.getDaysDownLoad();
        if (old <= 0) old = 1;
        String sSQL =
                " select " +
                        " (select count(*) from MSA where MSA_STATE=0 and MSA_DATE<date('now','-" + old + " day')) as KVO_0," +
                        " (select count(*) from MSA where MSA_STATE=2 and MSA_DATE<date('now','-" + old + " day')) as KVO_2," +
                        " (select count(*) from MSA where MSA_STATE=3 and MSA_DATE<date('now','-" + old + " day')) as KVO_3," +
                        " (select count(*) from MSA where MSA_STATE=4 and MSA_DATE<date('now','-" + old + " day')) as KVO_4 ";
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
        c.moveToFirst();
        int kvo_0 = c.getInt(c.getColumnIndex("KVO_0"));
        int kvo_2 = c.getInt(c.getColumnIndex("KVO_2"));
        int kvo_3 = c.getInt(c.getColumnIndex("KVO_3"));
        int kvo_4 = c.getInt(c.getColumnIndex("KVO_4"));
        c.close();
        if ((kvo_0 + kvo_2 + kvo_3 + kvo_4) > 0) {
            Intent intentvfy = new Intent(this, VerifyDbActivity.class);
            startActivity(intentvfy);
        }
    }


    public void displayFilerLayout() {
        LinearLayout ly_mes_filter = findViewById(R.id.ly_msa_filter);
        if (Appl.MSA_MODEVIEW != 0) ly_mes_filter.setVisibility(View.VISIBLE);
        else ly_mes_filter.setVisibility(View.INVISIBLE);
    }


    public void setImageSize(String MSA_ID, int IMAGESIZE) {
        for (int i = 0; i < list_msa.size(); i++) {
            if (MSA_ID.equals(list_msa.get(i).msa_id)) {
                // MsaMainDataStructure mes = new MsaMainDataStructure();
                MsaMainDataStructure mes;
                mes = list_msa.get(i);
                mes.imagesize = IMAGESIZE;
                list_msa.set(i, mes);
                return;
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


}
