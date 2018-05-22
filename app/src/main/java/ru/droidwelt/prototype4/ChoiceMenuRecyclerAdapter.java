package ru.droidwelt.prototype4;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


class ChoiceMenuRecyclerAdapter extends RecyclerView.Adapter<ChoiceMenuRecyclerAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_choicemenu_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    @Override
    public int getItemCount() {
        return ChoiceMemuActivity.list_main.size();
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MainDataStructure myListItem = ChoiceMemuActivity.list_main.get(position);
        viewHolder.MAIN_ID = myListItem.main_id;
        viewHolder.tv_mainitem_title.setText(myListItem.main_title);
        viewHolder.iv_mainitem_image.setImageDrawable(myListItem.main_img);
        int color = Color.parseColor(myListItem.main_color);
        viewHolder.ly_mainitem.setBackgroundColor(color);   // внутренность
        viewHolder.cv_mainitem.setCardBackgroundColor(color);   // рамка
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_mainitem_title;
        ImageView iv_mainitem_image;
        LinearLayout ly_mainitem;
        android.support.v7.widget.CardView cv_mainitem;
        String MAIN_ID;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            tv_mainitem_title = itemLayoutView.findViewById(R.id.tv_mainitem_title);
            iv_mainitem_image = itemLayoutView.findViewById(R.id.iv_mainitem_image);
            ly_mainitem = itemLayoutView.findViewById(R.id.ly_mainitem);
            cv_mainitem = itemLayoutView.findViewById(R.id.cv_mainitem);
            itemLayoutView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            MainActivity.startActivity(MAIN_ID);
            ChoiceMemuActivity.getInstance().finish();
        }
    }


}