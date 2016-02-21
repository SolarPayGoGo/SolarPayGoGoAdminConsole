package com.solarpaygogo.solarpaygogoadminconsole;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Terry on 20/1/16.
 */


public class DB_user_request {
    ProgressDialog progressDialog;
    public static final String SERVER_ADDRESS = "http://solarpaygogo.com/php/user/";

    public DB_user_request(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
    }

    public void changeUserPassword (int id, String Password, updateDBCallBack dbCallBack){
        progressDialog.show();
        new changeUserPasswordTask(id, Password,dbCallBack).execute();
    }

    public void getUserdata(user User, getUserCallBack userCallBack){
        progressDialog.show();
        new getUserDataTask(User,userCallBack).execute();
    }

    public void registerNewUser(user User, updateDBCallBack dbCallBack){
        progressDialog.show();
        new registerNewUserTask(User,dbCallBack).execute();
    }

    public void getAllUserData(getAllUserCallBack allUserCallBack){
        progressDialog.show();
        new getAllUserTask(allUserCallBack).execute();
    }

    public void getOneUserData(String username, getUserCallBack userCallBack){
        progressDialog.show();
        new getOneUserDataTask(username,userCallBack).execute();
    }

    public void deleteUser (int id, updateDBCallBack dbCallBack){
        progressDialog.show();
        new deleteUserTask(id,dbCallBack).execute();
    }

    public class changeUserPasswordTask extends AsyncTask<Void,Void,String>{
        int id;
        String password;
        updateDBCallBack dbCallBack;

        public changeUserPasswordTask (int ID, String Password, updateDBCallBack DBCallBack){
            this.id= ID;
            this.password = Password;
            this.dbCallBack= DBCallBack;
        }

        @Override
        protected String doInBackground(Void... params){
            String responds = "";
            try {
                String link = SERVER_ADDRESS + "changePassword.php?id=%27" + id + "%27&password=%27" + password + "%27";
                Log.d("SQL",link);
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    responds += line;
                }
                httpURLConnection.disconnect();
                Log.d("SQL",responds);

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            if (responds.equals("SUCCESS")== true ) {
                return "SUCCESS";
            } else {
                return "FAIL";
            }
        }
        @Override
        protected void onPostExecute (String returnedResult){
            super.onPostExecute(returnedResult);
            progressDialog.dismiss();
            dbCallBack.done(returnedResult);
        }
    }

    public class getUserDataTask extends AsyncTask<Void,Void,user> {
        user User;
        getUserCallBack userCallBack;

