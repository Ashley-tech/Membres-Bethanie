package com.example.bethaniemembre;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.icu.lang.UCharacter.toUpperCase;

public class UpdateCoordonatesInfoMembreActivity extends AppCompatActivity implements View.OnClickListener{
    TextView intro;
    EditText portperso, fixeperso, faxperso, portpro, fixepro, faxpro;
    Button retour, modifier;
    String ppe="",fipe="",fape="",ppr="",fipr="",fapr="",re="Succès", mid = "0", id="0",nom="",prenom="",sexe="",mel="";
    AccountManagement a;
    private static final int PERMISSION_REQUEST_CODE = 1;

    public static boolean isValidNumber(String texte, int start, int end, String regex, String car){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(texte);
        if (texte.substring(start,end).equals(car) && m.matches())
            return true;
        return false;
    }

    public static void SendSMSUpdateCoordonnees(String number, String message){
        SmsManager.getDefault().sendTextMessage(number,null,message,null,null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_coordonates_info_membre);

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }*/

        intro = (TextView) findViewById(R.id.updatecoordo_intro);
        portperso = (EditText) findViewById(R.id.editTextPortPerso);
        fixeperso = (EditText) findViewById(R.id.editTextFixePerso);
        faxperso = (EditText) findViewById(R.id.editTextFaxPerso);
        portpro = (EditText) findViewById(R.id.editTextPortPro);
        fixepro = (EditText) findViewById(R.id.editTextFixePro);
        faxpro = (EditText) findViewById(R.id.editTextFaxPro);

        retour = (Button) findViewById(R.id.buttonretourcoordonnes);
        retour.setOnClickListener(this);
        modifier = (Button) findViewById(R.id.buttonUpdatecoordonnes);
        modifier.setOnClickListener(this);

        a = new AccountManagement(this);
        HashMap<String,String> account = a.userDetails();
        mid = account.get(a.ID);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            id = extras.getString("id");
            sexe =extras.getString("sexe");
            nom = extras.getString("nom");
            prenom = extras.getString("prenom");
            mel = extras.getString("mail");
            ppe =extras.getString("ppe");
            fipe = extras.getString("fipe");
            fape = extras.getString("fape");
            ppr =extras.getString("ppr");
            fipr = extras.getString("fipr");
            fapr= extras.getString("fapr");

