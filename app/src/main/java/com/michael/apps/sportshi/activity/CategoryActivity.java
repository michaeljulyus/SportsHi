package com.michael.apps.sportshi.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.michael.apps.sportshi.R;
import com.michael.apps.sportshi.model.ActorAdapter;
import com.michael.apps.sportshi.model.Actors;
import com.michael.apps.sportshi.model.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {
    private ArrayList<Actors> actorsList;
    private ActorAdapter adapter;

    private EditText et;
    private AppCompatTextView textView;
    int textlength = 0;
    private ArrayList<Actors> array_sort= new ArrayList<Actors>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ActionBar mactionBar = getSupportActionBar();
        mactionBar.setDisplayHomeAsUpEnabled(true);

        actorsList = new ArrayList<Actors>();

        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        et = (EditText) findViewById(R.id.EditText01);
        textView = (AppCompatTextView) findViewById(R.id.textView);

        final ListView listview = (ListView)findViewById(R.id.list);
        adapter = new ActorAdapter(getApplicationContext(), R.layout.row, actorsList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                // TODO Auto-generated method stub
                Actors data = adapter.getItem(position);

                Intent i = new Intent();
                if(data.getUsername() != null)
                    i.putExtra("nama", data.getUsername());
                else
                    i.putExtra("nama", "");
                setResult(RESULT_OK, i);
                finish();
            }
        });

        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //final int DRAWABLE_LEFT = 0;
                //final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                //final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        et.setText("");
                    }
                }
                return false;
            }
        });

        et.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                // Abstract Method of TextWatcher Interface.
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // Abstract Method of TextWatcher Interface.
            }
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                textlength = et.getText().length();
                array_sort.clear();
                for (int i = 0; i < actorsList.size(); i++)
                {
                    String nama = actorsList.get(i).getUsername().toLowerCase();
                    if (textlength <= nama.length())
                    {
                        if(nama.contains(et.getText().toString().toLowerCase()))
                        {
                            array_sort.add(actorsList.get(i));
                        }
                    }
                }
                if (array_sort.isEmpty())
                {
                    textView.setText(R.string.empty);
                }
                else
                {
                    textView.setText("");
                }
                adapter = new ActorAdapter(getApplicationContext(), R.layout.row, array_sort);
                listview.setAdapter(adapter);
            }
        });
    }

    private final BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                    getInfo();
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                    getInfo();
                }
            } else {
                // not connected to the internet
                //Toast.makeText(getApplicationContext(), "Silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CategoryActivity.this);
                alertDialog.setTitle("Failed");
                alertDialog.setMessage(String.valueOf("You must enable data access on your device to be able use all the features available. Enable data access?"));
                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_DATA_ROAMING_SETTINGS);
                        CategoryActivity.this.startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
                textView.setText(R.string.empty);
            }
        }
    };

    private void getInfo(){
        //Fetching username from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String username = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "Not available");

        final ProgressDialog loading = ProgressDialog.show(CategoryActivity.this, "Retrieving data...", "Please wait...", true, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        response = response.replaceFirst(" ", "");
                        response = response.trim();

                        showJSON(response);
                        //Toast.makeText(TagFriendActivity.this, response, Toast.LENGTH_LONG ).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        showInfo();
                        textView.setText(R.string.empty);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.USERNAME_SHARED_PREF, username);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        String strName[], strFoto[];
        actorsList.clear();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("konten");
            strName = new String[result.length()];
            strFoto = new String[result.length()];

            for (int i = 0; i < result.length(); i++) {
                JSONObject data = result.getJSONObject(i);

                strName[i] = data.getString("kategori");
                strFoto[i] = data.getString("foto");
                Actors actor = new Actors();
                actor.setUsername(strName[i]);
                actor.setImage(strFoto[i]);
                actorsList.add(actor);

                Collections.sort(actorsList, new Comparator<Actors>() {
                    @Override
                    public int compare(Actors lhs, Actors rhs) {
                        return lhs.getUsername().compareTo(rhs.getUsername());
                    }
                });
            }
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
            case R.id.action_refresh:
                registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CategoryActivity.this);
        alertDialog.setTitle("Failed");
        alertDialog.setMessage("Can not retrieve data from server, Please check your internet connection!");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

}
