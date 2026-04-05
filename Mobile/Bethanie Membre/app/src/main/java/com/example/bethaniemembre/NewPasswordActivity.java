package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class NewPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button update, retour;
    EditText mdp1, mdp2;
    String l = "";
    String id = "0";
    private Object PasswordForgotte;
    Context c = (Context) PasswordForgotte;
    String r = "";
    private TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        update = (Button) findViewById(R.id.buttonUpdate);
        update.setOnClickListener(this);
        retour = (Button) findViewById(R.id.buttonretournewmdp);
        retour.setOnClickListener(this);
        mdp1 = (EditText) findViewById(R.id.editTextNewPassword);
        mdp2 = (EditText) findViewById(R.id.editTextNewPassword2);
        login = (TextView) findViewById(R.id.login_text);

        Bundle extras=getIntent().getExtras();
        if (extras != null){
            l = extras.getString("login");
            id = extras.getString("id");
            login.setText(login.getText().toString()+" "+l);
        }
        Log.i("id",id);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonUpdate:
                AsyncUpdatePwd a = new AsyncUpdatePwd();
                a.execute(mdp1.getText().toString(),mdp2.getText().toString());
                Log.i("result",r);
                Log.i("Succès",String.valueOf(r.equals("Succès")));
                break;
            default:
                finish();
                Intent i = new Intent(this, PasswordForgotte.class);
                startActivity(i);
                break;
        }
    }

    private class AsyncUpdatePwd extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(NewPasswordActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Vérification de vos informations...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/modifier_mdp.php?compte="+id);
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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("pwd",strings[0]).appendQueryParameter("pwdr",strings[1]);
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
                    return("Unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception";
            } finally {
                co.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            Log.d("message",result);
            if (result.equals("Succès")){
                Toast.makeText(NewPasswordActivity.this, "La modificaton de votre mot de passe a été effectuée avec succès. Veuillez vous reconnecter.", Toast.LENGTH_LONG).show();
                r = "Succès";
                finish();
            } else if (result.equals("Champ vide")){
                Toast.makeText(NewPasswordActivity.this,"Vous devez obligatoirement saisir tous les champs.",Toast.LENGTH_LONG).show();
                r = "Echec";
            } else if (result.equals("Mots de passe différents")){
                Toast.makeText(NewPasswordActivity.this,"Les mots de passes sont différents", Toast.LENGTH_LONG).show();
                r = "Echec";
            } else {
                Toast.makeText(NewPasswordActivity.this,"Impossible de modifier votre mot de passe.", Toast.LENGTH_LONG).show();
                r = "Echec";
            }
        }
    }
}