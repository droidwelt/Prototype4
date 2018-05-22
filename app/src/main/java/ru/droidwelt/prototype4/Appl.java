package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("CommitPrefEdits")
public class Appl extends Application {

    private static Appl instance;
    public static Context context;
    public static final String APP_PREFIX = "PRO";
//    public static final String GCM_Group_Prefix = "PRO";
    //   public static final String GCM_Project_ID = "327899043438";
    //   public static final String GCM_Sender_ID = "AIzaSyAB53cNbZAvzCJaq8RUYJUtArqXQ7On14Q";

	/*	Типы в MSA_STATE:   ( СОСТОЯНИЕ )
       0  - черновик
         1 -  шаблон
       2 -  входящие, полученные, лента ,,,,,,,  22 - прочтенные
       3 -  отправленные
       4 -  исходящие
         5 -  удаленные
       10 - избранное

        et_msa_title.setBackgroundColor(ContextCompat.getColor(Appl.getContext(),R.color.background_text));
       */

    public static final int BarColor_0 = Color.parseColor("#BF360C"); //черновик
    public static final int BarColor_2 = Color.parseColor("#388E3C"); //новые
    public static final int BarColor_22 = Color.parseColor("#00796b"); //входящие
    public static final int BarColor_3 = Color.parseColor("#1976D2"); //отправленные
    public static final int BarColor_4 = Color.parseColor("#AD1457"); //исходящие
    public static final int BarColor_10 = Color.parseColor("#BD07FD"); //избранное
    public static final int BarColor_avatar = Color.parseColor("#376197"); //аватар
    public static final int BarColor_main = Color.parseColor("#000000"); //главная форма

    public static boolean verify_executed = false; //главная форма
    public static int enb_client = 1, enb_price = 1, enb_inspect = 1, enb_oplat = 1, enb_tsk = 1;

    public static final String MSSQL_SERVER_IP = "192.168.1.2";

    public static final String MSSQL_SERVER_PORT = "1433";
    public static final String MSSQL_SERVER_DATABASE = "APR2";
    public static String MSSQL_DB = "";
    public static String MSSQL_LOGIN = "";
    public static String MSSQL_PASS = "";

    public static int FIO_ID = -1;
    public static String FIO_NAME = "Вход не осуществлён";
    public static String FIO_SUBNAME = "";
    public static Bitmap FIO_IMAGE = null;
    public static final int FIO_AVATAR_SIZE = 160;

    public static int MSA_MODEVIEW = -1;
    public static int MSA_MODEEDIT = 0;
    public static String MSA_ID = "";
    public static int MSA_POS = -1;
    public static boolean SETTING_MODE_GLOBAL = false;

    public static int APP_VERSIONCODE = 0;
    public static int DEVICE_DISABLED = 0;
    public static int DEVICE_ID = 0;
    public static String FILENAMETOAPDATE = "";
    public static String APP_VERSIONNAME = "";
    public static String DEVICE_FCM = "", DEVICE_MODEL = "", DEVICE_BRAND = "";
    public static Boolean VEFIRIED_IN_PROGRESS = false;
    public static Boolean DEVICE_REGISTRED = false;

    public static String DB_PATH = Environment.getExternalStorageDirectory().toString() + "/Prototype/";
    private static SQLiteDatabase database = null;
    public static String DB_NAME = "prototype.db3";
    public static String DB_NAMEMODEL = "prototype_etal.db3";
/*	public static String DB_DOWNLOAD = Environment.getExternalStorageDirectory().toString() + "/Download/";
    public static String DB_BLUETOOTH = Environment.getExternalStorageDirectory().toString() + "/Bluetooth/";*/

    private static String last_scanned_code = "";
    public static final int CODE_CHOICE_TEMPLATE = 887;

    public static String message_filter = "";
    public static String FILTER_LBL = "";
    public static String FILTER_CLR = "";


    public static List<Integer> afio_id = new ArrayList<>();
    public static List<Bitmap> afio_image = new ArrayList<>();

    public static boolean download_onfirstrecord;
    public static boolean download_in_progress;

    public static ProgressDialog Indicator;

    public static int IUpdateProgram;
    public static String SExec;


    public static synchronized Appl getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        verify_executed = false;
        instance = this;
        context = this.getBaseContext();
        download_onfirstrecord = getDownloadOnFirstrecord();
        download_in_progress = false;
        //   Firebase.setAndroidContext(this);

