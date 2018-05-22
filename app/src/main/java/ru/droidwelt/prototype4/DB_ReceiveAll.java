package ru.droidwelt.prototype4;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*

 public static final int TDS42 = 1;
    public static final int TDS50 = 2;
    public static final int TDS70 = 3;
    public static final int TDS80 = 4;
    public static final int TDS81 = 5;
    public static final int TDS90 = 6;
    public static final int SQLSERVER = 1;
    public static final int SYBASE = 2;
    public static final String APPNAME = "prop.appname";
    public static final String AUTOCOMMIT = "prop.autocommit";
    public static final String BATCHSIZE = "prop.batchsize";
    public static final String BINDADDRESS = "prop.bindaddress";
    public static final String BUFFERDIR = "prop.bufferdir";
    public static final String BUFFERMAXMEMORY = "prop.buffermaxmemory";
    public static final String BUFFERMINPACKETS = "prop.bufferminpackets";
    public static final String CACHEMETA = "prop.cachemetadata";
    public static final String CHARSET = "prop.charset";
    public static final String DATABASENAME = "prop.databasename";
    public static final String DOMAIN = "prop.domain";
    public static final String INSTANCE = "prop.instance";
    public static final String LANGUAGE = "prop.language";
    public static final String LASTUPDATECOUNT = "prop.lastupdatecount";
    public static final String LOBBUFFER = "prop.lobbuffer";
    public static final String LOGFILE = "prop.logfile";
    public static final String LOGINTIMEOUT = "prop.logintimeout";
    public static final String MACADDRESS = "prop.macaddress";
    public static final String MAXSTATEMENTS = "prop.maxstatements";
    public static final String NAMEDPIPE = "prop.namedpipe";
    public static final String PACKETSIZE = "prop.packetsize";
    public static final String PASSWORD = "prop.password";
    public static final String PORTNUMBER = "prop.portnumber";
    public static final String PREPARESQL = "prop.preparesql";
    public static final String PROGNAME = "prop.progname";
    public static final String SERVERNAME = "prop.servername";
    public static final String SERVERTYPE = "prop.servertype";
    public static final String SOTIMEOUT = "prop.sotimeout";
    public static final String SOKEEPALIVE = "prop.sokeepalive";
    public static final String PROCESSID = "prop.processid";
    public static final String SSL = "prop.ssl";
    public static final String TCPNODELAY = "prop.tcpnodelay";
    public static final String TDS = "prop.tds";
    public static final String USECURSORS = "prop.usecursors";
    public static final String USEJCIFS = "prop.usejcifs";
    public static final String USENTLMV2 = "prop.usentlmv2";
    public static final String USEKERBEROS = "prop.usekerberos";
    public static final String USELOBS = "prop.uselobs";
    public static final String USER = "prop.user";
    public static final String SENDSTRINGPARAMETERSASUNICODE = "prop.useunicode";
    public static final String WSID = "prop.wsid";
    public static final String XAEMULATION = "prop.xaemulation";
 */

class DB_ReceiveAll extends AsyncTask<URL, Integer, Long> {

    boolean RecordsLimit;

    /*
off SSL is not request or used; this is the default
request SSL is requested; if the server does not support it then a plain connection is used
require SSL is requested; if the server does not support it then an exception is thrown
authenticate Same as require except the server's certificate must be signed by a trusted CA
 android:networkSecurityConfig="@xml/network_security_config"  TDS=8.0;trustServerCertificate=false;  hostNameInCertificate=192.168.1.*;loginTimeout=30


  <?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config>
        <domain includeSubdomains="true">ACOMP</domain>
        <domain includeSubdomains="true">192.168.1.4</domain>
        <trust-anchors>
            <certificates src="@raw/asql"/>
        </trust-anchors>
        <pin-set>
            <pin digest="SHA-256">Yd32lG4GYJ9ikXEFUBdwMn3iNh13eObCLA4dicFrNbE=</pin>
        </pin-set>

    </domain-config>
</network-security-config>

  */

