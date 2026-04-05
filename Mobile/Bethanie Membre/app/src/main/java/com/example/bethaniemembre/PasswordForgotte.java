package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class PasswordForgotte extends AppCompatActivity implements View.OnClickListener {

    public static final int CONNECTION_TIMEOUT=15000;
    public static final int READ_TIMEOUT=15000;
    private Button retour, verifier;
    private EditText login;
    Intent i = null;
    String l = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forgotte);

        retour = (Button) findViewById(R.id.buttonretourm);
        retour.setOnClickListener(this);
        verifier = (Button) findViewById(R.id.buttonVerifLogin);
        verifier.setOnClickListener(this);
        login = (EditText) findViewById(R.id.editTextL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonVerifLogin:
                l = login.getText().toString();
                i = new Intent(this, NewPasswordActivity.class);
                i.putExtra("login", login.getText().toString());
                AsyncCheckLogin a = new AsyncCheckLogin();
                a.execute(l);
                break;
            default:
                finish();
                break;
        }
    }

    private class AsyncCheckLogin extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(PasswordForgotte.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Vérification du login...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/verif_login.php");
            } catch (MalformedURLException e){
                e.printStackTrace();
                return "Exception";
            }

            try {
                co = (HttpURLConnection) url.openConnection();
                co.setReadTimeout(READ_TIMEOUT);
                co.setConnectTimeout(CONNECTION_TIMEOUT);
                co.setRequestMethod("POST");

                co.setDoInput(true);
                co.setDoOutput(true);

                Log.i("a","a");
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("login",strings[0]);
                String query = builder.build().getEncodedQuery();

                Log.i("a","b");
                OutputStream os = co.getOutputStream();
                Log.i("a","c");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                co.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception";
            }

            try{
                int response_code = co.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = co.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        result.append(line);
                    }

                    return (result.toString());
                } else {
                    return("Unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception";
            } finally {
                co.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            Log.d("message",result);
            if (result.equals("Exception")  || result.equals("Unsuccessful")){
                Toast.makeText(PasswordForgotte.this,"Impossible de détecter le login.", Toast.LENGTH_LONG).show();
            } else if (result.equals("Login non trouvé")) {
                Toast.makeText(PasswordForgotte.this, "Le login que vous avez saisi est introuvable.", Toast.LENGTH_LONG).show();
            } else if (result.equals("Champ vide")){
                Toast.makeText(PasswordForgotte.this,"Vous n'avez pas renseigné le login.",Toast.LENGTH_LONG).show();
            } else {
                i.putExtra("id",result);
                startActivity(i);
                PasswordForgotte.this.finish();
            }
        }
    }
}