        public getUserDataTask (user User, getUserCallBack UserCallBack){
            this.User = User;
            this.userCallBack = UserCallBack;
        }
        @Override
        protected user doInBackground(Void... params) {
            String responds = "";
            try {
                String link = SERVER_ADDRESS + "login.php?username=%27" + User.username + "%27&password=%27" + User.password + "%27";
                Log.d("SQL",link);
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

                httpURLConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    responds += line;
                }
                httpURLConnection.disconnect();
                Log.d("SQL",responds);

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            String [] data = responds.split("<br>");
            if (data[0].equals("SUCCESS")== true ) {
                user User = new user(Integer.parseInt(data[1]), data[2], data[3], data[4], data[5], data[6], Integer.parseInt(data[7]));
                return User;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute (user returnedUser){
            super.onPostExecute(returnedUser);
            progressDialog.dismiss();
            userCallBack.done(returnedUser);
        }

    }

    public class registerNewUserTask extends AsyncTask<Void,Void,String>{
        user newUser;
        updateDBCallBack dbCallBack;

        public registerNewUserTask (user User, updateDBCallBack DBCallBack){
            this.newUser = User;
            this.dbCallBack= DBCallBack;
        }

        @Override
        protected String doInBackground(Void... params){
            String responds = "";
            try {
                String link = SERVER_ADDRESS + "registerUser.php?username=%27" + newUser.username + "%27&password=%27" + newUser.password + "%27&firstName=%27" + newUser.firstName + "%27&lastName=%27" + newUser.lastName + "%27&email=%27" +newUser.email + "%27&root=%27" + newUser.root + "%27";
                Log.d("SQL",link);
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    responds += line;
                }
                httpURLConnection.disconnect();
                Log.d("SQL",responds);

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            if (responds.equals("SUCCESS")== true ) {
                return "SUCCESS";
            } else {
                return "Failed : " + responds;
            }
        }
        @Override
        protected void onPostExecute (String returnedResult){
            super.onPostExecute(returnedResult);
            progressDialog.dismiss();
            dbCallBack.done(returnedResult);
        }
    }

    public class getAllUserTask extends AsyncTask<Void,Void,ArrayList<user>> {
        getAllUserCallBack allUserCallBack;

        public getAllUserTask (getAllUserCallBack allUserCallBack){
            this.allUserCallBack = allUserCallBack;
        }
        @Override
        protected ArrayList<user> doInBackground(Void... params) {
            String responds = "";
            try {
                String link = SERVER_ADDRESS + "showAllUser.php";
                Log.d("SQL",link);
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

                httpURLConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    responds += line;
                }
                httpURLConnection.disconnect();
                Log.d("SQL",responds);

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            ArrayList<user> allUser = new ArrayList<user>();
            String [] rowData = responds.split("<br>");
            if (rowData[0].equals("SUCCESS")== true ) {
                for (int k = 1; k < rowData.length; k++){
                    String [] columnData = rowData[k].split("&nbsp");
                    user User = new user(Integer.parseInt(columnData[0]),columnData[1],columnData[2],columnData[3],columnData[4],columnData[5],Integer.parseInt(columnData[6]));
                    allUser.add(User);
                }
                return allUser;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute (ArrayList<user> returnedUsers){
            super.onPostExecute(returnedUsers);
            progressDialog.dismiss();
            allUserCallBack.done(returnedUsers);
        }

    }

    public class getOneUserDataTask extends AsyncTask<Void,Void,user> {
        String username;
        getUserCallBack userCallBack;

        public getOneUserDataTask (String username, getUserCallBack UserCallBack){
            this.username = username;
            this.userCallBack = UserCallBack;
        }
        @Override
        protected user doInBackground(Void... params) {
            String responds = "";
            try {
                String link = SERVER_ADDRESS + "showOneUser.php?username=%27" + username + "%27";
                Log.d("SQL",link);
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

                httpURLConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    responds += line;
                }
                httpURLConnection.disconnect();
                Log.d("SQL",responds);

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            String [] data = responds.split("<br>");
            if (data[0].equals("SUCCESS")== true ) {
                user User = new user(Integer.parseInt(data[1]), data[2], data[3], data[4], data[5], data[6], Integer.parseInt(data[7]));
                return User;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute (user returnedUser){
            super.onPostExecute(returnedUser);
            progressDialog.dismiss();
            userCallBack.done(returnedUser);
        }

    }

    public class deleteUserTask extends AsyncTask<Void,Void,String>{
        int id;
        updateDBCallBack dbCallBack;

        public deleteUserTask (int ID, updateDBCallBack DBCallBack){
            this.id= ID;
            this.dbCallBack= DBCallBack;
        }

        @Override
        protected String doInBackground(Void... params){
            String responds = "";
            try {
                String link = SERVER_ADDRESS + "deleteUser.php?id=%27" + id + "%27";
                Log.d("SQL",link);
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    responds += line;
                }
                httpURLConnection.disconnect();
                Log.d("SQL",responds);

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            if (responds.equals("SUCCESS")== true ) {
                return "SUCCESS";
            } else {
                return "FAIL";
            }
        }
        @Override
        protected void onPostExecute (String returnedResult){
            super.onPostExecute(returnedResult);
            progressDialog.dismiss();
            dbCallBack.done(returnedResult);
        }
    }

}
