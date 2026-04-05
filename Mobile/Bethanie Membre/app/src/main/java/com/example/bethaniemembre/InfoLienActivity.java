package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class InfoLienActivity extends AppCompatActivity implements View.OnClickListener{
    JSONParser parser = new JSONParser();
    String libelle="", def="", sexe="",re="Succès";
    TextView definition, titre, sexe_particulier;
    Button retour;
    ProgressDialog dialog;
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_lien);

        retour = (Button) findViewById(R.id.buttonretourlinfo);
        retour.setOnClickListener(this);

        definition = (TextView) findViewById(R.id.lien_def);
        titre = (TextView) findViewById(R.id.lien_text);
        sexe_particulier = (TextView) findViewById(R.id.sexe_particulier);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            libelle = extras.getString("label");
        }
        titre.setText("Plus d'informations sur le lien \""+libelle+"\"");

        InitInfoLien i = new InitInfoLien();
        i.execute();
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    class InitInfoLien extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(InfoLienActivity.this);
            dialog.setMessage("Chargement des informations du lien \""+libelle+"\"...");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<String, String>();

            JSONObject object = parser.makeHttpRequest("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/info_lien.php?lien="+libelle, "GET", map);
            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray profil = object.getJSONArray("lien");
                        JSONObject o = profil.getJSONObject(0);
                        def = o.getString("definition");
                        sexe = o.getString("sexe_particulier");
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
                sexe_particulier.setText(sexe_particulier.getText().toString()+sexe);
                definition.setText(definition.getText().toString()+def);
            } else {
                Toast.makeText(InfoLienActivity.this,"Impossible de charger les informations complémentaires du lien \""+libelle+"\"", Toast.LENGTH_LONG).show();
            }
        }
    }
}