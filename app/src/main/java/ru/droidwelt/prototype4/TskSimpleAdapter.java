package ru.droidwelt.prototype4;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

class TskSimpleAdapter extends SimpleAdapter {

	private Context context;

	TskSimpleAdapter(Context context, List<? extends Map<String, ?>> data,
					 int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context; // ???
	}

	@Override
	public View getView(int pos, View inView, ViewGroup parent) {

		View row = super.getView(pos, inView, parent);
		if (row == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = mInflater != null ? mInflater.inflate(R.layout.activity_tsk, parent, false) : null;

			assert row != null;
			TextView listitem_name = row.findViewById(R.id.text_TSK_OK);
			String s = listitem_name.getText().toString();
			row.setBackgroundColor(Color.parseColor("#FFFFFF"));

			if (s.contains("выполнено")) {
				row.setBackgroundColor(Color.parseColor("#c5eec5"));
			}
			if (s.contains("сборка")) {
				row.setBackgroundColor(Color.parseColor("#f8f8c5"));
			}
			if (s.contains("принят")) {
				row.setBackgroundColor(Color.parseColor("#afc5f8"));
			}
			if (s.contains("черновик")) {
				row.setBackgroundColor(Color.parseColor("#dedede"));
			}
			if (s.contains("погрузка")) {
				row.setBackgroundColor(Color.parseColor("#fcd7a7"));
			}
		}
		return row;
	}

}