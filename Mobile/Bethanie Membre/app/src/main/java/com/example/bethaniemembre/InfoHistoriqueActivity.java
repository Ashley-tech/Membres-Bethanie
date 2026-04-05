package com.example.bethaniemembre;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import static android.icu.lang.UCharacter.toUpperCase;

public class InfoHistoriqueActivity extends AppCompatActivity implements View.OnClickListener{
    Button supp,retour;
    TextView t1,t2,t3;
    String id = "0",m="",d="",l="",resultat="Succès";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_historique);

        supp = (Button) findViewById(R.id.buttonDeleteHistorique);
        supp.setOnClickListener(this);
        retour = (Button) findViewById(R.id.buttonRetourInfoHistorique);
        retour.setOnClickListener(this);
        t1 = (TextView) findViewById(R.id.lbl);
        t2 = (TextView) findViewById(R.id.when_text);
        t3 = (TextView) findViewById(R.id.membre_txt);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            id = extras.getString("id");
            l = extras.getString("libelle");
            d = extras.getString("date");
            m = extras.getString("membre");
        }
        t1.setText(l);
        t2.setText(d);
        t3.setText("Membre : "+m);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonRetourInfoHistorique:
                finish();
                i = new Intent(InfoHistoriqueActivity.this,HistoriqueListActivity.class);
                startActivity(i);
                break;
            default:
                AsyncDeleteHistoric a = new AsyncDeleteHistoric();
                a.execute();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent i = new Intent(InfoHistoriqueActivity.this,HistoriqueListActivity.class);
        startActivity(i);
    }

    private class AsyncDeleteHistoric extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(InfoHistoriqueActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Suppression de l'historique...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/supprimer_historique.php?historique=" + id);
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
                InfoHistoriqueActivity.this.finish();
                Intent i = new Intent(InfoHistoriqueActivity.this, HistoriqueListActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(InfoHistoriqueActivity.this, "Impossible de supprimer l'historique pour l'instant.", Toast.LENGTH_LONG).show();
            }
        }
    }
}