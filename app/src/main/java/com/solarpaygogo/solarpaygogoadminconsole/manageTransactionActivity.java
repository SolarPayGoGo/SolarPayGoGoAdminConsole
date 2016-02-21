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

import java.util.ArrayList;
import java.util.HashMap;

public class manageTransactionActivity extends AppCompatActivity {

    Button btnAddTransaction, btnSearch, btnBack;
    ListView lvTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transaction);

        btnAddTransaction = (Button)findViewById(R.id.btnAddTransaction);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnBack = (Button)findViewById(R.id.btnBack);

        lvTransaction = (ListView)findViewById(R.id.lvTransaction);

        btnAddTransaction.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaction();
            }
        });

        btnSearch.setOnClickListener(new Button.OnClickListener() {
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

        lvTransaction.setOnItemClickListener (new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvSelectedItem = (TextView) view.findViewById(R.id.cellActivationCode);
                String selectedAC = tvSelectedItem.getText().toString();

                if (selectedAC.equals("AC") == false) {
                    getTransactionInfo(selectedAC);
                }
            }
        });
        try{
            String searchSerialNum = getIntent().getExtras().getString("serialNum");
            searchTransaction("serialNum", searchSerialNum);
        }catch (Exception e){
            searchTransaction("%27ALL%27", "ALL");
        }


    }

    private void getTransactionInfo (String selectedAC){
        DB_transaction_request db_transaction_request = new DB_transaction_request(this);
        db_transaction_request.getTransactionInfo(selectedAC, new getAllTransactionCallBack() {
            @Override
            public void done(ArrayList<Transaction> returnedTransaction) {
                if (returnedTransaction != null) {
                    showSelectedTransaction(returnedTransaction.get(0));
                } else {
                    showErrorMessage("Fail to load selected User data from database");
                }
            }
        });
    }

    private void showSelectedTransaction(final Transaction selectedTransaction){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageTransactionActivity.this);
        dialogBuilder.setMessage("Serial No : " + selectedTransaction.serialNum + "\n" + "Activation Code : " + selectedTransaction.activationCode + "\n" +
                "Number of Hours : " + selectedTransaction.numOfHours + "\n" + "Issue By : " + selectedTransaction.issueBy + "\n" +
                "Issue At : " + selectedTransaction.issueAt + "\n" + "Expire At : " + selectedTransaction.expireAt);
        dialogBuilder.setPositiveButton("Back", null);
        dialogBuilder.setNegativeButton("Send Activation Code", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getDeviceInfo(selectedTransaction);
            }
        });
        dialogBuilder.show();
    }

    private void sendActivationCode(String devicePhoneNum, Transaction transaction){
        String msg = "*" + transaction.activationCode + "*";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(devicePhoneNum, null, msg, null, null);
        showErrorMessage("ActivationCode Sent");
    }

    private void getDeviceInfo (final Transaction selectedTransaction){
        DB_device_request db_device_request = new DB_device_request(this);
        db_device_request.getDeviceInfo(selectedTransaction.serialNum, new getAllDeviceCallBack() {
            @Override
            public void done(ArrayList<Device> returnedDevices) {
                if (returnedDevices != null) {
                    String devicePhoneNum = returnedDevices.get(0).devicePhoneNum;
                    sendActivationCode(devicePhoneNum,selectedTransaction);
                } else {
                    showErrorMessage("Fail to load selected User data from database");
                }
            }
        });
    }

    private void addTransaction(){
        Intent intent = new Intent(this,addTransactionActivity.class);
        intent.putExtra("serialNum","");
        startActivity(intent);
    }

    private String searchParam;
    private String searchIndex;

    private void searchOption (){
        CharSequence [] items = {"Display All", "Serial Number", "Issue By", "Issue At", "ExpireAt"};
        String title = "Search by";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageTransactionActivity.this);
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case (0):
                        searchTransaction("%27ALL%27", "ALL");
                        break;
                    case (1):
                        searchIndex = "serialNum";
                        requestSearchParamDialog();
                        break;
                    case (2):
                        searchIndex = "issueBy";
                        requestSearchParamDialog();
                        break;
                    case (3):
                        searchIndex = "issueAt";
                        requestSearchParamDialog();
                        break;
                    case (4):
                        searchIndex = "ExpireAt";
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

    private void requestSearchParamDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(manageTransactionActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_request_info,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageTransactionActivity.this);
        dialogBuilder.setView(view);

        final TextView tvMsg = (TextView) view.findViewById(R.id.tvMsg);
        final EditText etDataInput = (EditText) view.findViewById(R.id.etInputData);

        if (searchIndex.equals("issueAt")==true || searchIndex.equals("expireAt")==true){

            tvMsg.setText("Please input Search Parameter (Date format: YYYY-MM-DD)");

        }else {
            tvMsg.setText("Please input Search Parameter ");
        }

        if (searchIndex.equals("serialNum")){
            dialogBuilder.setNegativeButton("Search Serial Number", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    searchSerialNum();
                }
            });
        }

        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchParam = etDataInput.getText().toString();
                if (searchIndex.equals("issueAt") == true || searchIndex.equals("expireAt") == true) {
                } else {
                    searchParam = "%27" + searchParam + "%27";
                }

                searchTransaction(searchIndex, searchParam);

                dialog.cancel();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void searchSerialNum(){
        startActivity(new Intent(this,searchSerialNumActivity.class));
    }

    private void searchTransaction(String searchIndex, String searchParam){
        DB_transaction_request db_transaction_request = new DB_transaction_request(this);
        db_transaction_request.searchTransaction(searchIndex, searchParam, new getAllTransactionCallBack() {
            @Override
            public void done(ArrayList<Transaction> returnedTransactions) {
                if (returnedTransactions == null) {
                    showErrorMessage("No result");
                } else {
                    updateLVTransaction(returnedTransactions);
                }
            }
        });
    }

    private void updateLVTransaction (ArrayList<Transaction> returnedTransactions){

        ArrayList<HashMap<String,String>> displayList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> HeaderMap = new HashMap<String, String>();
        HeaderMap.put("serialNum","Serial No");
        HeaderMap.put("activationCode","AC");
        HeaderMap.put("issueBy","Issue By");
        HeaderMap.put("issueAt","Issue At");
        HeaderMap.put("expireAt","Expire At");
        displayList.add(HeaderMap);

        for (int k = 0; k < returnedTransactions.size(); k++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("serialNum",returnedTransactions.get(k).serialNum);
            map.put("activationCode",returnedTransactions.get(k).activationCode);
            map.put("issueBy",returnedTransactions.get(k).issueBy);
            map.put("issueAt",returnedTransactions.get(k).issueAt);
            map.put("expireAt",returnedTransactions.get(k).expireAt);
            displayList.add(map);
        }
        SimpleAdapter listAdapter = new SimpleAdapter(this, displayList, R.layout.row_transaction,
                new String[] {"serialNum","activationCode","issueBy","issueAt","expireAt"},
                new int [] {R.id.cellSerialNum,R.id.cellActivationCode,R.id.cellIssueBy,R.id.cellIssueAt, R.id.cellExpireAt});
        lvTransaction.setAdapter(listAdapter);
    }

    private void showErrorMessage(String errorMsg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(manageTransactionActivity.this);
        dialogBuilder.setMessage(errorMsg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void goBack(){
        startActivity(new Intent(this,menuActivity.class));
    }
}
