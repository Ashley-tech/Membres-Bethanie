package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStoreOwner;

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

import java.util.HashMap;

public class InfoAccountActivity extends AppCompatActivity implements View.OnClickListener {

    Button modifier, retour;
    TextView login, mdp, profil, date;
    AccountManagement a;
    String mid="0", mLogin="", mPasswd="",Passwd="",l="",lo="";
    ProgressDialog dialog;
    JSONParser parser = new JSONParser();
    String p, d, h, re = "Succès";
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);

        modifier = (Button) findViewById(R.id.buttonUpdateInfo);
        modifier.setOnClickListener(this);
        retour = (Button) findViewById(R.id.buttonretourinfo);
        retour.setOnClickListener(this);
        login = (TextView) findViewById(R.id.login_text_info);
        mdp = (TextView) findViewById(R.id.mdp_text);
        profil = (TextView) findViewById(R.id.profil_text);
        date = (TextView) findViewById(R.id.date_creation_text);

        a = new AccountManagement(this);
        HashMap<String, String> account = a.userDetails();
        final String mLogin2 = account.get(a.LOGINB);
        mLogin = account.get(a.LOGIN);
        mid = account.get(a.ID);
        mPasswd = account.get(a.PASSWORD);
        lo = login.getText().toString();

        login.setText(lo + mLogin);

        InitInfoProfil ii = new InitInfoProfil();
        ii.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonretourinfo:
                finish();
                break;
            default:
                finish();
                Intent i = new Intent(this, UpdateAccountActivity.class);
                i.putExtra("login",l);
                startActivity(i);
                //Toast.makeText(this, "Cette option est encore en maintenance. \n Modifier vos informations sur le site Web de la gestion des membres de l'église protestante Unie de France", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    class InitInfoProfil extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(InfoAccountActivity.this);
            dialog.setMessage("Chargement de vos informations...");
            dialog.setCancelable(true);
            dialog.show();
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
                        l = o.getString("login");
                        Passwd = o.getString("mdp");
                        p = o.getString("profil");
                        d = o.getString("date");
                        h = o.getString("heure");
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
                login.setText(lo+l);
                profil.setText(profil.getText().toString() + p);
                date.setText(date.getText().toString() + d + " à " + h);
                for (int i = 0; i < Passwd.length(); i++) {
                    mdp.setText(mdp.getText().toString() + "*");
                }
            } else {
                Toast.makeText(InfoAccountActivity.this,"Nous n'avons pas pu charger vos informations.", Toast.LENGTH_LONG).show();
            }
        }
    }
}