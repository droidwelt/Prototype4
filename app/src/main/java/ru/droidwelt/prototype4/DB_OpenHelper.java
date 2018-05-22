package ru.droidwelt.prototype4;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DB_OpenHelper extends SQLiteOpenHelper {

	private static DB_OpenHelper sDB_OpenHelper;
	private static Context context;
	private static String DB_NAME;
	public static SQLiteDatabase database;

    // ----------------------------------------------------------------------------------------------------------------------

	public static DB_OpenHelper get(Context context, String dbName) {
		if (sDB_OpenHelper == null)
            sDB_OpenHelper = new DB_OpenHelper(context.getApplicationContext(), dbName);
		return sDB_OpenHelper;
	}
	
	

	DB_OpenHelper(Context context, String dbName) {
		super(context, dbName, null, 1);
		DB_OpenHelper.context = context;
		File PATH_TO = new File(Appl.DB_PATH); // куда
		if (!(PATH_TO.isDirectory() && PATH_TO.canExecute() && PATH_TO.canRead() && PATH_TO.canWrite()))
        { if (!PATH_TO.mkdir())
            return;}
		DB_NAME = dbName;
		database = openDataBase();
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

    /*	public long getDatabaseSize() {
		File file = new File(Appl.DB_PATH + DB_NAME);
		return file.length() / 1024 / 1024;
	}*/


    private static void copyDataBase() {
        try {
            InputStream externalDbStream = context.getAssets().open(Appl.DB_NAMEMODEL);
            String outFileName = Appl.DB_PATH + DB_NAME;
            OutputStream localDbStream = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = externalDbStream.read(buffer)) > 0)
                localDbStream.write(buffer, 0, bytesRead);
            localDbStream.close();
            externalDbStream.close();
        } catch (IOException ignored) {
        }
    }


    private static boolean checkDataBase() {
        SQLiteDatabase checkDb;
        try {
            checkDb = SQLiteDatabase.openDatabase(Appl.DB_PATH + DB_NAME,
                    null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            //Log.i("checkDataBase", "checkDataBase  - no file");
            return false;
        }
        if (checkDb != null) checkDb.close();
        return checkDb != null;
    }

    private static SQLiteDatabase openDataBase() throws SQLException {
        if (database == null) {
            if (!checkDataBase()) {
                Appl.DisplayToastOk(context.getResources().getString(
                        R.string.s_db_create));
                copyDataBase();
            }
            try {
                database = SQLiteDatabase.openDatabase(Appl.DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            } catch (SQLException e) {
               // Log.e("SQLiteDatabase", "openDataBase");
            }
        }
        return database;
    }


/*	public static void closeDatabase() {
		if (database != null) {
			database.close();
		}
	}*/

    @Override
    public synchronized void close() {
        if (database != null) database.close();
        super.close();
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}



}