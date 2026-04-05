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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewMembreActivity extends AppCompatActivity implements View.OnClickListener{
    Button retour, ajouter;
    Spinner sexe;
    EditText ln, fn, dateN, mel;
    //ArrayList<String> month = new ArrayList<>();
    String mid = "";
    AccountManagement a;


    public static boolean isValidDate(String text) {
        if (text == null || !text.matches("\\d{4}-[01]\\d-[0-3]\\d")) {
            return false;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        try {
            df.parse(text);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    public static boolean isValidEmail(String text){
        String regex = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher ma = pattern.matcher(text);
        if (ma.matches()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membre);

        /*for (int i = 1 ; i <= 12 ; i++){
            if (i < 10){
                month.add("0"+String.valueOf(i));
            } else {
                month.add(String.valueOf(i));
            }
        }*/

        retour = (Button) findViewById(R.id.buttonretournewmembre);
        retour.setOnClickListener(this);
        sexe = (Spinner) findViewById(R.id.spinnerSexe);
        ln = (EditText) findViewById(R.id.editTextPersonLN);
        fn = (EditText) findViewById(R.id.editTextPersonFN);
        dateN = (EditText) findViewById(R.id.editTextNaissance);
        mel = (EditText) findViewById(R.id.editTextEmailAddress);
        ajouter = (Button) findViewById(R.id.buttonaddmembre);
        ajouter.setOnClickListener(this);

        a = new AccountManagement(this);
        HashMap<String,String> account = a.userDetails();
        mid = account.get(a.ID);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonretournewmembre:
                finish();
                break;
            default:
                String t = sexe.getSelectedItem().toString();
                if (dateN.getText().toString().isEmpty() || ln.getText().toString().isEmpty() || fn.getText().toString().isEmpty() || mel.getText().toString().isEmpty() || t.equals("Sexe* :")){
                    Toast.makeText(NewMembreActivity.this,"Tous les champs sont obligatoires.", Toast.LENGTH_LONG).show();
                } else {
                    if (!isValidDate(dateN.getText().toString())){
                        Toast.makeText(NewMembreActivity.this,"La date de naissance saisie ne respecte pas le format AAAA-MM-DD, ou bien n'est pas valide.", Toast.LENGTH_LONG).show();
                    } else {
                        if (!isValidEmail(mel.getText().toString())){
                            Toast.makeText(NewMembreActivity.this,"L'adresse mail saisie n'est pas correct.", Toast.LENGTH_LONG).show();
                        } else {
                            if (t.equals("M.")){
                                t = "M";
                            } else if (t.equals("Mme")){
                                t = "F";
                            }
                            AsyncAddMembre a = new AsyncAddMembre();
                            a.execute(t,ln.getText().toString(),fn.getText().toString(),dateN.getText().toString(),mel.getText().toString());
                        }
                    }
                }
                break;
        }
    }

    private class AsyncAddMembre extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(NewMembreActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Ajout du membre...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/ajouter_membre.php?compte="+mid);
            } catch (MalformedURLException e){
                e.printStackTrace();
                return "Exception";
            }

            try {
                co = (HttpURLConnection) url.openConnection();
                co.setReadTimeout(15000);
                co.setConnectTimeout(15000);
                co.setRequestMethod("POST");

                co.setDoInput(true);
                co.setDoOutput(true);

                Log.i("a","a");
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("sexe",strings[0]).appendQueryParameter("nom",strings[1]).appendQueryParameter("prenom",strings[2]).appendQueryParameter("naissance",strings[3]).appendQueryParameter("mel",strings[4]);
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
                Toast.makeText(NewMembreActivity.this, "L'ajout du membre a bien été effectué. Un mail sera envoyé à l'adresse mail de ce membre. \nCependant, veuillez cliquer sur le titre du menu précédent pour mettre à jour la liste des membres inscrits.", Toast.LENGTH_LONG).show();
                NewMembreActivity.this.finish();
            } else {
                Toast.makeText(NewMembreActivity.this,"Impossible d'ajouter un membre actuellement.", Toast.LENGTH_LONG).show();
            }
        }
    }
}