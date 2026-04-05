package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class UsersListActivity extends AppCompatActivity implements View.OnClickListener{
    Button retour;
    ListView users;
    int success = 0;
    ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String,String>>();
    JSONParser parser = new JSONParser();
    String resultat = "Succès";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        retour = (Button) findViewById(R.id.buttonretouru);
        retour.setOnClickListener(this);
        users = (ListView) findViewById(R.id.user_list);

        InitUsers i = new InitUsers();
        i.execute();
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    class InitUsers extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(UsersListActivity.this);
        HttpURLConnection co;
        URL url = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(UsersListActivity.this);
            dialog.setMessage("Chargement de la liste des comptes...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<>();

            JSONObject object = parser.makeHttpRequest("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/liste_logins.php","GET",map);
            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray apps = object.getJSONArray("logins");
                        for (int i = 0; i < apps.length(); i++) {
                            JSONObject a = apps.getJSONObject(i);
                            HashMap<String, String> m = new HashMap<String, String>();
                            m.put("login", a.getString("login"));

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
                SimpleAdapter adapter = new SimpleAdapter(UsersListActivity.this, values, R.layout.item2,
                        new String[]{"login"}, new int[]{R.id.item20});

                Log.d("simpleadapter", String.valueOf(adapter));
                users.setAdapter(adapter);
            } else {
                Toast.makeText(UsersListActivity.this,"Impossible de charger la liste des comptes.", Toast.LENGTH_LONG).show();
            }
        }
    }
}