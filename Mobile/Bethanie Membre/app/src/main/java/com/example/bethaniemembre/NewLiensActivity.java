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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import java.util.*;
import java.lang.*;
import java.io.*;

import static android.icu.lang.UCharacter.toUpperCase;

public class NewLiensActivity extends AppCompatActivity implements View.OnClickListener{
    Button valider, voc,retour;
    TextView titre, liens;
    EditText assoc,dissoc;
    String id="0",nom="",prenom="",sexe="",mel="",resultat="Succès", mid="0",liena="",liend="",l="",re="Succès";
    AccountManagement a;
    JSONParser parser = new JSONParser();
    int success;
    public static String upperCaseFirst(String val) {
        char[] arr = val.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_liens);

        valider = (Button) findViewById(R.id.buttonValider);
        voc = (Button) findViewById(R.id.buttonVocLiens);
        retour=(Button) findViewById(R.id.buttonretourlien);
        titre=(TextView) findViewById(R.id.lien_attribution_title);
        liens = (TextView) findViewById(R.id.lien_list);
        assoc = (EditText) findViewById(R.id.editTextLienAssocier);
        dissoc= (EditText) findViewById(R.id.editTextLienDissocier);
        valider.setOnClickListener(this);
        voc.setOnClickListener(this);
        retour.setOnClickListener(this);

        a = new AccountManagement(this);
        HashMap<String,String> account = a.userDetails();
        mid = account.get(a.ID);

        Bundle e = getIntent().getExtras();
        if (e != null) {
            id = e.getString("id");
            prenom = e.getString("prenom");
            sexe = e.getString("sexe");
            nom = e.getString("nom");
            mel = e.getString("mail",mel);
        }

        titre.setText(titre.getText().toString()+sexe+" "+toUpperCase(nom)+" "+prenom);

