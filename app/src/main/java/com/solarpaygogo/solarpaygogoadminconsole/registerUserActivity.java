package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

public class registerUserActivity extends AppCompatActivity {

    Button btnRegister, btnCancel;
    EditText etUsername, etPassword, etConfirmPassword, etFirstName, etLastName, etEmail;
    CheckedTextView ctvManageUser, ctvManageDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etLastName = (EditText)findViewById(R.id.etLastName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        ctvManageUser = (CheckedTextView)findViewById(R.id.ctvManageUser);
        ctvManageDevice = (CheckedTextView)findViewById(R.id.ctVManageDevice);

        ctvManageUser.setOnClickListener(new CheckedTextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (ctvManageUser.isChecked()){
                    ctvManageUser.setChecked(false);
                } else {
                    ctvManageUser.setChecked(true);
                }
            }
        });

        ctvManageDevice.setOnClickListener(new CheckedTextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (ctvManageDevice.isChecked()){
                    ctvManageDevice.setChecked(false);
                } else {
                    ctvManageDevice.setChecked(true);
                }
            }
        });

        btnRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password, confirmPassword, firstName, lastName, email;
                boolean manageUserRight, manageDeviceRight ;
                int root;

                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();
                email = etEmail.getText().toString();
                manageUserRight = ctvManageUser.isChecked();
                manageDeviceRight = ctvManageDevice.isChecked();
                if (manageUserRight == true) {
                    if (manageDeviceRight == true) {
                        root = 3;
                    } else {
                        root = 1;
                    }
                } else {
                    if (manageDeviceRight == true) {
                        root = 2;
                    } else {
                        root = 0;
                    }
                }


                if (username.equals("") == false || password.equals("") == false){
                    if (password.equals(confirmPassword)){
                        user newUser = new user(-1,username,password,firstName,lastName,email,root);
                        register(newUser);
                    } else {
                        etPassword.setText("");
                        etConfirmPassword.setText("");
                        showErrorMessage("Confirm Password does not match with Password");
                    }
                } else {
                    showErrorMessage("Can't leave Username or Password Blank");
                }

            }
        });

        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void register(user newUser){
        DB_user_request db_user_request = new DB_user_request(this);
        db_user_request.registerNewUser(newUser, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result == "SUCCESS") {
                    showSuccessMessage();
                } else {
                    showErrorMessage(result);
                }
            }
        });
    }
    private void cancel(){
        startActivity(new Intent(this, manageUserActivity.class));
    }

    private void showErrorMessage(String errorMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(registerUserActivity.this);
        dialogBuilder.setMessage(errorMessage);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    private void showSuccessMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(registerUserActivity.this);
        dialogBuilder.setMessage("SUCCESS: Created New User");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancel();
            }
        });
        dialogBuilder.show();
    }
}
