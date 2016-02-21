package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class changePasswordActivity extends AppCompatActivity {
    userCache CurrentUserCache;
    user currentUser;

    TextView tvUsername;
    EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    Button btnChangePassword, btnCancel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        tvUsername = (TextView)findViewById(R.id.tvUsername);
        etCurrentPassword = (EditText)findViewById(R.id.etCurrentPassword);
        etNewPassword = (EditText)findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
        btnChangePassword = (Button)findViewById(R.id.btnChangePW);
        btnCancel = (Button)findViewById(R.id.btnCancel);

        CurrentUserCache = new userCache(this);
        currentUser = CurrentUserCache.getCurrentUser();

        tvUsername.setText(currentUser.username);

        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        btnChangePassword.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPW = etCurrentPassword.getText().toString();
                String newPW = etNewPassword.getText().toString();
                String confirmPW = etConfirmPassword.getText().toString();

                if (newPW.equals(confirmPW)== true){
                    if (currentPW.equals(currentUser.password)== true){
                        changePassword(currentUser.id,newPW);
                    } else {
                        etCurrentPassword.setText("");
                        showErrorMessage("Current Password is incorrect");
                    }
                }else{
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");
                    showErrorMessage("Confirm Password does not match with New Password.");
                }

            }
        });
    }

    private void changePassword(int id, final String updatePassword){
        DB_user_request db_user_request = new DB_user_request(this);
        db_user_request.changeUserPassword(id, updatePassword, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result.equals("SUCCESS")) {
                    updateUserPassword(updatePassword);
                } else {
                    showErrorMessage("Server Error");
                }
            }
        });
    }

    private void cancel(){
        startActivity(new Intent(this, menuActivity.class));
    }

    private void updateUserPassword(String password){
        currentUser.password = password;
        CurrentUserCache.setCurrentUser(currentUser);
        showSuccessMessage();
    }

    private void showErrorMessage(String errorMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(changePasswordActivity.this);
        dialogBuilder.setMessage(errorMessage);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void showSuccessMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(changePasswordActivity.this);
        dialogBuilder.setMessage("SUCCESS: Updated Password");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancel();
            }
        });
        dialogBuilder.show();
    }


}
