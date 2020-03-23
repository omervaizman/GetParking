package com.example.getparking.Helpers;

import android.app.ProgressDialog;
import android.content.Context;



public class MyProgressDialog extends ProgressDialog {

    public MyProgressDialog(Context context) {
        super(context );
        this.setCancelable(false);
        this.setMessage("אנא המתן...");
    }

    @Override
    public void show() {
        if (!isShowing()) {
            super.show();
        }
    }

    @Override
    public void hide() {

        if (isShowing()) {
            super.hide();
        }
    }
}
