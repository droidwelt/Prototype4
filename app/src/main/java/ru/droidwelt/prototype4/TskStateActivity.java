package ru.droidwelt.prototype4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class TskStateActivity extends AppCompatActivity implements OnClickListener {

	private String outText = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Appl.setMyThemesDlg(this);
		setContentView(R.layout.activity_tskstate);
		setTitle(R.string.s_task_state);
		Button btn_Ok = findViewById(R.id.button_tskstate_ok);
		btn_Ok.setOnClickListener(this);

        if ((getIntent().getExtras()) != null)
            outText = getIntent().getExtras().getString("outText");

		OnClickListener radioListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				RadioButton rb = (RadioButton) v;
				switch (rb.getId()) {
				case R.id.rb_tskstate_1:
					outText = getString(R.string.s_task_zadano);
					break;
				case R.id.rb_tskstate_2:
					outText = getString(R.string.s_task_prinato);
					break;
				case R.id.rb_tskstate_3:
					outText = getString(R.string.s_task_rabota);
					break;
				case R.id.rb_tskstate_4:
					outText = getString(R.string.s_task_otkaz);
					break;
				case R.id.rb_tskstate_5:
					outText = getString(R.string.s_task_vipolneno);
					break;
				default:
					break;
				}
			}
		};
		
		RadioButton rb_tskstate_1 = findViewById(R.id.rb_tskstate_1);
		RadioButton rb_tskstate_2 = findViewById(R.id.rb_tskstate_2);
		RadioButton rb_tskstate_3 = findViewById(R.id.rb_tskstate_3);
		RadioButton rb_tskstate_4 = findViewById(R.id.rb_tskstate_4);
		RadioButton rb_tskstate_5 = findViewById(R.id.rb_tskstate_5);
		rb_tskstate_1.setOnClickListener(radioListener);
		rb_tskstate_2.setOnClickListener(radioListener);
		rb_tskstate_3.setOnClickListener(radioListener);
		rb_tskstate_4.setOnClickListener(radioListener);
		rb_tskstate_5.setOnClickListener(radioListener);
		
		if (outText.compareTo(getString(R.string.s_task_zadano))==0) rb_tskstate_1.setChecked(true);
		if (outText.compareTo(getString(R.string.s_task_prinato))==0) rb_tskstate_2.setChecked(true);
		if (outText.compareTo(getString(R.string.s_task_rabota))==0) rb_tskstate_3.setChecked(true);
		if (outText.compareTo(getString(R.string.s_task_otkaz))==0) rb_tskstate_4.setChecked(true);
		if (outText.compareTo(getString(R.string.s_task_vipolneno))==0) rb_tskstate_5.setChecked(true);
	}

	@Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.button_tskstate_ok: {
                Intent answerIntent = new Intent();
                if (outText.length() != 0) {
                    answerIntent.putExtra("ANS", outText);
                    setResult(RESULT_OK, answerIntent);
                }
            }
            finish();
            break;

            default:
                break;
        }
    }

}