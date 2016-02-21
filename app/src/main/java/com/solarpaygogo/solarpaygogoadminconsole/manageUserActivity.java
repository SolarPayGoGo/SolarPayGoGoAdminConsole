package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class manageUserActivity extends AppCompatActivity {

    Button btnRegister, btnBack;
    ListView lvUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnBack = (Button)findViewById(R.id.btnBack);
        lvUser = (ListView)findViewById(R.id.lvUser);

        getAllUser();

        btnRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        lvUser.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvSelectedItem = (TextView) view.findViewById(R.id.cellUsername);
                String selectedUsername = tvSelectedItem.getText().toString();

                if (selectedUsername.equals("USERNAME") == false) {
                    getOneUser(selectedUsername);
                }
            }
        });
    }

    private void getOneUser (String username){
        DB_user_request db_user_request = new DB_user_request(this);
        db_user_request.getOneUserData(username, new getUserCallBack() {
            @Override
            public void done(user returnedUser) {
                if (returnedUser != null) {
                    showSelectedUser(returnedUser);
                } else {
                    showErrorMessage("Fail to load selected User data from database");
                }
            }
        });
    }

    private void getAllUser (){
        DB_user_request db_user_request = new DB_user_request(this);
        db_user_request.getAllUserData(new getAllUserCallBack() {
            @Override
            public void done(ArrayList<user> returnedUsers) {
                if (returnedUsers != null) {
                    updateLVUser(returnedUsers);
                } else {
                    showErrorMessage("Fail to load User list from database");
                }
            }
        });
    }

    private void updateLVUser (ArrayList<user> returnedUsers){
        ArrayList<HashMap<String,String>> displayList = new ArrayList<HashMap<String,String>>();

        HashMap<String, String> HeaderMap = new HashMap<String, String>();
        HeaderMap.put("username","USERNAME");
        HeaderMap.put("firstName","FIRST NAME");
        HeaderMap.put("lastName","LAST NAME");
        displayList.add(HeaderMap);

        for (int k = 0; k < returnedUsers.size(); k++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("username",returnedUsers.get(k).username);
            map.put("firstName",returnedUsers.get(k).firstName);
            map.put("lastName",returnedUsers.get(k).lastName);
            displayList.add(map);
        }
        SimpleAdapter listAdapter = new SimpleAdapter(this, displayList, R.layout.row_user, new String[] {"username","firstName","lastName"}, new int [] {R.id.cellUsername,R.id.cellFirstName,R.id.cellLastName});
        lvUser.setAdapter(listAdapter);
    }

    private void registerNewUser(){startActivity(new Intent(this, registerUserActivity.class));}

    private void goBack(){startActivity(new Intent(this, menuActivity.class));}

    private void showErrorMessage(String errorMsg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageUserActivity.this);
        dialogBuilder.setMessage(errorMsg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void showSelectedUser(final user selectedUser){
        String rootRight = "";
        switch (selectedUser.root){
            case (0): rootRight = "Manage Transaction"; break;
            case (1): rootRight = "Manage Transaction \n Manage User"; break;
            case (2): rootRight = "Manage Transaction \n Manage Solar Device"; break;
            case (3): rootRight = "Manage Transaction \n Manage Solar Device \n Manage User"; break;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageUserActivity.this);
        dialogBuilder.setMessage("Username : " + selectedUser.username + "\n" + "Password : " + selectedUser.password + "\n" +
                                "First name : "+ selectedUser.firstName + "\n" + "Last name : " + selectedUser.lastName + "\n" +
                                "Email : " + selectedUser.email + "\n" + "Right : "+ rootRight);
        dialogBuilder.setPositiveButton("Cancel", null);
        dialogBuilder.setNegativeButton("Delete User", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                confirmDeleteUser(selectedUser);
            }
        });
        dialogBuilder.setNeutralButton("Reset User password", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                confirmResetPassword(selectedUser);
            }
        });
        dialogBuilder.show();
    }

    private void confirmDeleteUser(final user selectedUser){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageUserActivity.this);
        dialogBuilder.setMessage("Confirm Deleting - " + selectedUser.username);
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteUser(selectedUser.id);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectedUser(selectedUser);
            }
        });
        dialogBuilder.show();
    }

    private void confirmResetPassword(final user selectedUser){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageUserActivity.this);
        dialogBuilder.setMessage("Confirm Reset Password - "+ selectedUser.username);
        dialogBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetPassword(selectedUser.id, selectedUser.username);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectedUser(selectedUser);
            }
        });
        dialogBuilder.show();
    }

    private void deleteUser(int id){
        DB_user_request db_user_request = new DB_user_request(this);
        db_user_request.deleteUser(id, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result.equals("SUCCESS")) {
                    getAllUser();
                } else {
                    showErrorMessage("Server Error");
                }
            }
        });
    }

    private void resetPassword(int id, final String username){
        String updatePassword = "123456";
        DB_user_request db_user_request = new DB_user_request(this);
        db_user_request.changeUserPassword(id, updatePassword, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result.equals("SUCCESS")) {
                    getOneUser(username);
                } else {
                    showErrorMessage("Server Error");
                }
            }
        });
    }
}
