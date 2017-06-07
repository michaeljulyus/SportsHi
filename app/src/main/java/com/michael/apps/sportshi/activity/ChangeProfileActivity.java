package com.michael.apps.sportshi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.michael.apps.sportshi.R;
import com.michael.apps.sportshi.model.Config;
import com.michael.apps.sportshi.model.RequestHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Michael on 25/02/2017.
 */
public class ChangeProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextNama;
    private EditText editTextTanggalLahir;
    private EditText editTextUsername;
    private EditText editTextEmail;

    private int SELECT_PICTURE = 1;
    private int SELECT_CAMERA = 1;

    private String ts;
    private Long tsLong;

    Calendar myCalendar = Calendar.getInstance();

    private ImageView imageViewFoto;
    private Button buttonFoto;
    private Button buttonUpdate;
    private TextView linkPassword;

    private String username, nama, jenisKelamin, tanggalLahir, email, foto;
    private static final int REQUEST_RUNTIME_PERMISSION = 123;
    final CharSequence[] items = { "Take Photo", "Choose from Library"};
    private Bitmap bitmap;
    private Uri photoPath;
    private RadioGroup radioJK;
    private RadioButton radioLaki;
    private RadioButton radioPerempuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        ActionBar mactionBar = getSupportActionBar();
        mactionBar.setDisplayHomeAsUpEnabled(true);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextTanggalLahir = (EditText) findViewById(R.id.editTextTanggalLahir);
        editTextNama = (EditText) findViewById(R.id.editTextNama);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        imageViewFoto = (ImageView) findViewById(R.id.imageViewFotoProfil);
        linkPassword = (TextView) findViewById(R.id.linkPassword);

        editTextUsername.setInputType(InputType.TYPE_NULL);

        radioJK = (RadioGroup) findViewById(R.id.radioJK);
        radioLaki = (RadioButton) findViewById(R.id.radioLaki);
        radioPerempuan = (RadioButton) findViewById(R.id.radioPerempuan);

        linkPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
            }
        });

        editTextTanggalLahir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ChangeProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        buttonFoto = (Button) findViewById(R.id.buttonFoto);
        buttonFoto.setOnClickListener(this);

        buttonUpdate = (Button) findViewById(R.id.buttonChangePassword);
        buttonUpdate.setOnClickListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        foto = sharedPreferences.getString(Config.IMAGE_SHARED_PREF, "Not available");
        nama = sharedPreferences.getString(Config.NAME_SHARED_PREF, "Not available");
        username = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "Not available");
        tanggalLahir = sharedPreferences.getString(Config.DATE_OF_BIRTH_SHARED_PREF, "Not available");
        email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not available");
        jenisKelamin = sharedPreferences.getString(Config.GENDER_SHARED_PREF, "Not available");

        editTextUsername.setText(username);
        editTextNama.setText(nama);
        editTextTanggalLahir.setText(tanggalLahir);;
        editTextEmail.setText(email);
        Picasso.with(getApplicationContext()).load(foto).placeholder(R.layout.progress).into(imageViewFoto);
        jenisKelamin = ((RadioButton)findViewById(radioJK.getCheckedRadioButtonId())).getText().toString().trim();
        if(jenisKelamin.equals("Male"))
            radioLaki.setEnabled(true);
        if(jenisKelamin.equals("Female"))
            radioPerempuan.setEnabled(true);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextTanggalLahir.setText(sdf.format(myCalendar.getTime()));
    }

    private void getData(){
        //Getting values from edit texts
        final String username = editTextUsername.getText().toString().trim();

        final ProgressDialog loading = ProgressDialog.show(ChangeProfileActivity.this, "Geting data...", "Please wait...", true, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        response = response.replaceFirst(" ", "");
                        response = response.trim();

                        showJSON(response);
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
            nama = data.getString("nama");
            jenisKelamin = data.getString("jenis_kelamin");
            tanggalLahir = data.getString("tanggal_lahir");
            email = data.getString("email");
            foto = data.getString("foto");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editTextNama.setText(nama);
        if(jenisKelamin.equals("Male"))
            radioLaki.setEnabled(true);
        if(jenisKelamin.equals("Female"))
            radioPerempuan.setEnabled(true);
        editTextTanggalLahir.setText(tanggalLahir);
        editTextEmail.setText(email);
        Picasso.with(getApplicationContext()).load(foto).placeholder(R.layout.progress).into(imageViewFoto);
    }

    private void updateData(){
        nama = editTextNama.getText().toString().trim();
        jenisKelamin = ((RadioButton)findViewById(radioJK.getCheckedRadioButtonId())).getText().toString().trim();
        tanggalLahir = editTextTanggalLahir.getText().toString().trim();
        username = editTextUsername.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();

        tsLong = System.currentTimeMillis();
        ts = tsLong.toString();

        //Toast.makeText(getApplicationContext(), jenisKelamin, Toast.LENGTH_LONG).show();

        if(bitmap != null) {
            foto = getStringImage(bitmap);
        }
        else {
            foto = "";
        }

        class UpdateData extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ChangeProfileActivity.this, "Updating...", "Please wait...", true, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                s = s.replaceFirst(" ","");
                s = s.trim();
                if (s.equals("Sukses")) {
                    //Toast.makeText(getApplicationContext(), "Gagal, silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();

                    //Creating a shared preference
                    SharedPreferences sharedPreferences = ChangeProfileActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    //Adding values to editor
                    editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                    editor.putString(Config.NAME_SHARED_PREF, nama);
                    editor.putString(Config.USERNAME_SHARED_PREF, username);
                    editor.putString(Config.GENDER_SHARED_PREF, jenisKelamin);
                    editor.putString(Config.DATE_OF_BIRTH_SHARED_PREF, tanggalLahir);
                    editor.putString(Config.EMAIL_SHARED_PREF, email);

                    String link = "http://192.168.98.50/sportshi/assets/img/foto_profil/" + ts + ".png";
                    editor.putString(Config.IMAGE_SHARED_PREF, link);

                    showAlertSuccess();
                }
                if (s.equals("Silahkan ubah data-data yang ada untuk mengubah data profil anda")) {
                    //Toast.makeText(getApplicationContext(), "Gagal, silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                    showAlertInfo();
                }
                if (s.equals("Email sudah digunakan oleh pengguna lain")) {
                    //Toast.makeText(getApplicationContext(), "Gagal, silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                    showAlertEmail();
                }
                if (s.equals("Gagal")) {
                    //Toast.makeText(getApplicationContext(), "Gagal, silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                    showAlertFailed();
                }
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_USERNAME, username);
                hashMap.put(Config.KEY_NAME, nama);
                hashMap.put(Config.KEY_GENDER, jenisKelamin);
                hashMap.put(Config.KEY_DATE_OF_BIRTH, tanggalLahir);
                hashMap.put(Config.KEY_EMAIL, email);
                hashMap.put(Config.KEY_IMAGE_NAME, ts);

                //Adding parameters
                hashMap.put(Config.KEY_IMAGE, foto);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Config.UPDATE_PROFILE_URL, hashMap);

                return s;
            }
        }

        UpdateData ue = new UpdateData();
        ue.execute();
    }

    private void showFileChooser() {
        if (CheckPermission(ChangeProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeProfileActivity.this);
            builder.setTitle("Add Photo");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Take Photo")) {

                        // you have permission go ahead
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Sports Hi");
                        imagesFolder.mkdir();

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        File image = new File(imagesFolder, "Photo_" + timeStamp +".jpg");
                        photoPath = Uri.fromFile(image);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
                        startActivityForResult(intent, SELECT_CAMERA);
                    } else if (items[item].equals("Choose from Library")) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_PICTURE);
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            // you do not have permission go request runtime permissions
            RequestPermission(ChangeProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_RUNTIME_PERMISSION);
        }
    }

    public void RequestPermission(Activity thisActivity, String Permission, int Code) {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Permission)) {
            } else {
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Permission},
                        Code);
            }
        }
    }

    public boolean CheckPermission(Context context, String Permission) {
        if (ContextCompat.checkSelfPermission(context,
                Permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            String tempPath = getPath(selectedImageUri, ChangeProfileActivity.this);
            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(tempPath, btmapOptions);
        }
        if (requestCode == SELECT_CAMERA && resultCode == RESULT_OK && data == null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            imageViewFoto.setImageBitmap(bitmap);
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (permsRequestCode) {

            case REQUEST_RUNTIME_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // you have permission go ahead
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeProfileActivity.this);
                    builder.setTitle("Add Photo");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (items[item].equals("Take Photo")) {

                                // you have permission go ahead
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Sports Hi");
                                imagesFolder.mkdir();

                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                File image = new File(imagesFolder, "Photo_" + timeStamp +".jpg");
                                photoPath = Uri.fromFile(image);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
                                startActivityForResult(intent, SELECT_CAMERA);
                            } else if (items[item].equals("Choose from Library")) {
                                Intent intent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(
                                        Intent.createChooser(intent, "Select File"),
                                        SELECT_PICTURE);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    // you do not have permission show toast.
                    showAlertPermission();
                }
                return;
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void showAlertSuccess() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeProfileActivity.this);
        alertDialog.setTitle("Success");
        alertDialog.setMessage("You have successfully changed your profile data.");
        alertDialog.setNegativeButton("Tutup",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    public void showAlertPermission() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeProfileActivity.this);
        alertDialog.setTitle("Failed to Open Camera");
        alertDialog.setMessage("You don't have permission to take a picture. If you want to allow this permission, please allow it in settings!");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeProfileActivity.this);
        alertDialog.setTitle("Information");
        alertDialog.setMessage("Please change the existing data to change your profile data!");
        alertDialog.setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertFailed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeProfileActivity.this);
        alertDialog.setTitle("Failed");
        alertDialog.setMessage("Failed to change your profile data.");
        alertDialog.setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertEmail() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeProfileActivity.this);
        alertDialog.setTitle("Failed");
        alertDialog.setMessage("Email is already used by other user. Please enter another email!");
        alertDialog.setNegativeButton("Close",new DialogInterface.OnClickListener() {
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
            case R.id.action_refresh:
                getData();
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
                        Intent intent = new Intent(ChangeProfileActivity.this, LoginActivity.class);
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
        if(v == buttonFoto){
            showFileChooser();
        }
        if(v == buttonUpdate){
            nama = editTextNama.getText().toString();
            if(nama.length() == 0) {
                editTextNama.setError("Name is required!");
            }

            tanggalLahir = editTextTanggalLahir.getText().toString();
            if(tanggalLahir.length() == 0) {
                editTextTanggalLahir.setError("Date of Birth is required!");
            }

            email = editTextEmail.getText().toString();
            if(email.length() == 0) {
                editTextEmail.setError("Email is required!");
            }
            else {
                if (!isValidEmail(email)) {
                    editTextEmail.setError("Email not valid!");
                }
            }

            if( (nama.length() != 0) && (tanggalLahir.length() != 0) && (email.length() != 0) && (isValidEmail(email))) {
                updateData();
            }
        }
    }

}