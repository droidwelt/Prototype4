package ru.droidwelt.prototype4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChoiceMemuActivity extends AppCompatActivity {

    static List<MainDataStructure> list_main;
    LinearLayoutManager mLayoutManager;
    static RecyclerView mRecyclerView;
    static RecyclerView.Adapter mAdapter;
    private static ChoiceMemuActivity instance;

    public static synchronized ChoiceMemuActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesDlg(this);
        setContentView(R.layout.activity_choicemenu);
        setTitle(getString(R.string.s_mainmenu_main));
        instance = this;

   /*     Bundle extras = getIntent().getExtras();
        String sDisable = extras.getString("MODE", "");
        if (!sDisable.isEmpty()) {
            if (sDisable.equalsIgnoreCase("CLT")) choice_menu_clt.setEnabled(false);
            if (sDisable.equalsIgnoreCase("INS")) choice_menu_ins.setEnabled(false);
            if (sDisable.equalsIgnoreCase("PRS")) choice_menu_prs.setEnabled(false);
            if (sDisable.equalsIgnoreCase("OPL")) choice_menu_opl.setEnabled(false);
            if (sDisable.equalsIgnoreCase("TSK")) choice_menu_tsk.setEnabled(false);
            if (sDisable.equalsIgnoreCase("MES")) choice_menu_mes.setEnabled(false);
        }*/

        list_main = new ArrayList<>();
        loadList();

        mRecyclerView = findViewById(R.id.rv_menu);
        mRecyclerView.setHapticFeedbackEnabled(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChoiceMenuRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }


    protected void loadList() {
        list_main.clear();

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "6";
            mes.main_title = "Сообщения";
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_message);
            mes.main_color = "#ECEFF1";
            list_main.add(mes);
        }

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "5";
            mes.main_title = "Задачи";
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_task);
            mes.main_color = "#FBE9E7";
            list_main.add(mes);
        }

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "1";
            mes.main_title = "Клиенты";
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_client);
            mes.main_color = "#FFF8E1";
            list_main.add(mes);
        }

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "2";
            mes.main_title = "Ревизия";
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_inspect);
            mes.main_color = "#F9FBE7";
            list_main.add(mes);
        }

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "3";
            mes.main_title = "Оплаты";
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_oplat);
            mes.main_color = "#F1F8E9";
            list_main.add(mes);
        }

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "4";
            mes.main_title = "Прайс";
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_price);
            mes.main_color = "#E1F5FE";
            list_main.add(mes);
        }

    }

}