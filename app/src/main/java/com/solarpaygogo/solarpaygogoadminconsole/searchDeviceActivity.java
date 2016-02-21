package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class searchDeviceActivity extends AppCompatActivity {

    Button btnSearchBy, btnBack;
    ListView lvDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        btnSearchBy = (Button)findViewById(R.id.btnSearch);
        btnBack = (Button)findViewById(R.id.btnBack);
        lvDevice = (ListView)findViewById(R.id.lvDevice);

        btnSearchBy.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOption();
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

    private String searchParam;
    private String searchIndex;

    private void requestSearchParamDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(searchDeviceActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_request_info,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(searchDeviceActivity.this);
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

    private void searchOption (){
        CharSequence [] items = {"Display All", "Serial Number", "Device Phone Number",
                "Client First Name", "Client Last Name", "Client Phone Number", "Client Email"};
        String title = "Search by";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(searchDeviceActivity.this);
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case (0):
                        searchDevice("%27ALL%27", "ALL");
                        break;
                    case (1):
                        searchIndex = "serialNum";
                        requestSearchParamDialog();
                        break;
                    case (2):
                        searchIndex = "devicePhoneNum";
                        requestSearchParamDialog();
                        break;
                    case (3):
                        searchIndex = "clientFirstName";
                        requestSearchParamDialog();
                        break;
                    case (4):
                        searchIndex = "clientLastName";
                        requestSearchParamDialog();
                        break;
                    case (5):
                        searchIndex = "clientPhoneNum";
                        requestSearchParamDialog();
                        break;
                    case (6):
                        searchIndex = "clientEmail";
                        requestSearchParamDialog();
                        break;
                }
            }
        });
        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton("Cancel", null);
        AlertDialog optionDialog = dialogBuilder.create();
        optionDialog.show();
    }

    private void goBack(){
        Intent intent = new Intent(this,addTransactionActivity.class);
        intent.putExtra("serialNum","");
        startActivity(intent);
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(searchDeviceActivity.this);
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
        dialogBuilder.setNegativeButton("Select this device", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                returnThisDevice(selectedDevice);
            }
        });
        dialogBuilder.show();
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

    private void showErrorMessage(String errorMsg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(searchDeviceActivity.this);
        dialogBuilder.setMessage(errorMsg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void returnThisDevice(Device selectedDevice){
        Intent intent = new Intent(this,addTransactionActivity.class);
        intent.putExtra("serialNum", selectedDevice.serialNum);
        startActivity(intent);
    }
}
