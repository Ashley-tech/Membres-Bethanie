package com.example.bethaniemembre;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MembresDesinscritActivity extends AppCompatActivity implements View.OnClickListener{
    ListView membres;
    Button retour;
    String resultat = "Succès",prenom="",nom="",sexe="",droit="";
    TextView intro;
    int success = 0;
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
        setContentView(R.layout.activity_membres_desinscrit);

        retour = (Button)findViewById(R.id.buttonretourdesinscrit);
        retour.setOnClickListener(this);
        membres = (ListView) findViewById(R.id.membre_desinscrit_list);

        Bundle e = getIntent().getExtras();
        if (e != null){
            droit = e.getString("droit");
        }

        InitMembresDesinscrits i = new InitMembresDesinscrits();
        i.execute();

        intro = (TextView) findViewById(R.id.membre_desinscrit_intro);
        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });

        membres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t1 = (TextView) findViewById(R.id.item01);
                TextView t2 = (TextView) findViewById(R.id.item02);

                Intent intent = new Intent(MembresDesinscritActivity.this, InfoMembreDesinscritActivity.class);
                intent.putExtra("id",String.valueOf(identifiant.get(i)));
                intent.putExtra("nom", noms.get(i));
                intent.putExtra("prenom",prenoms.get(i));
                intent.putExtra("sexe",sexes.get(i));
                intent.putExtra("droit",droit);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    class InitMembresDesinscrits extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(MembresDesinscritActivity.this);
        HttpURLConnection co;
        URL url = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MembresDesinscritActivity.this);
            dialog.setMessage("Chargement de la liste des membres désinscrits...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<>();

            JSONObject object = parser.makeHttpRequest("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/liste_membres_desinscrits.php","GET",map);
            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray apps = object.getJSONArray("membres_desinscrits");
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
                SimpleAdapter adapter = new SimpleAdapter(MembresDesinscritActivity.this, values, R.layout.item,
                        new String[]{"nom","prenom"}, new int[]{R.id.item01,R.id.item02});

                Log.d("simpleadapter", String.valueOf(adapter));
                membres.setAdapter(adapter);
            } else {
                Toast.makeText(MembresDesinscritActivity.this,"Impossible de charger les membres.", Toast.LENGTH_LONG).show();
            }
        }
    }
}