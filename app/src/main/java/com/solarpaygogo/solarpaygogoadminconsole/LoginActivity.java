package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity{

    EditText etUsername, etPassword;
    Button btnLogin;
    userCache currentUserCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        currentUserCache = new userCache(this);


        btnLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                user User = new user(username, password);
                authenticate(User);
            }
        });
    }

    public void authenticate(user User){
        DB_user_request db_user_request = new DB_user_request(this);
        db_user_request.getUserdata(User, new getUserCallBack() {
            @Override
            public void done(user returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void logUserIn(user returnedUser) {
        currentUserCache.setCurrentUser(returnedUser);
        startActivity(new Intent(this, menuActivity.class));
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Incorrect user details");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }





}
