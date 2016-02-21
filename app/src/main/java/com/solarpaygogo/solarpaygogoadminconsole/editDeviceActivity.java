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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class editDeviceActivity extends AppCompatActivity {
    userCache CurrentUserCache;
    user currentUser;

    int id;
    String SerialNum, CreatedBy, CreatedAt;

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

        etSerialNum.setEnabled(false);
        btnRanSerialNum.setEnabled(false);
        btnRegister.setText("Save Changes");

        CurrentUserCache = new userCache(this);
        currentUser = CurrentUserCache.getCurrentUser();

        SerialNum = getIntent().getExtras().getString("selectedSerialNum");

        getDeviceInfo(SerialNum);

        btnRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serialNum = SerialNum;
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
                    try{
                        int timezoneInt = Integer.parseInt(timezone);
                        int TimeUploadHHInt = Integer.parseInt(TimeUploadHH);
                        int TimeUploadMMInt = Integer.parseInt(TimeUploadMM);
                        long devicePhoneNumLong = Long.parseLong(devicePhoneNum);
                        long clientPhoneNumLong = Long.parseLong(clientPhoneNum);

                        String currentUsername = currentUser.username;

                        Date currentDate = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.UK);
                        String currentDateTime = simpleDateFormat.format(currentDate);

                        Device newDevice = new Device(-1,serialNum,devicePhoneNum,privateKey,timezoneInt,
                                clientFirstName,clientLastName,clientPhoneNum,clientEmail,APNname,APNusername,
                                APNpassword,FTPaddress,FTPport,FTPusername,FTPpassword,FTPpath,TimeUploadHHInt,
                                TimeUploadMMInt,CreatedBy,CreatedAt,currentUsername,currentDateTime);

                        deleteDevice(id);
                        registerNewDevice(newDevice);

                    } catch (NumberFormatException e) {
                        showErrorMessage("Error: Some inputs only accept numerical value!");
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

    private void deleteDevice(int id){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.deleteDevice(id, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result.equals("SUCCESS")) {
                } else {
                    showErrorMessage("Server Error");
                }
            }
        });
    }

    private void cancel(){
        startActivity(new Intent(this, manageDeviceActivity.class));
    }


    private void getDeviceInfo (String serialNum){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.getDeviceInfo(serialNum, new getAllDeviceCallBack() {
            @Override
            public void done(ArrayList<Device> returnedDevices) {
                if (returnedDevices != null) {
                    setDeviceEditText(returnedDevices.get(0));
                } else {
                    showErrorMessage("Fail to load selected User data from database");
                }
            }
        });
    }

    private void setDeviceEditText(Device device){
        id = device.id;
        CreatedBy = device.createdBy;
        CreatedAt = device.createdAt;

        etSerialNum.setText(device.serialNum);
        etDevicePhoneNum.setText(device.devicePhoneNum);
        etPrivateKey.setText(device.privateKey);
        etTimezone.setText(Integer.toString(device.timezone));
        etClientFirstName.setText(device.clientFirstName);
        etClientLastname.setText(device.clientLastName);
        etClientPhoneNum.setText(device.clientPhoneNum);
        etClientEmail.setText(device.clientEmail);
        etAPNname.setText(device.APNname);
        etAPNusername.setText(device.APNusername);
        etAPNpassword.setText(device.APNpassword);
        etFTPaddress.setText(device.FTPaddress);
        etFTPport.setText(device.FTPport);
        etFTPusername.setText(device.FTPusername);
        etFTPpassword.setText(device.FTPpassword);
        etFTPpath.setText(device.FTPpath);
        etTimeUploadHH.setText(Integer.toString(device.TimeUploadHH));
        etTimeUploadMM.setText(Integer.toString(device.TimeUploadMM));
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(editDeviceActivity.this);
        dialogBuilder.setMessage(errorMsg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void showSuccessMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(editDeviceActivity.this);
        dialogBuilder.setMessage("SUCCESS: Saved Changes");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancel();
            }
        });
        dialogBuilder.show();
    }

    private String inputData;

    private void requestInfoDialog(String Msg) {

        LayoutInflater layoutInflater = LayoutInflater.from(editDeviceActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_request_info,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(editDeviceActivity.this);
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
