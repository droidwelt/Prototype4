package ru.droidwelt.prototype4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;


public class RegisterDBActivity extends AppCompatActivity {

    private Button bt_register_dev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.setMyThemesDlg(this);
        setContentView(R.layout.activity_register_db);
        setTitle(getString(R.string.header_register_device));
        bt_register_dev = findViewById(R.id.button_register_dev);
        bt_register_dev.setOnClickListener(onclickButton);

        Log.i ("EEE",Appl.DEVICE_FCM);
        Appl.DEVICE_REGISTRED = JSONURL_DEVISREG(Appl.query_MSSQL_Background("DEV_VFYREGFCM '" + Appl.DEVICE_FCM + "'"));
        displayRegistredDevice();
    }


    android.view.View.OnClickListener onclickButton = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.button_register_dev:
                    Intent intent = new Intent(RegisterDBActivity.this, RegisterDeviceActivity.class);
                    startActivityForResult(intent, 999);
                    break;

                default:
                    break;
            }
        }
    };

	/*--------------------------------------------------------------------------------*/

    protected void displayRegistredDevice() {
        TextView tv_register_dev = findViewById(R.id.textView_register_dev);
        TextView tv_register_fcm = findViewById(R.id.textView_register_fcm);
        String s = getString(R.string.s_reg_fcm) +  FirebaseInstanceId.getInstance().getToken();
        tv_register_fcm.setText(s);
        TextView tv_register_model = findViewById(R.id.textView_register_model);
        s = getString(R.string.s_reg_model) + Appl.DEVICE_MODEL;
        tv_register_model.setText(s);
        if (Appl.DEVICE_REGISTRED) {
            s = getString(R.string.s_reg_registred) + Appl.DEVICE_ID;
            tv_register_dev.setText(s);
            bt_register_dev.setEnabled(false);
        } else {
            tv_register_dev.setText(R.string.s_reg_notregistred);
            bt_register_dev.setEnabled(true);
        }
    }


    public boolean JSONURL_DEVISREG(JSONArray  urls) {
        if (!urls.toString().contains("######")) {
            try {
                Appl.DEVICE_DISABLED = 0;
                if (urls.length() > 0) {
                    Appl.DEVICE_DISABLED = Integer.parseInt(urls.getJSONObject(0).getString("DEV_DIS"));
                    Appl.DEVICE_ID = Integer.parseInt(urls.getJSONObject(0).getString("DEV_ID"));
                    return true;
                } else {
                    Log.i ("EEE","urls.length() == 0");
                    return false;
                }
            } catch (JSONException e) {
                Log.i ("EEE",e.toString());
                return false;
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        if (requestCode == 999)
            displayRegistredDevice();
    }


}
