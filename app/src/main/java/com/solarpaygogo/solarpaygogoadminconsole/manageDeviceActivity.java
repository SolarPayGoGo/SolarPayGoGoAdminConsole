package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class manageDeviceActivity extends AppCompatActivity {
    userCache CurrentUserCache;
    user currentUser;

    Button btnRegister, btnBack, btnSearch;
    ListView lvDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);

        lvDevice = (ListView)findViewById(R.id.lvDevice);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnBack = (Button)findViewById(R.id.btnBack);

        CurrentUserCache = new userCache(this);
        currentUser = CurrentUserCache.getCurrentUser();

        btnSearch.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOption();
            }
        });

        btnRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewDevice();
            }
        });
        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        lvDevice.setOnItemClickListener (new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvSelectedItem = (TextView) view.findViewById(R.id.cellSerialNum);
                String selectedSerialNum = tvSelectedItem.getText().toString();

                if (selectedSerialNum.equals("Serial No") == false) {
                    getDeviceInfo(selectedSerialNum);
                }
            }
        });

        searchDevice("%27ALL%27","ALL");

    }

    private void getDeviceInfo (String serialNum){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.getDeviceInfo(serialNum, new getAllDeviceCallBack() {
            @Override
            public void done(ArrayList<Device> returnedDevices) {
                if (returnedDevices != null) {
                    showSelectedDevice(returnedDevices.get(0));
                } else {
                    showErrorMessage("Fail to load selected User data from database");
                }
            }
        });
    }

    private void showSelectedDevice(final Device selectedDevice){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageDeviceActivity.this);
        dialogBuilder.setMessage("Serial No : " + selectedDevice.serialNum + "\n" + "Device Phone No : " + selectedDevice.devicePhoneNum + "\n" +
                                "Private Key : " + selectedDevice.privateKey + "\n" + "Time Zone (GMT+/-) : " + selectedDevice.timezone + "\n" +
                                "Client First Name : " + selectedDevice.clientFirstName + "\n" + "Client Last Name : " + selectedDevice.clientLastName + "\n" +
                                "Client Phone No : " + selectedDevice.clientPhoneNum + "\n" + "Client Email : " + selectedDevice.clientEmail + "\n" +
                                "APN Name : " + selectedDevice.APNname + "\n" + "APN Username : " + selectedDevice.APNusername + "\n" +
                                "APN Password : " + selectedDevice.APNpassword + "\n" + "FTP address : " + selectedDevice.FTPaddress + "\n" +
                                "FTP Port : " + selectedDevice.FTPport + "\n" + "FTP Username : " + selectedDevice.FTPusername + "\n" +
                                "FTP Password : " + selectedDevice.FTPpassword + "\n" + "FTP Path : " + selectedDevice.FTPpath + "\n" +
                                "Time Upload HH:MM : " + selectedDevice.TimeUploadHH + ":" + selectedDevice.TimeUploadMM + "\n" +
                                "Created By : " + selectedDevice.createdBy + "\n" + "Created At : " + selectedDevice.createdAt + "\n" +
                                "Updated By : " + selectedDevice.updateBy + "\n" + "Updated By : " + selectedDevice.updateAt);
        dialogBuilder.setPositiveButton("Cancel", null);
        dialogBuilder.setNegativeButton("Device Maintenance", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                selectDeviceMaintenance(selectedDevice);
            }
        });
        dialogBuilder.setNeutralButton("Delete Device", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                confirmDeleteDevice(selectedDevice);
            }
        });
        dialogBuilder.show();
    }

    private void selectDeviceMaintenance(final Device selectedDevice){
        String title = "Select one of the following : ";
        CharSequence [] items = {"Edit Device Information", "Send Private Key",
                "Send APN, SMS notification & System setting ", "Send FTP setting", "Disable Device", "Restart Device", "Request Status From Device"};

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageDeviceActivity.this);
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case (0):
                        editDevice(selectedDevice);
                        break;
                    case (1):
                        sendPrivateKey(selectedDevice);
                        break;
                    case (2):
                        sendAPNSetting(selectedDevice);
                        break;
                    case (3):
                        sendFTPSetting(selectedDevice);
                        break;
                    case (4):
                        disableDevice(selectedDevice);
                        break;
                    case (5):
                        resetDevice(selectedDevice);
                        break;
                    case (6):
                        requestStatus(selectedDevice);
                        break;
                }
            }
        });
        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton("Cancel", null);
        AlertDialog optionDialog = dialogBuilder.create();
        optionDialog.show();

    }

    private void editDevice(final Device selectedDevice){
        Intent intent = new Intent(this, editDeviceActivity.class);
        intent.putExtra("selectedSerialNum", selectedDevice.serialNum);
        startActivity(intent);
    }

    private void sendPrivateKey(final Device selectedDevice){
        String devicePhoneNum = selectedDevice.devicePhoneNum;
        String msg = "*#" + selectedDevice.privateKey + "#";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(devicePhoneNum, null, msg, null, null);
        showErrorMessage("Private Key sent");
    }

    private void sendAPNSetting(final Device selectedDevice){
        String devicePhoneNum = selectedDevice.devicePhoneNum;
        String msg = "*#APN#" + selectedDevice.APNname + "," + selectedDevice.APNusername + "," + selectedDevice.APNpassword +
                ",0" + selectedDevice.clientPhoneNum + "," + selectedDevice.timezone + "#";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(devicePhoneNum,null,msg,null,null);
        showErrorMessage("APN, SMS Notification & System setting sent");
    }

    private void sendFTPSetting(final Device selectedDevice){
        String devicePhoneNum = selectedDevice.devicePhoneNum;
        String msg = "*#FTP#" + selectedDevice.FTPaddress + "," + selectedDevice.FTPport + "," + selectedDevice.FTPusername +
                "," + selectedDevice.FTPpassword + "," + selectedDevice.FTPpath + "#";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(devicePhoneNum,null,msg,null,null);
        showErrorMessage("FTP setting sent");
    }

    private void disableDevice(final Device selectedDevice){

        Date issueAt = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        String issueAtString = simpleDateFormat.format(issueAt);
        Transaction transaction = new Transaction(-1,selectedDevice.serialNum,"","0",currentUser.username,issueAtString,issueAtString);
        generateActivationCode(transaction, selectedDevice);
    }

    private void generateActivationCode(Transaction newTransaction, Device selectedDevice){
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

        String privateKeyString = selectedDevice.privateKey;

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

        addTransaction(newTransaction, selectedDevice);
    }

    private void addTransaction(final Transaction newTransaction, final Device selectedDevice){
        DB_transaction_request db_transaction_request = new DB_transaction_request(this);
        db_transaction_request.addTransaction(newTransaction, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result == "SUCCESS"){
                    sendActivationCode(newTransaction, selectedDevice);
                } else {
                    showErrorMessage("Error when adding Transaction to Database");
                }
            }
        });
    }

    private void sendActivationCode(Transaction transaction, Device selectedDevice){
        String msg = "*" + transaction.activationCode + "*";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(selectedDevice.devicePhoneNum,null,msg,null,null);
        showErrorMessage("Disable Device Activation code sent");
    }


    private void resetDevice(final Device selectedDevice){
        String devicePhoneNum = selectedDevice.devicePhoneNum;
        String msg = "*#RES#";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(devicePhoneNum,null,msg,null,null);
        showErrorMessage("Reset Command sent");
    }

    private void requestStatus(final Device selectedDevice){
        String devicePhoneNum = selectedDevice.devicePhoneNum;
        String msg = "*#STA#";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(devicePhoneNum,null,msg,null,null);
        showErrorMessage("Status Request Command sent");
    }

    private void confirmDeleteDevice(final Device selectedDevice){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageDeviceActivity.this);
        dialogBuilder.setMessage("Confirm Deleting - " + selectedDevice.serialNum);
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteDevice(selectedDevice.id);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showSelectedDevice(selectedDevice);
            }
        });
        dialogBuilder.show();
    }

    private void deleteDevice(int id){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.deleteDevice(id, new updateDBCallBack() {
            @Override
            public void done(String result) {
                if (result.equals("SUCCESS")) {
                    searchDevice("%27ALL%27", "ALL");
                } else {
                    showErrorMessage("Server Error");
                }
            }
        });
    }

    private void searchDevice(String searchIndex, String searchParam){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.searchDevices(searchIndex, searchParam, new getAllDeviceCallBack() {
            @Override
            public void done(ArrayList<Device> returnedDevices) {
                if (returnedDevices == null) {
                    showErrorMessage("No result");
                } else {
                    updateLVDevice(returnedDevices);
                }
            }
        });
    }

    private void updateLVDevice (ArrayList<Device> returnedDevices){

        ArrayList<HashMap<String,String>> displayList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> HeaderMap = new HashMap<String, String>();
        HeaderMap.put("serialNum","Serial No");
        HeaderMap.put("devicePhoneNum","DEV Phone No");
        HeaderMap.put("clientName","Client's Name");
        HeaderMap.put("clientPhoneNum","& Phone No");
        displayList.add(HeaderMap);

        for (int k = 0; k < returnedDevices.size(); k++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("serialNum",returnedDevices.get(k).serialNum);
            map.put("devicePhoneNum",returnedDevices.get(k).devicePhoneNum);
            map.put("clientName",(returnedDevices.get(k).clientFirstName + " " + returnedDevices.get(k).clientLastName));
            map.put("clientPhoneNum",returnedDevices.get(k).clientPhoneNum);
            displayList.add(map);
        }
        SimpleAdapter listAdapter = new SimpleAdapter(this, displayList, R.layout.row_device,
                new String[] {"serialNum","devicePhoneNum","clientName","clientPhoneNum"},
                new int [] {R.id.cellSerialNum,R.id.cellDevicePhoneNum,R.id.cellClientName,R.id.cellClientPhoneNum});
        lvDevice.setAdapter(listAdapter);
    }

    private void registerNewDevice() {
        startActivity(new Intent(this, registerDeviceActivity.class));
    }

    private void goBack(){
        startActivity(new Intent(this, menuActivity.class));
    }

    private void showErrorMessage(String errorMsg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageDeviceActivity.this);
        dialogBuilder.setMessage(errorMsg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private String searchParam;
    private String searchIndex;

    private void searchOption (){
        CharSequence [] items = {"Display All", "Serial Number", "Device Phone Number",
                "Client First Name", "Client Last Name", "Client Phone Number", "Client Email"};
        String title = "Search by";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageDeviceActivity.this);
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case (0): searchDevice("%27ALL%27","ALL");break;
                    case (1): searchIndex = "serialNum"; requestSearchParamDialog();break;
                    case (2): searchIndex = "devicePhoneNum"; requestSearchParamDialog();break;
                    case (3): searchIndex = "clientFirstName"; requestSearchParamDialog();break;
                    case (4): searchIndex = "clientLastName"; requestSearchParamDialog();break;
                    case (5): searchIndex = "clientPhoneNum"; requestSearchParamDialog();break;
                    case (6): searchIndex = "clientEmail"; requestSearchParamDialog();break;
                }
            }
        });
        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton("Cancel", null);
        AlertDialog optionDialog = dialogBuilder.create();
        optionDialog.show();
    }

    private void requestSearchParamDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(manageDeviceActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_request_info,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageDeviceActivity.this);
        dialogBuilder.setView(view);

        final TextView tvMsg = (TextView) view.findViewById(R.id.tvMsg);
        final EditText etDataInput = (EditText) view.findViewById(R.id.etInputData);

        tvMsg.setText("Please input Search Parameter");

        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchParam = etDataInput.getText().toString();
                searchDevice(searchIndex,searchParam);
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


}