        getDeviceFCM();
    }

    public static void startDbHelper() {
        DB_OpenHelper dbh = new DB_OpenHelper(context, DB_NAME);
        database = dbh.getDatabase();
    }


  /*  public static String getTopActivityName() {
        final ActivityManager am = (ActivityManager) getContext().getSystemService(Service.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(Integer.MAX_VALUE);
        for (final ActivityManager.RunningTaskInfo task : tasks) {
            if (task.topActivity.getPackageName().equals(getContext().getResources().getString(R.string.app_package))) {
                return task.topActivity.getClassName();
            }
        }
        return null;
    }*/


    public static void setMyThemes(Activity a) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        String currentThemeName = sp.getString("font_size", "1");

        if (currentThemeName.equals("0")) {
            a.setTheme(R.style.AppTheme_Small);
        }
        if (currentThemeName.equals("1")) {
            a.setTheme(R.style.AppTheme_Medium);
        }
        if (currentThemeName.equals("2")) {
            a.setTheme(R.style.AppTheme_Large);
        }
        if (currentThemeName.equals("3")) {
            a.setTheme(R.style.AppTheme_Huge);
        }
    }

    public static void setMyThemesDlg(Activity a) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        String currentThemeName = sp.getString("font_size", "1");

        if (currentThemeName.equals("0")) {
            a.setTheme(R.style.AppTheme_Dialog_Small);
        }
        if (currentThemeName.equals("1")) {
            a.setTheme(R.style.AppTheme_Dialog_Medium);
        }
        if (currentThemeName.equals("2")) {
            a.setTheme(R.style.AppTheme_Dialog_Large);
        }
        if (currentThemeName.equals("3")) {
            a.setTheme(R.style.AppTheme_Dialog_Huge);
        }
    }


    public static void setMyThemesNoActionBar(Activity a) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        String currentThemeName = sp.getString("font_size", "0");

        if (currentThemeName.equals("0")) {
            a.setTheme(R.style.AppTheme_NoActionBar_Small);
        }
        if (currentThemeName.equals("1")) {
            a.setTheme(R.style.AppTheme_NoActionBar_Medium);
        }
        if (currentThemeName.equals("2")) {
            a.setTheme(R.style.AppTheme_NoActionBar_Large);
        }
        if (currentThemeName.equals("3")) {
            a.setTheme(R.style.AppTheme_NoActionBar_Huge);
        }
    }


    public static boolean getExecOnStart() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("exec_on_start", false);
    }

    public static boolean getMesFabKey() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("mes_fab_key", true);
    }

    public static boolean getSaveFioInPreferens() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("save_fio_id", false);
    }


    public static int getDownload_maxsize() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String s = strnormalize(sp.getString("download_maxsize", "10"));
        if (s.equals("")) s = "10";
        int i;

        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            i = 10;
            //e.printStackTrace();
        }
        if (i < 0) i = 0;
        return i * 1024 * 1024;
    }


    public static void getMSSQLConnectionSettings() {
        // Log.i("LOG", "Appl.onGetSettings");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String server_nomer = sp.getString("server_nomer", "0");

        if (server_nomer.equals("0")) {
            MSSQL_LOGIN = "BL_Demo";
            MSSQL_PASS = "971sx51ER12m9";
            MSSQL_DB = "jdbc:jtds:sqlserver://"
                    + MSSQL_SERVER_IP + ":"
                    + MSSQL_SERVER_PORT + "/"
                    + MSSQL_SERVER_DATABASE;
        }
        if (server_nomer.equals("1")) {
            MSSQL_LOGIN = sp.getString("login", "000");
            MSSQL_PASS = sp.getString("password", "000");
            MSSQL_DB = "jdbc:jtds:sqlserver://"
                    + sp.getString("server_name", "000") + ":"
                    + sp.getString("server_port", "000") + "/"
                    + sp.getString("db_name", "000");
        }
        if (server_nomer.equals("2")) {
            MSSQL_LOGIN = sp.getString("login_2", "000");
            MSSQL_PASS = sp.getString("password_2", "000");
            MSSQL_DB = "jdbc:jtds:sqlserver://"
                    + sp.getString("server_name_2", "000") + ":"
                    + sp.getString("server_port_2", "000") + "/"
                    + sp.getString("db_name_2", "000");
        }
    }


    public static int getAnimation_mode() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String picture_size_val = sp.getString("animation_mode", "0");
        return Integer.parseInt(picture_size_val);
    }

    public static boolean getDownloadAttachments() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("download_attachments", false);
    }


    public static boolean getDownloadOnFirstrecord() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("download_onfirstrecord", true);
    }


    public static boolean getUploadImmediately() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("upload_immediately", false);
    }

    public static int getDaysDownLoad() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sd = sp.getString("download_dates", "10");
        return Integer.parseInt(sd);
    }

    public static int getKvoDownLoad() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sd = sp.getString("download_kvo", "100");
        return Integer.parseInt(sd);
    }

    public static void animateStart(Activity a) {
        //int am = getAnimation_mode(c);	//Log.i("LOG", "getAnimation_mode "+am);
        switch (getAnimation_mode()) {
            case 1:
                a.overridePendingTransition(R.anim.activity_down_up_enter, R.anim.activity_down_up_exit);
                break;
            case 2:
                a.overridePendingTransition(R.anim.activity_up_down_enter, R.anim.activity_up_down_exit);
                break;
            case 3:
                a.overridePendingTransition(R.anim.activity_left_rigth_enter, R.anim.activity_left_rigth_exit);
                break;
            case 4:
                a.overridePendingTransition(R.anim.activity_rigth_left_enter, R.anim.activity_rigth_left_exit);
                break;
            default:
                break;
        }
    }

    public static void animateFinish(Activity a) {
        switch (getAnimation_mode()) {
            case 1:
                a.overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                break;
            case 2:
                a.overridePendingTransition(R.anim.activity_up_down_close_enter, R.anim.activity_up_down_close_exit);
                break;
            case 3:
                a.overridePendingTransition(R.anim.activity_left_rigth_close_enter, R.anim.activity_left_rigth_close_exit);
                break;
            case 4:
                a.overridePendingTransition(R.anim.activity_rigth_left_close_enter, R.anim.activity_rigth_left_close_exit);
                break;
            default:
                break;
        }
    }


    public static String truncateString(String s, int len) {
        if (s.isEmpty()) {
            return "";
        } else {
            int slen = s.length();
            if (slen > len) {
                return s.substring(0, len - 1);
            } else {
                return s;
            }
        }
    }


    public static byte[] concatArray(byte[] a, byte[] b) {
        if (a == null) return b;
        if (b == null) return a;
        byte[] r = new byte[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    public static String strnormalize(String s) {
        if (s == null) return "";
        else if (s.isEmpty()) return "";
        else return s;
    }

  /*  public static float stringToFloat(String str) {
        Float f = (float) 0.0;
        assert str != null;
        if (!str.trim().equals("")) try {
            f = Float.valueOf(str);
        } catch (NumberFormatException ignored) {
        }
        return f;
    }
*/

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Appl.context = context;
    }


    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(SQLiteDatabase database) {
        Appl.database = database;
    }


    public static String getLast_scanned_code() {
        return last_scanned_code;
    }

    public static void setLast_scanned_code(String last_scanned_code) {
        Appl.last_scanned_code = last_scanned_code;
    }


    public static String generate_GUID() {
        return UUID.randomUUID().toString();
    }


    public static void getFioFromPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        FIO_ID = sp.getInt("fio_id", -1);
        FIO_NAME = sp.getString("tv_mesitem_fioname", "");
        FIO_SUBNAME = sp.getString("fio_subname", "");
    }


    public static void setFIO() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putInt("fio_id", FIO_ID);
        editor.putString("tv_mesitem_fioname", FIO_NAME);
        editor.putString("fio_subname", FIO_SUBNAME);
        editor.apply();
    }


    public static void getIMSA_MODE() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        MSA_MODEVIEW = sp.getInt("imsa_mode", -1);
    }


    public static void setIMSA_MODE() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putInt("imsa_mode", MSA_MODEVIEW);
        editor.apply();
    }


    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";


    public static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

