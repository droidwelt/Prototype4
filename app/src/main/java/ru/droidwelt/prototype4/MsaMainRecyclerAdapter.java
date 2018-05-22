package ru.droidwelt.prototype4;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


class MsaMainRecyclerAdapter extends RecyclerView.Adapter<MsaMainRecyclerAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_msa_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    @Override
    public int getItemCount() {
        return MsaMainActivity.list_msa.size();
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MsaMainDataStructure myListItem = MsaMainActivity.list_msa.get(position);
        viewHolder.MSA_ID = myListItem.msa_id;
        viewHolder.MSA_FILENAME = myListItem.msa_filename;
        viewHolder.MSA_FILETYPE = myListItem.msa_filetype;
        viewHolder.MSA_IMAGESIZE = myListItem.imagesize;
        viewHolder.tv_mesitem_title.setText(myListItem.msa_title);
        viewHolder.tv_mesitem_text.setText(myListItem.msa_text);
        viewHolder.tv_mesitem_fioname.setText(myListItem.fio_name);
        viewHolder.tv_mesitem_date.setText(myListItem.msa_date);
        viewHolder.tiv_msa_view.setImageResource(R.mipmap.ic_1x1);

        String sClr = myListItem.msa_clr;
        //   viewHolder.tv_mesitem_title.setBackgroundColor(Appl.getColorByString(sClr));
        viewHolder.iv_mesitem_layout.setBackgroundColor(Appl.getColorByString(sClr));  // внутренность
        viewHolder.iv_mesitem_cv.setCardBackgroundColor(Appl.getColorByString(sClr));  // рамка

        String MSA_LBL = myListItem.msa_lbl;
        viewHolder.iv_mesitem_lbl.setImageDrawable(Appl.getLabelDrawableByNomer(MSA_LBL, 0));

        String filetype = myListItem.msa_filetype;
        if (filetype.equals("")) {
            viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_empty);  // clip_empty   color_msa_rv_selected
        } else {
            if (viewHolder.MSA_IMAGESIZE > 0) {
                viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_other);
                if (filetype.equalsIgnoreCase("doc"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_doc);
                if (filetype.equalsIgnoreCase("docx"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_doc);
                if (filetype.equalsIgnoreCase("pdf"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_pdf);
                if (filetype.equalsIgnoreCase("zip"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_zip);
                if (filetype.equalsIgnoreCase("xls"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_xls);
                if (filetype.equalsIgnoreCase("xlsx"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_xls);
                if (filetype.equalsIgnoreCase("mp4"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_video);
                if (filetype.equalsIgnoreCase("mp3"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_audio);
                if (filetype.equalsIgnoreCase("jpg"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_image);
            } else {
                viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_other);
                if (filetype.equalsIgnoreCase("doc"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_doc_gray);
                if (filetype.equalsIgnoreCase("docx"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_doc_gray);
                if (filetype.equalsIgnoreCase("pdf"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_pdf_gray);
                if (filetype.equalsIgnoreCase("zip"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_zip_gray);
                if (filetype.equalsIgnoreCase("xls"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_xls_gray);
                if (filetype.equalsIgnoreCase("xlsx"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_xls_gray);
                if (filetype.equalsIgnoreCase("mp4"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_video_gray);
                if (filetype.equalsIgnoreCase("mp3"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_audio_gray);
                if (filetype.equalsIgnoreCase("jpg"))
                    viewHolder.iv_mesitem_filetype.setImageResource(R.mipmap.ic_filetype_image_gray);
            }
        }

        int fio_id = myListItem.fio_id;
        if (fio_id > 0) {
            Bitmap theImage = null;
            for (int i = 0; i < (Appl.afio_id.size() - 1); i++)
                if (Appl.afio_id.get(i) == fio_id) {
                    theImage = Appl.afio_image.get(i);
                    if (theImage != null) {
                        //  theImage = Bitmap.createScaledBitmap(theImage, Appl.FIO_AVATAR_SIZE, Appl.FIO_AVATAR_SIZE, false);
                        viewHolder.iv_mesitem_image.setImageBitmap(theImage);
                    }
                    break;
                }
            if (theImage == null) {
                viewHolder.iv_mesitem_image.setImageResource(R.mipmap.ic_avatar_unknown);
            }
        } else {
            viewHolder.iv_mesitem_image.setImageResource(R.mipmap.ic_avatar_unknown);
        }
        viewHolder.itemView.setLongClickable(true);
    }


    static void load_image_from_SQLite(final TouchImageView tiv_msa_view, String MSA_ID) {
        int i = 0;
        int theImagePos = 0;
        int rdbytes = 200000;
        byte[] resall = null;

        while (rdbytes == 200000 & i < 3000) {
            Cursor cursor = Appl.getDatabase().rawQuery(
                    "select substr(MSA_IMAGE,1+" + String.valueOf(i)
                            + "*200000,200000) from MSA  where MSA_ID='"
                            + MSA_ID + "'", null);
            i = i + 1;
            cursor.moveToFirst();
            byte[] res;
            rdbytes = cursor.getBlob(0).length;
            if (rdbytes > 0) {
                res = cursor.getBlob(0);
                resall = Appl.concatArray(resall, res);
                theImagePos = theImagePos + res.length;
            }
            cursor.close();
        }

        if (resall != null) {
            final Bitmap imageBig = BitmapFactory.decodeByteArray(resall, 0, resall.length);
            tiv_msa_view.setEnabled(true);
            new Thread(new Runnable() {
                public void run() {
                    tiv_msa_view.post(new Runnable() {
                        public void run() {
                            tiv_msa_view.setImageBitmap(imageBig);
                        }
                    });
                }
            }).start();
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder /*implements  View.OnClickListener,View.OnLongClickListener */ {

        TextView tv_mesitem_title, tv_mesitem_text, tv_mesitem_fioname, tv_mesitem_date;
        ImageView iv_mesitem_lbl, iv_mesitem_filetype, iv_mesitem_image;
        LinearLayout iv_mesitem_layout;
        android.support.v7.widget.CardView iv_mesitem_cv;
        public String MSA_ID, MSA_FILETYPE, MSA_FILENAME;
        int MSA_IMAGESIZE;
        TouchImageView tiv_msa_view;


        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            tv_mesitem_title = itemLayoutView.findViewById(R.id.tv_mesitem_title);
            tv_mesitem_text = itemLayoutView.findViewById(R.id.tv_mesitem_text);
            tv_mesitem_fioname = itemLayoutView.findViewById(R.id.tv_mesitem_fioname);
            tv_mesitem_date = itemLayoutView.findViewById(R.id.tv_mesitem_date);
            iv_mesitem_lbl = itemLayoutView.findViewById(R.id.iv_mesitem_lbl);
            iv_mesitem_filetype = itemLayoutView.findViewById(R.id.iv_mesitem_filetype);
            iv_mesitem_image = itemLayoutView.findViewById(R.id.iv_mesitem_image);
            iv_mesitem_layout = itemLayoutView.findViewById(R.id.iv_mesitem_layout);
            iv_mesitem_cv = itemLayoutView.findViewById(R.id.iv_mesitem_cv);
            tiv_msa_view = itemLayoutView.findViewById(R.id.tiv_msa_view);
          /*  itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Appl.getContext(), "Click", Toast.LENGTH_SHORT).show();
                }
            });*/

            itemLayoutView.setOnTouchListener(new OnSwipeTouchListener(itemLayoutView.getContext()) {

                public void onSwipeTop() {
                    //  Toast.makeText(Appl.getContext(), "top", Toast.LENGTH_SHORT).show();
                }

                public void onSwipeRight() {
                    //   Toast.makeText(Appl.getContext(), "right", Toast.LENGTH_SHORT).show();
                    if (!MSA_ID.isEmpty() & (Appl.MSA_MODEVIEW == 22)) {
                        Appl.MSA_ID = MSA_ID;
                        Appl.MSA_POS = getAdapterPosition();
                            MsaUtils.change_record_state_SQLite(2);
                            MsaMainActivity.refreshRecords();
                    }
                }

                public void onSwipeLeft() {
                    //   Toast.makeText(Appl.getContext(), "left", Toast.LENGTH_SHORT).show();
                    if (!MSA_ID.isEmpty() & (Appl.MSA_MODEVIEW == 2)) {
                        Appl.MSA_ID = MSA_ID;
                        Appl.MSA_POS = getAdapterPosition();
                            MsaUtils.change_record_state_SQLite(22);
                            MsaMainActivity.refreshRecords();

                    }
                }

                public void onSwipeBottom() {
                    //  Toast.makeText(Appl.getContext(), "bottom", Toast.LENGTH_SHORT).show();
                }

                public void onClick() {
                    if (!MSA_ID.isEmpty()) {
                        if ((Appl.MSA_MODEVIEW == 0) || (Appl.MSA_MODEVIEW == 10)) {
                            Appl.MSA_MODEEDIT = Appl.MSA_MODEVIEW;
                            Appl.MSA_ID = MSA_ID;
                            Appl.MSA_POS = getAdapterPosition();
                            Intent mesReposte = new Intent(MsaMainActivity.getInstance(), MsaEditActivity.class);
                            MsaMainActivity.getInstance().startActivityForResult(mesReposte, 1001);
                            Appl.animateStart(MsaMainActivity.getInstance());
                        } else {
                            if (!MSA_FILETYPE.equals("")) {  // A
                                if (MSA_IMAGESIZE > 0) {  // B
                                    if (MSA_FILETYPE.equalsIgnoreCase("jpg"))
                                        load_image_from_SQLite(tiv_msa_view, MSA_ID);

                                } else // B
                                    if (Appl.checkConnectToServer(false)) {
                                        Appl.ShowProgressIndicatior(itemLayoutView.getContext());
                                        DB_ReceiveBlobFormMSSQL ld = new DB_ReceiveBlobFormMSSQL();
                                        ld.tiv_msa_view = tiv_msa_view;
                                        ld.MSA_ID = MSA_ID;
                                        ld.MSA_FILETYPE = MSA_FILETYPE;
                                        ld.execute();
                                    }
                            }  // A
                        }
                    }
                }

                public void onLongClick() {
                    //Toast.makeText(Appl.getContext(), "LongClick", Toast.LENGTH_SHORT).show();
                    if (!MSA_ID.isEmpty()) {
                        Appl.MSA_ID = MSA_ID;
                        Appl.MSA_POS = getAdapterPosition();

                        Intent viewMenu = new Intent(MsaMainActivity.getInstance(), ChoiceMsaActionActivity.class);
                        viewMenu.putExtra("MSA_ID", MSA_ID);
                        viewMenu.putExtra("MSA_FILETYPE", MSA_FILETYPE);
                        viewMenu.putExtra("MSA_IMAGESIZE", MSA_IMAGESIZE);
                        viewMenu.putExtra("MSA_FILENAME", MSA_FILENAME);
                        MsaMainActivity.getInstance().startActivity(viewMenu);
                    }
                }
            });
        }
    }
}