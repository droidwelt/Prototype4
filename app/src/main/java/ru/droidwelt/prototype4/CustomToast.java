package ru.droidwelt.prototype4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


@SuppressLint("InflateParams")
class CustomToast extends android.widget.Toast {

    private static TextView toastText;
    private static ImageView toastImage;

    private CustomToast(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater != null ? inflater.inflate(R.layout.toast_info, null) : null;
        if (rootView != null) {
            toastImage = rootView.findViewById(R.id.iv_mesedit_lbl);
        }
        toastImage.setImageResource(R.drawable.ic_launcher);
        if (rootView != null) {
            toastText = rootView.findViewById(R.id.textView1);
        }
        this.setView(rootView);
        this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        this.setDuration(android.widget.Toast.LENGTH_LONG);
    }
 
  /*  private static  CustomToast makeText(Context context, CharSequence text) {
        CustomToast result = new CustomToast(context);
        toastText.setText(text); 
        return result;
    }
 
    public static CustomToast makeText(Context context, CharSequence text, int duration ) {
        CustomToast result = new CustomToast(context);
        result.setDuration(duration);
        toastText.setText(text); 
        return result;
    }*/
    
    static CustomToast makeText(Context context, CharSequence text, int duration, int resId) {
        CustomToast result = new CustomToast(context);
        result.setDuration(duration);
        toastText.setText(text); 
        toastImage.setImageResource(resId);
        return result;
    }

    static CustomToast makeText(Context context, CharSequence text, int duration, Drawable dr) {
        CustomToast result = new CustomToast(context);
        result.setDuration(duration);
        toastText.setText(text);
        toastImage.setImageDrawable(dr);
        return result;
    }
 

}