/*	public static String bytesToHex(byte[] data) {
        if (data==null)        {return null;}
        int len = data.length;
        String str = "";
		for (byte aData : data) {
			if ((aData & 0xFF) < 16)
				str = str + "0" + Integer.toHexString(aData & 0xFF);
			else
				str = str + Integer.toHexString(aData & 0xFF);
		}
        return str;
    }*/

    public static byte[] hexToBytes(String str) {
        if (str == null) return null;
        else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }


    public static void DisplayToastError(String result) {
        Toast toast3 = CustomToast.makeText(Appl.context, result, Toast.LENGTH_LONG, R.mipmap.ic_cancel);
        toast3.show();
    }

    public static void DisplayToastError(int resID) {
        Toast toast3 = CustomToast.makeText(Appl.context, Appl.context.getString(resID), Toast.LENGTH_LONG, R.mipmap.ic_cancel);
        toast3.show();
    }

    public static void DisplayToastOk(String result) {
        Toast toast3 = CustomToast.makeText(Appl.context, result, Toast.LENGTH_LONG, R.mipmap.ic_checked);
        toast3.show();
    }


    public static void DisplayToastInfo(String result, int idimage, int duration) {
        CustomToast toast3 = CustomToast.makeText(Appl.context, result, duration, idimage);//.LENGTH_SHORT
        toast3.show();
    }

    //  вызывается в основном потоке
    public static JSONArray query_MSSQL_Background(String sql_string) {
        Appl.getMSSQLConnectionSettings();
        DB_Query ar = new DB_Query();
        ar.execute(sql_string);
        JSONArray result = null;
        try {
            result = ar.get();
        } catch (InterruptedException | ExecutionException e) {
            DisplayToastError(R.string.s_fail_data);
        }
        assert result != null;
        String s = result.toString();
        if (s.contains("######"))
            DisplayToastError(R.string.s_fail_data + "\n" + s);
        return result;
    }

    //  вызывается в дополнительном потоке, который не UI
    public static JSONArray query_MSSQL_Simple(String sql_string) {
        Appl.getMSSQLConnectionSettings();
        JSONArray result = query_MSSQL_Exec(sql_string);
        assert result != null;
        String s = result.toString();
        if (s.contains("######"))
            DisplayToastError(R.string.s_fail_data + "\n" + s);
        return result;
    }


    public static  JSONArray query_MSSQL_Exec(String... query) {
        JSONArray resultSet = new JSONArray();
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection con = null;
            Statement st = null;
            ResultSet rs = null;
            try {
                con = DriverManager.getConnection(Appl.MSSQL_DB, Appl.MSSQL_LOGIN, Appl.MSSQL_PASS);
                if (con != null) {
                    st = con.createStatement();
                    rs = st.executeQuery(query[0]);
                    if (rs != null) {
                        int columnCount = rs.getMetaData().getColumnCount();
                        // Сохранение данных в JSONArray
                        while (rs.next()) {
                            JSONObject rowObject = new JSONObject();
                            for (int i = 1; i <= columnCount; i++) {
                                rowObject.put(rs.getMetaData().getColumnName(i), (rs.getString(i) != null) ? rs.getString(i) : "");
                            }
                            resultSet.put(rowObject);
                        }
                    }
                    con.close();
                }
            } catch (SQLException e) {
                resultSet.put("######1 " + e.toString());

            } catch (JSONException e) {
                resultSet.put("######2 " + e.getMessage());
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    //  throw new RuntimeException(e.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            resultSet.put("######3 " + e.getMessage());
        }
        return resultSet;
    }


    public static Drawable getMyBarDrawable() {
        if (Appl.MSA_MODEVIEW == 0) return new ColorDrawable(BarColor_0);
        if (Appl.MSA_MODEVIEW == 2) return new ColorDrawable(BarColor_2);
        if (Appl.MSA_MODEVIEW == 22) return new ColorDrawable(BarColor_22);
        if (Appl.MSA_MODEVIEW == 3) return new ColorDrawable(BarColor_3);
        if (Appl.MSA_MODEVIEW == 4) return new ColorDrawable(BarColor_4);
        if (Appl.MSA_MODEVIEW == 10) return new ColorDrawable(BarColor_10);
        return new ColorDrawable(Color.BLACK);
    }


 /*   public static int getMyBarColor() {
        Drawable d = new ColorDrawable(Color.BLACK);
        if (Appl.MSA_MODEVIEW == 0) return BarColor_0;
        if (Appl.MSA_MODEVIEW == 2) return BarColor_2;
        if (Appl.MSA_MODEVIEW == 22) return BarColor_22;
        if (Appl.MSA_MODEVIEW == 3) return BarColor_3;
        if (Appl.MSA_MODEVIEW == 4) return BarColor_4;
        if (Appl.MSA_MODEVIEW == 10) return BarColor_10;
        return Color.BLACK;
    } */


    @SuppressLint("SimpleDateFormat")
    public static String getStringCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.mmm");
        return sdf.format(calendar.getTime());
    }

    public static String createValidFileName(String s) {
        s = generValidFileName(Appl.strnormalize(s));
        if (s.equals("")) s = getRandomString(12);
        return s;
    }

    public static byte[] getByteArrayfromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }

