package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
import java.util.HashMap;

import static com.example.bethaniemembre.NewMembreActivity.isValidDate;
import static com.example.bethaniemembre.NewMembreActivity.isValidEmail;

public class UpdateClassicInfoMembreActivity extends AppCompatActivity implements View.OnClickListener{
    Button modifier, annuler;
    EditText nom, prenom, dateNaissance, mail;
    Spinner sexe;
    String s,id,n,p,dN,mel,mid;
    AccountManagement a = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_classic_info_membre);

        sexe = (Spinner) findViewById(R.id.spinnerSexe2);
        nom = (EditText) findViewById(R.id.editTextPersonLN2);
        prenom = (EditText) findViewById(R.id.editTextPersonFN2);
        dateNaissance = (EditText) findViewById(R.id.editTextNaissance2);
        mail = (EditText) findViewById(R.id.editTextEmailAddress2);

        a = new AccountManagement(this);
        HashMap<String,String> account = a.userDetails();
        mid = account.get(a.ID);

        modifier = (Button) findViewById(R.id.buttonupdateclassicinfo);
        modifier.setOnClickListener(this);
        annuler = (Button) findViewById(R.id.buttonretourupdateclassicinfo);
        annuler.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            s = extras.getString("sexe");
            n = extras.getString("nom");
            nom.setText(n);
            p = extras.getString("prenom");
            prenom.setText(p);
            dN = extras.getString("naissance");
            dateNaissance.setText(dN);
            mel = extras.getString("mail");
            mail.setText(mel);
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonretourupdateclassicinfo:
                i = new Intent (this, InfoMembreInscritActivity.class);
                i.putExtra("id",id);
                i.putExtra("sexe",s);
                i.putExtra("nom", n);
                i.putExtra("prenom",p);
                i.putExtra("naissance",dN);
                i.putExtra("mail",mel);
                startActivity(i);
                finish();
                break;
            default:
                String t = sexe.getSelectedItem().toString();
                if (dateNaissance.getText().toString().isEmpty() || nom.getText().toString().isEmpty() ||prenom.getText().toString().isEmpty() || mail.getText().toString().isEmpty() || t.equals("Sexe* :")){
                    Toast.makeText(UpdateClassicInfoMembreActivity.this,"Tous les champs sont obligatoires.", Toast.LENGTH_LONG).show();
                } else {
                    if (!isValidDate(dateNaissance.getText().toString())){
                        Toast.makeText(UpdateClassicInfoMembreActivity.this,"La date de naissance saisie ne respecte pas le format AAAA-MM-DD, ou bien n'est pas valide.", Toast.LENGTH_LONG).show();
                    } else {
                        if (!isValidEmail(mail.getText().toString())){
                            Toast.makeText(UpdateClassicInfoMembreActivity.this,"L'adresse mail saisie n'est pas correct.", Toast.LENGTH_LONG).show();
                        } else {
                            if (t.equals("M.")){
                                t = "M";
                            } else if (t.equals("Mme")){
                                t = "F";
                            }
                            AsyncUpdateClassicInfoMembre a = new AsyncUpdateClassicInfoMembre();
                            a.execute(t,nom.getText().toString(),prenom.getText().toString(),dateNaissance.getText().toString(),mail.getText().toString());
                        }
                    }
                }
                break;
        }
    }

    private class AsyncUpdateClassicInfoMembre extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(UpdateClassicInfoMembreActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Modification des informations classiques du membre...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/modifier_infos_classique_membre.php?compte="+mid+"&personne="+id);
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
                Toast.makeText(UpdateClassicInfoMembreActivity.this, "La modification des informations classique de ce membre a bien été effectué. Un mail sera envoyé à la nouvelle adresse mail de ce membre.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(UpdateClassicInfoMembreActivity.this, InfoMembreInscritActivity.class);
                i.putExtra("id",id);
                i.putExtra("sexe",sexe.getSelectedItem().toString());
                i.putExtra("nom",nom.getText().toString());
                i.putExtra("prenom",prenom.getText().toString());
                startActivity(i);
                UpdateClassicInfoMembreActivity.this.finish();
            } else {
                Toast.makeText(UpdateClassicInfoMembreActivity.this,"Impossible de modifier les informations classiques de ce membre actuellement.", Toast.LENGTH_LONG).show();
            }
        }
    }
}