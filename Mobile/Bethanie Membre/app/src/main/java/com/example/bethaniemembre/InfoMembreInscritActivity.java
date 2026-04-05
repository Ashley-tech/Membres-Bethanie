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

public class InfoMembreInscritActivity extends AppCompatActivity implements View.OnClickListener{
    String nom = "", prenom = "", sexe = "",id = "0", mel="",droit="", fipe="",fape="",ppe="", fipr="",fapr="",ppr="", adresse="", comp="", cp="",ville="",q="",boite="",lbl = "",l = "Liens : ", f = "";
    TextView intro, classique, contact, localisa,lien;
    ProgressDialog dialog;
    JSONParser parser = new JSONParser();
    String d, re = "Succès";
    int success;
    Button modif_class, modif_contact, modif_lien, modif_local, delete, retour;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_membre_inscrit);

        intro = (TextView) findViewById(R.id.info_account_inscrit_intro);
        classique = (TextView) findViewById(R.id.info_classique);
        contact = (TextView) findViewById(R.id.info_contact);
        localisa = (TextView) findViewById(R.id.info_localisation);
        lien = (TextView) findViewById(R.id.liens);

        modif_class = (Button) findViewById(R.id.buttonModifier);
        modif_class.setOnClickListener(this);
        modif_contact = (Button) findViewById(R.id.buttonModifierContact);
        modif_contact.setOnClickListener(this);
        modif_lien = (Button) findViewById(R.id.buttonModifierLien);
        modif_lien.setOnClickListener(this);
        modif_local = (Button) findViewById(R.id.buttonModifierLocal);
        modif_local.setOnClickListener(this);
        delete = (Button) findViewById(R.id.buttonDesinscrireMembre);
        delete.setOnClickListener(this);
        retour = (Button) findViewById(R.id.buttonretourinfoa);
        retour.setOnClickListener(this);

        Bundle extras=getIntent().getExtras();
        if (extras != null){
            id = extras.getString("id");
            nom = extras.getString("nom");
            prenom = extras.getString("prenom");
            sexe = extras.getString("sexe");
            droit = extras.getString("droit");
        }
        intro.setText(intro.getText().toString()+sexe+" "+toUpperCase(nom)+" "+prenom);
        InitInfoMembreInscrit i = new InitInfoMembreInscrit();
        i.execute();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonretourinfoa:
                finish();
                i = new Intent(this, MembreActivity.class);
                startActivity(i);
                break;
            case R.id.buttonModifierLien:
                if (droit.equals("Administrateur")) {
                    finish();
                    i = new Intent(this, NewLiensActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("sexe", sexe);
                    i.putExtra("nom", nom);
                    i.putExtra("prenom", prenom);
                    i.putExtra("mail", mel);
                    startActivity(i);
                }else {
                    Toast.makeText(InfoMembreInscritActivity.this,"Votre compte n'est pas administrateur pour pouvoir modifier les liens d'un membre.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonModifier:
                if (droit.equals("Administrateur")) {
                    i = new Intent(this, UpdateClassicInfoMembreActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("sexe", sexe);
                    i.putExtra("nom", nom);
                    i.putExtra("prenom", prenom);
                    i.putExtra("naissance", d);
                    i.putExtra("mail", mel);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(InfoMembreInscritActivity.this,"Votre compte n'est pas administrateur pour pouvoir modifier les coordonnées classiques d'un membre.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonModifierContact:
                if (droit.equals("Administrateur")) {
                    i = new Intent(this, UpdateCoordonatesInfoMembreActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("sexe", sexe);
                    i.putExtra("nom", nom);
                    i.putExtra("prenom", prenom);
                    i.putExtra("mail", mel);
                    if (ppe.equals("Non renseigné")) {
                        ppe = "";
                    }
                    i.putExtra("ppe", ppe);
                    if (fipe.equals("Non renseigné")) {
                        fipe = "";
                    }
                    i.putExtra("fipe", fipe);
                    if (fape.equals("Non renseigné")) {
                        fape = "";
                    }
                    i.putExtra("fape", fape);
                    if (ppr.equals("Non renseigné")) {
                        ppr = "";
                    }
                    i.putExtra("ppr", ppr);
                    if (fipr.equals("Non renseigné")) {
                        fipr = "";
                    }
                    i.putExtra("fipr", fipr);
                    if (fapr.equals("Non renseigné")) {
                        fapr = "";
                    }
                    i.putExtra("fapr", fapr);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(InfoMembreInscritActivity.this,"Votre compte n'est pas administrateur pour pouvoir modifier les coordonnées de contact d'un membre.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonModifierLocal:
                if (droit.equals("Administrateur")) {
                    i = new Intent(this, UpdateLocalisationMembreActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("sexe", sexe);
                    i.putExtra("nom", nom);
                    i.putExtra("prenom", prenom);
                    i.putExtra("mail", mel);
                    if (adresse.equals("Non renseigné")) {
                        adresse = "";
                    }
                    i.putExtra("adresse", adresse);
                    if (comp.equals("Non renseigné")) {
                        comp = "";
                    }
                    i.putExtra("adresse_comp", comp);
                    if (cp.equals("Non renseigné")) {
                        cp = "";
                    }
                    i.putExtra("cp", cp);
                    if (ville.equals("Non renseigné")) {
                        ville = "";
                    }
                    i.putExtra("ville", ville);
                    if (q.equals("Non renseigné")) {
                        q = "";
                    }
                    i.putExtra("quartier", q);
                    if (boite.equals("Non renseigné")) {
                        boite = "";
                    }
                    i.putExtra("boite", boite);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(InfoMembreInscritActivity.this,"Votre compte n'est pas administrateur pour pouvoir modifier les coordonnées de localisation d'un membre.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                if (droit.equals("Administrateur")) {
                    i = new Intent(this, ConfirmDesinscrireMembreActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("sexe", sexe);
                    i.putExtra("nom", nom);
                    i.putExtra("prenom", prenom);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(InfoMembreInscritActivity.this,"Votre compte n'est pas administrateur pour pouvoir désinscrire un membre.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent i = new Intent(this, MembreActivity.class);
        startActivity(i);
    }

    class InitInfoMembreInscrit extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(InfoMembreInscritActivity.this);
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
                localisa.setText("Adresse : "+adresse
                        +"\nComplément d'adresse : "+comp
                        +"\nCode postal : "+cp
                        +"\nVille : "+ville+"\nNuméro de boîte ou d'appartement : "+boite);
                lien.setText(l);
            } else {
                Toast.makeText(InfoMembreInscritActivity.this,"Nous n'avons pas pu charger complètement les informations de ce membre.", Toast.LENGTH_LONG).show();
            }
        }
    }
}