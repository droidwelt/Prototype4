package ru.droidwelt.prototype4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class ChoiceMsaActionActivity extends AppCompatActivity {

    private String MSA_ID;
    private String MSA_FILETYPE;
    private String MSA_FILENAME;
    private int MSA_IMAGESIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesDlg(this);
        setContentView(R.layout.activity_choicemsaaction);
        setTitle(getString(R.string.s_message_action));
        LinearLayout ly_msa_picture_export, ly_msa_send_selected, ly_msa_move_to_draft, ly_msa_copy_to_draft,
                ly_msa_copy_to_fav, ly_msa_copy_to_draft_and_open, ly_msa_delete_selected;
        ly_msa_picture_export = findViewById(R.id.ly_msa_picture_export);
        ly_msa_send_selected = findViewById(R.id.ly_msa_send_selected);
        ly_msa_move_to_draft = findViewById(R.id.ly_msa_move_to_draft);
        ly_msa_copy_to_draft = findViewById(R.id.ly_msa_copy_to_draft);
        ly_msa_copy_to_fav = findViewById(R.id.ly_msa_copy_to_fav);
        ly_msa_copy_to_draft_and_open = findViewById(R.id.ly_msa_copy_to_draft_and_open);
        ly_msa_delete_selected = findViewById(R.id.ly_msa_delete_selected);

        Bundle extras = getIntent().getExtras();
        MSA_ID = extras != null ? extras.getString("MSA_ID", "") : null;
        MSA_FILETYPE = extras != null ? extras.getString("MSA_FILETYPE", "") : null;
        MSA_FILENAME = extras != null ? extras.getString("MSA_FILENAME", "") : null;
        MSA_IMAGESIZE = extras != null ? extras.getInt("MSA_IMAGESIZE", 0) : 0;

        if (Appl.MSA_MODEVIEW != 4) ly_msa_send_selected.setVisibility(View.GONE);
        if (!((Appl.MSA_MODEVIEW == 3) | (Appl.MSA_MODEVIEW == 4))) ly_msa_move_to_draft.setVisibility(View.GONE);
        if (!(Appl.MSA_MODEVIEW == 10)) ly_msa_copy_to_fav.setVisibility(View.GONE);
        if (!((Appl.MSA_MODEVIEW == 2) | (Appl.MSA_MODEVIEW == 4) | (Appl.MSA_MODEVIEW == 4))) ly_msa_copy_to_draft_and_open.setVisibility(View.GONE);
        if (MSA_IMAGESIZE == 0) ly_msa_picture_export.setVisibility(View.GONE);

        ly_msa_picture_export.setOnClickListener(oclBtnOk);
        ly_msa_send_selected.setOnClickListener(oclBtnOk);
        ly_msa_move_to_draft.setOnClickListener(oclBtnOk);
        ly_msa_copy_to_draft.setOnClickListener(oclBtnOk);
        ly_msa_copy_to_fav.setOnClickListener(oclBtnOk);
        ly_msa_copy_to_draft_and_open.setOnClickListener(oclBtnOk);
        ly_msa_delete_selected.setOnClickListener(oclBtnOk);
    }


    OnClickListener oclBtnOk = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.ly_msa_picture_export:
                        if ((!MSA_FILETYPE.equals("")) & (MSA_IMAGESIZE > 0)) {
                            String fn = Appl.createValidFileName(MSA_FILENAME);
                            DB_ExportSQLite ex = new DB_ExportSQLite();
                            ex.sFileName = fn;
                            ex.execute();
                            finish();
                        }
                    break;

                case R.id.ly_msa_send_selected:
                    if (Appl.checkConnectToServer(true)) {
                        Appl.ShowProgressIndicatior(v.getContext());
                        DB_SendOne ar = new DB_SendOne();
                        ar.MSA_ID_SAVED = MSA_ID;
                        ar.execute();
                        finish();
                    }
                    break;

                case R.id.ly_msa_move_to_draft:
                        MsaUtils.move_one_to_draft_SQLite(MSA_ID);
                        MsaMainActivity.refreshRecords();
                        finish();
                    break;

                case R.id.ly_msa_copy_to_draft:
                        MsaUtils.copy_one_to_folder_SQLite(MSA_ID, 0);
                        if (Appl.MSA_MODEEDIT == 0)
                            MsaMainActivity.refreshRecords();
                        finish();
                    break;

                case R.id.ly_msa_copy_to_fav:
                        MsaUtils.copy_one_to_folder_SQLite(MSA_ID, 10);
                    break;

                case R.id.ly_msa_copy_to_draft_and_open:
                        Appl.MSA_ID = MsaUtils.copy_one_to_folder_SQLite(MSA_ID, 0);
                        Appl.MSA_MODEEDIT = 0;
                        Intent mesEdit = new Intent(Appl.getContext(), MsaEditActivity.class);
                        MsaMainActivity.getInstance().startActivityForResult(mesEdit, 1001);
                        Appl.animateStart(MsaMainActivity.getInstance());
                        finish();
                    break;

                case R.id.ly_msa_delete_selected:
                    finish();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MsaMainActivity.getInstance());
                    builder.setTitle(Appl.getContext().getString(R.string.dlg_confirm_req));
                    builder.setMessage(Appl.getContext().getString(R.string.s_message_delete_confirm_one));
                    builder.setNegativeButton(Appl.getContext().getString(R.string.dlg_prg_no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.setPositiveButton((Appl.getContext().getString(R.string.dlg_prg_yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MsaUtils.delete_selected_SQLite(MSA_ID);
                                    MsaMainActivity.refreshRecords();
                                }
                            });
                    builder.create();
                    builder.show();
                    break;

                default:
                    break;
            }
        }
    };


}