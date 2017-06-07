package com.michael.apps.sportshi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private int SELECT_PICTURE = 1;
    private int SELECT_CAMERA = 1;

    private ImageView imageView;
    private Bitmap bitmap;
    private Uri photoPath;

    private EditText editTextNama;
    private EditText editTextTanggalLahir;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextEmail;

    Calendar myCalendar = Calendar.getInstance();

    private RadioGroup radioJK;
    private RadioButton radioLaki;
    private RadioButton radioPerempuan;

    private Button buttonFoto;
    private Button buttonRegister;

    private String nama;
    private String jenisKelamin;
    private String tanggalLahir;
    private String username;
    private String password;
    private String konfirmasiPassword;
    private String email;
    private String foto;

    private static final int REQUEST_RUNTIME_PERMISSION = 123;
    final CharSequence[] items = { "Take Photo", "Choose from Library"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar mactionBar = getSupportActionBar();
        mactionBar.setSubtitle("Sports Hi!");
        mactionBar.setDisplayHomeAsUpEnabled(true);

        editTextNama = (EditText) findViewById(R.id.editTextNama);
        editTextTanggalLahir = (EditText) findViewById(R.id.editTextTanggalLahir);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextEmail= (EditText) findViewById(R.id.editTextEmail);


        editTextTanggalLahir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        radioJK = (RadioGroup) findViewById(R.id.radioJK);
        radioLaki = (RadioButton) findViewById(R.id.radioLaki);
        radioPerempuan = (RadioButton) findViewById(R.id.radioPerempuan);

        imageView = (ImageView) findViewById(R.id.imageFoto);

        buttonFoto = (Button) findViewById(R.id.buttonFoto);
        buttonFoto.setOnClickListener(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);
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

    private void registerUser() {
        nama = editTextNama.getText().toString().trim();
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        tanggalLahir = editTextTanggalLahir.getText().toString().trim();
        jenisKelamin = ((RadioButton)findViewById(radioJK.getCheckedRadioButtonId())).getText().toString().trim();

        //Toast.makeText(RegisterActivity.this, nama, Toast.LENGTH_LONG).show();
        //Toast.makeText(RegisterActivity.this, jenisKelamin, Toast.LENGTH_LONG).show();
        //Toast.makeText(RegisterActivity.this, tanggalLahir, Toast.LENGTH_LONG).show();

        if(bitmap != null) {
            foto = getStringImage(bitmap);
        }
        else {
            foto = "";
        }

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Registering...", "Please wait...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        response = response.replaceFirst(" ","");
                        response = response.trim();

                        //Showing toast message of the response
                        Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();

                        switch (response) {
                            case "Registrasi berhasil": {
                                showAlertSuccess();
                                break;
                            }
                            case "Username sudah digunakan": {
                                showAlertUsername();
                                break;
                            }
                            case "Email sudah pernah didaftarkan": {
                                showAlertEmail();
                                break;
                            }
                            default:
                                Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                                showAlertFailed();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        //Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        showAlertInfo();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_NAME, nama);
                params.put(Config.KEY_GENDER, jenisKelamin);
                params.put(Config.KEY_DATE_OF_BIRTH, tanggalLahir);
                params.put(Config.KEY_USERNAME, username);
                params.put(Config.KEY_PASSWORD, password);
                params.put(Config.KEY_EMAIL, email);

                //Adding parameters
                params.put(Config.KEY_IMAGE, foto);

                //returning parameters
                return params;
            }

        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        if (CheckPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
            RequestPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_RUNTIME_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            String tempPath = getPath(selectedImageUri, RegisterActivity.this);
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
            imageView.setImageBitmap(bitmap);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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

    public void showAlertInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Failed");
        alertDialog.setMessage("Please check your internet connection!");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertSuccess() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Registration Successful");
        alertDialog.setMessage("Please check your email inbox.");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    public void showAlertUsername() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Registration Failed");
        alertDialog.setMessage("Username already in use.");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertEmail() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Registration Failed");
        alertDialog.setMessage("Email has already been registered.");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertFailed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Registration Failed");
        alertDialog.setMessage("Image file size is too large. Please reduce the size of the image file, and try again later.");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertPermission() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Failed to Open Camera");
        alertDialog.setMessage("You don't have permission to take a picture. If you want to allow this permission, please allow it in settings!.");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
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

    @Override
    public void onClick(View v) {
        if(v == buttonFoto){
            showFileChooser();
        }
        if(v == buttonRegister){
            nama = editTextNama.getText().toString();
            if(nama.length() == 0) {
                editTextNama.setError("Name is required!");
            }

            tanggalLahir = editTextTanggalLahir.getText().toString();
            if(tanggalLahir.length() == 0) {
                editTextTanggalLahir.setError("Date of birth is required!");
            }

            username = editTextUsername.getText().toString();
            if(username.length() == 0) {
                editTextUsername.setError("Username is required!");
            }

            password = editTextPassword.getText().toString();
            if(password.length() == 0) {
                editTextPassword.setError("Password is required!");
            }
            if(password.length() <= 3) {
                editTextPassword.setError("Password is too short!");
            }

            konfirmasiPassword = editTextConfirmPassword.getText().toString();
            if(konfirmasiPassword.length() == 0) {
                editTextConfirmPassword.setError("Confirmation password is required!");
            }

            if(!konfirmasiPassword.equals(password)) {
                editTextConfirmPassword.setError("Confirmation password not appropriate!");
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

            if( (nama.length() != 0) && (tanggalLahir.length() != 0) && (username.length() != 0) && (password.length() > 3) && (konfirmasiPassword.equals(password)) && (isValidEmail(email))) {
                registerUser();
            }
        }
    }
}
