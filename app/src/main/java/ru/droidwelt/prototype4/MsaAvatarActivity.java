package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import static ru.droidwelt.prototype4.Appl.getRandomString;


public class MsaAvatarActivity extends AppCompatActivity implements OnClickListener {

    static final int GALLERY_REQUEST = 1;
    static final int CAMERA_CAPTURE = 2;
    static final int PIC_CROP = 3;
    static final int PICK_ACTION = 4;


    private static Uri outputFileUri_photo; // куда сохраняется наше фото
    private static Uri outputFileUri_crop; // куда сохраняется наше фото
    private final String imageFilename_photo = "photo_ava.jpg";
    public static EditText et_msaava_name, et_msaava_subname;
    public static ImageView tiv_msa_ava;
    public static DisplayMetrics displaymetrics;
    public static String FIO_NAME, FIO_SUBNAME;
    public static ImageView iv_msaava_ava;

    public static Bitmap imageBig, imageSmall;
    public static boolean imageModified;

    public DisplayMetrics getMyDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }


    public  void save_ava_to_MSSQL() {
        Appl.FIO_NAME = FIO_NAME;
        Appl.FIO_SUBNAME = FIO_SUBNAME;
        Appl.FIO_IMAGE = imageSmall;
        Appl.getMSSQLConnectionSettings();

        if (Appl.checkConnectToServer(true)) {
            Appl.ShowProgressIndicatior(this);
            DB_SaveAvatar ar = new DB_SaveAvatar();
            ar.execute();
        }
    }


    public static void save_ava_to_SQLite() {
        FIO_NAME = Appl.truncateString(Appl.strnormalize(et_msaava_name.getText().toString()), 99);
        FIO_SUBNAME = Appl.truncateString(Appl.strnormalize(et_msaava_subname.getText().toString()), 99);
        if (FIO_NAME.equals("")) {
            FIO_NAME = getRandomString(12);
        }
        et_msaava_name.setText(FIO_NAME);
        et_msaava_subname.setText(FIO_SUBNAME);

        String sSQL = " update FIO set "
                + " FIO_NAME = '" + FIO_NAME + "',"
                + " FIO_SUBNAME = '" + FIO_SUBNAME + "' "
                + " where FIO_ID=" + Appl.FIO_ID + "; ";
        Appl.getDatabase().execSQL(sSQL);

        if (imageSmall != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageSmall.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] imageInByte = stream.toByteArray();
            ContentValues editMessage = new ContentValues();
            editMessage.put("FIO_IMAGE", imageInByte);
            Appl.getDatabase().update("FIO", editMessage, "FIO_ID='" + Appl.FIO_ID + "'", null);
        } else {
            sSQL = " update FIO set FIO_IMAGE=NULL where FIO_ID='" + Appl.FIO_ID + "'; ";
            Appl.getDatabase().execSQL(sSQL);
        }

        imageModified = false;
        setVisibleInfo();
    }


    public static void load_ava_record_from_SQLite() {
        String sSQL = "select FIO_ID,FIO_NAME,FIO_SUBNAME,FIO_IMAGE from FIO where FIO_ID=" + Appl.FIO_ID;
        Cursor c = Appl.getDatabase().rawQuery(sSQL, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            FIO_NAME = Appl.strnormalize(c.getString(c
                    .getColumnIndex("FIO_NAME")));
            FIO_SUBNAME = Appl.strnormalize(c.getString(c
                    .getColumnIndex("FIO_SUBNAME")));
            et_msaava_name.setText(FIO_NAME);
            et_msaava_subname.setText(FIO_SUBNAME);
            byte[] res = c.getBlob(c.getColumnIndex("FIO_IMAGE"));
            if (res != null) imageBig = BitmapFactory.decodeByteArray(res, 0, res.length);
            c.close();
        }
        setVisibleInfo();
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemes(this);
        setContentView(R.layout.activity_msaavatar);
        imageBig = null;
        imageModified = false;
        FIO_NAME = "";
        FIO_SUBNAME = "";
        displaymetrics = getMyDisplayMetrics();

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Appl.BarColor_avatar));
        getSupportActionBar().setTitle("Аватар");

        iv_msaava_ava = findViewById(R.id.iv_mesava_ava);
        et_msaava_name = findViewById(R.id.et_mesava_name);
        et_msaava_subname = findViewById(R.id.et_mesava_subname);
        tiv_msa_ava = findViewById(R.id.tiv_msa_ava);
        tiv_msa_ava.setOnClickListener(this);

        load_ava_record_from_SQLite();
    }


    // подключение меню----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_msa_ava, menu);
        setVisibleInfo();
        return true;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

		/*case R.id.////
			break;*/

            default:
                break;
        }
    }


    public Boolean Vefify_Message() {
        FIO_NAME = Appl.strnormalize(et_msaava_name.getText().toString());
        if (FIO_NAME.equals("")) {
            FIO_NAME = getRandomString(12);
        }
        return true;
    }


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
            if (file.exists()) {
              if (!file.delete()) return;
            }
        } catch (Exception ignored) {
        }
        try {
            String imageFilename_crop = "proto_acr.jpg";
            File file = new File(Environment.getExternalStorageDirectory(), imageFilename_crop);
            if (file.exists())
             if (!file.delete()) {
                 return;}
        } catch (Exception ignored) {
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 75, bytes);//100
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // сохранить аватар
            case R.id.mi_ava_save:
                if (Vefify_Message()) {
                    save_ava_to_SQLite();
                    save_ava_to_MSSQL();
                }
                return true;

            // выбор изображения для аватара
            case R.id.mi_ava_picture_choice:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                return true;

            // сделать фото
            case R.id.mi_ava_picture_takephoto:
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                deleteTempFile();
                File file = new File(Environment.getExternalStorageDirectory(), imageFilename_photo);
                outputFileUri_photo = Uri.fromFile(file);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri_photo);
                //	captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,	android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(captureIntent, CAMERA_CAPTURE);
                return true;

            // обрезать изображение
            case R.id.mi_ava_picture_crop:
                outputFileUri_photo = getImageUri(this, imageBig);
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(outputFileUri_photo, "image/*");
                cropIntent.putExtra("aspectX", 4);
                cropIntent.putExtra("aspectY", 4);
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                cropIntent.putExtra("output", outputFileUri_crop);
                cropIntent.putExtra("noFaceDetection", true);
                startActivityForResult(cropIntent, PIC_CROP);
                return true;

            // очистить изображение
            case R.id.mi_ava_picture_clear:
                imageBig = (null);
                FIO_NAME = "";
                FIO_SUBNAME = "";
                imageModified = true;
                setVisibleInfo();
                return true;

            // повернуть влево
            case R.id.mi_ava_picture_rotate_left:
                rotateImage(-1);
                setVisibleInfo();
                return true;

            // повернуть вправо
            case R.id.mi_ava_picture_rotate_right:
                rotateImage(1);
                setVisibleInfo();
                return true;

            // сделать круглой
            case R.id.mi_ava_picture_curcle:
                imageBig = transform(imageBig);
                setVisibleInfo();
                return true;

            // домой
            case android.R.id.home:
                smart_exit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        final Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, source.getWidth() / 2, paint);

        if (source != output) source.recycle();
        return output;
    }



    protected void smart_exit() {
        FIO_NAME = Appl.strnormalize(et_msaava_name.getText().toString());
        FIO_SUBNAME = Appl.strnormalize(et_msaava_subname.getText().toString());

        if ((!FIO_NAME.equalsIgnoreCase(Appl.FIO_NAME))
                || (!FIO_SUBNAME.equalsIgnoreCase(Appl.FIO_SUBNAME))
                || (imageModified)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dlg_confirm_req))

                    .setMessage(getString(R.string.dlg_date_changed))

                    .setNegativeButton((getString(R.string.dlg_prg_yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    if (Vefify_Message()) {
                                        save_ava_to_SQLite();
                                        load_ava_record_from_SQLite();
                                        setResult(RESULT_OK);
                                        finish();
                                        Appl.animateFinish(MsaAvatarActivity.this);
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
                                    Appl.animateFinish(MsaAvatarActivity.this);
                                }
                            })
                    .create()
                    .show();
        } else {
            setResult(RESULT_OK);
            finish();
            Appl.animateFinish(MsaAvatarActivity.this);
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
                        assert imageUri != null;
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        //	final InputStream imageStream = getContentResolver().openInputStream(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        imageBig = (BitmapFactory.decodeStream(imageStream));
                        imageModified = true;
                        setVisibleInfo();
                    } catch (Exception e) {
                        Appl.DisplayToastError(Appl.context.getString(R.string.s_no_request_image_from_gallery));
                        break;
                    }
                    break;

                case CAMERA_CAPTURE:
                    try {
                        imageBig = (Media.getBitmap(getContentResolver(), outputFileUri_photo));
                        imageModified = true;
                        setVisibleInfo();
                    } catch (Exception e) {
                        // Utils.DisplayToastError(Appl.context.getString(R.string.s_no_take_photo));
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
                            int i = fn.lastIndexOf(".");
                            if (i > 0) {
                                save_ava_to_SQLite();
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


    public static void setVisibleInfo() {
        if (imageBig != null) {
            int picwidth = imageBig.getWidth();
            int picheight = imageBig.getHeight();
            LinearLayout.LayoutParams paramsMyImage = (LinearLayout.LayoutParams) tiv_msa_ava.getLayoutParams();
            paramsMyImage.height = displaymetrics.widthPixels * picheight / picwidth;

            imageSmall = Bitmap.createScaledBitmap(imageBig, Appl.FIO_AVATAR_SIZE, Appl.FIO_AVATAR_SIZE, false);
            iv_msaava_ava.setImageBitmap(imageSmall);

            new Thread(new Runnable() {
                public void run() {
                    tiv_msa_ava.post(new Runnable() {
                        public void run() {
                            tiv_msa_ava.setImageBitmap(imageBig);
                        }
                    });
                }
            }).start();
        } else {
            imageSmall = null;
            iv_msaava_ava.setImageBitmap(null);
            tiv_msa_ava.setImageBitmap(imageBig);
        }
    }


}
