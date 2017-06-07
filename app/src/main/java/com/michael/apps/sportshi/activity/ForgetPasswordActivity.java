package com.michael.apps.sportshi.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.michael.apps.sportshi.model.Config;
import com.michael.apps.sportshi.R;
import com.michael.apps.sportshi.model.RequestHandler;

import java.util.HashMap;

/**
 * Created by Michael on 25/02/2017.
 */
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail;
    private Button buttonReset;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ActionBar mactionBar = getSupportActionBar();
        mactionBar.setSubtitle("Sports Hi!");
        mactionBar.setDisplayHomeAsUpEnabled(true);

        //Initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        buttonReset = (Button) findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(this);

    }

    private void resetPassword(){
        //Getting values from edit texts
        final String email = editTextEmail.getText().toString().trim();

        class UpdateData extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ForgetPasswordActivity.this, "Reseting Password...", "Please wait...", true, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                s = s.replaceFirst(" ","");
                s = s.trim();

                if (s.equals("Email tidak terdaftar")) {
                    //Toast.makeText(getApplicationContext(), "Gagal, silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                    showAlertFailed();
                }
                else {
                    //Toast.makeText(ForgetPasswordActivity.this, s, Toast.LENGTH_LONG).show();
                    showAlertSuccess();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_EMAIL, email);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Config.RESET_URL, hashMap);

                return s;
            }
        }

        UpdateData ue = new UpdateData();
        ue.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_category).setVisible(false);
        menu.findItem(R.id.action_refresh).setVisible(false);
        menu.findItem(R.id.action_tag).setVisible(false);
        menu.findItem(R.id.action_location).setVisible(false);
        menu.findItem(R.id.action_image).setVisible(false);
        menu.findItem(R.id.action_post).setVisible(false);
        menu.findItem(R.id.action_about).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intentB = new Intent(getApplicationContext(), AboutActivity.class);
                intentB.putExtra("about", true);
                startActivity(intentB);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAlertFailed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgetPasswordActivity.this);
        alertDialog.setTitle("Failed");
        alertDialog.setMessage("Email not registered");
        alertDialog.setNegativeButton("Tutup",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertSuccess() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgetPasswordActivity.this);
        alertDialog.setTitle("Sucess");
        alertDialog.setMessage("Your password has been reset. Email message has been sent to your email.");
        alertDialog.setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onClick(View v) {
        email = editTextEmail.getText().toString();
        if(email.length() == 0) {
            editTextEmail.setError("Email is required!");
        }
        else {
            if (!isValidEmail(email)) {
                editTextEmail.setError("Email not valid!");
            }
        }

        if ((email.length() != 0) && (isValidEmail(email))) {
            resetPassword();
        }
    }
}