    @Override
    protected Long doInBackground(URL... params) {
        publishProgress(0);
        int res = 0;
        if (Appl.getDatabase() == null) {
            Appl.startDbHelper();
        }
        Cursor c = Appl.getDatabase().rawQuery("select case when (MAX(MSA_NOMER) is null) then 0 else MAX(MSA_NOMER) end as MSA_NOMER from MSA where MSA_STATE in (2,22);", null);
        c.moveToFirst();
        int MSA_NOMERMAX = Integer.parseInt(c.getString(0));
        c.close();

        String sTop = "";
        if (RecordsLimit) sTop = " top " + Appl.getKvoDownLoad();

        String msSQL =
                "select " + sTop
                        + " B.MSA_ID,"  //0 
                        + " A.FIO_ID, "
                        + " MSA_NOMER,"  //2
                        + " ISNULL(MSA_CLR,'0') MSA_CLR,"  // 4 
                        + " ISNULL(MSA_LBL,'0') MSA_LBL,"
                        + " CONVERT(VARCHAR(30),MSA_DATE,120) as MSA_DATE,"
                        + " ISNULL(MSA_TITLE,'') as MSA_TITLE,"  // 6
                        + " ISNULL(MSA_TEXT,'') as MSA_TEXT,"   // 7
                        + " ISNULL(MSA_FILETYPE,'') as MSA_FILETYPE,"  // 8
                        + " ISNULL(MSA_FILENAME,'') as MSA_FILENAME"   // 9
                        + " from dbo.MSB B "
                        + " left join dbo.MSA A on B.MSA_ID=A.MSA_ID "
                        + " where MSA_STATE=2 and  MSA_DATE>(GETDATE()-" + Appl.getDaysDownLoad()
                        + ") and B.FIO_ID=" + Appl.FIO_ID
                        + " and A.MSA_NOMER>" + MSA_NOMERMAX
                        + " group by MSA_DATE,B.MSA_ID,A.FIO_ID, MSA_NOMER,"
                        + " MSA_CLR,MSA_LBL,MSA_LBL,MSA_TITLE,MSA_TEXT,MSA_FILETYPE, MSA_FILENAME"
                        + " order by MSA_DATE";

        JSONArray result = Appl.query_MSSQL_Exec(msSQL);
        publishProgress(1);
        List<String> list_msa_id = new ArrayList<>();

        final SQLiteDatabase mydb = Appl.getDatabase();
        boolean downLoadAtt = Appl.getDownloadAttachments();
        if (!result.toString().contains("######")) {
            Appl.getDatabase().rawQuery("PRAGMA journal_mode = 'MEMORY'; ", null);
            Appl.getDatabase().rawQuery("PRAGMA synchronous = 'OFF'; ", null);

            for (int i = 0; i < result.length(); i++)
                try {
                    res++;
                    //   if (res == 50 ) publishProgress(2);
                    JSONObject js = result.getJSONObject(i);
                    int FIO_ID = Integer.parseInt(js.getString("FIO_ID"));
                    String MSA_ID = js.getString("MSA_ID");
                    String MSA_DATE = js.getString("MSA_DATE");
                    String MSA_TITLE = js.getString("MSA_TITLE");
                    String MSA_TEXT = js.getString("MSA_TEXT");
                    String MSA_FILETYPE = js.getString("MSA_FILETYPE");
                    String MSA_FILENAME = js.getString("MSA_FILENAME");
                    String MSA_CLR = js.getString("MSA_CLR");
                    String MSA_LBL = js.getString("MSA_LBL");
                    if ((downLoadAtt) & (!MSA_FILETYPE.equals(""))) list_msa_id.add(MSA_ID);
                    int MSA_NOMER;
                    try {
                        MSA_NOMER = Integer.parseInt(Appl.strnormalize(js.getString("MSA_NOMER")));
                    } catch (NumberFormatException e) {
                        MSA_NOMER = 0;
                    }

                    String sLite =
                            "INSERT INTO MSA  (MSA_ID, MSA_NOMER, MSA_CLR, MSA_LBL, FIO_ID, MSA_STATE, MSA_DATE, " +
                                    "MSA_TITLE ,MSA_TEXT, MSA_FILETYPE, MSA_FILENAME) " +
                                    "VALUES ('" + MSA_ID + "'," + MSA_NOMER + ",'" + MSA_CLR + "','" + MSA_LBL + "'," + FIO_ID + ",2,'" + MSA_DATE + "','" +
                                    MSA_TITLE + "','" + MSA_TEXT + "','" + MSA_FILETYPE + "','" + MSA_FILENAME + "');";
                    mydb.execSQL(sLite);

                } catch (JSONException ignored) {
                }
            Appl.getDatabase().rawQuery("PRAGMA journal_mode = 'DELETE'; ", null);
            Appl.getDatabase().rawQuery("PRAGMA synchronous = 'ON'; ", null);
        }

        publishProgress(2);

        if (downLoadAtt) {
            for (int i = 0; i < list_msa_id.size(); i++) {
                if (Appl.getDownloadAttachments()) {
                    JSONArray resultImg = Appl.query_MSSQL_Exec("MSAIMAGEGET_STR '" + list_msa_id.get(i) + "'");
                    if (!resultImg.toString().contains("######")) try {
                        String resstr = resultImg.getJSONObject(0).getString("MSA_IMAGE");
                        byte[] resall = Appl.hexToBytes(resstr);
                        ContentValues ediMessage = new ContentValues();
                        ediMessage.put("MSA_IMAGE", resall);
                        Appl.getDatabase().update("MSA", ediMessage, "MSA_ID='" + list_msa_id.get(i) + "'", null);
                    } catch (JSONException ignored) {
                    }
                }
            }
            publishProgress(2);
        }
        return (long) res;
    }

    protected void onProgressUpdate(Integer... progress) {
        if ((progress[0] == 2) & (Appl.MSA_MODEVIEW == 2)) MsaMainActivity.refreshRecords();
    }

    protected void onPostExecute(Long result) {
        Appl.download_in_progress = false;

        MainActivity.getInstance().getBoxCount();
        MainActivity.getInstance().refreshMainMenu();
        try {
            Appl.Indicator.dismiss();
        } catch (Exception ignored) {
        }
    }

}
