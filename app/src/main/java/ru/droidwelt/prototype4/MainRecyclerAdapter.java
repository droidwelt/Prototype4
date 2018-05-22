package ru.droidwelt.prototype4;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    @Override
    public int getItemCount() {
        return MainActivity.list_main.size();
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MainDataStructure myListItem = MainActivity.list_main.get(position);
        viewHolder.MAIN_ID = myListItem.main_id;
        viewHolder.tv_mainitem_title.setText(myListItem.main_title);
        String text = myListItem.main_text;
        if ((text == null) || (text.isEmpty())) {
            //  viewHolder.tv_mainitem_text.setVisibility(View.GONE);
            viewHolder.tv_mainitem_text.setText("");
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.tv_mainitem_text.getLayoutParams();
            lp.height = 1;
            viewHolder.tv_mainitem_text.setLayoutParams(lp);
          //  Log.i("WWWWWW-onBindViewHolder", myListItem.main_title+"  E_M_P_T_Y");
        } else {
          //  Log.i("WWWWWW-onBindViewHolder", myListItem.main_title+" "+text);
            viewHolder.tv_mainitem_text.setText(Html.fromHtml(text));
        }
        viewHolder.iv_mainitem_image.setImageDrawable(myListItem.main_img);
        int color = Color.parseColor(myListItem.main_color);
        viewHolder.ly_mainitem.setBackgroundColor(color);   // внутренность
        viewHolder.cv_mainitem.setCardBackgroundColor(color);   // рамка
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_mainitem_title, tv_mainitem_text;
        ImageView iv_mainitem_image;
        LinearLayout ly_mainitem;
        android.support.v7.widget.CardView cv_mainitem;
        String MAIN_ID;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            tv_mainitem_title = itemLayoutView.findViewById(R.id.tv_mainitem_title);
            tv_mainitem_text = itemLayoutView.findViewById(R.id.tv_mainitem_text);
            iv_mainitem_image = itemLayoutView.findViewById(R.id.iv_mainitem_image);
            ly_mainitem = itemLayoutView.findViewById(R.id.ly_mainitem);
            cv_mainitem = itemLayoutView.findViewById(R.id.cv_mainitem);
            itemLayoutView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            MainActivity.startActivity(MAIN_ID);
        }
    }


}