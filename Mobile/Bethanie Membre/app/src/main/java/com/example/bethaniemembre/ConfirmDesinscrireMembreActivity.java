package com.example.bethaniemembre;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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

import static android.icu.lang.UCharacter.toUpperCase;

public class ConfirmDesinscrireMembreActivity extends AppCompatActivity implements View.OnClickListener{
    TextView intro;
    Button oui,non;
    AccountManagement a;
    String id = "0",nom="",prenom="",sexe="",mid="0",resultat="Succès";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_desinscrire_membre);

        intro = (TextView) findViewById(R.id.desinscrireMembre_intro);

        oui = (Button) findViewById(R.id.buttonDesinscrire);
        oui.setOnClickListener(this);
        non = (Button) findViewById(R.id.buttonDesinscrireNo);
        non.setOnClickListener(this);

        a = new AccountManagement(this);
        HashMap<String,String> account = a.userDetails();
        mid = account.get(a.ID);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            id = extras.getString("id");
            nom=extras.getString("nom");
            sexe = extras.getString("sexe");
            prenom = extras.getString("prenom");
            intro.setText("Voulez-vous vraiment désinscrire le membre "+prenom+" "+toUpperCase(nom)+" ?");
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonDesinscrire:
                AsyncDesinscrireMembre ad = new AsyncDesinscrireMembre();
                ad.execute();
                break;
            default:
                i = new Intent(this, InfoMembreInscritActivity.class);
                i.putExtra("id",id);
                i.putExtra("sexe",sexe).putExtra("nom",nom).putExtra("prenom",prenom);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, InfoMembreInscritActivity.class);
        i.putExtra("id",id);
        i.putExtra("sexe",sexe).putExtra("nom",nom).putExtra("prenom",prenom);
        startActivity(i);
        finish();
    }

    private class AsyncDesinscrireMembre extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(ConfirmDesinscrireMembreActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Désinscription du membre...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/desinscrire_membre.php?compte=" + mid+"&personne="+id);
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

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            if (resultat.equals("Succès")) {
                ConfirmDesinscrireMembreActivity.this.finish();
                Intent i = new Intent(ConfirmDesinscrireMembreActivity.this, MembreActivity.class);
                startActivity(i);
                Toast.makeText(ConfirmDesinscrireMembreActivity.this, "Le membre "+prenom+" "+toUpperCase(nom)+" a été désinscrit avec succès.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ConfirmDesinscrireMembreActivity.this, "Impossible de désinscrire un membre.", Toast.LENGTH_LONG).show();
            }
        }
    }
}