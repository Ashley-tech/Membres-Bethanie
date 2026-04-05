package com.example.bethaniemembre;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;

import static android.icu.lang.UCharacter.toUpperCase;

public class MembreActivity extends AppCompatActivity implements View.OnClickListener{
    Button membrenon, retour, newm;
    ListView membres;
    String resultat = "Succès",prenom="",nom="",sexe="",profil="",id="0";
    TextView intro;
    int success = 0;
    AccountManagement a;
    ArrayList<Integer> identifiant = new ArrayList<>();
    ArrayList<String> noms = new ArrayList<>();
    ArrayList<String> prenoms = new ArrayList<>();
    ArrayList<String> sexes = new ArrayList<>();
    ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String,String>>();
    JSONParser parser = new JSONParser();

    public void reload() {

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membre);

        membres = (ListView) findViewById(R.id.membre_list);
        membrenon = (Button) findViewById(R.id.buttonmembren);
        membrenon.setOnClickListener(this);
        retour = (Button) findViewById(R.id.buttonretourme);
        retour.setOnClickListener(this);
        newm = (Button) findViewById(R.id.buttonnewmembre);
        newm.setOnClickListener(this);

        intro = (TextView) findViewById(R.id.membre_intro);
        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });

        InitMembresInscrits i = new InitMembresInscrits();
        i.execute();

        membres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t1 = (TextView) findViewById(R.id.item01);
                TextView t2 = (TextView) findViewById(R.id.item02);

                Intent intent = new Intent(MembreActivity.this, InfoMembreInscritActivity.class);
                intent.putExtra("id",String.valueOf(identifiant.get(i)));
                intent.putExtra("nom", noms.get(i));
                intent.putExtra("prenom",prenoms.get(i));
                intent.putExtra("sexe",sexes.get(i));
                intent.putExtra("droit",profil);
                startActivity(intent);
                finish();
            }
        });

        a = new AccountManagement(this);
        HashMap<String,String> account = a.userDetails();
        id = account.get(a.ID);

        AsyncGetType at = new AsyncGetType();
        at.execute();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonnewmembre:
                if (profil.equals("Administrateur")) {
                    i = new Intent(this, NewMembreActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(MembreActivity.this,"Votre compte n'est pas administrateur pour pouvoir créer un nouveau membre.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonretourme:
                finish();
                break;
            default:
                i = new Intent(this,MembresDesinscritActivity.class);
                i.putExtra("droit",profil);
                startActivity(i);
                break;
        }
    }

    class InitMembresInscrits extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(MembreActivity.this);
        HttpURLConnection co;
        URL url = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MembreActivity.this);
            dialog.setMessage("Chargement de la liste des membres...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<>();

            JSONObject object = parser.makeHttpRequest("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/liste_membres_inscrits.php","GET",map);
            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray apps = object.getJSONArray("membres_inscrits");
                        for (int i = 0; i < apps.length(); i++) {
                            JSONObject a = apps.getJSONObject(i);
                            HashMap<String, String> m = new HashMap<String, String>();
                            identifiant.add(a.getInt("id"));
                            sexes.add(a.getString("sexe"));
                            m.put("nom", "Nom : "+a.getString("nom"));
                            noms.add(a.getString("nom"));
                            m.put("prenom", "Prénom : "+a.getString("prenom"));
                            prenoms.add(a.getString("prenom"));

                            Log.i("m", String.valueOf(m));
                            values.add(m);
                        }
                    }
                    Log.i("values", String.valueOf(values));
                } catch (JSONException e) {
                    e.printStackTrace();
                    resultat = "Exception";
                }
            } else {
                resultat = "Exception";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.cancel();
            for (int b=0;b<identifiant.size();b++){
                Log.i("identifiant",String.valueOf(identifiant.get(b)));
                Log.i("nom",noms.get(b));
                Log.i("pre",prenoms.get(b));
            }
            if (resultat.contains("Succès")) {
                SimpleAdapter adapter = new SimpleAdapter(MembreActivity.this, values, R.layout.item,
                        new String[]{"nom","prenom"}, new int[]{R.id.item01,R.id.item02});

                Log.d("simpleadapter", String.valueOf(adapter));
                membres.setAdapter(adapter);
            } else {
                Toast.makeText(MembreActivity.this,"Impossible de charger les membres.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncGetType extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(MembreActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Est-ce que votre compte permet d'inscrire, de désinscrire, de modifier et de supprimer un membre ?");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/get_type.php?profil=" + id);
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
            if (result.equals("Administrateur")){
                Toast.makeText(MembreActivity.this,"Votre compte est administrateur et peut donc permettre d'inscrire, de désinscrire, de modifier et de supprimer un membre.", Toast.LENGTH_LONG).show();
            } else if (result.equals("Lecture seule")) {
                Toast.makeText(MembreActivity.this,"Votre compte est en lecture seule, et ne permet donc pas d'inscrire, de désinscrire, de modifier et de supprimer un membre.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MembreActivity.this,"Nous ne pouvons pas savoir si votre compte permet d'inscrire, de désinscrire, de modifier et de supprimer un membre.", Toast.LENGTH_LONG).show();
            }
            profil = result;
        }
    }
}