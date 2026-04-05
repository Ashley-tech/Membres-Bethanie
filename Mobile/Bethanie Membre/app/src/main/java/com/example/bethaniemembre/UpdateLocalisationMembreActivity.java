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
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.icu.lang.UCharacter.toUpperCase;

public class UpdateLocalisationMembreActivity extends AppCompatActivity implements View.OnClickListener{
    EditText adresse, adresse_comp, code_postal, ville, quartierVille, boite_appart;
    Button modifier, retour;
    String id="0",nom="",prenom="",sexe="",mail="",a="",ac="",cp="",v="",q="",b="",re="Succès",mid = "0";
    AccountManagement am = null;
    TextView intro;

    public static boolean checkCP(String texte, String regex){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(texte);
        if (m.matches())
            return true;
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_localisation_membre);

        adresse = (EditText) findViewById(R.id.editTextAdress);
        adresse_comp = (EditText) findViewById(R.id.editTextAdressComp);
        code_postal = (EditText) findViewById(R.id.editTextCP);
        ville = (EditText) findViewById(R.id.editTextVille);
        quartierVille = (EditText) findViewById(R.id.editTextQuartier);
        boite_appart = (EditText) findViewById(R.id.editTextBoite);

        intro = (TextView) findViewById(R.id.update_local_info_intro);

        modifier = (Button) findViewById(R.id.buttonupdateLocal);
        modifier.setOnClickListener(this);
        retour = (Button) findViewById(R.id.buttonretourlocalisa);
        retour.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            nom = extras.getString("nom");
            sexe = extras.getString("sexe");
            prenom = extras.getString("prenom");
            mail = extras.getString("mail");
            a = extras.getString("adresse");
            ac = extras.getString("adresse_comp");
            v = extras.getString("ville");
            cp = extras.getString("cp");
            b = extras.getString("boite");
            q = extras.getString("quartier");

            intro.setText(intro.getText().toString()+sexe+" "+toUpperCase(nom)+" "+prenom);
            adresse.setText(a);
            adresse_comp.setText(ac);
            code_postal.setText(cp);
            ville.setText(v);
            quartierVille.setText(q);
            boite_appart.setText(b);
        }

        am = new AccountManagement(this);
        HashMap<String,String> account = am.userDetails();
        mid = account.get(am.ID);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonupdateLocal:
                if (adresse.getText().toString().equals(a) && adresse_comp.getText().toString().equals(ac) && code_postal.getText().toString().equals(cp) && ville.getText().toString().equals(v) && quartierVille.getText().toString().equals(q) && boite_appart.getText().toString().equals(b)){
                    Toast.makeText(UpdateLocalisationMembreActivity.this,"Toutes les coordonnées de localisation sont identiques aux anciennes coordonnées de localisation. Veuillez en modifier au moins une.",Toast.LENGTH_LONG).show();
                } else {
                    if (!code_postal.getText().toString().isEmpty() && !checkCP(code_postal.getText().toString(),"[0-9]{0,}")){
                        Toast.makeText(UpdateLocalisationMembreActivity.this,"Le code postal doit comporter jusqu'à 5 chiffres et seulememt que des chiffres",Toast.LENGTH_LONG).show();
                    } else {
                        AsyncUpdateLocalisationMembre aul = new AsyncUpdateLocalisationMembre();
                        aul.execute(adresse.getText().toString(),adresse_comp.getText().toString(),code_postal.getText().toString(),ville.getText().toString(),quartierVille.getText().toString(),boite_appart.getText().toString());
                    }
                }
                break;
            default:
                i = new Intent(this, InfoMembreInscritActivity.class);
                i.putExtra("id",id);
                i.putExtra("nom",nom);
                i.putExtra("prenom",prenom);
                i.putExtra("sexe",sexe);
                startActivity(i);
                finish();
                break;
        }
    }

    private class AsyncUpdateLocalisationMembre extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(UpdateLocalisationMembreActivity.this);
        HttpURLConnection co;
        URL url = null;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Modification des informations de localisation du membre de "+sexe+" "+toUpperCase(nom)+" "+prenom+"...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/modifier_localisation_membre.php?compte="+mid+"&personne="+id);
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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("adresse",strings[0])
                        .appendQueryParameter("complement",strings[1])
                        .appendQueryParameter("cp",strings[2])
                        .appendQueryParameter("ville",strings[3])
                        .appendQueryParameter("quartier",strings[4])
                        .appendQueryParameter("boite",strings[5]);
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
                Log.i("b","0");
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
                    Log.i("c","0");
                    return("Unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("d",String.valueOf(e));
                return "Exception";
            } finally {
                co.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            Log.i("message",result);
            boolean t = (result.contains("Succès"));
            Log.i("tag","(result = Succès) : " + t);
            if (result.equals("Succès")){
                Toast.makeText(UpdateLocalisationMembreActivity.this, "La modification des coordonnées de localisation du membre "+prenom+" "+nom+" a bien été effectué.", Toast.LENGTH_LONG).show();
                UpdateLocalisationMembreActivity.this.finish();
                Intent i = new Intent(UpdateLocalisationMembreActivity.this, InfoMembreInscritActivity.class);
                i.putExtra("id",id);
                i.putExtra("sexe",sexe);
                i.putExtra("nom",nom);
                i.putExtra("prenom",prenom);
                startActivity(i);
            } else {
                Toast.makeText(UpdateLocalisationMembreActivity.this,"Impossible de modifier les localisation de contact du membre "+prenom+" "+nom+" actuellement.", Toast.LENGTH_LONG).show();
            }
        }
    }
}