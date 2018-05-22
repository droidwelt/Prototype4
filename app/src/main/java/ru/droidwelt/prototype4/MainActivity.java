package ru.droidwelt.prototype4;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final int EXIT_CODE_MSA = 10001;

    private static MainActivity instance;
    NavigationView navigationView;
    Toolbar main_toolbar;
    public static final int RequestPermissionCode = 1;
    private static boolean mastRestartedOnResume = false;
    static List<MainDataStructure> list_main;
    static LinearLayoutManager mLayoutManager;
    static RecyclerView mRecyclerView;
    static RecyclerView.Adapter mAdapter;

    public static synchronized MainActivity getInstance() {
        return instance;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        Appl.setMyThemesNoActionBar(this);
        setContentView(R.layout.activity_main);

        LinearLayout ml = findViewById(R.id.ly_main);
        ml.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ml.setBackgroundResource(R.drawable.bg_main1);
        ml.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        main_toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);
        main_toolbar.setTitle(Appl.getMainTitle());
        main_toolbar.setBackgroundColor(Appl.BarColor_main);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, main_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /* Called when a drawer has settled in a completely closed state. */
           /* public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }*/

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                ImageView iv_main_avatar = findViewById(R.id.iv_main_avatar);
                iv_main_avatar.setImageDrawable(Appl.getMyAvatar());
                TextView tv_avatar = findViewById(R.id.tv_main_avatar);
                tv_avatar.setText(Appl.FIO_NAME);
                TextView tv_avatar2 = findViewById(R.id.tv_main_avatar2);
                tv_avatar2.setText(Appl.FIO_SUBNAME);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        list_main = new ArrayList<>();
        loadList();

        mRecyclerView = findViewById(R.id.rv_main);
        mRecyclerView.setHapticFeedbackEnabled(true);
        mLayoutManager = new LinearLayoutManager(this);
        //   mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MainRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        if (checkMyPremissoins()) {
            Appl.getDeviceFCM();
            startMyActions();
        } else {
            //controlMyActions(false);
            ActivityCompat.requestPermissions(this, new String[]
                    {CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
        }
    }


    @Override
    protected void onResume() {
        if (mastRestartedOnResume) {
            mastRestartedOnResume = false;
            finish();
            startActivity(getIntent());
        }
        super.onResume();
    }


    public static void setMastRestartedOnResume(boolean ms) {
        mastRestartedOnResume = ms;
    }


    protected boolean checkMyPremissoins() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                !((ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadExternalStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission & ReadExternalStoragePermission & WriteExternalStoragePermission) {
                        Appl.getDeviceFCM();
                        startMyActions();
                        //controlMyActions(true);
                    } else {
                        finish();
                    }
                }
                break;
        }
    }


    protected void startMyActions() {
        Appl.startDbHelper();
        Appl.getFioFromPreferences();
        Intent intenstart = new Intent(this, StartActivity.class);
        startActivity(intenstart);
        refreshMainMenu();
    }


    // подключение меню----------------------------------------
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    } */

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_main_register_gcm:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    } */


    protected void start_Connnect() {
        //    Appl.DisplayToastOk("start_Connnect");
        if (!Appl.VEFIRIED_IN_PROGRESS) {
            if (Appl.checkConnectToServer(true)) {
                Appl.VEFIRIED_IN_PROGRESS = true;
                if (Appl.DEVICE_DISABLED == 1) {
                    Appl.DisplayToastError(getString(R.string.device_disabled));
                } else {
                    Intent intentMessage = new Intent(MainActivity.this, EnterPassword.class);
                    startActivity(intentMessage);
                }
            }
        }
    }


    private void verifyUpdate() {
        if (!Appl.verify_executed) {
            if (Appl.checkConnectToServer(false)) {
                Appl.getMSSQLConnectionSettings();
                Appl.ShowProgressIndicatior(this);
                new MyTaskUpdate(this).execute();
            }
        }
    }

    private static class MyTaskUpdate extends AsyncTask<Void, Void, String> {
        private WeakReference<MainActivity> activityReference;
        MyTaskUpdate(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Void... params) {
            MainActivity activity = activityReference.get();
            JSONArray urls = Appl.query_MSSQL_Exec("EXQ_ONE '" + Appl.APP_PREFIX + "'," + Appl.APP_VERSIONCODE);
            try {
                if (urls.length() == 1) {
                    Appl.FILENAMETOAPDATE = urls.getJSONObject(0).getString("EXQ_FILENAME");
                    String resstr = urls.getJSONObject(0).getString("EXQ_OBJ");
                    byte[] resall = Appl.hexToBytes(resstr);

                    String myApkPath = Environment.getExternalStorageDirectory().toString() + "/Download/" + Appl.FILENAMETOAPDATE;
                    OutputStream localDbStream = null;
                    try {
                        localDbStream = new FileOutputStream(myApkPath);
                    } catch (FileNotFoundException ignored) {
                    }
                    try {
                        if (localDbStream != null)
                            localDbStream.write(resall);
                    } catch (IOException ignored) {
                    }
                    Appl.IUpdateProgram=1;
                }
            } catch (JSONException e) {
                Appl.IUpdateProgram=0;
            }
            return "END";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Appl.Indicator.dismiss();
            } catch (Exception ignored) {
            }
            MainActivity activity = activityReference.get();
            if (activity == null) return;

            if (Appl.IUpdateProgram==1)
            {
                Appl.DisplayToastInfo(getInstance().getString(R.string.update_now), R.mipmap.ic_setting, Toast.LENGTH_LONG);
                String myApkPath = Environment.getExternalStorageDirectory().toString() + "/Download/";
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                newIntent.setDataAndType(Uri.fromFile(new File(myApkPath + Appl.FILENAMETOAPDATE)), "application/vnd.android.package-archive");
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getInstance().startActivity(newIntent);
            }
        }
    }




    //защита от закрытия по Back Обработка нажатия
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (Appl.FIO_ID == -1) {
                    MainActivity.super.onBackPressed();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
                    builder.setTitle(getString(R.string.dlg_prg_exit));
                    builder.setMessage(getString(R.string.dlg_prg_confirm));
                    builder.setNegativeButton((getString(R.string.dlg_prg_no)), null);
                    builder.setPositiveButton((getString(R.string.dlg_prg_yes)), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Appl.FIO_ID = -1;
                                    MainActivity.super.onBackPressed();
                                }
                            });
                    final AlertDialog dlg = builder.create();
                    dlg.show();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }


    public static void loginProcess(String inputText) {
        JSONArray urls= Appl.query_MSSQL_Background("FIO_LOGINFCM '" + inputText + "','" + Appl.DEVICE_FCM + "'");
        if (!urls.toString().contains("######")) {
            try {
                if (urls.length() > 0) {
                    String ANS = urls.getJSONObject(0).getString("ANS");
                    Appl.FIO_NAME = urls.getJSONObject(0).getString("FIO_NAME");
                    Appl.FIO_SUBNAME = urls.getJSONObject(0).getString("FIO_SUBNAME");
                    Appl.FIO_ID = Integer.parseInt(urls.getJSONObject(0).getString("FIO_ID"));
                    if (Appl.getSaveFioInPreferens())
                        Appl.setFIO();
                    if (Appl.FIO_ID > 0) {
                        Appl.enb_client = urls.getJSONObject(0).getInt("FIO_S1");
                        Appl.enb_price = urls.getJSONObject(0).getInt("FIO_S2");
                        Appl.enb_inspect = urls.getJSONObject(0).getInt("FIO_S3");
                        Appl.enb_oplat = urls.getJSONObject(0).getInt("FIO_S4");
                        Appl.enb_tsk = urls.getJSONObject(0).getInt("FIO_S5");
                        Intent intenstart = new Intent(MainActivity.getInstance(), StartActivity.class);
                        MainActivity.getInstance().startActivity(intenstart);

                    } else {
                        if (!(ANS.equals("!")))
                            Appl.DisplayToastInfo(ANS, R.mipmap.ic_cancel, Toast.LENGTH_LONG);
                    }

                }
            } catch (JSONException e) {
                Appl.DisplayToastError(R.string.s_bad_data);
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mi_main_logput) {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogColorButton);
            builder.setTitle(getString(R.string.dlg_confirm_req));
            builder.setMessage(getString(R.string.s_confirm_logout));
            builder.setNegativeButton(getString(R.string.dlg_prg_no), null);
            builder.setPositiveButton((getString(R.string.dlg_prg_yes)),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Appl.FIO_ID = -1;
                            Appl.FIO_NAME = getString(R.string.action_no_user);
                            Appl.FIO_SUBNAME = "";
                            Appl.setFIO();
                            finish();
                        }

                    });
            builder.create();
            builder.show();

        } else if (id == R.id.mi_main_about) {

            Intent intentabout = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intentabout);

        } else if (id == R.id.mi_main_setting) {

            Appl.SETTING_MODE_GLOBAL = true;
            Intent intentsetting = new Intent(MainActivity.this, PrefActivity.class);
            startActivity(intentsetting);
            Appl.animateStart(MainActivity.this);

        } else if (id == R.id.mi_main_register_db) {

            if (Appl.checkConnectToServer(true)) {
                Intent intentregdb = new Intent(MainActivity.this, RegisterDBActivity.class);
                startActivity(intentregdb);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void startActivity(String MAIN_ID) {
        if (MAIN_ID.equalsIgnoreCase("1")) {
            Intent intent = new Intent(MainActivity.getInstance(), ClientActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            MainActivity.getInstance().startActivity(intent);
            Appl.animateStart(MainActivity.getInstance());
        }

        if (MAIN_ID.equalsIgnoreCase("2")) {
            Intent intent = new Intent(MainActivity.getInstance(), InspActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            MainActivity.getInstance().startActivity(intent);
            Appl.animateStart(MainActivity.getInstance());
        }

        if (MAIN_ID.equalsIgnoreCase("3")) {
            Intent intent = new Intent(MainActivity.getInstance(), OplActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            MainActivity.getInstance().startActivity(intent);
            Appl.animateStart(MainActivity.getInstance());
        }

        if (MAIN_ID.equalsIgnoreCase("4")) {
            Intent intent = new Intent(MainActivity.getInstance(), PriceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            MainActivity.getInstance().startActivity(intent);
            Appl.animateStart(MainActivity.getInstance());
        }

        if (MAIN_ID.equalsIgnoreCase("5")) {
            Intent intent = new Intent(MainActivity.getInstance(), TskActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            MainActivity.getInstance().startActivity(intent);
            Appl.animateStart(MainActivity.getInstance());
        }

        if (MAIN_ID.equalsIgnoreCase("6")) {
            MainActivity.getInstance().verifyUpdate();
            if (Appl.FIO_ID >= 0) {
                Intent intent = new Intent(MainActivity.getInstance(), MsaMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                MainActivity.getInstance().startActivityForResult(intent, EXIT_CODE_MSA);
                Appl.animateStart(MainActivity.getInstance());
            } else {
                MainActivity.getInstance().start_Connnect();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        switch (requestCode) {
            case EXIT_CODE_MSA:
                getBoxCount();
                refreshMainMenu();
                break;
        }
    }


    protected void refreshMainMenu() {
        loadList();
        for (int i = 0; i <= mAdapter.getItemCount() - 1; i = i + 1) {
            mAdapter.notifyItemChanged(i);
        }
    }


    protected String getBoxCount() {
        String s = "";
        if (Appl.getDatabase() != null) {
            String sSQL = " select "
                    + " (select count(*) from  MSA  where MSA_STATE=4) as KVO4,"
                    + " (select count(*) from  MSA  where MSA_STATE=2) as KVO2";
            Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
            c.moveToFirst();
            int cnt4 = c.getInt(0);
            int cnt2 = c.getInt(1);
            c.close();
            if (cnt2 > 0) {
                if (!s.equals("")) s = s + "; ";
                s = s + "<font color=\"#388E3C\">Новых - " + cnt2 + "</font>";
            }

            if (cnt4 > 0) {
                if (!s.equals("")) s = s + "; ";
                s = s + "<font color=\"#AD1457\">Исходящих - " + cnt4 + "</font>";
            }
        }
        return s;
    }

    protected void loadList() {
        list_main.clear();

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "6";
            mes.main_title = "Сообщения";
            mes.main_text = getBoxCount();
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_message);
            mes.main_color = "#f9fbe7";
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
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            mes.main_text = "обновлено " + sp.getString("clt_dateload", getString(R.string.s_time_not_defined));
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_client);
            mes.main_color = "#FFF8E1";
            list_main.add(mes);
        }

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "2";
            mes.main_title = "Ревизия";
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_inspect);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            mes.main_text = "обновлено " + sp.getString("ins_dateload", getString(R.string.s_time_not_defined));
            mes.main_color = "#F9FBE7";
            list_main.add(mes);
        }

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "3";
            mes.main_title = "Оплаты";
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            mes.main_text = "обновлено " + sp.getString("opl_dateload", getString(R.string.s_time_not_defined));
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_oplat);
            mes.main_color = "#F1F8E9";
            list_main.add(mes);
        }

        {
            MainDataStructure mes = new MainDataStructure();
            mes.main_id = "4";
            mes.main_title = "Прайс";
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            mes.main_text = "обновлено " + sp.getString("prn_dateload", getString(R.string.s_time_not_defined));
            mes.main_img = getApplicationContext().getResources().getDrawable(R.mipmap.main_price);
            mes.main_color = "#E1F5FE";
            list_main.add(mes);
        }

    }


}
