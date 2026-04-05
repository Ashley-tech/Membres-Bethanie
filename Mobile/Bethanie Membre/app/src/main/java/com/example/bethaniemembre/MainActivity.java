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

import static com.example.bethaniemembre.PasswordForgotte.READ_TIMEOUT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;
    boolean correct = false;
    Button mdp_oublie, inscription, connecter, check;
    String l="",m="";
    String identifiant = "";
    private EditText login, pwd;
    AccountManagement a = null;
    Object MainActivity;
    Context c = (Context) MainActivity;

    Intent i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mdp_oublie = (Button) findViewById(R.id.buttonmdp);
        mdp_oublie.setOnClickListener(this);
        inscription = (Button)findViewById(R.id.buttonI);
        inscription.setOnClickListener(this);
        connecter =(Button)findViewById(R.id.buttonC);
        connecter.setOnClickListener(this);

        login = (EditText) findViewById(R.id.Login);
        pwd = (EditText) findViewById(R.id.password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonmdp:
                i = new Intent(this, PasswordForgotte.class);
                startActivity(i);
                break;
            case R.id.buttonI:
                i = new Intent(this, InscriptionActivity.class);
                startActivity(i);
                finish();
                break;
            default:
                AsyncConnect a = new AsyncConnect();
                a.execute(login.getText().toString(),pwd.getText().toString());
                break;
        }
    }

    private class AsyncGetId extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Récupération de votre identifiant...");
            pdLoading.setCancelable(true);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/get_id.php?pseudo="+login.getText().toString()+"&pwd="+pwd.getText().toString());
            } catch (MalformedURLException e){
                e.printStackTrace();
                return "Exception";
            }

            try {
                co = (HttpURLConnection) url.openConnection();
                co.setReadTimeout(READ_TIMEOUT);
                co.setConnectTimeout(CONNECTION_TIMEOUT);
                co.setRequestMethod("GET");

                co.setDoInput(true);
                co.setDoOutput(true);

                Log.i("a","a");
                Uri.Builder builder = new Uri.Builder();
                String query = builder.build().getEncodedQuery();

                Log.i("a","b");
                OutputStream os = co.getOutputStream();
                Log.i("a","c");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
            if (result.equals("0") || result.equals("Exception") || result.equals("Unsuccessful")){
                Toast.makeText(MainActivity.this,"Nous n'avons pas réussi à récupérer votre identifiant", Toast.LENGTH_LONG).show();
            } else {
                identifiant = result;
                Log.d("identifiant",identifiant);
                a = new AccountManagement(MainActivity.this);
                a.AccountSessionManage(identifiant, login.getText().toString(),pwd.getText().toString());
                Toast.makeText(MainActivity.this, "Votre identifiant généré est "+identifiant, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncConnect extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Vérification de vos informations...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/connexion.php");
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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("login",strings[0]).appendQueryParameter("pwd",strings[1]);
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
            boolean t = (result.contains("Succès"));
            Log.i("tag","(result = Succès) : " + t);
            if (result.contains("Succès")){
                Toast.makeText(MainActivity.this, "Les informations saisies correspondent à un compte. Récupération de votre identifiant...", Toast.LENGTH_LONG).show();
                l = login.getText().toString();
                m = pwd.getText().toString();
                connecter.setClickable(true);
                AsyncGetId ag = new AsyncGetId();
                ag.execute();
            } else if (result.contains("Compte supprimé")) {
                Toast.makeText(MainActivity.this, "Ce compte a été supprimé", Toast.LENGTH_LONG).show();
            } else if (result.contains("Saisie incorrecte")){
                Toast.makeText(MainActivity.this,"Les informations saisies ne correspondent à aucun compte",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this,"Connexion impossible", Toast.LENGTH_LONG).show();
            }
        }
    }
}