/*	public static Bitmap getBitmapfromByteArray(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap , 0, bitmap.length);
	}

	public static  Bitmap convertBase64StringToBitmap(String source) {
		byte[] rawBitmap = Base64.decode(source.getBytes(), Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(rawBitmap, 0, rawBitmap.length);
	}
	*/


    @SuppressLint("DefaultLocale")
    public static String generValidFileName(String s_in) {
        if (s_in.isEmpty()) return "";
        StringBuilder s_out = new StringBuilder();
        try {
            if (!s_in.isEmpty()) {
                String expression = "[.~+=?0123456789QWERTYUIOPASDFGHJKLZXCVBNM_-ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ]";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                int l = s_in.length();
                for (int i = 0; i < l; i = i + 1) {
                    String s = s_in.substring(i, i + 1) /*.toUpperCase()*/;
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches()) s_out.append(s);
                    else s_out.append("_");
                }
            }
        } catch (Exception e) {
            return "";
        }
        return s_out.toString();
    }

	
/*	public static String getRealPathFromURI(Context context, Uri contentUri) {
          Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			  assert cursor != null;
			  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		    } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}*/


    public static String convertToKbStr(int i) {
        String s = "";
        if (i <= 9999) s = i + " б";
        if (i > 9999) s = "" + i / 1024 + " кб";
        if (i > 9999999) s = "" + i / 1024 / 1024 + " Мб";
        return s;
    }
	
	
	/*
	  Extension MIME Type
.doc      application/msword
.dot      application/msword

.docx     application/vnd.openxmlformats-officedocument.wordprocessingml.document
.dotx     application/vnd.openxmlformats-officedocument.wordprocessingml.template
.docm     application/vnd.ms-word.document.macroEnabled.12
.dotm     application/vnd.ms-word.template.macroEnabled.12

.xls      application/vnd.ms-excel
.xlt      application/vnd.ms-excel
.xla      application/vnd.ms-excel

.xlsx     application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
.xltx     application/vnd.openxmlformats-officedocument.spreadsheetml.template
.xlsm     application/vnd.ms-excel.sheet.macroEnabled.12
.xltm     application/vnd.ms-excel.template.macroEnabled.12
.xlam     application/vnd.ms-excel.addin.macroEnabled.12
.xlsb     application/vnd.ms-excel.sheet.binary.macroEnabled.12

.ppt      application/vnd.ms-powerpoint
.pot      application/vnd.ms-powerpoint
.pps      application/vnd.ms-powerpoint
.ppa      application/vnd.ms-powerpoint

.pptx     application/vnd.openxmlformats-officedocument.presentationml.presentation
.potx     application/vnd.openxmlformats-officedocument.presentationml.template
.ppsx     application/vnd.openxmlformats-officedocument.presentationml.slideshow
.ppam     application/vnd.ms-powerpoint.addin.macroEnabled.12
.pptm     application/vnd.ms-powerpoint.presentation.macroEnabled.12
.potm     application/vnd.ms-powerpoint.template.macroEnabled.12
.ppsm     application/vnd.ms-powerpoint.slideshow.macroEnabled.12

.mdb      application/vnd.ms-access

	 */

    //http://stackoverflow.com/questions/4212861/what-is-a-correct-mime-type-for-docx-pptx-etc
    @SuppressLint("DefaultLocale")
    public static String getMimeType(String url) {
        String type;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        //	Log.i("getMimeType","extension="+extension);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
            if (type == null) {
                if (url.toLowerCase().endsWith("mp4")) {
                    type = "video/*";
                }
                if (url.toLowerCase().endsWith("mpg")) {
                    type = "video/*";
                }
                if (url.toLowerCase().endsWith("xls")) {
                    type = "application/vnd.ms-excel";
                }
                if (url.toLowerCase().endsWith("xlsx")) {
                    type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                }
                if (url.toLowerCase().endsWith("doc")) {
                    type = "application/msword";
                }
                if (url.toLowerCase().endsWith("docx")) {
                    type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                }
            }
            return type;
        } else return "*/*";
    }


    public static boolean verifyMaxFileSize(String fn, boolean displayToast) {
        int maxFileSize = getDownload_maxsize();
        if (maxFileSize > 0) {
            File file = new File(fn);
            if (file.exists()) {
                if ((int) file.length() < maxFileSize) return true;
                else {
                    if (displayToast)
                        DisplayToastError("Размер вложения не может быть более " + maxFileSize + " Мб");
                    return false;
                }
            } else return false;
        } else return true;
    }


    public static int getColorByString(String sClr) {
        if (sClr == null) return Color.parseColor("#EEEEEE"); // White
        else {
            if (sClr.equalsIgnoreCase("0")) return Color.parseColor("#EEEEEE"); // White
            if (sClr.equalsIgnoreCase("1")) return Color.parseColor("#ECEFF1"); // Blue Grey
            if (sClr.equalsIgnoreCase("2")) return Color.parseColor("#FBE9E7"); // Deep Orange
            if (sClr.equalsIgnoreCase("3")) return Color.parseColor("#FFF8E1"); // Amber
            if (sClr.equalsIgnoreCase("4")) return Color.parseColor("#F9FBE7"); // Lime
            if (sClr.equalsIgnoreCase("5")) return Color.parseColor("#F1F8E9"); // Light Green
            if (sClr.equalsIgnoreCase("6")) return Color.parseColor("#E8F5E9"); // Green
            if (sClr.equalsIgnoreCase("7")) return Color.parseColor("#E0F2F1"); // Teal
            if (sClr.equalsIgnoreCase("8")) return Color.parseColor("#E0F7FA"); // Cyan
            if (sClr.equalsIgnoreCase("9")) return Color.parseColor("#E1F5FE"); // Light Blue
            if (sClr.equalsIgnoreCase("A")) return Color.parseColor("#E8EAF6"); // Indigo
            if (sClr.equalsIgnoreCase("B")) return Color.parseColor("#E3F2FD"); // Blue
            if (sClr.equalsIgnoreCase("C")) return Color.parseColor("#EDE7F6"); // Deep Purple
            if (sClr.equalsIgnoreCase("D")) return Color.parseColor("#F3E5F5"); // Purple
            if (sClr.equalsIgnoreCase("E")) return Color.parseColor("#FCE4EC"); // Pink
            if (sClr.equalsIgnoreCase("F")) return Color.parseColor("#FFEBEE"); // Red
            return Color.parseColor("#EEEEEE"); // White
        }
    }


    @SuppressLint("NewApi")
    public static Drawable getLabelDrawableByNomer(String sx, int mode) {
        // mode = 0  показ нормальный, 1 - для фильтра или выбора
        String sn = Appl.strnormalize(sx);
        if ((mode == 1) & (sn.equals(""))) return context.getDrawable(R.mipmap.q_filter);
        if ((mode == 1) & (sn.equals("0"))) return context.getDrawable(R.mipmap.q_filter);
        if (sn.equalsIgnoreCase("")) return context.getDrawable(R.mipmap.q_00);
        if (sn.equalsIgnoreCase("0")) return context.getDrawable(R.mipmap.q_00);
        if (sn.equalsIgnoreCase("1")) return context.getDrawable(R.mipmap.q_01);
        if (sn.equalsIgnoreCase("2")) return context.getDrawable(R.mipmap.q_02);
        if (sn.equalsIgnoreCase("3")) return context.getDrawable(R.mipmap.q_03);
        if (sn.equalsIgnoreCase("4")) return context.getDrawable(R.mipmap.q_04);
        if (sn.equalsIgnoreCase("5")) return context.getDrawable(R.mipmap.q_05);
        if (sn.equalsIgnoreCase("6")) return context.getDrawable(R.mipmap.q_06);
        if (sn.equalsIgnoreCase("7")) return context.getDrawable(R.mipmap.q_07);
        if (sn.equalsIgnoreCase("8")) return context.getDrawable(R.mipmap.q_08);
        if (sn.equalsIgnoreCase("9")) return context.getDrawable(R.mipmap.q_09);
        if (sn.equalsIgnoreCase("10")) return context.getDrawable(R.mipmap.q_10);
        if (sn.equalsIgnoreCase("11")) return context.getDrawable(R.mipmap.q_11);
        if (sn.equalsIgnoreCase("12")) return context.getDrawable(R.mipmap.q_12);
        if (sn.equalsIgnoreCase("13")) return context.getDrawable(R.mipmap.q_13);
        if (sn.equalsIgnoreCase("14")) return context.getDrawable(R.mipmap.q_14);
        if (sn.equalsIgnoreCase("15")) return context.getDrawable(R.mipmap.q_15);
        if (sn.equalsIgnoreCase("16")) return context.getDrawable(R.mipmap.q_16);
        if (sn.equalsIgnoreCase("17")) return context.getDrawable(R.mipmap.q_17);
        if (sn.equalsIgnoreCase("18")) return context.getDrawable(R.mipmap.q_18);
        if (sn.equalsIgnoreCase("19")) return context.getDrawable(R.mipmap.q_19);
        if (sn.equalsIgnoreCase("20")) return context.getDrawable(R.mipmap.q_20);
        if (sn.equalsIgnoreCase("21")) return context.getDrawable(R.mipmap.q_21);
        if (sn.equalsIgnoreCase("22")) return context.getDrawable(R.mipmap.q_22);
        if (sn.equalsIgnoreCase("23")) return context.getDrawable(R.mipmap.q_23);
        return context.getDrawable(R.mipmap.q_00);
    }

    public static int getResIDbyName(String sx) {
        // mode = 0  показ нормальный, 1 - для фильтра или выбора
        String sn = Appl.strnormalize(sx);
        String sres = "q_00";
        if (sn.equals("0")) sres = "q_00";
        if (sn.equals("1")) sres = "q_01";
        if (sn.equals("2")) sres = "q_02";
        if (sn.equals("3")) sres = "q_03";
        if (sn.equals("4")) sres = "q_04";
        if (sn.equals("5")) sres = "q_05";
        if (sn.equals("6")) sres = "q_06";
        if (sn.equals("7")) sres = "q_07";
        if (sn.equals("8")) sres = "q_08";
        if (sn.equals("9")) sres = "q_09";
        if (sn.equals("10")) sres = "q_10";
        if (sn.equals("11")) sres = "q_11";
        if (sn.equals("12")) sres = "q_12";
        if (sn.equals("13")) sres = "q_13";
        if (sn.equals("14")) sres = "q_14";
        if (sn.equals("15")) sres = "q_15";
        if (sn.equals("16")) sres = "q_16";
        if (sn.equals("17")) sres = "q_17";
        if (sn.equals("18")) sres = "q_18";
        if (sn.equals("19")) sres = "q_19";
        if (sn.equals("20")) sres = "q_20";
        if (sn.equals("21")) sres = "q_21";
        if (sn.equals("22")) sres = "q_22";
        if (sn.equals("23")) sres = "q_23";
        if ((sres.equals("q_00"))) sres = "q_filter";
        return context.getResources().getIdentifier(sres, "mipmap", context.getPackageName());
    }

    @SuppressLint("NewApi")
    public static Drawable getMyAvatar() {
        Drawable dr;
        if (FIO_ID < 0) {
            FIO_NAME = "Вход не осуществлён";
            FIO_SUBNAME = "";
            FIO_IMAGE = null;
            dr = context.getDrawable(R.mipmap.ic_avatar_one);
        } else {
            String sSQL = " select FIO_NAME,FIO_SUBNAME,FIO_IMAGE from FIO where FIO_ID=" + FIO_ID + "";
            Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
            c.moveToFirst();
            //	Log.i ("W_W_W", "FIO_ID="+Appl.FIO_ID);
            //	Log.i ("W_W_W", "c.getCount()="+c.getCount());
            if (c.getCount() > 0) {
                FIO_NAME = Appl.strnormalize(c.getString(0));
                FIO_SUBNAME = Appl.strnormalize(c.getString(1));

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                Editor editor = sp.edit();
                editor.putString("tv_mesitem_fioname", FIO_NAME);
                editor.putString("fio_subname", FIO_SUBNAME);
                editor.apply();

                byte[] img = c.getBlob(2);
                if (img != null) FIO_IMAGE = BitmapFactory.decodeByteArray(img, 0, img.length);
                else FIO_IMAGE = null;
                c.close();
            } else
                Appl.DisplayToastInfo("Обновите базу респондентов.\nВашего имени нет в базе устройства", R.mipmap.ic_avatar_my, Toast.LENGTH_LONG);

            if (FIO_IMAGE != null) dr = new BitmapDrawable(context.getResources(), FIO_IMAGE);
            else dr = context.getDrawable(R.mipmap.ic_avatar_my);
        }

        return dr;
    }


    protected static void getDeviceFCM() {
        Appl.DEVICE_FCM = FirebaseInstanceId.getInstance().getToken();
        Appl.DEVICE_MODEL = android.os.Build.MODEL;
        Appl.DEVICE_BRAND = android.os.Build.BRAND;
    }

    public static String getMainTitle() {
        PackageInfo pinfo;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Appl.APP_VERSIONCODE = pinfo.versionCode;
            Appl.APP_VERSIONNAME = pinfo.versionName;
            return context.getString(R.string.app_name) + "  " + Appl.APP_VERSIONNAME + "." + Appl.APP_VERSIONCODE;
        } catch (NameNotFoundException e) {
            return context.getString(R.string.app_name);
        }
    }


    public static void loadFioArray() {
        afio_id.clear();
        afio_image.clear();

        String sSQL = "select FIO_ID,FIO_IMAGE from FIO  where FIO_TP in (1,2) order by FIO_ID";
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            afio_id.add(c.getInt(0));
            if (c.getBlob(1) == null) afio_image.add(null);
            else {
                byte[] bt = c.getBlob(1);
                afio_image.add(BitmapFactory.decodeByteArray(bt, 0, bt.length));
            }
            c.moveToNext();
        }
        c.close();
    }



    public static Boolean checkConnectToServer(Boolean showToast) {
        if (isNetworkAvailable(showToast)) {
            Appl.getMSSQLConnectionSettings();
            DB_CheckInet ar = new DB_CheckInet();
            ar.execute("");
            String res;
            try {
                res = ar.get();
            } catch (InterruptedException | ExecutionException e) {
                if (showToast) DisplayToastError(R.string.s_no_server);
                return false;
            }
            if (res.contains("######")) {
                if (showToast) DisplayToastError(R.string.s_no_server);
                return false;
            }
            return true;
        } else {
            if (showToast) DisplayToastError(R.string.s_no_internet);
            return false;
        }
    }


    public static boolean isNetworkAvailable(Boolean showToast) {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                assert connectivityManager != null;
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        if (showToast) DisplayToastError(R.string.s_no_internet);
        return false;
    }


    public static void startIntentFromFile(String sFileName) {
        String mime = Appl.getMimeType(sFileName);
        String url = "file://" + Environment.getExternalStorageDirectory().toString() + "/Download/" + sFileName;
        File f = new File(Environment.getExternalStorageDirectory().toString() + "/Download/", sFileName);
        Intent intent = getDefaultViewIntent(Uri.fromFile(f));
        Uri data = Uri.parse(url);
        assert intent != null;
        intent.setDataAndType(data, mime);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Appl.context.startActivity(intent);
    }


    // http://stackoverflow.com/questions/6621789/how-to-open-an-excel-file-in-android
    public static Intent getDefaultViewIntent(Uri uri) {
        PackageManager pm = Appl.context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String name = (new File(uri.getPath())).getName();
        intent.setDataAndType(uri, Appl.getMimeType(name));
        final List<ResolveInfo> lri = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (lri.size() > 0)
            return intent;
        return null;
    }


    public static void loadMsaAll() {
        if (Appl.checkConnectToServer(true)) {
            Appl.download_in_progress = true;
            DB_ReceiveAll ar = new DB_ReceiveAll();
            ar.RecordsLimit = false;
            ar.execute();
        }
    }


    public static void ShowProgressIndicatior(Context c) {
        Appl.Indicator = new ProgressDialog(c);
        Appl.Indicator.setMessage(c.getResources().getString(R.string.s_wait));
        Appl.Indicator.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // STYLE_HORIZONTAL
        Appl.Indicator.show();
    }


}
