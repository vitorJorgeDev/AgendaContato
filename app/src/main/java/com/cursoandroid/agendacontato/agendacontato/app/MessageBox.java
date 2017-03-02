package com.cursoandroid.agendacontato.agendacontato.app;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by Vitor on 01/03/17.
 */

public class MessageBox {

    public static void showInfo(Context ctx, String title, String msg) {

        show(ctx, title, msg, android.R.drawable.ic_dialog_info);
    }

    public static void showAlert(Context ctx, String title, String msg) {

        show(ctx, title, msg, android.R.drawable.ic_dialog_alert);
    }

    public static void show(Context ctx, String title, String msg) {

        show(ctx, title, msg, 0);
    }

    public static void show(Context ctx, String title, String msg, int iconId){

        AlertDialog.Builder dig = new AlertDialog.Builder(ctx);
        dig.setIcon(iconId);
        dig.setTitle(title);
        dig.setMessage(msg);
        dig.setNeutralButton("OK", null);
        dig.show();

    }
}
