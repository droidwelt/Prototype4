package ru.droidwelt.prototype4;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


class ChoiceFioRecyclerAdapter extends RecyclerView.Adapter<ChoiceFioRecyclerAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_choicefio_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    @Override
    public int getItemCount() {
        return ChoiceFioActivity.list_fio.size();
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ChoiceFioStructure myListItem = ChoiceFioActivity.list_fio.get(position);
        viewHolder.FIO_ID = myListItem.fio_id;
        viewHolder.tv_fio_name.setText(myListItem.fio_name);
        String text = myListItem.fio_subname;
        if (text.isEmpty()) {
            viewHolder.tv_fio_subname.setText("");
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.tv_fio_subname.getLayoutParams();
            lp.height = 1;
            viewHolder.tv_fio_subname.setLayoutParams(lp);
        } else {
            viewHolder.tv_fio_subname.setText(fromHtml(text));
        }

        if (myListItem.fio_img != null) {
            viewHolder.iv_fio_image.setImageBitmap(myListItem.fio_img);
        } else {
            if (myListItem.fio_tp == 2) {
                viewHolder.iv_fio_image.setImageResource(R.mipmap.ic_avatar_one);
            } else {
                viewHolder.iv_fio_image.setImageResource(R.mipmap.ic_avatar_group);
            }
        }

        viewHolder.FIO_CHOICE = myListItem.fio_choice;
        if (myListItem.fio_choice.equals("1")) {
            viewHolder.iv_fio_choice.setImageResource(R.mipmap.ic_checked);
            viewHolder.ly_choicefioitem.setBackgroundColor(Color.parseColor("#fff9c4"));   // внутренность
            viewHolder.cv_choicefioitem.setCardBackgroundColor(Color.parseColor("#fff9c4"));   // рамка
        } else {
            viewHolder.iv_fio_choice.setImageResource(R.mipmap.ic_empty_96);
            viewHolder.ly_choicefioitem.setBackgroundColor(0xFFFFFFFF);  // внутренность
            viewHolder.cv_choicefioitem.setBackgroundColor(0xFFFFFFFF);   // рамка
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_fio_name, tv_fio_subname;
        ImageView iv_fio_image, iv_fio_choice;
        LinearLayout ly_choicefioitem;
        android.support.v7.widget.CardView cv_choicefioitem;
        String FIO_ID, FIO_CHOICE;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            tv_fio_name = itemLayoutView.findViewById(R.id.tv_fio_name);
            tv_fio_subname = itemLayoutView.findViewById(R.id.tv_fio_subname);
            iv_fio_image = itemLayoutView.findViewById(R.id.iv_fio_image);
            iv_fio_choice = itemLayoutView.findViewById(R.id.iv_fio_choice);
            ly_choicefioitem = itemLayoutView.findViewById(R.id.ly_choicefioitem);
            cv_choicefioitem = itemLayoutView.findViewById(R.id.cv_choicefioitem);
            itemLayoutView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            ChoiceFioActivity.setFilChecked(FIO_ID, FIO_CHOICE);
        }
    }


}