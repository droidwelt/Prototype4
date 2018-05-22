package ru.droidwelt.prototype4;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChoiceClrActivity extends AppCompatActivity {

	private   TextView tv_color_test;
	private static String sClr;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Appl.setMyThemesDlg(this);
		setContentView(R.layout.activity_choiceclr);
		setTitle(getString(R.string.s_message_choice_clr));
		ImageButton clr0,clr1,clr2,clr3,clr4,clr5,clr6,clr7,clr8,clr9,clr10,clr11,clr12,clr13,clr14,clr15 ;
		clr0 = findViewById(R.id.clr0);
		clr1 = findViewById(R.id.clr1);
		clr2 = findViewById(R.id.clr2);
		clr3 = findViewById(R.id.clr3);
		clr4 = findViewById(R.id.clr4);
		clr5 = findViewById(R.id.clr5);
		clr6 = findViewById(R.id.clr6);
		clr7 = findViewById(R.id.clr7);
		clr8 = findViewById(R.id.clr8);
		clr9 = findViewById(R.id.clr9);
		clr10 = findViewById(R.id.clr10);
		clr11 = findViewById(R.id.clr11);
		clr12 = findViewById(R.id.clr12);
		clr13 = findViewById(R.id.clr13);
		clr14 = findViewById(R.id.clr14);
		clr15 = findViewById(R.id.clr15);
		
		clr0.setBackgroundColor(Appl.getColorByString("0"));
		clr1.setBackgroundColor(Appl.getColorByString("1"));
		clr2.setBackgroundColor(Appl.getColorByString("2"));
		clr3.setBackgroundColor(Appl.getColorByString("3"));
		clr4.setBackgroundColor(Appl.getColorByString("4"));
		clr5.setBackgroundColor(Appl.getColorByString("5"));
		clr6.setBackgroundColor(Appl.getColorByString("6"));
		clr7.setBackgroundColor(Appl.getColorByString("7"));
		clr8.setBackgroundColor(Appl.getColorByString("8"));
		clr9.setBackgroundColor(Appl.getColorByString("9"));
		clr10.setBackgroundColor(Appl.getColorByString("A"));
		clr11.setBackgroundColor(Appl.getColorByString("B"));
		clr12.setBackgroundColor(Appl.getColorByString("C"));
		clr13.setBackgroundColor(Appl.getColorByString("D"));
		clr14.setBackgroundColor(Appl.getColorByString("E"));
		clr15.setBackgroundColor(Appl.getColorByString("F"));
		
		clr0.setOnClickListener(oclBtnOk);
		clr1.setOnClickListener(oclBtnOk);
		clr2.setOnClickListener(oclBtnOk);
		clr3.setOnClickListener(oclBtnOk);
		clr4.setOnClickListener(oclBtnOk);
		clr5.setOnClickListener(oclBtnOk);
		clr6.setOnClickListener(oclBtnOk);
		clr7.setOnClickListener(oclBtnOk);
		clr8.setOnClickListener(oclBtnOk);
		clr9.setOnClickListener(oclBtnOk);
		clr10.setOnClickListener(oclBtnOk);
		clr11.setOnClickListener(oclBtnOk);
		clr12.setOnClickListener(oclBtnOk);
		clr13.setOnClickListener(oclBtnOk);
		clr14.setOnClickListener(oclBtnOk);
		clr15.setOnClickListener(oclBtnOk);
		
		tv_color_test = findViewById(R.id.tv_color_test);
		sClr = Appl.strnormalize(MsaEditActivity.MSA_CLR);
		if (sClr.equals("")) sClr="0";
		tv_color_test.setBackgroundColor(Appl.getColorByString(sClr));
	}
	
	public  void setClr (String s) {
		sClr=s;
		tv_color_test.setBackgroundColor(Appl.getColorByString(sClr));		
		MsaEditActivity.setTitleClr (sClr);
	}

	
	OnClickListener oclBtnOk = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {

			case R.id.clr0:
				setClr ("0");
				break;
				
			case R.id.clr1:
				setClr ("1");
				break;
				
			case R.id.clr2:
				setClr ("2");
				break;
				
			case R.id.clr3:
				setClr ("3");
				break;
				
			case R.id.clr4:
				setClr ("4");
				break;
				
			case R.id.clr5:
				setClr ("5");
				break;
				
			case R.id.clr6:
				setClr ("6");
				break;
				
			case R.id.clr7:
				setClr ("7");
				break;
				
			case R.id.clr8:
				setClr ("8");
				break;
				
			case R.id.clr9:
				setClr ("9");
				break;
				
			case R.id.clr10:
				setClr ("A");
				break;
				
			case R.id.clr11:
				setClr ("B");
				break;
				
			case R.id.clr12:
				setClr ("C");
				break;
				
			case R.id.clr13:
				setClr ("D");
				break;
				
			case R.id.clr14:
				setClr ("E");
				break;
				
			case R.id.clr15:
				setClr ("F");
				break;
		
			default:
				break;
			}
		}
	};	
	

	
	}