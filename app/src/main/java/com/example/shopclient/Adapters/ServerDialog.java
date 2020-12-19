package com.example.shopclient.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.shopclient.R;

public class ServerDialog {
    private Activity activity;
    private AlertDialog dialog;
    public ServerDialog(Activity myActivity){
        activity = myActivity;
    }
    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.server_dialog,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }
    public void dismissDialog(){
        dialog.dismiss();
    }
}
