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
import android.widget.Spinner;
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

public class InscriptionActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner profil;
    String identifiant="";
    private Button inscrire, retour,user;
    private EditText login, pwd, pwd2;
    String t = "";
    private Object MainActivity;
    Context c = (Context) MainActivity;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        profil = (Spinner) findViewById(R.id.Profil_Type);
        login = (EditText) findViewById(R.id.editTextLogin);
        pwd = (EditText) findViewById(R.id.editTextPassword);
        pwd2= (EditText) findViewById(R.id.editTextPassword2);
        inscrire = (Button) findViewById(R.id.buttonInscription);
        retour = (Button) findViewById(R.id.buttonretouri);
        inscrire.setOnClickListener(this);
        retour.setOnClickListener(this);
        user = (Button) findViewById(R.id.buttonUserList);
        user.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonInscription:
                //((MainActivity) c).finish();
                t = profil.getSelectedItem().toString();
                if (t.equals("Profil* :")){
                    t = "";
                }
                Log.i("profil",t);
                AsyncAddAccount a = new AsyncAddAccount();
                a.execute(login.getText().toString(),pwd.getText().toString(),pwd2.getText().toString(),t);
                break;
            case R.id.buttonUserList:
                Intent i = new Intent(this, UsersListActivity.class);
                startActivity(i);
                break;
            default:
                finish();
                i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private class AsyncAddAccount extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(InscriptionActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Ajout du compte...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/ajouter_compte.php");
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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("login",strings[0]).appendQueryParameter("pwd",strings[1]).appendQueryParameter("pwdr",strings[2]).appendQueryParameter("profil",strings[3]);
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
            if (result.equals("Succès")){
                Toast.makeText(InscriptionActivity.this, "Votre inscription a bien été efféctué. Connexion...", Toast.LENGTH_LONG).show();
                AsyncGetId af = new AsyncGetId();
                af.execute();
            } else if (result.equals("Mots de passes différents")) {
                Toast.makeText(InscriptionActivity.this, "Les mots de passes saisis sont différents", Toast.LENGTH_LONG).show();
            } else if (result.equals("Compte existant")){
                Toast.makeText(InscriptionActivity.this,"Le login saisi correspond déjà à un compte. Veuillez saisir un autre login.",Toast.LENGTH_LONG).show();
            } else if (result.equals("Champ vide")){
                Toast.makeText(InscriptionActivity.this,"Vous devez impérativement remplir tous les champs",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(InscriptionActivity.this,"Impossible de vous inscrire pour le moment", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncGetId extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(InscriptionActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Génération de votre identifiant...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/get_id.php?pseudo=" + login.getText().toString() + "&pwd=" + pwd.getText().toString());
            } catch (MalformedURLException e) {
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
                    return ("Unsuccessful");
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
            if (result.equals("0") ||result.equals("Unsuccessful") ||result.equals("Exception")){
                Toast.makeText(InscriptionActivity.this,"Nous n'avons pas réussi à récupérer votre identifiant", Toast.LENGTH_LONG).show();
            } else {
                identifiant = result;
                Log.d("identifiant",identifiant);
                AccountManagement a = new AccountManagement(InscriptionActivity.this);
                a.AccountSessionManage2(identifiant,login.getText().toString(),pwd.getText().toString());
                Toast.makeText(InscriptionActivity.this, "Votre identifiant généré est "+identifiant, Toast.LENGTH_LONG).show();
            }
        }
    }
}