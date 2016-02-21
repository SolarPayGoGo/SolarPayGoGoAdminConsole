package com.solarpaygogo.solarpaygogoadminconsole;

/**
 * Created by Terry on 20/1/16.
 */
public class user {
    public int id, root;
    public String username, password, firstName, lastName, email;

    public user (int Id, String Username, String Password, String FirstName, String LastName, String Email, int Root){
        this.id = Id;
        this.username = Username;
        this.password = Password;
        this.firstName = FirstName;
        this.lastName = LastName;
        this.email = Email;
        this.root = Root;
    }

    public user (String Username, String Password){
        this(-1,Username,Password,"","","",0);
    }



}
