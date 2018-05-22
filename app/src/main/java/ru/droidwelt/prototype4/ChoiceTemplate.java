package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

public class ChoiceTemplate extends AppCompatActivity {

	private static ChoiceTemplateCursorAdapter myAdapter;
	private static int  _id=-1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Appl.setMyThemesDlg(this);
		setContentView(R.layout.activity_choicetemplate);	
		setTitle(getString(R.string.s_message_choice_template_header));
		ImageButton ib_ok = findViewById(R.id.ib_choiceTemplate_ok);
		ImageButton ib_cancel = findViewById(R.id.ib_choiceTemplate_cancel);
		ImageButton ib_load = findViewById(R.id.ib_choiceTemplate_load);
		ib_ok.setOnClickListener(oclBtnOk);
		ib_cancel.setOnClickListener( oclBtnOk);
		ib_load.setOnClickListener( oclBtnOk);
			
		OnItemClickListener tpl_ViewListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				_id=(int) (id);				
			} 
		}; 
						
		String[] from = new String[] { "TPL_TITLE","TPL_TEXT" };
		int[] to = new int[] { R.id.tv_choicetemplate_item_title,R.id.tv_choicetemplate_item_text };

		myAdapter =(new ChoiceTemplateCursorAdapter(this, R.layout.activity_choicetemplate_item, null, from, to));
		myAdapter.swapCursor(getTemplateRecords());
		ListView lv = findViewById(R.id.lv_choicetemplate);
		lv.setOnItemClickListener(tpl_ViewListener);	
		lv.setAdapter(myAdapter);
		lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
	}


	android.view.View.OnClickListener oclBtnOk = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {

				case R.id.ib_choiceTemplate_cancel:
					setResult(RESULT_CANCELED);
					finish();
					break;

				case R.id.ib_choiceTemplate_ok:
					if (_id >= 0) {
						String msa_text = "";
						Cursor c = Appl.getDatabase().rawQuery(
								"select TPL_TEXT from TPL where _id=" + _id, null);
						c.moveToFirst();
						if (c.getCount() > 0) msa_text = c.getString(0);
						c.close();
						MsaEditActivity.setMSA_TEXT(msa_text);
						setResult(RESULT_OK);
						finish();
					}
					break;

				case R.id.ib_choiceTemplate_load:
					if (Appl.checkConnectToServer(false)) {
						loadTpl();
						refreshRecords();
					}
					break;

				default:
					break;
			}
		}
	};	
	

	public static void refreshRecords() {
		myAdapter.changeCursor(getTemplateRecords());
		myAdapter.notifyDataSetChanged();		
	}
	
	
	public static Cursor getTemplateRecords() {		
		String sSQL = "select  _id, TPL_ID,TPL_TITLE,TPL_TEXT from TPL order by TPL_TITLE ";
		return Appl.getDatabase().rawQuery(sSQL, null);
	}

	public void loadTpl  () {
		String result = Appl.query_MSSQL_Background("select MSA_ID,MSA_TITLE,MSA_TEXT from dbo.MSA where MSA_STATE=1 order by MSA_TITLE").toString();
		if (!result.contains("######")) {
			try {
				String sLite = "DELETE FROM TPL;";
				Appl.getDatabase().execSQL(sLite);
				JSONArray urls = new JSONArray(result);
				for (int i = 0; i < urls.length(); i++) {
					String MSA_ID = urls.getJSONObject(i).getString("MSA_ID");
					String MSA_TITLE = Appl.strnormalize(urls.getJSONObject(i).getString("MSA_TITLE"));
					String MSA_TEXT = Appl.strnormalize(urls.getJSONObject(i).getString("MSA_TEXT"));
					sLite = "INSERT INTO TPL  (TPL_ID, TPL_TITLE, TPL_TEXT) VALUES ('"+MSA_ID+"','"+MSA_TITLE+"','"+MSA_TEXT+"');";
					Appl.getDatabase().execSQL(sLite);
				}
			} catch (JSONException e) {
				Appl.DisplayToastError(R.string.s_bad_data);
			}
		}
	}

	

	
	}