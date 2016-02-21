package com.solarpaygogo.solarpaygogoadminconsole;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class menuActivity extends AppCompatActivity {
    userCache CurrentUserCache;
    user currentUser;

    Button btnTransaction, btnSolarDevice, btnLogout, btnUser, btnChangePW;
    TextView tvUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnTransaction = (Button)findViewById(R.id.btnTransaction);
        btnSolarDevice = (Button)findViewById(R.id.btnSolarDevice);
        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnUser = (Button)findViewById(R.id.btnUser);
        btnChangePW = (Button)findViewById(R.id.btnChangePW);

        tvUsername = (TextView)findViewById(R.id.tvUsername);

        CurrentUserCache = new userCache(this);
        currentUser = CurrentUserCache.getCurrentUser();

        tvUsername.setText(currentUser.username);
        switch(currentUser.root){
            case (0):break;
            case (1):btnUser.setEnabled(true);break;
            case (2):btnSolarDevice.setEnabled(true);break;
            case (3):btnUser.setEnabled(true);btnSolarDevice.setEnabled(true);break;
        }

        btnTransaction.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageTransaction();
            }
        });

        btnSolarDevice.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageDevice();
            }
        });

        btnChangePW.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnUser.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageUser();
            }
        });
    }

    private void manageTransaction() { startActivity(new Intent(this, manageTransactionActivity.class));}

    private void manageDevice() { startActivity(new Intent(this,manageDeviceActivity.class));}

    private void manageUser(){
        startActivity(new Intent(this,manageUserActivity.class));
    }

    private void changePassword(){
        startActivity(new Intent(this,changePasswordActivity.class));
    }

    private void logoutUser(){
        CurrentUserCache.logoutCurrentUser();
        startActivity(new Intent(this,LoginActivity.class));
    }


}
