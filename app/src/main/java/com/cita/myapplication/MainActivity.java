package com.cita.myapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.ui.profile.ProfileFragment;
import com.cita.myapplication.utils.Server;
import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog progressDialog;
    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences sharedPreferences;
    private static int userId;

    private static final String URL_UPLOAD_PHOTO = Server.URL + "user/upload_photo_profile.php";
    private static final String URL_UPLOAD_COVER = Server.URL + "user/upload_cover_profile.php";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_JSON_OBJ = "json_obj_req";
    private static final String TAG_FULL_NAME = "full_name";
    private static final String TAG_EMAIL_ADDRESS = "email_address";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PHOTO = "photo";
    private static final String TAG_COVER = "cover";
    private static final String TAG_PHOTO_PROFILE = "photo_profile";
    private static final String TAG_COVER_PROFILE = "cover_profile";

    @SuppressLint("StaticFieldLeak")
    private static TextView tvHeaderName, tvHeaderEmail;
    private static CircularImageView civHeaderPhotoProfile;
    @SuppressLint("StaticFieldLeak")
    private static ImageView ivHeaderCoverProfile;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    Bitmap bitmap, decoded;
    private static final int BITMAP_SIZE = 60; // range 1 - 100

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        sharedPreferences = getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(TAG_USER_ID, 0);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View navigationHeader = navigationView.getHeaderView(0);
        tvHeaderName = navigationHeader.findViewById(R.id.tv_header_name);
        tvHeaderEmail = navigationHeader.findViewById(R.id.tv_header_email);
        civHeaderPhotoProfile = navigationHeader.findViewById(R.id.civ_header_photo_profile);
        ivHeaderCoverProfile = navigationHeader.findViewById(R.id.iv_header_cover_profile);
        new NavigationDrawer().execute();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_nutrient, R.id.nav_profile,
                R.id.nav_child, R.id.nav_diagnoses)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setItemSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void setItemSelected(int itemSelected) {
        switch (itemSelected) {
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, "On the way", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        return true;
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LoginActivity.SESSION_STATUS, false);
        editor.putInt(LoginActivity.TAG_USER_ID, 0);
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    public static class NavigationDrawer extends AsyncTask<Integer, String, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.URL + "user/read_profile.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                tvHeaderName.setText(jsonObject.getString(TAG_FULL_NAME));
                                tvHeaderEmail.setText(jsonObject.getString(TAG_EMAIL_ADDRESS));
                                String photo_profile = jsonObject.getString(TAG_PHOTO_PROFILE);
                                String cover_profile = jsonObject.getString(TAG_COVER_PROFILE);
                                Glide.with(context)
                                        .load(Server.URL + photo_profile)
                                        .asBitmap()
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(civHeaderPhotoProfile);
                                Glide.with(context)
                                        .load(Server.URL + cover_profile)
                                        .asBitmap()
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(ivHeaderCoverProfile);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(TAG_USER_ID, String.valueOf(userId));
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, context);
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProfileFragment.PICK_PHOTO_PROFILE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
                updatePhotoProfile();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        if (requestCode == ProfileFragment.PICK_COVER_PROFILE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 1024));
                updateCoverProfile();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, BITMAP_SIZE, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

//        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
//        imageView.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void updatePhotoProfile() {
        //menampilkan progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memproses ...");
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD_PHOTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e(TAG, "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Toast.makeText(MainActivity.this, jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                new ProfileFragment.UpdateProfile().execute();
                                new NavigationDrawer().execute();
                            } else {
                                Toast.makeText(MainActivity.this, jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        hideProgressDialog();

                        //menampilkan toast
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<>();

                //menambah parameter yang di kirim ke web servis
                params.put(TAG_PHOTO, getStringImage(decoded));
                params.put(TAG_USER_ID, String.valueOf(userId));

                //kembali ke parameters
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, this);
    }

    private void updateCoverProfile() {
        //menampilkan progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memproses ...");
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD_COVER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e(TAG, "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Toast.makeText(MainActivity.this, jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                new ProfileFragment.UpdateProfile().execute();
                                new NavigationDrawer().execute();
                            } else {
                                Toast.makeText(MainActivity.this, jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        hideProgressDialog();

                        //menampilkan toast
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<>();

                //menambah parameter yang di kirim ke web servis
                params.put(TAG_COVER, getStringImage(decoded));
                params.put(TAG_USER_ID, String.valueOf(userId));

                //kembali ke parameters
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, this);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, BITMAP_SIZE, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
