package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

class InspCursorAdapter extends SimpleCursorAdapter {

	private Context context;

	@SuppressWarnings("deprecation")
	InspCursorAdapter(Context context, int layout, Cursor c,
					  String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int pos, View inView, ViewGroup parent) {
		View v = super.getView(pos, inView, parent);

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater != null ? inflater.inflate(R.layout.activity_inspect, null) : null;
		}
		// int id = InspActivity.get_InspectAdapterd_ID(pos);
		 int index = InspActivity.getInstance().getInspectAdapter().getCursor().getColumnIndex(
					"RVV_STATE");
		String rvv_state = InspActivity.getInstance().getInspectAdapter().getCursor().getString(index);
	
		if (rvv_state.contains("N")) {
			if (v != null) {
				v.setBackgroundColor(Color.parseColor("#ffe8e8"));
			}
		} else
		if (rvv_state.contains("V")) {
			if (v != null) {
				v.setBackgroundColor(Color.parseColor("#e8ffe8"));
			}
		} else if (v != null) {
			v.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		return (v);
	}
	
	
	
	
}
