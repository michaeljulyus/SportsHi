package com.michael.apps.sportshi.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.michael.apps.sportshi.model.Config;
import com.michael.apps.sportshi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Defining views
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private AppCompatTextView TextForgetPassword;
    private AppCompatTextView TextSignup;

    private String username;
    private String password;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;
    private Boolean exit = false;

    private String name, gender, date_of_birth, email, foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar mactionBar = getSupportActionBar();
        mactionBar.setSubtitle("Sports Hi!");
        mactionBar.setDisplayShowHomeEnabled(true);
        mactionBar.setLogo(R.drawable.ic_logo);
        mactionBar.setDisplayUseLogoEnabled(true);

        //Initializing views
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

        TextForgetPassword = (AppCompatTextView) findViewById(R.id.linkForgetPassword);
        TextForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class));
            }
        });

        TextSignup = (AppCompatTextView) findViewById(R.id.linkSignup);
        TextSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Main Activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void login(){
        //Getting values from edit texts
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();

        final ProgressDialog loading = ProgressDialog.show(this, "Login...", "Please wait...", true, false);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        response = response.replace(" ","");
                        response = response.trim();

                        //If we are getting success from server
                        if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){
                            getData();
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                            showAlertIncorrect();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        //You can handle error here if you want
                        //Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                        showAlertFailed();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_USERNAME, username);
                params.put(Config.KEY_PASSWORD, password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getData(){
        //Getting values from edit texts
        username = editTextUsername.getText().toString().trim();

        final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "Login...", "Please wait...", true, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        response = response.replaceFirst(" ", "");
                        response = response.trim();

                        showJSON(response);

                        //Starting main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        //You can handle error here if you want
                        //Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                        //showAlertFailed();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_USERNAME, username);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("konten");
            JSONObject data = result.getJSONObject(0);
            name = data.getString("nama");
            username = data.getString("username");
            gender = data.getString("jenisKelamin");
            date_of_birth = data.getString("tanggalLahir");
            email = data.getString("email");
            foto = data.getString("foto");

            //Creating a shared preference
            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            //Creating editor to store values to shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();

            //Adding values to editor
            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
            editor.putString(Config.NAME_SHARED_PREF, name);
            editor.putString(Config.USERNAME_SHARED_PREF, username);
            editor.putString(Config.GENDER_SHARED_PREF, gender);
            editor.putString(Config.DATE_OF_BIRTH_SHARED_PREF, date_of_birth);
            editor.putString(Config.EMAIL_SHARED_PREF, email);
            editor.putString(Config.IMAGE_SHARED_PREF, foto);

            //Saving values to editor
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAlertFailed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Login Failed");
        alertDialog.setMessage("Please check your internet connection!");
        alertDialog.setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);

        } else {
            Toast.makeText(this, "Press again to exit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    public void showAlertIncorrect() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Failed");
        alertDialog.setMessage("Incorrect username or password");
        alertDialog.setNegativeButton("Tutup",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        //Calling the login function
        username = editTextUsername.getText().toString();
        if(username.length() == 0) {
            editTextUsername.setError("Username is required!");
        }

        password = editTextPassword.getText().toString();
        if(password.length() == 0) {
            editTextPassword.setError("Password is required!");
        }

        if ((username.length() != 0) && (password.length() != 0) ) {
            login();
        }
    }
}