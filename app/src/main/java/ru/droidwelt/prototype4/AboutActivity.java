package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class AboutActivity extends AppCompatActivity {


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesDlg(this);
        setContentView(R.layout.activity_about);
        setTitle(Appl.getMainTitle());
     //   String refreshedToken = FirebaseInstanceId.getInstance().getToken();
     //   Log.d("EEEEEEEE", "Refreshed token: " + refreshedToken);

        String s = "";
        //  s +=Appl.getTopActivityName() + " \n";
    //    s += getString(R.string.s_reg_fcm) + refreshedToken + " \n";
        s += " OS VIERSION: " + System.getProperty("os.version") + " \n";
        s += " OS INCREMENTAL:" + android.os.Build.VERSION.INCREMENTAL + " \n";
        s += " OS API RELEASE: " + android.os.Build.VERSION.RELEASE + " \n";
        s += " OS API SDK_INT: " + android.os.Build.VERSION.SDK_INT + " \n";
        s += " DEVICE: " + android.os.Build.DEVICE + " \n";
        s += " MODEL: " + android.os.Build.MODEL + " \n";
        s += " PRODUCT: " + android.os.Build.PRODUCT + " \n";
        s += " HARDWARE: " + android.os.Build.HARDWARE + " \n";
        s += " MANUFACTURE: " + android.os.Build.MANUFACTURER + " \n";
        s += " ID: " + android.os.Build.ID + " \n";
        s += " BRAND: " + android.os.Build.BRAND + " \n";
        s += " DISPLAY: " + android.os.Build.DISPLAY + " \n";
        s += " HOST: " + android.os.Build.HOST + " \n";
        s += " SCREEN : " + getDisplaySizeX() + " x " + getDisplaySizeY() + " / " + getDensityDpi() + " DPI";
        s += " / " + String.format("%.2f", getDisplayFisSize()) + " inch";

        TextView tv_about3 = findViewById(R.id.tv_about3);
        tv_about3.setText(s);
    }



    public double getDisplayFisSize() {
        return Math.hypot((double) getDisplaySizeX(), (double) getDisplaySizeY()) / getDensityDpi();
    }

    public int getDisplaySizeX() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    public int getDisplaySizeY() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    public int getDensityDpi() {
        return getResources().getDisplayMetrics().densityDpi;
    }


}
