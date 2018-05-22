package ru.droidwelt.prototype4;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.Date;

public class OplActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

	private  OplCursorAdapter myAdapter;
	public  static String filter="";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Appl.setMyThemesNoActionBar(this);
		setContentView(R.layout.activity_opl);

        Toolbar tb_opl = findViewById(R.id.tb_opl);
        setSupportActionBar(tb_opl);
        ImageButton ib_opl_mainmenu = findViewById(R.id.ib_opl_mainmenu);
        ib_opl_mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewMenu = new Intent(OplActivity.this, ChoiceMemuActivity.class);
                viewMenu.putExtra("MODE", "OPL");
                startActivity(viewMenu);
            }
        });

		displayLoadDateTime();

		String[] from = new String[] { "CLT_NAME","OPL_DATE","OPL_NAME","OPL_SUM" };
		int[] to = new int[] { R.id.text_OPL_CLT,R.id.text_OPL_DATE,R.id.text_OPL_NAME,R.id.text_OPL_SUM };

		setmyAdapter(new OplCursorAdapter(this, R.layout.activity_opl_item, null, from, to));
		ListView lv = findViewById(R.id.lv_opl);
		lv.setOnItemClickListener(viewContactListener);
		lv.setAdapter(getOplAdapter());
		getLoaderManager().initLoader(0, null, this);
		lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		EditText editFilter = findViewById(R.id.editText_opl_Filter);
		editFilter.setText (filter);
		editFilter.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filter=s.toString();
				getOplAdapter().changeCursor(getOplRecords(filter));
				getOplAdapter().notifyDataSetChanged();
			}
		});
	}


	// слушатель событий в ListView
	OnItemClickListener viewContactListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		/*	Intent viewContact = new Intent(Main_Activity.this,
					View_Activity.class);
			viewContact.putExtra("CONTACT_ID", id);
			WMA.setMAS_ID(id);
			startActivityForResult(viewContact, CONT_MODIFIED);
			WMA.animateStart(Main_Activity.this);*/
		}
	};



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_opl, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.mi_opl_download:
			if (Appl.checkConnectToServer(true)) {
                Appl.ShowProgressIndicatior (this);
				new MyTaskOpl(this).execute();
			}
			return true;

		case android.R.id.home:
			finish();
			Appl.animateFinish(OplActivity.this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private static class MyTaskOpl extends AsyncTask<Void, Void, String> {
		private WeakReference<OplActivity> activityReference;
		MyTaskOpl(OplActivity context) {
			activityReference = new WeakReference<>(context);
		}

        @Override
        protected String doInBackground(Void... params) {
            OplActivity activity = activityReference.get();
            activity.loadClt();
            activity.loadOpl();
            return "END";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Appl.Indicator.dismiss();
            } catch (Exception ignored) {
            }
            OplActivity activity = activityReference.get();
            if (activity == null) return;

            activity.getOplAdapter().changeCursor(getOplRecords(filter));
            activity.setLoadDateTime();
            activity.displayLoadDateTime();
            MainActivity.getInstance().refreshMainMenu();
        }
    }



    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new MyCursorLoader(this);
	}


	private static class MyCursorLoader extends CursorLoader {
		MyCursorLoader(Context context) {
			super(context);
		}

		@Override
		public Cursor loadInBackground() {
			return getOplRecords(filter);
		}
	}

	public static Cursor getOplRecords(String filter) {
		String sSQL =
				"select  O._id as _id, 0 as ORD,OPL_ID,CLT_NAME, date(OPL_DATE) as OPL_DATE, "
				+ "OPL_NAME,ROUND(OPL_SUM,0) as OPL_SUM "
				+ "from OPL O "
				+ "left join CLT C on O.CLT_ID=C.CLT_ID "
				+ "where C.CLT_NAME like '%"+filter+"%' "
				+ "union all "
				+ "select  99999,1 as ORD,-1 as OPL_ID,'Итого :' as CLT_NAME,'' as OPL_DATE,'' as OPL_NAME,SUM(OPL_SUM) as OPL_SUM "
				+ "from OPL O "
				+ "left join CLT C on O.CLT_ID=C.CLT_ID "
				+ "where C.CLT_NAME like '%"+filter+"%' "
				+ "order by ORD,CLT_NAME,OPL_DATE ";
		return Appl.getDatabase().rawQuery(sSQL, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		getOplAdapter().swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}


	public  OplCursorAdapter getOplAdapter() {
		return myAdapter;
	}

	public  void setmyAdapter(OplCursorAdapter oplatAdapter) {
		myAdapter = oplatAdapter;
	}

	public void loadClt  () {
		JSONArray urls = Appl.query_MSSQL_Simple("select CLT_ID,CLT_NAME,CLT_ADDR,CLT_PHONE from dbo.CLT order by CLT_NAME");
		if (!urls.toString().contains("######")) {
			try {
				Appl.getDatabase().execSQL("DELETE FROM CLT");
				for (int i = 0; i < urls.length(); i++) {
					String CLT_ID = urls.getJSONObject(i).getString("CLT_ID");
					String CLT_NAME = Appl.strnormalize(urls.getJSONObject(i).getString("CLT_NAME"));
					String CLT_ADDR = Appl.strnormalize(urls.getJSONObject(i).getString("CLT_ADDR"));
					String CLT_PHONE = Appl.strnormalize(urls.getJSONObject(i).getString("CLT_PHONE"));
					String sLite =
							"INSERT INTO CLT  (CLT_ID, CLT_NAME, CLT_ADDR, CLT_PHONE) "+
									"VALUES ('"+CLT_ID+"','"+CLT_NAME+"','"+CLT_ADDR+"','"+CLT_PHONE+"');";
					Appl.getDatabase().execSQL(sLite);
				}

			} catch (JSONException e) {
				Appl.DisplayToastError(R.string.s_bad_data);
			}
		}
	}

	public void loadOpl  () {
		JSONArray urls = Appl.query_MSSQL_Simple("OPL_LIST");
		if (!urls.toString().contains("######")) {
			try {
				Appl.getDatabase().execSQL("DELETE FROM OPL");
				for (int i = 0; i < urls.length(); i++) {
					String OPL_ID = urls.getJSONObject(i).getString("OPL_ID");
					String CLT_ID = urls.getJSONObject(i).getString("CLT_ID");
					String OPL_NAME = Appl.strnormalize(urls.getJSONObject(i).getString("OPL_NAME"));
					String OPL_SUM = Appl.strnormalize(urls.getJSONObject(i).getString("OPL_SUM"));
					String OPL_DATE = Appl.strnormalize(urls.getJSONObject(i).getString("OPL_DATE"));
					String sLite =
							"INSERT INTO OPL  (OPL_ID,CLT_ID, OPL_NAME, OPL_SUM, OPL_DATE) "+
									"VALUES ('"+OPL_ID+"','"+CLT_ID+"','"+OPL_NAME+"','"+OPL_SUM+"','"+OPL_DATE+"');";
					Appl.getDatabase().execSQL(sLite);
				}
			} catch (JSONException e) {
				Appl.DisplayToastError(R.string.s_bad_data);
			}
		}
	}

	private void displayLoadDateTime() {
		TextView tv = findViewById(R.id.textView_OplLastLoad);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String s = getString(R.string.s_lastload)+sp.getString("opl_dateload",getString(R.string.s_time_not_defined) );
		tv.setText(s);
	}

	private void setLoadDateTime() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String sdate = DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString();
		Editor editor = sp.edit();
		editor.putString("opl_dateload", sdate);
		editor.apply();
	}



}
