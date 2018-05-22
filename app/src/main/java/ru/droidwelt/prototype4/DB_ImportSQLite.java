package ru.droidwelt.prototype4;

import android.content.ContentValues;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


class DB_ImportSQLite extends AsyncTask < URL, Integer, Long > {
	
	String MSA_FILETYPE="", MSA_FILENAME="",MSA_FULLFILENAME="";
	private int  bytesRead = 0;
		
	@Override
	protected Long doInBackground(URL... params) {
		publishProgress(0);
		byte[] resall = null;
		int btR;
		try {
			InputStream stream = new FileInputStream(MSA_FULLFILENAME);
			try {
				byte[] buffer = new byte [1024 * 100];
				while ((btR = stream.read(buffer)) > 0) {
					resall = Appl.concatArray(resall, buffer);
					bytesRead = bytesRead + btR;
				}

			} catch (IOException ignored) {
			}
			try {
				stream.close();
			} catch (IOException ignored) {
			}

			ContentValues editMessage = new ContentValues();
			editMessage.put("MSA_IMAGE", resall);
			Appl.getDatabase().update("MSA", editMessage, "MSA_ID='" + Appl.MSA_ID + "'", null);
		} catch (FileNotFoundException ignored) {
		}

		return (long) bytesRead;
	}
	
	

	protected void onPostExecute(Long result) {
		try {
			MsaEditActivity.et_msa_filename.setText(MSA_FILENAME);
			MsaEditActivity.tv_msa_filetype.setText(MSA_FILETYPE);
			MsaEditActivity.MSA_FILETYPE=MSA_FILETYPE;
			MsaEditActivity.MSA_FILENAME=MSA_FILENAME;
			MsaEditActivity.MSA_IMAGESIZE=bytesRead;
			MsaEditActivity.message_save_to_SQLite_header(0);
			MsaEditActivity.setVisibleInfo();
		} catch (Exception ignored) {
		}
	}


}
