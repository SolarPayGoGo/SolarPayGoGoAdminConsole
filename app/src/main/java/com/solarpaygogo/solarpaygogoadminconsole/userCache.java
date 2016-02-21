package com.solarpaygogo.solarpaygogoadminconsole;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Terry on 21/1/16.
 */
public class userCache {
    SharedPreferences currentUserCache;

    public userCache(Context context){
        currentUserCache = context.getSharedPreferences("currentUser",0);
    }

    public void setCurrentUser(user currentUser){
        SharedPreferences.Editor editor = currentUserCache.edit();
        editor.putInt("id", currentUser.id);
        editor.putString("username", currentUser.username);
        editor.putString("password", currentUser.password);
        editor.putString("firstName", currentUser.firstName);
        editor.putString("lastName", currentUser.lastName);
        editor.putString("email", currentUser.email);
        editor.putInt("root", currentUser.root);
        editor.commit();
    }

    public user getCurrentUser (){
        int id = currentUserCache.getInt("id", -1);
        String username = currentUserCache.getString("username", "");
        String password = currentUserCache.getString("password", "");
        String firstName = currentUserCache.getString("firstName", "");
        String lastName = currentUserCache.getString("lastName","");
        String email = currentUserCache.getString("email","");
        int root = currentUserCache.getInt("root", 0);
        user CurrentUser = new user(id,username, password, firstName, lastName,email,root);
        return CurrentUser;
    }

    public void logoutCurrentUser (){
        SharedPreferences.Editor editor = currentUserCache.edit();
        editor.clear();
        editor.commit();
    }


}