            intro.setText(intro.getText().toString()+sexe+" "+toUpperCase(nom)+" "+prenom);
            portperso.setText(ppe);
            fixeperso.setText(fipe);
            faxperso.setText(fape);
            portpro.setText(ppr);
            fixepro.setText(fipr);
            faxpro.setText(fapr);
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.buttonretourcoordonnes:
                i = new Intent(this, InfoMembreInscritActivity.class);
                i.putExtra("id",id);
                i.putExtra("nom",nom);
                i.putExtra("prenom",prenom);
                i.putExtra("sexe",sexe);
                startActivity(i);
                finish();
                break;
            default:
                if (portperso.getText().toString().equals(ppe)
                        && fixeperso.getText().toString().equals(fipe)
                        && faxperso.getText().toString().equals(fape)
                        && portpro.getText().toString().equals(ppr)
                        && fixepro.getText().toString().equals(fipr)
                        && faxpro.getText().toString().equals(fapr)) {
                    Toast.makeText(UpdateCoordonatesInfoMembreActivity.this,"Toutes les coordonnées de contact sont identiques aux anciennes coordonnées de contact. Veuillez en modifier au moins une.",Toast.LENGTH_LONG).show();
                } else {
                    if ((!portperso.getText().toString().isEmpty() && !isValidNumber(portperso.getText().toString(),0,1,"[0-9]{0,}","0")) ||
                            (!fixeperso.getText().toString().isEmpty() && !isValidNumber(fixeperso.getText().toString(),0,1,"[0-9]{0,}","0")) ||
                            (!faxperso.getText().toString().isEmpty() && !isValidNumber(faxperso.getText().toString(),0,1,"[0-9]{0,}","0")) ||
                            (!portpro.getText().toString().isEmpty() && !isValidNumber(portpro.getText().toString(),0,1,"[0-9]{0,}","0")) ||
                            (!fixepro.getText().toString().isEmpty() && !isValidNumber(fixepro.getText().toString(),0,1,"[0-9]{0,}","0")) ||
                            (!faxpro.getText().toString().isEmpty() && !isValidNumber(faxpro.getText().toString(),0,1,"[0-9]{0,}","0"))) {
                        Toast.makeText(UpdateCoordonatesInfoMembreActivity.this,"Chaque numéro de téléphone saisi doit commencer impérativement par un zéro et ne doit comporter que des chiffres",Toast.LENGTH_LONG).show();
                    } else {
                        AsyncUpdateContactMembre au = new AsyncUpdateContactMembre();
                        au.execute(portperso.getText().toString(),fixeperso.getText().toString(),faxperso.getText().toString(),portpro.getText().toString(), fixepro.getText().toString(),faxpro.getText().toString());
                    }
                }
                break;
        }
    }

    private class AsyncUpdateContactMembre extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(UpdateCoordonatesInfoMembreActivity.this);
        HttpURLConnection co;
        URL url = null;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Modification des informations de contact du membre de "+sexe+" "+toUpperCase(nom)+" "+prenom+"...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/modifier_coordonnees_contact_membre.php?compte="+mid+"&personne="+id);
            } catch (MalformedURLException e){
                e.printStackTrace();
                return "Exception";
            }

            try {
                co = (HttpURLConnection) url.openConnection();
                co.setReadTimeout(15000);
                co.setConnectTimeout(15000);
                co.setRequestMethod("POST");

                co.setDoInput(true);
                co.setDoOutput(true);

                Log.i("a","a");
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("telperso",strings[0])
                        .appendQueryParameter("telfixeperso",strings[1])
                        .appendQueryParameter("telfaxperso",strings[2])
                        .appendQueryParameter("telpro",strings[3])
                        .appendQueryParameter("telfixepro",strings[4])
                        .appendQueryParameter("telfaxpro",strings[5]);
                String query = builder.build().getEncodedQuery();

                Log.i("a","b");
                OutputStream os = co.getOutputStream();
                Log.i("a","c");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                co.connect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("b","0");
                return "Exception";
            }

            try{
                int response_code = co.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = co.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        result.append(line);
                    }

                    return (result.toString());
                } else {
                    Log.i("c","0");
                    return("Unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("d",String.valueOf(e));
                return "Exception";
            } finally {
                co.disconnect();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            Log.i("message",result);
            boolean t = (result.contains("Succès"));
            Log.i("tag","(result = Succès) : " + t);
            if (result.equals("Succès")){
                Toast.makeText(UpdateCoordonatesInfoMembreActivity.this, "La modification des coordonnées de contact du membre "+prenom+" "+nom+" a bien été effectué.", Toast.LENGTH_LONG).show();
                UpdateCoordonatesInfoMembreActivity.this.finish();
                /*if (!portperso.getText().toString().equals(ppe)) {
                    SendSMSUpdateCoordonnees(portperso.getText().toString(),"Bonjour "+sexe+" "+prenom+" "+toUpperCase(nom)+",\n Votre numéro de téléphone portable personnel vient d'être modifié. \nVoici votre nouveau numéro de téléphone portable personnel : "+portperso+"\n \n Cordialement,\n \nEGLISE PROTESTANTE UNIE DE FRANCE \nGestion des membres");
                }*/
                Intent i = new Intent(UpdateCoordonatesInfoMembreActivity.this, InfoMembreInscritActivity.class);
                i.putExtra("id",id);
                i.putExtra("sexe",sexe);
                i.putExtra("nom",nom);
                i.putExtra("prenom",prenom);
                startActivity(i);
            } else {
                Toast.makeText(UpdateCoordonatesInfoMembreActivity.this,"Impossible de modifier les coordonnées de contact du membre "+prenom+" "+nom+" actuellement.", Toast.LENGTH_LONG).show();
            }
        }
    }
}