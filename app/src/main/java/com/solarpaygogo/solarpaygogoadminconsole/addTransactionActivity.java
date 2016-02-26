package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class addTransactionActivity extends AppCompatActivity {

    userCache CurrentUserCache;
    user currentUser;

    String serialNum;

    Button btnSearchDevice, btnAddTransaction, btnCancel;
    EditText etSerialNum, etDays, etHrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        btnSearchDevice = (Button)findViewById(R.id.btnSearchDevice);
        btnAddTransaction = (Button)findViewById(R.id.btnAddTransaction);
        btnCancel = (Button)findViewById(R.id.btnCancel);

        etSerialNum = (EditText)findViewById(R.id.etSerialNum);
        etDays = (EditText)findViewById(R.id.etDurationDays);
        etHrs = (EditText)findViewById(R.id.etDurationHrs);

        serialNum = getIntent().getExtras().getString("serialNum");
        etSerialNum.setText(serialNum);

        CurrentUserCache = new userCache(this);
        currentUser = CurrentUserCache.getCurrentUser();

        etDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int tempHrs;
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
                String currentHour = simpleDateFormat.format(date);
                try {
                    if (s.toString().equals("") == false) {
                        tempHrs = Integer.parseInt(s.toString()) * 24 + 24 - Integer.parseInt(currentHour);
                        etHrs.setText(Integer.toString(tempHrs));
                    }
                } catch (NumberFormatException e){
                    showErrorMessage("Only accept Numerical value");
                }
            }
        });

        btnAddTransaction.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputSerialNum = etSerialNum.getText().toString();
                String inputHours = etHrs.getText().toString();
                try{
                    if (inputSerialNum.equals("")==false && inputHours.equals("")==false) {
                        Long.parseLong(inputSerialNum);
                        int inputHours_int = Integer.parseInt(inputHours);
                        if((inputHours_int <= 9999) && (inputHours_int >= 0)) {
                            Transaction transaction = new Transaction(-1, inputSerialNum, "", inputHours, currentUser.username, "", "");
                            checkSerialNumExist(inputSerialNum,transaction);
                        } else {
                            showErrorMessage("Number of Hours can not be larger than 9999 or smaller than 0");
                        }
                    } else {
                        showErrorMessage("Please do not leave blanks");
                    }
                } catch (NumberFormatException e){
                    showErrorMessage("Please only input Numerical Values");
                }

            }
        });

        btnSearchDevice.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            searchDevice();
            }
        });

        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            cancel();
            }
        });

    }

    private void confirmAddingTransaction(final Transaction newTranscation){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(addTransactionActivity.this);
        dialogBuilder.setMessage("Confirm adding Transaction? (This action is not reversible)");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addTransactionDates(newTranscation);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.show();
    }

    private void addTransactionDates (Transaction newTransaction) {
        Date issueAt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(issueAt);
        calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(newTransaction.numOfHours));
        Date expireAt = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        String issueAtString = simpleDateFormat.format(issueAt);
        String expireAtString = simpleDateFormat.format(expireAt);
        newTransaction.issueAt = issueAtString;
        newTransaction.expireAt = expireAtString;
        getDeviceInfo(newTransaction.serialNum, newTransaction);
    }

    private void generateActivationCode(Transaction newTransaction){
        int [] codeToEncrypt = new int[15];
        Arrays.fill(codeToEncrypt, 0);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm");
        String issueDate = simpleDateFormat.format(date);
        for (int i = 0; i < 10; i++){
            codeToEncrypt [i] = issueDate.charAt(i) - '0';
        }

        String numOfHourString = newTransaction.numOfHours;
        for (int i = 13; i > (13 - numOfHourString.length()) ; i--){
            codeToEncrypt [i] = numOfHourString.charAt((numOfHourString.length()-1)- (13-i)) - '0';
        }

        int checksum_BeforeEncryption = 0;
        for (int i = 0; i < 14; i++){
            checksum_BeforeEncryption += codeToEncrypt [i];
        }
        checksum_BeforeEncryption = checksum_BeforeEncryption % 10;
        codeToEncrypt [14] = checksum_BeforeEncryption;

        String serialNumString = newTransaction.serialNum;
        int [] serialNumber = new int[15];
        for (int i = 0; i < 15; i++){
            serialNumber [i] = serialNumString.charAt(i) - '0';
        }

        int [][] encryptionTable = new int[10][10];
        for (int j = 0; j < 10; j++){
            for (int i = 0; i < 10; i++) {
                encryptionTable[i][j] = privateKeyString.charAt(i + (j*10)) - '0';
            }
        }

        int [] activationCode = new int[16];
        Arrays.fill(activationCode, 0);
        int checksum_AfterEncryption = 0;
        for (int i = 0; i < 15; i++){
            int EncryptedDigit = 0;
            for (int x = 0; x <10 ; x++){
                if (encryptionTable[x][serialNumber[i]]==codeToEncrypt[i]){
                    EncryptedDigit = x;
                }
            }
            activationCode [i] = EncryptedDigit;
            checksum_AfterEncryption += EncryptedDigit;
        }
        checksum_AfterEncryption = checksum_AfterEncryption%10;
        activationCode [15] = checksum_AfterEncryption;

        String activationCodeString = "";
        for (int k = 0; k<16; k++){
            activationCodeString += Integer.toString(activationCode[k]);
        }
        newTransaction.activationCode = activationCodeString;

        addTransaction(newTransaction);
    }

    private void addTransaction(final Transaction newTransaction){
        DB_transaction_request db_transaction_request = new DB_transaction_request(this);
        db_transaction_request.addTransaction(newTransaction, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result == "SUCCESS"){
                    sendActivationCode(newTransaction);
                } else {
                    showErrorMessage("Error when adding Transaction to Database");
                }
            }
        });
    }

    private void sendActivationCode(Transaction transaction){
        String msg = "*" + transaction.activationCode + "*";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(devicePhoneNum,null,msg,null,null);
        showSuccessMessage();
    }

    String privateKeyString;
    String devicePhoneNum;

    private void getDeviceInfo (String serialNum, final Transaction newTransaction){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.getDeviceInfo(serialNum, new getAllDeviceCallBack() {
            @Override
            public void done(ArrayList<Device> returnedDevices) {
                if (returnedDevices != null) {
                    privateKeyString = returnedDevices.get(0).privateKey;
                    devicePhoneNum = returnedDevices.get(0).devicePhoneNum;
                    generateActivationCode(newTransaction);
                } else {
                    showErrorMessage("Fail to load selected User data from database");
                }
            }
        });
    }

    private void checkSerialNumExist (String serialNum, final Transaction transaction){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.getDeviceInfo(serialNum, new getAllDeviceCallBack() {
            @Override
            public void done(ArrayList<Device> returnedDevices) {
                if (returnedDevices != null) {
                    confirmAddingTransaction(transaction);
                } else {
                    showErrorMessage("Serial Number not exist in dataBase");
                }
            }
        });
    }

    private void showSuccessMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(addTransactionActivity.this);
        dialogBuilder.setMessage("SUCCESS: Added new transaction and activation code sent to device");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancel();
            }
        });
        dialogBuilder.show();
    }

    private void showErrorMessage(String errorMsg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(addTransactionActivity.this);
        dialogBuilder.setMessage(errorMsg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void searchDevice(){
        startActivity(new Intent(this,searchDeviceActivity.class));
    }

    private void cancel(){
        startActivity(new Intent(this,manageTransactionActivity.class));
    }
}
