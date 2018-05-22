package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MsaEditActivity extends AppCompatActivity implements OnClickListener {

    static final int GALLERY_REQUEST = 1;
    static final int CAMERA_CAPTURE = 2;
    static final int PIC_CROP = 3;
    static final int PICK_ACTION = 4;

    private static Uri outputFileUri_photo; // куда сохраняется наше фото
    private static Uri outputFileUri_crop; // куда сохраняется наше фото
    private final String imageFilename_photo = "proto_photo.jpg";
    public static String MSA_TITLE_FIRST, MSA_TEXT_FIRST, MSA_CLR, MSA_LBL, MSA_CLR_FIRST, MSA_LBL_FIRST;
    public static EditText et_msa_title, et_msa_text, et_msa_filename;
    public static TextView tv_msa_fio, tv_msa_datetime, tv_send, tv_msa_filetype, tv_msa_imagesize;
    public static TouchImageView tiv_msa_edit;
    public static DisplayMetrics displaymetrics;
    public static String MSA_FILETYPE, MSA_FILENAME;
    public static int MSA_IMAGESIZE;
    public static ImageButton ib_mesedit_lbl, ib_mesedit_clr;
    public static Toolbar tb_mes_edit;

    public static Bitmap imageBig;
    public static boolean imageModified;

    public static MenuItem mi_edit_picture_clear;
    public static MenuItem mi_edit_picture_export;
    public static MenuItem mi_edit_picture_crop;
    public static MenuItem mi_edit_deletedraft;
    public static MenuItem mi_edit_picture_rotate_left;
    public static MenuItem mi_edit_picture_rotate_right;
    public static MenuItem mi_edit_picture_reduce;
    public static MenuItem mi_edit_picture_import;
    public static MenuItem mi_edit_picture_choice;
    public static MenuItem mi_edit_picture_takephoto;
    public static MenuItem mi_edit_attachment_clear;


    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesNoActionBar(this);
        setContentView(R.layout.activity_msaedit);
        imageBig = null;
        imageModified = false;
        MSA_TITLE_FIRST = "";
        MSA_TEXT_FIRST = "";
        MSA_FILETYPE = "";
        MSA_FILENAME = "";
        MSA_CLR = "0";
        MSA_LBL = "0";
        MSA_CLR_FIRST = "0";
        MSA_LBL_FIRST = "0";
        MSA_IMAGESIZE = 0;
        displaymetrics = getMyDisplayMetrics();

        tb_mes_edit = findViewById(R.id.tb_mes_edit);
        setSupportActionBar(tb_mes_edit);
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Appl.BarColor_0));

        et_msa_text = findViewById(R.id.et_mesedit_text);
        et_msa_title = findViewById(R.id.et_mesedit_title);
        tv_msa_filetype = findViewById(R.id.tv_mesedit_filetype);
        et_msa_filename = findViewById(R.id.et_mesedit_filename);
        tv_msa_imagesize = findViewById(R.id.tv_mesedit_imagesize);
        tv_msa_fio = findViewById(R.id.tv_mesedit_fio);
        tv_msa_datetime = findViewById(R.id.tv_mesedit_datetime);
        tv_send = findViewById(R.id.tv_mesedit_send);
        tv_send.setOnClickListener(this);
        tiv_msa_edit = findViewById(R.id.tiv_msa_edit);
        tiv_msa_edit.setOnClickListener(this);
        ib_mesedit_lbl = findViewById(R.id.ib_mesedit_lbl);
        ib_mesedit_lbl.setOnClickListener(onClickButtonListtiner);
        ib_mesedit_clr = findViewById(R.id.ib_mesedit_clr);
        ib_mesedit_clr.setOnClickListener(onClickButtonListtiner);

        ImageView ib_mesedit_fio = findViewById(R.id.ib_mesedit_fio);
        ib_mesedit_fio.setOnClickListener(onClickButtonListtiner);

        load_msa_record_from_SQLite();
        display_send_list();

        et_msa_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_msa_title.getText().length() < 98)
                    et_msa_title.setBackgroundColor(ContextCompat.getColor(Appl.getContext(), R.color.background_text));
                else
                    et_msa_title.setBackgroundColor(ContextCompat.getColor(Appl.getContext(), R.color.background_text_overhead));
            }
        });

        et_msa_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_msa_text.getText().length() < 3990)
                    et_msa_text.setBackgroundColor(ContextCompat.getColor(Appl.getContext(), R.color.background_text));
                else
                    et_msa_text.setBackgroundColor(ContextCompat.getColor(Appl.getContext(), R.color.background_text_overhead));
            }
        });
    }

    public static void display_send_list() {
        tv_send.setText(MsaUtils.message_get_send_list());
    }


    // подключение меню----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_msa_edit, menu);
        mi_edit_picture_clear = menu.findItem(R.id.mi_edit_picture_clear);
        mi_edit_picture_crop = menu.findItem(R.id.mi_edit_picture_crop);
        mi_edit_picture_export = menu.findItem(R.id.mi_edit_picture_export);
        mi_edit_deletedraft = menu.findItem(R.id.mi_edit_deletedraft);
        mi_edit_picture_rotate_left = menu.findItem(R.id.mi_edit_picture_rotate_left);
        mi_edit_picture_rotate_right = menu.findItem(R.id.mi_edit_picture_rotate_right);
        mi_edit_picture_reduce = menu.findItem(R.id.mi_edit_picture_reduce);
        mi_edit_picture_import = menu.findItem(R.id.mi_edit_picture_import);
        mi_edit_picture_choice = menu.findItem(R.id.mi_edit_picture_choice);
        mi_edit_picture_takephoto = menu.findItem(R.id.mi_edit_picture_takephoto);
        mi_edit_attachment_clear = menu.findItem(R.id.mi_edit_attachment_clear);

        setVisibleInfo();
        return true;
    }


    OnClickListener onClickButtonListtiner = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.ib_mesedit_fio:
                    Intent mesChoice = new Intent(MsaEditActivity.this, ChoiceFioActivity.class);
                    startActivity(mesChoice);
                    break;

                case R.id.ib_mesedit_lbl:
                    select_msa_lbl();
                    break;

                case R.id.ib_mesedit_clr:
                    select_msa_clr();
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tiv_msa_edit:
                if (!MSA_FILETYPE.equals("")) {
                    if (MSA_IMAGESIZE == 0) {
                        if (Appl.checkConnectToServer(true)) {
                            Appl.ShowProgressIndicatior(v.getContext());
                            DB_ReceiveBlobFormMSSQL ld = new DB_ReceiveBlobFormMSSQL();
                            ld.MSA_FILETYPE = MSA_FILETYPE;
                            ld.execute();
                        }
                    } else {
                        if (!MSA_FILETYPE.equalsIgnoreCase("jpg")) {
                            export_attacment();
                        }
                    }
                }
                break;

            case R.id.tv_mesedit_send:
                Intent mesChoice = new Intent(MsaEditActivity.this, ChoiceFioActivity.class);
                startActivity(mesChoice);
                break;


            default:
                break;
        }
    }


    public Boolean Vefify_Message(Boolean vfy_fio_list) {
        if (!vfy_fio_list) return true;
        else {
            if (tv_send.getText().toString().equals("")) {
                Appl.DisplayToastError(Appl.context
                        .getString(R.string.action_devlist_isempty));
                return false;
            } else
                return true;
        }
    }


    // защита от закрытия по Back Обработка нажатия
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                smart_exit();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //--------------------------------------------------------

    private void deleteTempFile() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), imageFilename_photo);
            if (file.exists())
                file.delete();
        } catch (Exception ignored) {
        }
        try {
            String imageFilename_crop = "proto_crop.jpg";
            File file = new File(Environment.getExternalStorageDirectory(), imageFilename_crop);
            if (file.exists())
                file.delete();
        } catch (Exception ignored) {
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 75, bytes);//100
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void clear_attachment() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dlg_confirm_req))
                .setMessage(getString(R.string.s_message_clear_attachment_confirm))
                .setNegativeButton((getString(R.string.dlg_prg_no)), null)
                .setPositiveButton((getString(R.string.dlg_prg_yes)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                clearAttachment_from_SQLite();
                                setVisibleInfo();
                            }

                        }).create().show();
    }

    public void delete_current_message() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dlg_confirm_req))
                .setMessage(getString(R.string.s_message_delete_confirm_one))
                .setNegativeButton((getString(R.string.dlg_prg_no)), null)
                .setPositiveButton((getString(R.string.dlg_prg_yes)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                MsaUtils.message_delete_from_SQLite();
                                setResult(RESULT_OK);
                                finish();
                                Appl.animateFinish(MsaEditActivity.this);
                            }

                        }).create().show();
    }


    private void export_attacment() {
        if ((!MSA_FILETYPE.equals("")) & (MSA_IMAGESIZE > 0)) {
            String fn = Appl.createValidFileName(et_msa_filename.getText().toString());
            DB_ExportSQLite ex = new DB_ExportSQLite();
            ex.sFileName = fn;
            ex.execute();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // скопировать этот черновик
            case R.id.mi_edit_copytoraft:
                message_save_to_SQLite_header(Appl.MSA_MODEVIEW);
                message_save_to_SQLite_image();
                Appl.MSA_ID = MsaUtils.copy_one_to_folder_SQLite(Appl.MSA_ID, Appl.MSA_MODEVIEW);
                load_msa_record_from_SQLite();
                return true;

            // копировать в избранное
            case R.id.mi_edit_copytofav:
                message_save_to_SQLite_header(Appl.MSA_MODEVIEW);
                message_save_to_SQLite_image();
                Appl.MSA_ID = MsaUtils.copy_one_to_folder_SQLite(Appl.MSA_ID, Appl.MSA_MODEVIEW);
                load_msa_record_from_SQLite();
                return true;

            // сохранить черновик
            case R.id.mi_edit_savedraft:
                //tiv_msa_edit.invalidate();
                if (Vefify_Message(false)) {
                    message_save_to_SQLite_header(Appl.MSA_MODEVIEW);
                    message_save_to_SQLite_image();
                    load_msa_record_from_SQLite();
                }
                return true;

            // импорт  выбрать файл для вложения
            case R.id.mi_edit_picture_import:
                Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.setType("files/*");
                startActivityForResult(pickIntent, PICK_ACTION);
                return true;

            // удалить черновик
            case R.id.mi_edit_deletedraft:
                delete_current_message();
                return true;

            // послать в исходящие		   //// TODO
            case R.id.mi_edit_send:
                tiv_msa_edit.invalidate();
                if (Vefify_Message(true)) {
                    message_save_to_SQLite_header(4);
                    message_save_to_SQLite_image();
                    load_msa_record_from_SQLite();
                    if ((Appl.getUploadImmediately()) & (Appl.checkConnectToServer(false))) {
                        Appl.ShowProgressIndicatior(MsaEditActivity.this);
                        DB_SendOne ar = new DB_SendOne();
                        ar.MSA_ID_SAVED = Appl.MSA_ID;
                        ar.execute();
                    }
                    setResult(RESULT_OK);
                    finish();
                    Appl.animateFinish(MsaEditActivity.this);
                }
                return true;

            // выбор изображения
            case R.id.mi_edit_picture_choice:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                return true;

            // сделать фото
            case R.id.mi_edit_picture_takephoto:
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                deleteTempFile();
                File file = new File(Environment.getExternalStorageDirectory(), imageFilename_photo);
                outputFileUri_photo = Uri.fromFile(file);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri_photo);
                //	captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,	android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(captureIntent, CAMERA_CAPTURE);
                return true;

            // обрезать изображение
            case R.id.mi_edit_picture_crop:
                outputFileUri_photo = getImageUri(this, imageBig);
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(outputFileUri_photo, "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                cropIntent.putExtra("output", outputFileUri_crop);
                cropIntent.putExtra("noFaceDetection", true);
                startActivityForResult(cropIntent, PIC_CROP);
                return true;

            // экспортировать изображение из черновика
            case R.id.mi_edit_picture_export:
                export_attacment();
                return true;

            // очистить изображение
            case R.id.mi_edit_picture_clear:
                imageBig = (null);
                MSA_FILETYPE = "";
                MSA_FILENAME = "";
                MSA_IMAGESIZE = 0;
                imageModified = true;
                setVisibleInfo();
                return true;

            // повернуть влево
            case R.id.mi_edit_picture_rotate_left:
                rotateImage(-1);
                setVisibleInfo();
                return true;

            // повернуть вправо
            case R.id.mi_edit_picture_rotate_right:
                rotateImage(1);
                setVisibleInfo();
                return true;

            // уменьшить размер  изображения
            case R.id.mi_edit_picture_reduce:
                if (imageBig != null) {
                    if (imageBig.getByteCount() > 5000) imageinfo();
                }
                return true;

            // Стереть вложение
            case R.id.mi_edit_attachment_clear:
                clear_attachment();
                return true;

            // выбрать шаблон
            case R.id.mi_edit_template:
                Intent intentChoice = new Intent(MsaEditActivity.this, ChoiceTemplate.class);
                startActivityForResult(intentChoice, Appl.CODE_CHOICE_TEMPLATE);
                return true;

            // цвет заголовка изображения
            case R.id.mi_edit_set_clr:
                select_msa_clr();
                return true;

            // значок сообщения
            case R.id.mi_edit_set_lbl:
                select_msa_lbl();
                return true;


            // домой
            case android.R.id.home:
                smart_exit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void select_msa_clr() {
        Intent intentClr = new Intent(MsaEditActivity.this, ChoiceClrActivity.class);
        startActivity(intentClr);
    }


    protected void select_msa_lbl() {
        Intent intentLbl = new Intent(MsaEditActivity.this, ChoiceLblActivity.class);
        startActivity(intentLbl);
    }


    protected void smart_exit() {
        String MSA_TITLE = Appl.strnormalize(et_msa_title.getText().toString());
        String MSA_TEXT = Appl.strnormalize(et_msa_text.getText().toString());

        if ((!MSA_TITLE.equalsIgnoreCase(MSA_TITLE_FIRST))
                || (!MSA_TEXT.equalsIgnoreCase(MSA_TEXT_FIRST))
                || (!MSA_CLR.equalsIgnoreCase(MSA_CLR_FIRST))
                || (!MSA_LBL.equalsIgnoreCase(MSA_LBL_FIRST))
                || (imageModified)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dlg_confirm_req))
                    .setMessage(getString(R.string.dlg_date_changed))
                    .setNegativeButton((getString(R.string.dlg_prg_yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    if (Vefify_Message(false)) {
                                        message_save_to_SQLite_header(Appl.MSA_MODEVIEW);
                                        message_save_to_SQLite_image();
                                        load_msa_record_from_SQLite();
                                        setResult(RESULT_OK);
                                        finish();
                                        Appl.animateFinish(MsaEditActivity.this);
                                    }
                                }
                            })
                    .setPositiveButton((getString(R.string.dlg_prg_no)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    setResult(RESULT_OK);
                                    finish();
                                    Appl.animateFinish(MsaEditActivity.this);
                                }
                            })
                    .create()
                    .show();
        } else {
            setResult(RESULT_OK);
            finish();
            Appl.animateFinish(MsaEditActivity.this);
        }
    }

    /*  mode=-1 влево, против часовой стрелки. mode=1 вправо, по часовой стрелке 	 */
    protected void rotateImage(int mode) {
        Matrix matrix = new Matrix();
        //	matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(0);
        if (mode == -1) matrix.postRotate(-90);
        if (mode == 1) matrix.postRotate(90);
        imageBig = Bitmap.createBitmap(imageBig, 0, 0, imageBig.getWidth(), imageBig.getHeight(), matrix, true);
        imageModified = true;
    }


    protected void imageinfo() {
        Intent reduceintent = new Intent(MsaEditActivity.this, ReduceImageActivity.class);
        startActivity(reduceintent);
    }


    public Bitmap getThumbnail(Uri uri) throws IOException {
        InputStream input = getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        // onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        if (input != null) input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 2048) ? (originalSize / 2048) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        //  bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        if (input != null) input.close();
        return bitmap;
    }

    private int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case Appl.CODE_CHOICE_TEMPLATE:
                    break;

                case GALLERY_REQUEST:
                    try {
                        final Uri imageUri = returnedIntent.getData();
                        imageBig = getThumbnail(imageUri);
                        imageModified = true;
                        MSA_FILENAME = "";
                        MSA_FILETYPE = "jpg";
                        setVisibleInfo();
                    } catch (Exception e) {
                        Appl.DisplayToastError(Appl.context.getString(R.string.s_no_request_image_from_gallery));
                        break;
                    }
                    break;

                case CAMERA_CAPTURE:
                    try {
                        imageBig = getThumbnail(outputFileUri_photo);
                        imageModified = true;
                        MSA_FILENAME = "";
                        MSA_FILETYPE = "jpg";
                        setVisibleInfo();
                    } catch (Exception e) {
                        Appl.DisplayToastError(e.toString());
                        break;
                    }
                    break;

                case PIC_CROP:
                    try {
                        outputFileUri_crop = returnedIntent.getData(); // ???????????????
                        imageBig = (Media.getBitmap(getContentResolver(), outputFileUri_crop));
                    } catch (Exception e) {
                        Appl.DisplayToastError(Appl.context.getString(R.string.s_no_prepate_after_crop));
                        break;
                    }
                    imageModified = true;
                    setVisibleInfo();
                    break;

                case PICK_ACTION:
                    if (returnedIntent.getData() != null) {
                        String fn = returnedIntent.getData().getLastPathSegment();
                        if (Appl.verifyMaxFileSize(returnedIntent.getData().getPath(), true)) {
                            //int fs = Appl.getFileSize(returnedIntent.getData().getSchemeSpecificPart());
                            int i = fn.lastIndexOf(".");
                            if (i > 0) {
                                message_save_to_SQLite_header(0);
                                fn = fn.substring(i + 1, fn.length());
                                DB_ImportSQLite ld = new DB_ImportSQLite();
                                ld.MSA_FILETYPE = fn;
                                ld.MSA_FULLFILENAME = returnedIntent.getData().getSchemeSpecificPart();
                                ld.MSA_FILENAME = returnedIntent.getData().getLastPathSegment();
                                ld.execute();
                            }
                        }
                    }
                    break;

            }
        }
    }


    public static void setMSA_TEXT(String s) {
        et_msa_text.setText(s);
    }


    public static void setVisibleInfo() {
        if (imageBig != null) {
            int picwidth = imageBig.getWidth();
            int picheight = imageBig.getHeight();
            LinearLayout.LayoutParams paramsMyImage = (LinearLayout.LayoutParams) tiv_msa_edit.getLayoutParams();
            paramsMyImage.height = displaymetrics.widthPixels * picheight / picwidth;
        }

        if (imageBig == null) {
            tiv_msa_edit.setEnabled(false);
            if (MSA_FILETYPE.equals("")) {
                tiv_msa_edit.setImageResource(R.mipmap.ic_empty);
            } else {
                tiv_msa_edit.setEnabled(MSA_FILETYPE.equalsIgnoreCase("jpg"));
                if (MSA_IMAGESIZE > 0) {
                    tiv_msa_edit.setEnabled(true); //////////////
                    tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_other);
                    if (MSA_FILETYPE.equalsIgnoreCase("doc"))
                        tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_doc);
                    if (MSA_FILETYPE.equalsIgnoreCase("docx"))
                        tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_doc);
                    if (MSA_FILETYPE.equalsIgnoreCase("pdf"))
                        tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_pdf);
                    if (MSA_FILETYPE.equalsIgnoreCase("zip"))
                        tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_zip);
                    if (MSA_FILETYPE.equalsIgnoreCase("xls"))
                        tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_xls);
                    if (MSA_FILETYPE.equalsIgnoreCase("xlsx"))
                        tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_xls);
                    if (MSA_FILETYPE.equalsIgnoreCase("mp4"))
                        tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_video);
                    if (MSA_FILETYPE.equalsIgnoreCase("mp3"))
                        tiv_msa_edit.setImageResource(R.mipmap.ic_filetype_audio);

                } else tiv_msa_edit.setImageResource(R.mipmap.ic_import);
            }
        } else {
            tiv_msa_edit.setEnabled(true);
            new Thread(new Runnable() {
                public void run() {
                    tiv_msa_edit.post(new Runnable() {
                        public void run() {
                            tiv_msa_edit.setImageBitmap(imageBig);
                        }
                    });
                }
            }).start();
        }

        if (imageBig != null) {
            MSA_IMAGESIZE = imageBig.getByteCount();
            tv_msa_imagesize.setText(Appl.convertToKbStr(MSA_IMAGESIZE));
        } else {
            if (MSA_IMAGESIZE == 0) tv_msa_imagesize.setText("");
            else tv_msa_imagesize.setText(Appl.convertToKbStr(MSA_IMAGESIZE));
        }

        if (mi_edit_picture_clear != null) {
            mi_edit_picture_clear.setVisible(imageBig != null);
            mi_edit_picture_crop.setVisible(imageBig != null);
            mi_edit_picture_export.setVisible((MSA_IMAGESIZE > 0) & (!MSA_FILETYPE.equals("")));
            mi_edit_picture_rotate_left.setVisible(imageBig != null);
            mi_edit_picture_rotate_right.setVisible(imageBig != null);
            mi_edit_picture_reduce.setVisible(imageBig != null);
            mi_edit_picture_import.setVisible(imageBig == null);
            mi_edit_picture_takephoto.setVisible((MSA_FILETYPE.equals("") | (MSA_FILETYPE.equalsIgnoreCase("jpg"))));
            mi_edit_picture_choice.setVisible((MSA_FILETYPE.equals("") | (MSA_FILETYPE.equalsIgnoreCase("jpg"))));
        }

        et_msa_filename.setText(MSA_FILENAME);
        tv_msa_filetype.setText(MSA_FILETYPE);
        int title_clr = Appl.getColorByString(MSA_CLR);
        if (title_clr == Color.parseColor("#EEEEEE")) title_clr = Color.WHITE;
        et_msa_title.setBackgroundColor(title_clr);

        ib_mesedit_lbl.setImageResource(Appl.getResIDbyName(MSA_LBL));
    }


    public static void setTitleClr(String s) {
        MSA_CLR = s;
        et_msa_title.setBackgroundColor(Appl.getColorByString(MSA_CLR));
    }


    public DisplayMetrics getMyDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }


    public static void clearAttachment_from_SQLite() {
        MSA_FILETYPE = "";
        MSA_FILENAME = "";
        MSA_IMAGESIZE = 0;
        String sSQL = "update MSA set MSA_IMAGE=NULL,MSA_FILENAME=NULL,MSA_FILETYPE=NULL where MSA_ID='" + Appl.MSA_ID + "';";
        Appl.getDatabase().execSQL(sSQL);
    }

    public static void message_save_to_SQLite_header(int msa_state_to_save) {
        String MSA_TITLE = Appl.strnormalize(et_msa_title.getText().toString());
        if (MSA_TITLE.equals("")) {
            MSA_TITLE = Appl.getStringCurrentDateTime();
            et_msa_title.setText(MSA_TITLE);
        }
        MSA_FILENAME = Appl.strnormalize(et_msa_filename.getText().toString());
        MSA_FILETYPE = Appl.strnormalize(tv_msa_filetype.getText().toString());
        if ((!MSA_FILETYPE.equals("")) & (MSA_FILENAME.equals(""))) {
            MSA_FILENAME = MSA_TITLE;
            et_msa_filename.setText(MSA_FILENAME);
        }

        String MSA_TEXT = Appl.strnormalize(et_msa_text.getText().toString());
        String sSQL = " update MSA set "
                + " MSA_DATE=datetime ('now', 'localtime'),"
                + " MSA_STATE = " + msa_state_to_save + ","
                + " MSA_CLR = '" + MSA_CLR + "',"
                + " MSA_LBL = '" + MSA_LBL + "',"
                + " MSA_TITLE = '" + Appl.truncateString(MSA_TITLE, 99) + "',"
                + " MSA_FILENAME = '" + Appl.truncateString(MSA_FILENAME, 100) + "', "
                + " MSA_TEXT = '" + Appl.truncateString(MSA_TEXT, 3999) + "', "
                + " MSA_FILETYPE = '" + Appl.strnormalize(MSA_FILETYPE) + "' "
                + " where MSA_ID='" + Appl.MSA_ID + "'; ";
        Appl.getDatabase().execSQL(sSQL);
    }


    public static void message_save_to_SQLite_image() {
        //	Log.i ("WWW","MSA_FILETYPE="+MSA_FILETYPE);
        //	Log.i ("WWW","imageModified="+imageModified);
        //	Log.i ("WWW","imageBig="+(imageBig != null));
        if ((MSA_FILETYPE.equalsIgnoreCase("jpg")) & (imageModified)) {
            if (imageBig != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBig.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                byte[] imageInByte = stream.toByteArray();
                ContentValues editMessage = new ContentValues();
                editMessage.put("MSA_IMAGE", imageInByte);
                Appl.getDatabase().update("MSA", editMessage,
                        "MSA_ID='" + Appl.MSA_ID + "'", null);
            } else {
                String sSQL = " update MSA set MSA_IMAGE=NULL where MSA_ID='" + Appl.MSA_ID + "'; ";
                Appl.getDatabase().execSQL(sSQL);
            }
        }
    }


    public static void load_msa_record_from_SQLite() {
        String sSQL =
                "select A._id as _id, MSA_ID,MSA_CLR,MSA_LBL,MSA_TITLE,"
                        + "MSA_TEXT,A.FIO_ID,MSA_FILETYPE,MSA_FILENAME,FIO_NAME,MSA_DATE,"
                        + "case when length(MSA_IMAGE) is NULL then 0 else length(MSA_IMAGE) end as MSA_IMAGESIZE_MSSQL "
                        + "from MSA A "
                        + "left join FIO F on A.FIO_ID=F.FIO_ID "
                        + "where MSA_ID='" + Appl.MSA_ID + "'";
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            MSA_TITLE_FIRST = Appl.strnormalize(c.getString(c.getColumnIndex("MSA_TITLE")));
            MSA_TEXT_FIRST = Appl.strnormalize(c.getString(c.getColumnIndex("MSA_TEXT")));
            MSA_FILENAME = Appl.strnormalize(c.getString(c.getColumnIndex("MSA_FILENAME")));
            MSA_FILETYPE = Appl.strnormalize(c.getString(c.getColumnIndex("MSA_FILETYPE")));
            MSA_CLR = Appl.strnormalize(c.getString(c.getColumnIndex("MSA_CLR")));
            MSA_LBL = Appl.strnormalize(c.getString(c.getColumnIndex("MSA_LBL")));
            MSA_CLR_FIRST = MSA_CLR;
            MSA_LBL_FIRST = MSA_LBL;
            MSA_IMAGESIZE = c.getInt(c.getColumnIndex("MSA_IMAGESIZE_MSSQL"));
            imageModified = false;
            et_msa_title.setText(MSA_TITLE_FIRST);
            et_msa_text.setText(MSA_TEXT_FIRST);
            et_msa_filename.setText(MSA_FILENAME);
            tv_msa_filetype.setText(MSA_FILETYPE);
            tv_msa_fio.setText(Appl.strnormalize(c.getString(c.getColumnIndex("FIO_NAME"))));
            tv_msa_datetime.setText(Appl.strnormalize(c.getString(c.getColumnIndex("MSA_DATE"))));
            c.close();

            if (MSA_FILETYPE.equalsIgnoreCase("jpg") & (MSA_IMAGESIZE > 0)) {
                int i = 0;
                int theImagePos = 0;
                int rdbytes = 200000;
                byte[] resall = null;

                while (rdbytes == 200000 & i < 3000) {
                    Cursor cursor = Appl.getDatabase().rawQuery(
                            "select substr(MSA_IMAGE,1+" + String.valueOf(i)
                                    + "*200000,200000) from MSA  where MSA_ID='"
                                    + Appl.MSA_ID + "'", null);
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

                if (resall != null)
                    imageBig = BitmapFactory.decodeByteArray(resall, 0, resall.length);
            }
        } else {
            tv_msa_fio.setText(Appl.FIO_NAME);
            c.close();
        }
        setVisibleInfo();
    }

}