        InitLiensMembre ini = new InitLiensMembre();
        ini.execute();
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent i = new Intent(this, InfoMembreInscritActivity.class);
        i.putExtra("id",id);
        i.putExtra("sexe",sexe);
        i.putExtra("nom",nom);
        i.putExtra("prenom",prenom);
        startActivity(i);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.buttonretourlien:
                finish();
                i = new Intent(this, InfoMembreInscritActivity.class);
                i.putExtra("id",id);
                i.putExtra("sexe",sexe);
                i.putExtra("nom",nom);
                i.putExtra("prenom",prenom);
                startActivity(i);
                break;
            case R.id.buttonVocLiens:
                i = new Intent(this, VocLiensActivity.class);
                startActivity(i);
                break;
            default:
                if (!assoc.getText().toString().isEmpty()){
                    liena = upperCaseFirst(assoc.getText().toString()).replaceAll("^\\s+", "").replaceAll("\\s+$", "");
                    switch (liena){
                        case "Aïeul":
                        case "Aïeule":
                            liena = "Aïeul(e)";
                            break;
                        case "Aîné":
                        case "Aînée":
                            liena = "Aîné(e)";
                            break;
                        case "Apparenté":
                        case "Apparentée":
                            liena = "Apparenté(e)";
                            break;
                        case "Arrière-grand-parent":
                            liena = "Arrière-grands-parents";
                            break;
                        case "Arrière-neveu":
                        case "Arrière-nièce":
                            liena = "Arrière-neveu, arrière-nièce";
                            break;
                        case "Arrière-petit-fils":
                        case "Arrière-petite-fille":
                            liena = "Arrière-petit-fils, arrière-petite-fille";
                            break;
                        case "Arrière-petit-enfant":
                            liena = "Arrière-petits-enfants";
                            break;
                        case "Beau-parent":
                            liena = "Beaux-parents";
                            break;
                        case "Benjamin":
                        case "Benjamine:":
                            liena = "Benjamin(e)";
                            break;
                        case "Bisaïeul":
                        case "Bisaïeule":
                            liena = "Bisaïeul(e)";
                            break;
                        case "Collatéral":
                        case "Collatérale":
                        case "Collatéraux":
                        case "Collatérales":
                            liena = "Collatéral(e)(aux)";
                            break;
                        case "Cousin germain":
                        case "Cousine germaine":
                        case "Cousins germains":
                        case "Cousines germaines":
                            liena = "Cousin(e) germain(e)";
                            break;
                        case "Cousin issu de germain":
                        case "Cousine issue de germaine":
                        case "Cousins issus de germains":
                        case "Cousines issues de germaines":
                            liena = "Cousin(e)s issu(e)s de germain(e)s:";
                            break;
                        case "Cousin":
                        case "Cousine":
                            liena = "Cousin(e)";
                            break;
                        case "Enfant du premier lit":
                        case "Enfant du 1er lit":
                        case "Enfant du second lit":
                        case "Enfant du 2nd lit":
                        case "Enfant du 2ème lit":
                        case "Enfant du deuxième lit":
                            liena = "Enfant du premier, du second lit";
                            break;
                        case "Filleul":
                        case "Filleule":
                            liena = "Filleul(e)";
                            break;
                        case "Grand-parent":
                            liena = "Grand-parents";
                            break;
                        case "Parent":
                            liena = "Parents";
                            break;
                        case "Petit-neveu":
                        case "Petite-nièce":
                            liena = "Petit-neveu, petite-nièce";
                            break;
                        case "Petit-enfant":
                            liena = "Petits-enfants";
                            break;
                        case "Trisaïeul":
                        case "Trisaïeule":
                            liena = "Trisaïeul, e";
                            break;
                        case "Incestueux":
                        case "Incestueuse":
                            liena = "Incestueux (Enfant)";
                            break;
                        case "Sœur":
                            liena = "Soeur";
                            break;
                        default:
                            break;
                    }
                    Log.i("lien",liena);
                    AsyncAssocierLien aa = new AsyncAssocierLien();
                    aa.execute(liena);
                } else {
                    Toast.makeText(this,"Le 1er champ est vide. L'association du lien ne sera donc pas effectuée.",Toast.LENGTH_LONG).show();
                }
                if (!dissoc.getText().toString().isEmpty()) {
                    liend = upperCaseFirst(dissoc.getText().toString()).replaceAll("^\\s+", "").replaceAll("\\s+$", "");
                    switch (liend){
                        case "Aïeul":
                        case "Aïeule":
                            liend = "Aïeul(e)";
                            break;
                        case "Incestueux":
                        case "Incestueuse":
                            liend = "Incestueux (Enfant)";
                            break;
                        case "Aîné":
                        case "Aînée":
                            liend = "Aîné(e)";
                            break;
                        case "Apparenté":
                        case "Apparentée":
                            liend = "Apparenté(e)";
                            break;
                        case "Arrière-grand-parent":
                            liend = "Arrière-grands-parents";
                            break;
                        case "Arrière-neveu":
                        case "Arrière-nièce":
                            liend = "Arrière-neveu, arrière-nièce";
                            break;
                        case "Arrière-petit-fils":
                        case "Arrière-petite-fille":
                            liend = "Arrière-petit-fils, arrière-petite-fille";
                            break;
                        case "Arrière-petit-enfant":
                            liend = "Arrière-petits-enfants";
                            break;
                        case "Beau-parent":
                            liend = "Beaux-parents";
                            break;
                        case "Benjamin":
                        case "Benjamine:":
                            liend = "Benjamin(e)";
                            break;
                        case "Bisaïeul":
                        case "Bisaïeule":
                            liend = "Bisaïeul(e)";
                            break;
                        case "Collatéral":
                        case "Collatérale":
                        case "Collatéraux":
                        case "Collatérales":
                            liend = "Collatéral(e)(aux)";
                            break;
                        case "Cousin germain":
                        case "Cousine germaine":
                        case "Cousins germains":
                        case "Cousines germaines":
                            liend = "Cousin(e) germain(e)";
                            break;
                        case "Cousin issu de germain":
                        case "Cousine issue de germaine":
                        case "Cousins issus de germains":
                        case "Cousines issues de germaines":
                            liend = "Cousin(e)s issu(e)s de germain(e)s:";
                            break;
                        case "Cousin":
                        case "Cousine":
                            liend = "Cousin(e)";
                            break;
                        case "Enfant du premier lit":
                        case "Enfant du 1er lit":
                        case "Enfant du second lit":
                        case "Enfant du 2nd lit":
                        case "Enfant du 2ème lit":
                        case "Enfant du deuxième lit":
                            liend = "Enfant du premier, du second lit";
                            break;
                        case "Filleul":
                        case "Filleule":
                            liend = "Filleul(e)";
                            break;
                        case "Grand-parent":
                            liend = "Grand-parents";
                            break;
                        case "Parent":
                            liend = "Parents";
                            break;
                        case "Petit-neveu":
                        case "Petite-nièce":
                            liend = "Petit-neveu, petite-nièce";
                            break;
                        case "Petit-enfant":
                            liend = "Petits-enfants";
                            break;
                        case "Trisaïeul":
                        case "Trisaïeule":
                            liend = "Trisaïeul, e";
                            break;
                        case "Sœur":
                            liend = "Soeur";
                            break;
                        default:
                            break;
                    }
                    Log.i("lien",liend);
                    AsyncDissocierLien ad = new AsyncDissocierLien();
                    ad.execute(liend);
                } else {
                    Toast.makeText(this,"Le 2nd champ est vide. La dissociation du lien ne sera donc pas effectuée.",Toast.LENGTH_LONG).show();
                }
        }
    }

    private class AsyncAssocierLien extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(NewLiensActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Dissociation du lien...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/associer.php?compte="+mid+"&personne="+id);
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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("libelle",strings[0]);
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
                Toast.makeText(NewLiensActivity.this, "L'association du lien a été effectuée avec succès.", Toast.LENGTH_LONG).show();
                InitLiensMembre ii = new InitLiensMembre();
                ii.execute();
                assoc.setText("");
            } else if (result.equals("Lien existant")){
                Toast.makeText(NewLiensActivity.this, "Le lien que vous avez saisi n'existe pas dans le registre du vocabulaire des liens. \nVeuillez le consulter en appuyant sur le bouton 'Vocabulaire des liens', puis réécrivez-le en respectant la casse.", Toast.LENGTH_LONG).show();
            } else if (result.contains("Déjà associé au lien")){
                Toast.makeText(NewLiensActivity.this,"Le lien que vous avez saisi existe, mais est déjà lié à "+prenom+" "+nom+".", Toast.LENGTH_LONG).show();
            }else if (result.contains("Lien non associé au sexe du membre")){
                Toast.makeText(NewLiensActivity.this,"Le lien que vous avez saisi existe, mais ce lien ne concerne que le sexe opposé au membre concerné", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(NewLiensActivity.this,"Impossible d'associer un lien actuellement.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncDissocierLien extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(NewLiensActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Dissociation du lien...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/dissocier.php?compte="+mid+"&personne="+id);
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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("libelle",strings[0]);
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
                Toast.makeText(NewLiensActivity.this, "La dissociation du lien a été effectuée avec succès.", Toast.LENGTH_LONG).show();
                InitLiensMembre ii = new InitLiensMembre();
                ii.execute();
                dissoc.setText("");
            } else if (result.equals("Lien existant")){
                Toast.makeText(NewLiensActivity.this, "Le lien que vous avez saisi n'existe pas dans le registre du vocabulaire des liens. \nVeuillez le consulter en appuyant sur le bouton 'Vocabulaire des liens', puis réécrivez-le en respectant la casse.", Toast.LENGTH_LONG).show();
            } else if (result.contains("Non associé au lien")){
                Toast.makeText(NewLiensActivity.this,"Le lien que vous avez saisi existe, mais n'est pas lié à "+prenom+" "+nom+".", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(NewLiensActivity.this,"Impossible de dissocier un lien actuellement.", Toast.LENGTH_LONG).show();
            }
        }
    }

    class InitLiensMembre extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        HttpURLConnection co;
        URL url = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(NewLiensActivity.this);
            dialog.setMessage("Chargement des informations de "+nom+" "+prenom+"...");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<String, String>();

            JSONObject object = parser.makeHttpRequest("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/liste_liens_membre.php?membre=" + id, "GET", map);
            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray li = object.getJSONArray("liens");
                        l = "Liens : ";
                        for (int i = 0; i < li.length(); i++) {
                            JSONObject s = li.getJSONObject(i);
                            if (i != li.length() - 1){
                                l = l+s.getString("lien")+" - ";
                            } else {
                                l = l+s.getString("lien");
                            }
                        }
                    } else {
                        l = "Liens : Aucun";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    re = "Exception";
                }
            } else {
                re = "Exception";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.cancel();

            if (re.equals("Succès")) {
                liens.setText(l);
            } else {
                Toast.makeText(NewLiensActivity.this,"Nous n'avons pas pu charger les liens de ce membre.", Toast.LENGTH_LONG).show();
            }
        }
    }
}