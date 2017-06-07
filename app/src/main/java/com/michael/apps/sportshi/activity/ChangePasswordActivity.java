package com.michael.apps.sportshi.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.michael.apps.sportshi.R;
import com.michael.apps.sportshi.model.Config;
import com.michael.apps.sportshi.model.RequestHandler;

import java.util.HashMap;

/**
 * Created by Michael on 25/02/2017.
 */
public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextUsername;
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmNewPassword;
    private Button buttonUpdate;

    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ActionBar mactionBar = getSupportActionBar();
        mactionBar.setDisplayHomeAsUpEnabled(true);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextOldPassword = (EditText) findViewById(R.id.editTextOldPassword);
        editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = (EditText) findViewById(R.id.editTextConfirmNewPassword);

        editTextUsername.setInputType(InputType.TYPE_NULL);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);

        //Fetching username from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "Not available");

        editTextUsername.setText(username);
    }


    private void updateData(){
        final String username = editTextUsername.getText().toString().trim();
        final String old_password = editTextOldPassword.getText().toString().trim();
        final String new_password = editTextNewPassword.getText().toString().trim();
        final String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();

        if (new_password.equals(confirmNewPassword)) {

            class UpdateData extends AsyncTask<Void, Void, String> {
                ProgressDialog loading;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading = ProgressDialog.show(ChangePasswordActivity.this, "Updating...", "Please wait...", true, false);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    loading.dismiss();

                    s = s.replaceFirst(" ","");
                    s = s.trim();
                    if (s.equals("Sukses")) {
                        //Toast.makeText(getApplicationContext(), "Gagal, silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                        showAlertSuccess();
                    }
                    if (s.equals("Gagal")) {
                        //Toast.makeText(getApplicationContext(), "Gagal, silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                        showAlertNotValidPassword();
                    }
                }

                @Override
                protected String doInBackground(Void... params) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Config.KEY_USERNAME, username);
                    hashMap.put(Config.KEY_OLD_PASSWORD, old_password);
                    hashMap.put(Config.KEY_NEW_PASSWORD, new_password);

                    RequestHandler rh = new RequestHandler();

                    String s = rh.sendPostRequest(Config.UPDATE_PASSWORD_URL, hashMap);

                    return s;
                }
            }

            UpdateData ue = new UpdateData();
            ue.execute();
        }

        else{
            //Toast.makeText(ChangePasswordActivity.this, "Password baru dan konfirmasi password baru tidak sesuai", Toast.LENGTH_LONG).show();
            showAlertNotValid();
        }
    }

    public void showAlertSuccess() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this);
        alertDialog.setTitle("Success");
        alertDialog.setMessage("Password has been changed");
        alertDialog.setNegativeButton("Tutup",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    public void showAlertNotValidPassword() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this);
        alertDialog.setTitle("Failed");
        alertDialog.setMessage("Incorrect current password");
        alertDialog.setNegativeButton("Tutup",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertNotValid() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this);
        alertDialog.setTitle("Tidak Sesuai");
        alertDialog.setMessage("Password baru dan konfirmasi password baru tidak sesuai");
        alertDialog.setNegativeButton("Tutup",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(false);
        menu.findItem(R.id.action_category).setVisible(false);
        menu.findItem(R.id.action_image).setVisible(false);
        menu.findItem(R.id.action_tag).setVisible(false);
        menu.findItem(R.id.action_location).setVisible(false);
        menu.findItem(R.id.action_post).setVisible(false);
        menu.findItem(R.id.action_about).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Logout function
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Keluar");
        alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar?");
        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to username
                        editor.putString(Config.USERNAME_SHARED_PREF, "");

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void onClick(View v) {
        if(v == buttonUpdate){
            oldPassword = editTextOldPassword.getText().toString();
            if(oldPassword.length() == 0) {
                editTextOldPassword.setError("Current password is required!");
            }

            newPassword = editTextNewPassword.getText().toString();
            if(newPassword.length() == 0) {
                editTextNewPassword.setError("New password is required!");
            }

            confirmNewPassword = editTextConfirmNewPassword.getText().toString();
            if(confirmNewPassword.length() == 0) {
                editTextConfirmNewPassword.setError("New password confirmation is required!");
            }

            if ((oldPassword.length() != 0) && (newPassword.length() != 0) && (confirmNewPassword.length() != 0) ) {
                updateData();
            }
        }
    }
}
