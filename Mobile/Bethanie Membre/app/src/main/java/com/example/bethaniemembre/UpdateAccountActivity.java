package com.example.bethaniemembre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStoreOwner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class UpdateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    AccountManagement a;
    String mid="0",mLogin = "",t="";
    EditText pwd, pwd2,login;
    Spinner type;
    Button annuler, modifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        a = new AccountManagement(this);
        HashMap<String, String> account = a.userDetails();
        final String mLogin2 = account.get(a.LOGINB);
        mid = account.get(a.ID);
        //mPasswd = account.get(a.PASSWORD);

        pwd = (EditText) findViewById(R.id.editTextPassword_0);
        pwd2 = (EditText) findViewById(R.id.editTextPassword2_0);
        login = (EditText) findViewById(R.id.editTextLogin_0);
        type = (Spinner) findViewById(R.id.Profil_Type_0);
        annuler = (Button) findViewById(R.id.buttonretourUpdateAccount);
        annuler.setOnClickListener(this);
        modifier = (Button) findViewById(R.id.buttonUpdateAccount);
        modifier.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            mLogin = extras.getString("login");
        }

        login.setText(mLogin);
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent i = new Intent(this, InfoAccountActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.buttonretourUpdateAccount:
                finish();
                i = new Intent(this, InfoAccountActivity.class);
                startActivity(i);
                break;
            default:
                t = type.getSelectedItem().toString();
                if (t.equals("Profil* :")){
                    t = "";
                }
                if (login.getText().toString().isEmpty() || pwd.getText().toString().isEmpty() || pwd2.getText().toString().isEmpty() || t.isEmpty()){
                    Toast.makeText(UpdateAccountActivity.this,"Vous devez impérativement remplir tous les champs",Toast.LENGTH_LONG).show();
                } else {
                    if (!pwd.getText().toString().equals(pwd2.getText().toString())){
                        Toast.makeText(UpdateAccountActivity.this, "Les mots de passes saisis sont différents", Toast.LENGTH_LONG).show();
                    } else {
                        AsyncUpdateAccount as = new AsyncUpdateAccount();
                        as.execute(login.getText().toString(),pwd.getText().toString(),t);
                    }
                }
                break;
        }
    }

    private class AsyncUpdateAccount extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(UpdateAccountActivity.this);
        HttpURLConnection co;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Modification des informations de votre compte...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL("http://192.168.1.24/arakot1/Membres%20B%C3%A9thanie/controleurs_mobile/modifier_compte.php?compte="+mid);
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
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("login",strings[0]).appendQueryParameter("pwd",strings[1]).appendQueryParameter("profil",strings[2]);
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
            boolean t = (result.contains("Succès"));
            Log.i("tag","(result = Succès) : " + t);
            if (result.equals("Succès")) {
                Toast.makeText(UpdateAccountActivity.this, "Vos informations ont été modifiées avec succès. \nVous devriez, au menu principal, cliquer sur son titre pour mettre votre compte à jour.", Toast.LENGTH_LONG).show();
                a = new AccountManagement(UpdateAccountActivity.this);
                a.UpdateInfo(mid,login.getText().toString(),pwd.getText().toString());
                Intent i = new Intent(UpdateAccountActivity.this, InfoAccountActivity.class);
                startActivity(i);
                UpdateAccountActivity.this.finish();
            } else if (result.equals("Compte existant")){
                Toast.makeText(UpdateAccountActivity.this,"Le login saisi correspond déjà à un compte. Veuillez saisir un autre login.",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UpdateAccountActivity.this,"Impossible de modifier vos informations pour le moment", Toast.LENGTH_LONG).show();
            }
        }
    }
}