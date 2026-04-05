package com.example.bethaniemembre;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.icu.lang.UCharacter.toUpperCase;

public class InfoMembreDesinscritActivity extends AppCompatActivity implements View.OnClickListener{
    TextView intro,classique,local,contact,liens;
    Button retour, reinscrire, delete;
    String nom = "", prenom = "", sexe = "",id = "0", mel="", fipe="",fape="",ppe="", droit="",fipr="",fapr="",ppr="", adresse="", comp="", cp="",ville="",q="",boite="",lbl = "",l = "Liens : ", f = "";
    ProgressDialog dialog;
    JSONParser parser = new JSONParser();
    String d, re = "Succès";
    int success = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_membre_desinscrit);

        retour = (Button) findViewById(R.id.buttonretourdesinscrit);
        retour.setOnClickListener(this);
        reinscrire = (Button) findViewById(R.id.buttonReinscrire);
        reinscrire.setOnClickListener(this);
        delete = (Button)findViewById(R.id.buttonDeleteM);
        delete.setOnClickListener(this);
        intro = (TextView) findViewById(R.id.info_account_desinscrit_intro);
        classique = (TextView)findViewById(R.id.info_classique_0);
        local = (TextView) findViewById(R.id.info_localisation_0);
        contact = (TextView) findViewById(R.id.info_contact_0);
        liens = (TextView) findViewById(R.id.liens_0);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            id = extras.getString("id");
            nom = extras.getString("nom");
            sexe = extras.getString("sexe");
            prenom = extras.getString("prenom");
            droit = extras.getString("droit");
            intro.setText(intro.getText().toString()+sexe+" "+toUpperCase(nom)+" "+prenom);
        }

        InitInfoMembreDesinscrit im = new InitInfoMembreDesinscrit();
        im.execute();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.buttonretourdesinscrit:
                finish();
                i = new Intent(this, MembresDesinscritActivity.class);
                startActivity(i);
                break;
            case R.id.buttonReinscrire:
                if (droit.equals("Administrateur")) {
                    i = new Intent(this, ConfirmReinscrireMembreActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("sexe", sexe);
                    i.putExtra("nom", nom);
                    i.putExtra("prenom", prenom);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(InfoMembreDesinscritActivity.this,"Votre compte n'est pas administrateur pour pouvoir réinscrire un membre.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                if (droit.equals("Administrateur")) {
                    i = new Intent(this, ConfirmDeleteMembreActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("sexe", sexe);
                    i.putExtra("nom", nom);
                    i.putExtra("prenom", prenom);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(InfoMembreDesinscritActivity.this,"Votre compte n'est pas administrateur pour pouvoir supprimer un membre.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent i = new Intent(this, MembresDesinscritActivity.class);
        startActivity(i);
    }

    class InitInfoMembreDesinscrit extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(InfoMembreDesinscritActivity.this);
            dialog.setMessage("Chargement des informations de "+nom+" "+prenom+"...");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<String, String>();

            JSONObject object = parser.makeHttpRequest("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/vues_mobile/info_membre.php?membre=" + id, "GET", map);
            if (parser.getResult() != null) {
                try {
                    success = object.getInt("success");
                    if (success == 1) {
                        JSONArray profil = object.getJSONArray("membre");
                        JSONObject o = profil.getJSONObject(0);
                        d = o.getString("date");
                        mel = o.getString("mail");
                        fipe = o.getString("telephone_fixe_perso");
                        ppe = o.getString("telephone_portable_perso");
                        fape = o.getString("telephone_fax_perso");
                        fipr = o.getString("telephone_fixe_pro");
                        ppr = o.getString("telephone_portable_pro");
                        fapr = o.getString("telephone_fax_pro");
                        adresse = o.getString("adresse");
                        comp = o.getString("complement_adresse");
                        cp = o.getString("code_postal");
                        ville = o.getString("ville");
                        q = o.getString("quartier");
                        boite = o.getString("boite_appart");
                        JSONArray lis = o.getJSONArray("liens");
                        for (int i = 0; i < lis.length(); i++) {
                            JSONObject s = lis.getJSONObject(i);
                            lbl = s.getString("lien");
                            if (i != lis.length() - 1){
                                l = l+lbl+" - ";
                            } else {
                                l = l+lbl;
                            }
                        }
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
                classique.setText("Date de naissance : "+d+"\nAdresse e-mail : "+mel);
                contact.setText("Téléphone portable personnel : "+ppe
                        +"\nTéléphone fixe personnel : "+fipe
                        +"\nTéléphone fax personnel : "+fape
                        +"\nTéléphone portable professionnel : "+ppr
                        +"\nTéléphone fixe professionnel : "+fipr
                        +"\nTéléphone fax professionnel : "+fapr);
                local.setText("Adresse : "+adresse
                        +"\nComplément d'adresse : "+comp
                        +"\nCode postal : "+cp
                        +"\nVille : "+ville+"\nNuméro de boîte ou d'appartement : "+boite);
                liens.setText(l);
            } else {
                Toast.makeText(InfoMembreDesinscritActivity.this,"Nous n'avons pas pu charger complètement les informations de ce membre.", Toast.LENGTH_LONG).show();
            }
        }
    }
}