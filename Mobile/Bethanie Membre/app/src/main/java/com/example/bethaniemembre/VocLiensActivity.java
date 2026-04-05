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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class VocLiensActivity extends AppCompatActivity implements View.OnClickListener{
    Button retour;
    ListView voc;
    JSONParser parser = new JSONParser();
    int success;
    ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String,String>>();
    String resultat = "Succès";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voc_liens);

        retour = (Button) findViewById(R.id.buttonretourvocliens);
        retour.setOnClickListener(this);
        voc = (ListView) findViewById(R.id.voc_list);

        InitGloLien i = new InitGloLien();
        i.execute();

        voc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t1 = (TextView) findViewById(R.id.itemlien1);

                Intent intent = new Intent(VocLiensActivity.this, InfoLienActivity.class);
                intent.putExtra("label",String.valueOf(t1.getText()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    class InitGloLien extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        HttpURLConnection co;
        URL url = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(VocLiensActivity.this);
            dialog.setMessage("Chargement du glossaire des liens...");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<String, String>();

            JSONObject object = parser.makeHttpRequest("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/liste_liens.php", "GET", map);
            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray apps = object.getJSONArray("liens");
                        for (int i = 0; i < apps.length(); i++) {
                            JSONObject a = apps.getJSONObject(i);
                            HashMap<String, String> m = new HashMap<String, String>();
                            m.put("lien", a.getString("lien"));

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
            if (resultat.contains("Succès")) {
                SimpleAdapter adapter = new SimpleAdapter(VocLiensActivity.this, values, R.layout.itemlien,
                        new String[]{"lien"}, new int[]{R.id.itemlien1});

                Log.d("simpleadapter", String.valueOf(adapter));
                voc.setAdapter(adapter);
            } else {
                Toast.makeText(VocLiensActivity.this,"Impossible de charger le glossaire des liens.", Toast.LENGTH_LONG).show();
            }
        }
    }
}