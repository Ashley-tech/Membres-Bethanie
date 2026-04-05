package com.example.bethaniemembre;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoriqueListActivity extends AppCompatActivity implements View.OnClickListener {

    ListView historique;
    Button retour;
    int success;
    ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String,String>>();
    JSONParser parser = new JSONParser();
    AccountManagement a;
    String mid;
    String v = "Succès";
    ArrayList<String> identifiants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique_list);

        retour = (Button) findViewById(R.id.buttonretourh);
        retour.setOnClickListener(this);
        historique = (ListView) findViewById(R.id.historique_list);

        a = new AccountManagement(this);
        HashMap<String,String> account = a.userDetails();
        mid = account.get(a.ID);

        InitHistorique i = new InitHistorique();
        i.execute();

        historique.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t1 = (TextView) findViewById(R.id.item11);
                TextView t2 = (TextView) findViewById(R.id.item12);
                TextView t3 = (TextView) findViewById(R.id.item13);

                Intent intent = new Intent(HistoriqueListActivity.this, InfoHistoriqueActivity.class);
                intent.putExtra("id",identifiants.get(i));
                intent.putExtra("libelle", t1.getText().toString());
                intent.putExtra("date",t2.getText().toString());
                intent.putExtra("membre",t3.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    class InitHistorique extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(HistoriqueListActivity.this);
        HttpURLConnection co;
        String U = "http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/historique_liste.php?compte=" + mid;
        URL url = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(HistoriqueListActivity.this);
            dialog.setMessage("Chargement de votre historique...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<>();

            JSONObject object = parser.makeHttpRequest(U,"GET",map);

            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray apps = object.getJSONArray("historiques");
                        for (int i = 0; i < apps.length(); i++) {
                            JSONObject a = apps.getJSONObject(i);
                            HashMap<String, String> m = new HashMap<String, String>();
                            identifiants.add(a.getString("id"));
                            m.put("texte", a.getString("intitule"));
                            m.put("date_heure", a.getString("date") + " - " + a.getString("heure"));
                            m.put("membre", a.getString("personne"));

                            Log.i("m", String.valueOf(m));
                            values.add(m);
                        }
                    }
                    Log.i("values", String.valueOf(values));
                } catch (JSONException e) {
                    e.printStackTrace();
                    v = "Exception";
                }
            } else {
                v = "Exception";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.cancel();
            if (v.equals("Succès")){
                SimpleAdapter adapter=new SimpleAdapter(HistoriqueListActivity.this,values,R.layout.item1,
                        new String[]{"texte","date_heure","membre"},new int[]{R.id.item11,R.id.item12,R.id.item13});

                Log.d("simpleadapter", String.valueOf(adapter));
                historique.setAdapter(adapter);
            } else {
                Toast.makeText(HistoriqueListActivity.this,"Nous n'avons pas pu charger votre historique.", Toast.LENGTH_LONG).show();
            }
        }
    }
}