package ru.droidwelt.prototype4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesDlg(this);
        setContentView(R.layout.activity_start);
        //  setTitle(getString(R.string.app_name));
        setTitle(Appl.getMainTitle());

        TextView tv_welcome = findViewById(R.id.tv_welcome);
        if (Appl.FIO_ID >= 0) {
            String s = getString(R.string.action_pwd_ok) + " " + Appl.FIO_NAME;
            tv_welcome.setText(s);
        } else tv_welcome.setText(getString(R.string.action_no_user));

        ImageView iv_avatar = findViewById(R.id.iv_avatar);
        iv_avatar.setImageDrawable(Appl.getMyAvatar());

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                timer.purge();
                timer.cancel();
                finish();
            }
        }, 2000);
    }


}
