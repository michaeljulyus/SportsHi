package com.michael.apps.sportshi.activity;

import android.Manifest;
import android.app.Activity;
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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.michael.apps.sportshi.R;
import com.michael.apps.sportshi.model.Config;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    private String TAG = "Location";
    private ImageView imageView;
    private Bitmap bitmap;
    private Uri photoPath;

    private ImageView imgProfile;
    private TextView TextViewNama;
    private TextView TextCategory;
    private TextView TextLocation;
    private EditText editTextDeskripsi;
    private String deskripsi;
    private String nama;
    private String username;
    private String location;
    private String category;
    private String foto;
    private String foto_event;

    private static final int REQUEST_RUNTIME_PERMISSION = 123;
    final CharSequence[] items = { "Take Photo", "Choose from Library"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar mactionBar = getSupportActionBar();
        mactionBar.setDisplayHomeAsUpEnabled(true);

        imgProfile = (ImageView) findViewById(R.id.profilePic);
        TextViewNama = (TextView) findViewById(R.id.name);
        TextLocation = (TextView) findViewById(R.id.location);
        TextCategory = (TextView) findViewById(R.id.sport);
        editTextDeskripsi = (EditText) findViewById(R.id.txtDescription);
        imageView = (ImageView) findViewById(R.id.imageEvent);

        //Fetching username from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        foto = sharedPreferences.getString(Config.IMAGE_SHARED_PREF, "Not available");
        nama = sharedPreferences.getString(Config.NAME_SHARED_PREF, "Not available");
        username = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "Not available");

        Picasso.with(getApplicationContext()).load(foto).placeholder(R.layout.progress).into(imgProfile);
        TextViewNama.setText(nama);

        TextCategory.setVisibility(View.GONE);
        TextLocation.setVisibility(View.GONE);
    }

    private void showFileChooser() {
        if (CheckPermission(EventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
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
                        startActivityForResult(intent, 2);
                    } else if (items[item].equals("Choose from Library")) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                1);
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
            RequestPermission(EventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_RUNTIME_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case (1): {
                Uri selectedImageUri = data.getData();

                String tempPath = getPath(selectedImageUri, EventActivity.this);
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(tempPath, btmapOptions);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
            break;

            case (2): {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoPath);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;

            case (3): {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.i(TAG, "Place:" + place.toString());

                    if (TextCategory.equals(""))
                        TextCategory.setVisibility(View.GONE);

                    location = place.getName().toString();
                    TextLocation.setVisibility(View.VISIBLE);
                    TextLocation.setText("at " + location);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
            break;

            case (4): {
                if(data.getStringExtra("nama") != null) {
                    TextCategory.setVisibility(View.VISIBLE);
                    category = data.getStringExtra("nama");
                    TextCategory.setText("Event: " + category);
                }
            }
            break;
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
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
                                startActivityForResult(intent, 2);
                            } else if (items[item].equals("Choose from Library")) {
                                Intent intent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(
                                        Intent.createChooser(intent, "Select File"),
                                        1);
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

    private void postEvent() {
        deskripsi = editTextDeskripsi.getText().toString().trim();

        if (category == null)
            category = "";

        if (bitmap != null) {
            foto_event = getStringImage(bitmap);
        } else {
            foto_event = "";
        }

        if (deskripsi.equals("") && bitmap == null)
            showAlertInformation();
        else {

            //Showing the progress dialog
            final ProgressDialog loading = ProgressDialog.show(this, "Posting...", "Please wait...", true, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.EVENT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Dismissing the progress dialog
                            loading.dismiss();

                            response = response.replaceFirst(" ", "");
                            response = response.trim();

                            //Showing toast message of the response
                            //Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();

                            switch (response) {
                                case "Berhasil": {
                                    finish();
                                    Toast.makeText(EventActivity.this, "Successfully create an event", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                default:
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
                            Toast.makeText(EventActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            showAlertInfo();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();

                    //Map<String,String> params = new HashMap<String, String>();
                    params.put(Config.KEY_USERNAME, username);
                    params.put(Config.KEY_TAG_CATEGORY, category);
                    params.put(Config.KEY_DESCRIPTION, deskripsi);

                    //Adding parameters
                    params.put(Config.KEY_IMAGE, foto_event);

                    //returning parameters
                    return params;
                }

            };
            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Adding request to the queue
            requestQueue.add(stringRequest);
        }
    }

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, 3);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void showAlertInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventActivity.this);
        alertDialog.setTitle("Failed");
        alertDialog.setMessage("Please check your internet connection!");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertInformation() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventActivity.this);
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Your post is empty, please fill the required coloumn!");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertFailed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventActivity.this);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventActivity.this);
        alertDialog.setTitle("Failed to Open Camera");
        alertDialog.setMessage("You don't have permission to take a picture. If you want to allow this permission, please allow it in settings!.");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertFail() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventActivity.this);
        alertDialog.setTitle("Failed to Add Event");
        alertDialog.setMessage("Please add a photo first!");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventActivity.this);
        alertDialog.setTitle("Failed to Add Event");
        alertDialog.setMessage("Please add location first!");
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
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
        menu.findItem(R.id.action_tag).setVisible(false);
        menu.findItem(R.id.action_about).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_category:
                Intent intent = new Intent(EventActivity.this, CategoryActivity.class);
                startActivityForResult(intent, 4);
                return true;
            case R.id.action_image:
                showFileChooser();
                return true;
            case R.id.action_location:
                callPlaceAutocompleteActivityIntent();
                return true;
            case R.id.action_post:
                if(bitmap == null)
                    showAlertFail();
                if(TextLocation.getText().toString().equals(""))
                    showAlert();
                else
                    postEvent();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
