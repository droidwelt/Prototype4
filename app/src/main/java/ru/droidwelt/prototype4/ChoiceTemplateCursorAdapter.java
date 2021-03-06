package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;


 class ChoiceTemplateCursorAdapter extends SimpleCursorAdapter {

	private Context context;

	@SuppressWarnings("deprecation")
	 ChoiceTemplateCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
	}


	@SuppressLint("InflateParams")
	@Override
	public View getView(int pos, View inView, ViewGroup parent) {
		View v = super.getView(pos, inView, parent);

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater != null ? inflater.inflate(R.layout.activity_choicetemplate, null) : null;
		}
		//int id = ClientActivity.get_ClientAdapterd_ID(pos);					
		return (v);
	}
}

