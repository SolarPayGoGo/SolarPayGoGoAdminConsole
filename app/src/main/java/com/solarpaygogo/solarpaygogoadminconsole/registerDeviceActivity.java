package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class registerDeviceActivity extends AppCompatActivity {
    userCache CurrentUserCache;
    user currentUser;

    String default_Timezone = "";
    String default_APNname = "";
    String default_APNusername = "";
    String default_APNpassword = "";
    String default_FTPaddress = "";
    String default_FTPport = "";
    String default_FTPusername = "";
    String default_FTPpassword = "";
    String default_FTPpath = "";
    String default_TimeUploadHH = "";
    String default_TimeUploadMM = "";

    Button btnRegister, btnCancel, btnRanSerialNum, btnRanPrivateKey;
    EditText etSerialNum, etDevicePhoneNum, etPrivateKey, etTimezone, etClientFirstName, etClientLastname,
            etClientPhoneNum, etClientEmail, etAPNname, etAPNusername, etAPNpassword, etFTPaddress, etFTPport,
            etFTPusername, etFTPpassword,etFTPpath, etTimeUploadHH, etTimeUploadMM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnRanSerialNum = (Button)findViewById(R.id.btnRanSerialNum);
        btnRanPrivateKey = (Button)findViewById(R.id.btnRanPrivateKey);

        etSerialNum = (EditText)findViewById(R.id.etSerialNumber);
        etDevicePhoneNum = (EditText)findViewById(R.id.etDevicePhoneNum);
        etPrivateKey = (EditText)findViewById(R.id.etPrivateKey);
        etTimezone = (EditText)findViewById(R.id.etTimeZone);
        etClientFirstName = (EditText)findViewById(R.id.etClientFirstName);
        etClientLastname = (EditText)findViewById(R.id.etClientLastName);
        etClientPhoneNum = (EditText)findViewById(R.id.etClientPhoneNum);
        etClientEmail = (EditText)findViewById(R.id.etClientEmail);
        etAPNname = (EditText)findViewById(R.id.etAPNname);
        etAPNusername = (EditText)findViewById(R.id.etAPNusername);
        etAPNpassword = (EditText)findViewById(R.id.etAPNpassword);
        etFTPaddress = (EditText)findViewById(R.id.etFTPaddress);
        etFTPport = (EditText)findViewById(R.id.etFTPport);
        etFTPusername = (EditText)findViewById(R.id.etFTPusername);
        etFTPpassword = (EditText)findViewById(R.id.etFTPpassword);
        etFTPpath = (EditText)findViewById(R.id.etFTPpath);
        etTimeUploadHH = (EditText)findViewById(R.id.etTimeUploadHH);
        etTimeUploadMM = (EditText)findViewById(R.id.etTimeUploadMM);

        CurrentUserCache = new userCache(this);
        currentUser = CurrentUserCache.getCurrentUser();

        getDefaultDeviceSetting();

        btnRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serialNum = etSerialNum.getText().toString();
                String devicePhoneNum = etDevicePhoneNum.getText().toString();
                String privateKey = etPrivateKey.getText().toString();
                String timezone = etTimezone.getText().toString();
                String clientFirstName = etClientFirstName.getText().toString();
                String clientLastName = etClientLastname.getText().toString();
                String clientPhoneNum = etClientPhoneNum.getText().toString();
                String clientEmail = etClientEmail.getText().toString();
                String APNname = etAPNname.getText().toString();
                String APNusername = etAPNusername.getText().toString();
                String APNpassword = etAPNpassword.getText().toString();
                String FTPaddress = etFTPaddress.getText().toString();
                String FTPport = etFTPport.getText().toString();
                String FTPusername = etFTPusername.getText().toString();
                String FTPpassword = etFTPpassword.getText().toString();
                String FTPpath = etFTPpath.getText().toString();
                String TimeUploadHH = etTimeUploadHH.getText().toString();
                String TimeUploadMM = etTimeUploadMM.getText().toString();

                if (serialNum.equals("")||devicePhoneNum.equals("")||privateKey.equals("")||
                        timezone.equals("")||TimeUploadHH.equals("")||TimeUploadMM.equals("")){
                    showErrorMessage("Error can not leave * item/items blank");
                } else {
                    if ((serialNum.length() == 15)&&(privateKey.length() == 100)) {
                        try {
                            int timezoneInt = Integer.parseInt(timezone);
                            int TimeUploadHHInt = Integer.parseInt(TimeUploadHH);
                            int TimeUploadMMInt = Integer.parseInt(TimeUploadMM);
                            long serialNumLong = Long.parseLong(serialNum);
                            long devicePhoneNumLong = Long.parseLong(devicePhoneNum);
                            long clientPhoneNumLong = Long.parseLong(clientPhoneNum);

                            int serialNum_CheckSum = 0;
                            for (int k = 0; k <14; k++){
                                serialNum_CheckSum += (serialNum.charAt(k) - '0');
                            }
                            serialNum_CheckSum = serialNum_CheckSum % 10;

                            if (serialNum_CheckSum == (serialNum.charAt(14)- '0')) {

                                String currentUsername = currentUser.username;

                                Date currentDate = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
                                String currentDateTime = simpleDateFormat.format(currentDate);

                                Device newDevice = new Device(-1, serialNum, devicePhoneNum, privateKey, timezoneInt,
                                        clientFirstName, clientLastName, clientPhoneNum, clientEmail, APNname, APNusername,
                                        APNpassword, FTPaddress, FTPport, FTPusername, FTPpassword, FTPpath, TimeUploadHHInt,
                                        TimeUploadMMInt, currentUsername, currentDateTime, currentUsername, currentDateTime);
                                registerNewDevice(newDevice);
                            } else {
                                showErrorMessage("Error: checksum error on the serial number field!");
                            }

                        } catch (NumberFormatException e) {
                            showErrorMessage("Error: Some inputs only accept numerical value!");
                        }
                    } else {
                        showErrorMessage("Error: Serial Number must be 15 digits long and Private Key must be 100 digits long!");
                    }
                }

            }
        });
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        btnRanSerialNum.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterHWVersion();
            }
        });
        btnRanPrivateKey.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ranPrivateKey();
            }
        });
    }

    private void registerNewDevice(Device newDevice){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.registerNewDevice(newDevice, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result == "SUCCESS") {
                    showSuccessMessage();
                } else {
                    showErrorMessage("Error when registering new device");
                }
            }
        });
    }

    private void cancel(){
        startActivity(new Intent(this, manageDeviceActivity.class));
    }

    private void getDefaultDeviceSetting(){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.getDefaultDeviceSetting(new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result == null || result == "") {
                    showErrorMessage("Error when getting default device setting");
                } else {
                    setDefaultDeviceSetting(result);
                }
            }
        });
    }

    private void setDefaultDeviceSetting(String result){
        String column [] = result.split(",");
        default_Timezone = column[0];
        default_APNname = column[1];
        default_APNusername = column[2];
        default_APNpassword = column[3];
        default_FTPaddress = column[4];
        default_FTPport = column[5];
        default_FTPusername = column[6];
        default_FTPpassword = column[7];
        default_FTPpath = column[8];
        default_TimeUploadHH = column[9];
        default_TimeUploadMM = column[10];

        etTimezone.setText(default_Timezone);
        etAPNname.setText(default_APNname);
        etAPNusername.setText(default_APNusername);
        etAPNpassword.setText(default_APNpassword);
        etFTPaddress.setText(default_FTPaddress);
        etFTPport.setText(default_FTPport);
        etFTPusername.setText(default_FTPusername);
        etFTPpassword.setText(default_FTPpassword);
        etFTPpath.setText(default_FTPpath);
        etTimeUploadHH.setText(default_TimeUploadHH);
        etTimeUploadMM.setText(default_TimeUploadMM);
    }

    private void enterHWVersion(){
        inputData = "";
        requestInfoDialog("Please Enter Hardware Version (0-9)");
    }

    private void postEnterHWVersion(){
        int inputHWNum;
        try {
            inputHWNum = Integer.parseInt(inputData);
            if (inputHWNum < 0 || inputHWNum > 9) {
                showErrorMessage("Error: Only accept integer value between 0-9");
            } else {
                ranSerialNumber(inputHWNum);
            }
        } catch (NumberFormatException e){
            showErrorMessage("Error: Please only input numerical value!");
        }
    }

    private void ranSerialNumber(int hwVersion){
        Random random = new Random();
        int[]coreNum = new int[15];
        coreNum[0] = hwVersion;
        int sum = coreNum[0];
        for (int k = 1; k<14;k++ ){
            coreNum[k] = random.nextInt(10);
            sum += coreNum[k];
        }
        coreNum[14]= sum % 10;
        String tempSerialNumber = "";
        for (int k=0; k<15; k++){
            tempSerialNumber += coreNum[k];
        }
        etSerialNum.setText(tempSerialNumber.toString());
    }
    private void ranPrivateKey(){
        Random random = new Random();
        String tempPrivateKeyString= "";
        int [][] tempPrivateKey = new int[10][10];
        for (int k = 0; k<10; k++){
            for (int n = 0; n<10; n++){
                boolean repeat = true;
                while (repeat) {
                    tempPrivateKey[k][n] = random.nextInt(10);
                    repeat = false;
                    for (int x = 0; x < n; x++) {
                        if (tempPrivateKey[k][n] == tempPrivateKey[k][x]){
                            repeat = true;
                        }
                    }
                }
                tempPrivateKeyString += tempPrivateKey[k][n];
            }
        }
        etPrivateKey.setText(tempPrivateKeyString);
    }

    private void showErrorMessage(String errorMsg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(registerDeviceActivity.this);
        dialogBuilder.setMessage(errorMsg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void showSuccessMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(registerDeviceActivity.this);
        dialogBuilder.setMessage("SUCCESS: Register New Device");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancel();
            }
        });
        dialogBuilder.show();
    }

    private String inputData;

    private void requestInfoDialog(String Msg) {

        LayoutInflater layoutInflater = LayoutInflater.from(registerDeviceActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_request_info,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(registerDeviceActivity.this);
        dialogBuilder.setView(view);

        final TextView tvMsg = (TextView) view.findViewById(R.id.tvMsg);
        final EditText etDataInput = (EditText) view.findViewById(R.id.etInputData);

        tvMsg.setText(Msg);

        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputData = etDataInput.getText().toString();
                dialog.cancel();
                postEnterHWVersion();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

}
