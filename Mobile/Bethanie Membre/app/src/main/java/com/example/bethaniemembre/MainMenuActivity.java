package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener{
    AccountManagement a;
    Button info, membres, deconnexion, supprimer, historique;
    TextView intro;
    int success;
    ProgressDialog p;
    String mLogin = "";
    String mid = "",re="Succès";
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
        setContentView(R.layout.activity_main_menu);

        info = (Button) findViewById(R.id.buttonInfo);
        info.setOnClickListener(this);
        membres = (Button) findViewById(R.id.buttonMembres);
        membres.setOnClickListener(this);
        deconnexion = (Button) findViewById(R.id.buttonDeconnect);
        deconnexion.setOnClickListener(this);
        supprimer = (Button) findViewById(R.id.buttonSupp);
        supprimer.setOnClickListener(this);
        historique = (Button) findViewById(R.id.buttonHistorique);
        historique.setOnClickListener(this);
        intro = (TextView) findViewById(R.id.menu_text);

        a = new AccountManagement(this);
        a.checkLogin();

        p = new ProgressDialog(this);
        p.setMessage("Chargement...");
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        HashMap<String,String> account = a.userDetails();
        final String mLogin2 = account.get(a.LOGINB);
        mLogin = account.get(a.LOGIN);
        mid = account.get(a.ID);
        String mPasswd = account.get(a.PASSWORD);
        //Log.i("L",mLogin);
        intro.setText("Menu principal de "+mLogin+" (Identifiant : "+mid+")");
        InitInfoProfil i = new InitInfoProfil();
        i.execute();
        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///Toast.makeText(MainMenuActivity.this, "Le texte intro a été cliqué", Toast.LENGTH_LONG).show();
                reload();
            }
        });
    }

    class InitInfoProfil extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.setCancelable(true);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<String, String>();

            JSONObject object = parser.makeHttpRequest("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/info_profil.php?compte=" + mid, "GET", map);
            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray profil = object.getJSONArray("compte");
                        JSONObject o = profil.getJSONObject(0);
                        mLogin = o.getString("login");
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
            p.cancel();

            if (re.equals("Succès")) {
                intro.setText("Menu principal de "+mLogin+" (Identifiant : "+mid+")");
            } else {
                Toast.makeText(MainMenuActivity.this,"Connexion impossible. L'application ne pourra pas fonctionner comme d'habitude.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonDeconnect:
                a.logout();
                break;
            case R.id.buttonInfo:
                i = new Intent(this, InfoAccountActivity.class);
                startActivity(i);
                break;
            case R.id.buttonMembres:
                i = new Intent(this, MembreActivity.class);
                startActivity(i);
                break;
            case R.id.buttonHistorique:
                i = new Intent(this, HistoriqueListActivity.class);
                startActivity(i);
                break;
            default:
                i = new Intent(this, ConfirmDeleteAccountActivity.class);
                startActivity(i);
                break;
        }
    }
}