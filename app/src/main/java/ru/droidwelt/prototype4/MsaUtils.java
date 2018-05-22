package ru.droidwelt.prototype4;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;

class MsaUtils {
	

	
	// cоздать новую запись 
	static void create_new_draft() {
		String sSQL = 
			 " insert into MSA "
			+ " (MSA_ID,FIO_ID,MSA_STATE,MSA_DATE,MSA_GRP,MSA_TITLE,MSA_TEXT,MSA_FILETYPE,MSA_CLR,MSA_LBL) "
			+ " values " + " ('" + Appl.MSA_ID + "'," + Appl.FIO_ID
			+ ",0,datetime ('now', 'localtime'),'"
			+ Appl.APP_PREFIX + "','Новое','','','',''); ";
		Appl.getDatabase().execSQL(sSQL);

	}

	
	// переместить в черновики
	static void move_one_to_draft_SQLite(String xmsa_id) {
		if (!Appl.strnormalize(xmsa_id).equals("")) {
			String sSQL = "update MSA set MSA_STATE=0,FIO_ID="+Appl.FIO_ID+" where MSA_ID='" + xmsa_id+ "';";
			Appl.getDatabase().execSQL(sSQL);
		}
	}
	
	// копировать в черновики, избранное
	static String copy_one_to_folder_SQLite(String xmsa_id, int imsa_target) {
		String nes_MSA_ID = "";
		if (!Appl.strnormalize(xmsa_id).equals("")) {
			nes_MSA_ID = Appl.generate_GUID ();
			String sSQL = " insert into MSA "
					+ " (MSA_ID,FIO_ID,MSA_STATE,MSA_DATE,MSA_CLR,MSA_LBL,MSA_GRP,MSA_TITLE,MSA_TEXT,MSA_FILETYPE,MSA_FILENAME,MSA_IMAGE) "
					+ " select '" + nes_MSA_ID + "', "+Appl.FIO_ID+", "+imsa_target+","
					+ " datetime ('now', 'localtime'),MSA_CLR,MSA_LBL,MSA_GRP,MSA_TITLE,MSA_TEXT," +
					"case when length(MSA_IMAGE)>0 then MSA_FILETYPE else '' end as MSA_FILETYPE," +
					"case when length(MSA_IMAGE)>0 then MSA_FILENAME else '' end as MSA_FILENAME," +
					"MSA_IMAGE "
					+ "from MSA where MSA_ID='"+ xmsa_id+"';"	;			
			Appl.getDatabase().execSQL(sSQL);
			
			sSQL = " select FIO_ID from MSB where MSA_ID='"+ xmsa_id+"';";					
			Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
			c.moveToFirst();
			while (!c.isAfterLast()) {
				int fio_id = c.getInt(c.getColumnIndex("FIO_ID")); 				
				String sBSQL = "insert into MSB  (MSA_ID,FIO_ID)  values ('"+nes_MSA_ID+"',"+fio_id+");";
				Appl.getDatabase().execSQL(sBSQL);		
				c.moveToNext();
			}
			c.close();
		}
		return nes_MSA_ID;
	}
	
	// удалить выбранную запись
	static  void delete_selected_SQLite(String MSA_ID) {
		if (!Appl.strnormalize(MSA_ID).equals("")) {
		Appl.getDatabase().execSQL("delete from MSA where MSA_ID='"+MSA_ID + "'");
		}
	}
	
	
	//изменить статус записи (папку)
	static void change_record_state_SQLite(int new_state) {
		String sSQL = " update MSA set "
			//	+ " MSA_DATE=datetime ('now', 'localtime'),"
				+ " MSA_STATE = "+ new_state 
				+ " where MSA_ID='"+Appl.MSA_ID+"'; ";
		Appl.getDatabase().execSQL(sSQL);
	}


	//изменить статус всех записей папки
	static void change_all_record_state_SQLite(int old_state, int new_state) {
		String sSQL = " update MSA set "
				+ " MSA_STATE = "+ new_state
				+ " where MSA_STATE="+old_state+"; ";
		Appl.getDatabase().execSQL(sSQL);
	}



	// удалить одну рабочую запись 
	static void message_delete_from_SQLite() {
		Appl.getDatabase().execSQL("delete from MSB where MSA_ID='" + Appl.MSA_ID + "'");
		Appl.getDatabase().execSQL("delete from MSA where MSA_ID='" + Appl.MSA_ID + "'");
	}
	
