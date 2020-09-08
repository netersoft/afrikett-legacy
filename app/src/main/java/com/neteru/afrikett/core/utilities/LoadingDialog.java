package com.neteru.afrikett.core.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.neteru.afrikett.R;

/**
 * Created by Ark Noam on 19/07/2018.
 */

@SuppressWarnings("unused")
public class LoadingDialog {
    private Context context;
    private AlertDialog LoadingBox;
    private ProgressBar progressBar;
    private TextView msgText;

    public LoadingDialog(Context ctx){

        context = ctx;

        LoadingBox = new AlertDialog.Builder(context).create();

        LayoutInflater factory = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View LoadingView = factory.inflate(R.layout.layout_loading, null);

        progressBar = LoadingView.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        msgText = LoadingView.findViewById(R.id.msg);
        msgText.setText(context.getString(R.string.loading_in_progress));

        LoadingBox.setView(LoadingView);
        LoadingBox.setCancelable(false);
    }

    public void setColor(int color){
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void setCancelable(){
        LoadingBox.setCancelable(true);
    }

    public boolean isShowing(){
        return LoadingBox.isShowing();
    }

    public void show(){
        this.LoadingBox.show();
    }

    public void dismiss() {
        if (this.LoadingBox.isShowing()) { this.LoadingBox.dismiss(); }
    }

    public void setMsg(String m){
        this.msgText.setText(m);
    }

}
