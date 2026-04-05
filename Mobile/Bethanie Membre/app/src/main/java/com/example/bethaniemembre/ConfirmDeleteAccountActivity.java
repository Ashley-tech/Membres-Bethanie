package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ConfirmDeleteAccountActivity extends AppCompatActivity implements View.OnClickListener {
    String mLogin, mid, resultat = "Succès";
    AccountManagement a;
    private Button o, n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_delete_account);

        a = new AccountManagement(this);

        HashMap<String,String> account = a.userDetails();

        mLogin = account.get(a.LOGIN);
        mid = account.get(a.ID);

        o = (Button) findViewById(R.id.buttonDeleteAccount);
        o.setOnClickListener(this);
        n = (Button) findViewById(R.id.buttonNoDeleteAccount);
        n.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonNoDeleteAccount:
                finish();
                break;
            default:
                AsyncDeleteAccount r = new AsyncDeleteAccount();
                r.execute();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private class AsyncDeleteAccount extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(ConfirmDeleteAccountActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Suppression de votre identifiant...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/supprimer_compte.php?compte=" + mid);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                resultat = "Exception";
                return "Exception";
            }

            try {
                co = (HttpURLConnection) url.openConnection();
                co.setReadTimeout(15000);
                co.setConnectTimeout(15000);
                co.setRequestMethod("GET");

                co.setDoInput(true);
                co.setDoOutput(true);

                Log.i("a", "a");
                Uri.Builder builder = new Uri.Builder();
                String query = builder.build().getEncodedQuery();

                Log.i("a", "b");
                OutputStream os = co.getOutputStream();
                Log.i("a", "c");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.flush();
                writer.close();
                os.close();
                co.connect();
            } catch (IOException e) {
                e.printStackTrace();
                resultat = "Exception";
                return "Exception";
            }

            try {
                int response_code = co.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = co.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return (result.toString());
                } else {
                    resultat = "Exception";
                    return ("Unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                resultat = "Exception";
                return "Exception";
            } finally {
                co.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            if (resultat.equals("Succès")) {
                AccountManagement a = new AccountManagement(ConfirmDeleteAccountActivity.this);
                a.deleteAccount();
                Toast.makeText(ConfirmDeleteAccountActivity.this, "Votre compte a été supprimé avec succès. Déconnexion.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ConfirmDeleteAccountActivity.this, "Impossible de supprimer votre compte.", Toast.LENGTH_LONG).show();
            }
        }
    }
}