	// составить список выбранных получателей в виде строки
	static String message_get_send_list() {
		StringBuilder res = new StringBuilder();
		String sSQL = 
				  " select  FIO_NAME " 
		        + " from MSB B "
				+ " left join FIO F on B.FIO_ID=F.FIO_ID " 
		        + " where MSA_ID='"+ Appl.MSA_ID+"'"  
		        + " order by FIO_NAME;";

		Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			res.append(c.getString(0)).append("; ");
			c.moveToNext();
		}
		c.close();
		return res.toString();
	}



	static boolean loadFIO() {
		if (Appl.isNetworkAvailable(false)) {
			JSONArray urls = Appl.query_MSSQL_Background("select FIO_IMAGE,FIO_ID,FIO_TP,FIO_IDGR,FIO_IDMB,FIO_NAME,FIO_SUBNAME from dbo.FIO order by FIO_NAME");
			if (!urls.toString().contains("######")) {
				try {
					String sLite = "DELETE FROM FIO;";
					Appl.getDatabase().execSQL(sLite);
					for (int i = 0; i < urls.length(); i++) {
						String FIO_ID = Appl.strnormalize(urls.getJSONObject(i).getString("FIO_ID"));
						String FIO_TP = Appl.strnormalize(urls.getJSONObject(i).getString("FIO_TP"));
						String FIO_IDGR = Appl.strnormalize(urls.getJSONObject(i).getString("FIO_IDGR"));
						String FIO_IDMB = Appl.strnormalize(urls.getJSONObject(i).getString("FIO_IDMB"));
						String FIO_NAME = Appl.strnormalize(urls.getJSONObject(i).getString("FIO_NAME"));
						String FIO_SUBNAME = Appl.strnormalize(urls.getJSONObject(i).getString("FIO_SUBNAME"));
						sLite =
								"INSERT INTO FIO  (FIO_ID,FIO_TP,FIO_IDGR,FIO_IDMB,FIO_NAME,FIO_SUBNAME) " +
										"VALUES ('" + FIO_ID + "','" + FIO_TP + "','" + FIO_IDGR + "','" + FIO_IDMB + "','" + FIO_NAME + "','" + FIO_SUBNAME + "');";
						Appl.getDatabase().execSQL(sLite);

						String resstr = urls.getJSONObject(i).getString("FIO_IMAGE");
						byte[] resall = Appl.hexToBytes(resstr);
						if (resall != null) {
							ContentValues editFIO = new ContentValues();
							editFIO.put("FIO_IMAGE", resall);
							Appl.getDatabase().update("FIO", editFIO, "FIO_ID='" + FIO_ID + "'", null);
						}
					}
				} catch (JSONException e) {
					Appl.DisplayToastError(R.string.s_bad_data);
					return false;
				}
			}
			return true;
		} else return false;
	}


	static boolean loadDEV() {
		if (Appl.isNetworkAvailable(false)) {
			JSONArray urls = Appl.query_MSSQL_Background("select DEV_ID,FIO_ID,DEV_FCM,DEV_CODE,DEV_MODEL from dbo.DEV where DEV_DIS=0 order by DEV_ID");
			if (!urls.toString().contains("######")) {
				try {
					String sLite = "DELETE FROM DEV;";
					Appl.getDatabase().execSQL(sLite);
					for (int i = 0; i < urls.length(); i++) {
						String DEV_ID = Appl.strnormalize(urls.getJSONObject(i).getString("DEV_ID"));
						String FIO_ID = Appl.strnormalize(urls.getJSONObject(i).getString("FIO_ID"));
						String DEV_FCM = Appl.strnormalize(urls.getJSONObject(i).getString("DEV_FCM"));
						String DEV_CODE = Appl.strnormalize(urls.getJSONObject(i).getString("DEV_CODE"));
						String DEV_MODEL = Appl.strnormalize(urls.getJSONObject(i).getString("DEV_MODEL"));
						sLite = "INSERT INTO DEV  (DEV_ID,FIO_ID,DEV_FCM,DEV_CODE,DEV_MODEL) " +
								"VALUES ('" + DEV_ID + "','" + FIO_ID + "','" + DEV_FCM + "','" + DEV_CODE + "','" + DEV_MODEL + "');";
						Appl.getDatabase().execSQL(sLite);
					}
				} catch (JSONException e) {
					Appl.DisplayToastError(R.string.s_bad_data);
					return false;
				}
			}
			return true;
		} else return false;
	}






}
