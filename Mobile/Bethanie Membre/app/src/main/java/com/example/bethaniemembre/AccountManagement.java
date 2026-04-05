package com.example.bethaniemembre;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

public class AccountManagement {
    Context co;
    SharedPreferences sP;
    public SharedPreferences.Editor editor;

    public static final String PREF_NAME = "User_login";
    public static final String LOGINB = "is_user_login";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ID = "id";

    public AccountManagement(Context co){
        this.co = co;
        sP = co.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sP.edit();
    }

    public boolean isAccountLogin(){
        return sP.getBoolean(LOGINB, false);
    }

    public void AccountSessionManage(String id,String login,
                                     String password){
        editor.putBoolean(LOGINB, true);
        editor.putString(ID,id);
        editor.putString(PASSWORD, password);
        editor.putString(LOGIN, login);
        editor.apply();

        Intent intent = new Intent(co, MainMenuActivity.class);
        intent.putExtra("login",login);
        co.startActivity(intent);
        ((MainActivity) co).finish();
    }

    public void AccountSessionManage2(String id,String login,
                                     String password){
        editor.putBoolean(LOGINB, true);
        editor.putString(ID,id);
        editor.putString(PASSWORD, password);
        editor.putString(LOGIN, login);
        editor.apply();

        Intent intent = new Intent(co, MainMenuActivity.class);
        intent.putExtra("login",login);
        co.startActivity(intent);
        ((InscriptionActivity) co).finish();
    }

    public void AccountSessionUpdate(String login, String password){
        editor.putString(PASSWORD, password);
        editor.putString(LOGIN, login);
        editor.apply();
    }

    public void AccountSessionManage(String id){
        editor.putBoolean(LOGINB, true);
        editor.putString(ID,id);
        editor.apply();

        Intent i = new Intent(co,MainMenuActivity.class);
        i.putExtra("id",id);
        ((MainActivity) co).startActivity(i);
    }

    public void checkLogin() {
        if (!this.isAccountLogin()){
            Intent intent = new Intent(co, MainActivity.class);
            co.startActivity(intent);
            ((MainMenuActivity) co).finish();
        }
    }

    public HashMap<String, String> userDetails(){
        HashMap<String, String> account = new HashMap<>();
        account.put(LOGIN,sP.getString(LOGIN, null));
        account.put(PASSWORD, sP.getString(PASSWORD, null));
        account.put(ID,sP.getString(ID,null));
        return account;
    }

    public void UpdateInfo(String id, String login, String password){
        editor.clear();
        editor.commit();

        editor.putBoolean(LOGINB, true);
        editor.putString(ID,id);
        editor.putString(PASSWORD, password);
        editor.putString(LOGIN, login);
        editor.apply();
    }

    public void logout(){
        editor.clear();
        editor.commit();

        Intent intent = new Intent(co, MainActivity.class);
        co.startActivity(intent);
        ((MainMenuActivity) co).finish();
    }

    public void deleteAccount(){
        editor.clear();
        editor.commit();

        Intent intent = new Intent(co, MainActivity.class);
        co.startActivity(intent);
        ((ConfirmDeleteAccountActivity) co).finish();
    }
}
