package ru.droidwelt.prototype4;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;

public class RegisterDeviceActivity extends AppCompatActivity {

	private static String s ="";
	private  ImageView bln_1,bln_2,bln_3,bln_4,bln_5,bln_6,bln_7,bln_8;

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Appl.setMyThemesDlg(this);
		setContentView(R.layout.activity_register_device);
		setTitle(getString(R.string.header_register_device));
		ImageButton btn_Ok,btn_0,btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,btn_Back;
		bln_1 = findViewById(R.id.imageView_idcs1);
		bln_2 = findViewById(R.id.imageView_idcs2);
		bln_3 = findViewById(R.id.imageView_idcs3);
		bln_4 = findViewById(R.id.imageView_idcs4);
		bln_5 = findViewById(R.id.imageView_idcs5);
		bln_6 = findViewById(R.id.imageView_idcs6);
		bln_7 = findViewById(R.id.imageView_idcs7);
		bln_8 = findViewById(R.id.imageView_idcs8);
		
		btn_Ok = findViewById(R.id.imageButton_idcwok);
		btn_Back = findViewById(R.id.imageButton_idcwback);
		btn_0 = findViewById(R.id.clr12);
		btn_1 = findViewById(R.id.imageButton_idcw1);
		btn_2 = findViewById(R.id.imageButton_idcw2);
		btn_3 = findViewById(R.id.imageButton_idcw3);
		btn_4 = findViewById(R.id.imageButton_idcw4);
		btn_5 = findViewById(R.id.imageButton_idcw5);
		btn_6 = findViewById(R.id.clr6);
		btn_7 = findViewById(R.id.imageButton_idcw7);
		btn_8 = findViewById(R.id.imageButton_idcw8);
		btn_9 = findViewById(R.id.imageButton_idcw9);
		btn_Ok.setOnClickListener(onclickButton);
		btn_Back.setOnClickListener(onclickButton);
		btn_0.setOnClickListener(onclickButton);
		btn_1.setOnClickListener(onclickButton);
		btn_2.setOnClickListener(onclickButton);
		btn_3.setOnClickListener(onclickButton);
		btn_4.setOnClickListener(onclickButton);
		btn_5.setOnClickListener(onclickButton);
		btn_6.setOnClickListener(onclickButton);
		btn_7.setOnClickListener(onclickButton);
		btn_8.setOnClickListener(onclickButton);
		btn_9.setOnClickListener(onclickButton);
		s= "";
	}

	

	public void DisplayBln () {
	 if (s.length() >= 1) {bln_1.setImageResource(R.mipmap.ic_dlg_full); } else {bln_1.setImageResource(R.mipmap.ic_dlg_empty);}
	 if (s.length() >= 2) {bln_2.setImageResource(R.mipmap.ic_dlg_full); } else {bln_2.setImageResource(R.mipmap.ic_dlg_empty);}
	 if (s.length() >= 3) {bln_3.setImageResource(R.mipmap.ic_dlg_full); } else {bln_3.setImageResource(R.mipmap.ic_dlg_empty);}
	 if (s.length() >= 4) {bln_4.setImageResource(R.mipmap.ic_dlg_full); } else {bln_4.setImageResource(R.mipmap.ic_dlg_empty);}
	 if (s.length() >= 5) {bln_5.setImageResource(R.mipmap.ic_dlg_full); } else {bln_5.setImageResource(R.mipmap.ic_dlg_empty);}
	 if (s.length() >= 6) {bln_6.setImageResource(R.mipmap.ic_dlg_full); } else {bln_6.setImageResource(R.mipmap.ic_dlg_empty);}
	 if (s.length() >= 7) {bln_7.setImageResource(R.mipmap.ic_dlg_full); } else {bln_7.setImageResource(R.mipmap.ic_dlg_empty);}
	 if (s.length() >= 8) {bln_8.setImageResource(R.mipmap.ic_dlg_full); } else {bln_8.setImageResource(R.mipmap.ic_dlg_empty);}
	}


	android.view.View.OnClickListener onclickButton = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {

				case R.id.imageButton_idcwok:
					InitDev_Exec_Querty();
					break;

				case R.id.clr12:
					s = s + "0";
					DisplayBln();
					break;

				case R.id.imageButton_idcw1:
					s = s + "1";
					DisplayBln();
					break;

				case R.id.imageButton_idcw2:
					s = s + "2";
					DisplayBln();
					break;

				case R.id.imageButton_idcw3:
					s = s + "3";
					DisplayBln();
					break;

				case R.id.imageButton_idcw4:
					s = s + "4";
					DisplayBln();
					break;

				case R.id.imageButton_idcw5:
					s = s + "5";
					DisplayBln();
					break;

				case R.id.clr6:
					s = s + "6";
					DisplayBln();
					break;

				case R.id.imageButton_idcw7:
					s = s + "7";
					DisplayBln();
					break;

				case R.id.imageButton_idcw8:
					s = s + "8";
					DisplayBln();
					break;

				case R.id.imageButton_idcw9:
					s = s + "9";
					DisplayBln();
					break;

				case R.id.imageButton_idcwback:
					if (s.length() > 0) {
						s = s.substring(0, s.length() - 1);
						DisplayBln();
					}
					break;

				default:
					break;
			}
		}
	};


	public void InitDev_Exec_Querty() {
		Appl.getMSSQLConnectionSettings();
		Appl.ShowProgressIndicatior(this);
		Appl.SExec = "DEV_TRYINITFCM '" + s + "','" + Appl.DEVICE_FCM + "','"
				+ (Appl.DEVICE_MODEL + " " + Appl.DEVICE_BRAND) + "'";
		new MyTaskRegisterDevice(this).execute();
	}


	private static class MyTaskRegisterDevice extends AsyncTask<Void, Void, JSONArray> {
		private WeakReference<RegisterDeviceActivity> activityReference;
		MyTaskRegisterDevice(RegisterDeviceActivity context) {	activityReference = new WeakReference<>(context);
		}

		@Override
		protected JSONArray doInBackground(Void... params) {
			//RegisterDeviceActivity activity = activityReference.get();
			return Appl.query_MSSQL_Simple (Appl.SExec);
		}

		@Override
		protected void onPostExecute(JSONArray urls) {
			try {
				Appl.Indicator.dismiss();
			} catch (Exception ignored) {
			}
			RegisterDeviceActivity activity = activityReference.get();
			if (activity == null) return;

			int iRES = 0;
			try {
				if (urls.length() > 0) {
					for (int i = 0; i < urls.length(); i++) {
						iRES = Integer.parseInt(urls.getJSONObject(i).getString("CNT"));
					}
				}
			} catch (JSONException ignored) {
			}

			if (iRES == 1) {
				Appl.DisplayToastOk(Appl.context.getString(R.string.registation_ok));
				activity.finish();
			} else {
				Appl.DisplayToastError(Appl.context.getString(R.string.registation_fail));
			}
		}
	}




}