package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * Created by Terry on 25/2/16.
 */
public class SMSRespondDialogActivity extends Activity
{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String dialogMessage = getIntent().getExtras().getString("Respond_Msg").toString();
        new AlertDialog.Builder(this)
                .setTitle("New SMS Respond from device")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                })
                .setMessage(dialogMessage)
                .show();
    }


}
