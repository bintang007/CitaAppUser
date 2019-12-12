package com.cita.myapplication.ui.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cita.myapplication.LoginActivity;
import com.cita.myapplication.MainActivity;
import com.cita.myapplication.R;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.utils.Server;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    @SuppressLint("StaticFieldLeak")
    private static ProgressDialog progressDialog;

    public static final int PICK_PHOTO_PROFILE_REQUEST = 1;
    public static final int PICK_COVER_PROFILE_REQUEST = 2;

    private static final String TAG_JSON_OBJ = "json_obj_req";
    private static final String TAG_FULL_NAME = "full_name";
    private static final String TAG_EMAIL_ADDRESS = "email_address";
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String URL = Server.URL + "user/read_profile.php";
    private final static String TAG_USER_ID = "user_id";
    private static final String TAG_PHOTO_PROFILE = "photo_profile";
    private static final String TAG_COVER_PROFILE = "cover_profile";

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static int userId;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvFullName, tvEmailAddress;
    @SuppressLint("StaticFieldLeak")
    private static CircularImageView civPhotoProfile;
    @SuppressLint("StaticFieldLeak")
    private static ImageView ivCoverProfile;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(TAG_USER_ID, 0);

        context = getActivity();
        FloatingActionButton fabPhotoProfile = root.findViewById(R.id.fab_photo_profile);
        FloatingActionButton fabCoverProfile = root.findViewById(R.id.fab_cover_profile);
        MaterialCardView mcvFullName = root.findViewById(R.id.mcv_full_name);
        MaterialCardView mcvEmailAddress = root.findViewById(R.id.mcv_email_address);
        tvFullName = root.findViewById(R.id.tv_full_name);
        tvEmailAddress = root.findViewById(R.id.tv_email_address);
        civPhotoProfile = root.findViewById(R.id.civ_photo_profile);
        ivCoverProfile = root.findViewById(R.id.iv_cover_profile);

        new UpdateProfile().execute();

        civPhotoProfile.setOnClickListener(this);
        ivCoverProfile.setOnClickListener(this);
        fabPhotoProfile.setSupportBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fabPhotoProfile.setSupportImageTintList(getResources().getColorStateList(R.color.colorWhite));
        fabPhotoProfile.setOnClickListener(this);
        fabCoverProfile.setSupportBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fabCoverProfile.setSupportImageTintList(getResources().getColorStateList(R.color.colorWhite));
        fabCoverProfile.setOnClickListener(this);

        mcvFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                final EditText input = new EditText(getActivity());
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                input.setSingleLine();
                input.setLayoutParams(params);
                container.addView(input);
                alert.setTitle("Nama Lengkap");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String fullName = input.getText().toString();
                        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                                Server.URL + "user/update_profile_full_name.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e(TAG, response);
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Toast.makeText(getActivity(),
                                                    jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                    Toast.LENGTH_SHORT).show();
                                            new UpdateProfile().execute();
                                            new MainActivity.NavigationDrawer().execute();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put(TAG_FULL_NAME, fullName);
                                params.put(LoginActivity.TAG_USER_ID, String.valueOf(userId));
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(stringRequest1, TAG_JSON_OBJ, getActivity());
                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });
        mcvEmailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                final EditText input = new EditText(getActivity());
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                input.setSingleLine();
                input.setLayoutParams(params);
                container.addView(input);
                alert.setTitle("Alamat Email");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String emailAddress = input.getText().toString();
                        if (isEmailValid(emailAddress)) {
                            StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                                    Server.URL + "user/update_profile_email_address.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e(TAG, response);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Toast.makeText(getActivity(),
                                                        jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                        Toast.LENGTH_SHORT).show();
                                                new UpdateProfile().execute();
                                                new MainActivity.NavigationDrawer().execute();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put(TAG_EMAIL_ADDRESS, emailAddress);
                                    params.put(LoginActivity.TAG_USER_ID, String.valueOf(userId));
                                    return params;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(stringRequest1, TAG_JSON_OBJ, getActivity());
                        }

                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });
//        mcvPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
//                final EditText input = new EditText(getActivity());
//                FrameLayout container = new FrameLayout(getActivity());
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
//                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
//                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
//                input.setSingleLine();
//                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                input.setLayoutParams(params);
//                container.addView(input);
//                alert.setTitle("Kata Sandi");
//                alert.setView(container);
//
//                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        final String password = input.getText().toString();
//                        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
//                                Server.URL + "user/update_profile_password.php",
//                                new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        Log.e(TAG, response);
//                                        try {
//                                            JSONObject jsonObject = new JSONObject(response);
//                                            Toast.makeText(getActivity(),
//                                                    jsonObject.getString(LoginActivity.TAG_MESSAGE),
//                                                    Toast.LENGTH_SHORT).show();
//                                            showProfile();
//
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                },
//                                new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }) {
//                            @Override
//                            protected Map<String, String> getParams() {
//                                Map<String, String> params = new HashMap<>();
//                                params.put(TAG_PASSWORD, password);
//                                params.put(LoginActivity.TAG_USER_ID, String.valueOf(userId));
//                                return params;
//                            }
//                        };
//                        AppController.getInstance().addToRequestQueue(stringRequest1, TAG_JSON_OBJ, getActivity());
//                    }
//                });
//                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//                alert.show();
//            }
//        });
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_photo_profile:
            case R.id.civ_photo_profile:
                Intent intentPhotoProfile = new Intent();
                intentPhotoProfile.setType("image/*");
                intentPhotoProfile.setAction(Intent.ACTION_GET_CONTENT);
                Objects.requireNonNull(getActivity()).startActivityForResult(Intent.createChooser(intentPhotoProfile, "Foto profil"), PICK_PHOTO_PROFILE_REQUEST);
                break;
            case R.id.fab_cover_profile:
            case R.id.iv_cover_profile:
                Intent intentCoverProfile = new Intent();
                intentCoverProfile.setType("image/*");
                intentCoverProfile.setAction(Intent.ACTION_GET_CONTENT);
                Objects.requireNonNull(getActivity()).startActivityForResult(Intent.createChooser(intentCoverProfile, "Kover profil"), PICK_COVER_PROFILE_REQUEST);
                break;
            default:

        }
    }

    public static class UpdateProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Tunggu sebentar ...");
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                tvFullName.setText(jsonObject.getString(TAG_FULL_NAME));
                                tvEmailAddress.setText(jsonObject.getString(TAG_EMAIL_ADDRESS));
                                String photo_profile = jsonObject.getString(TAG_PHOTO_PROFILE);
                                String cover_profile = jsonObject.getString(TAG_COVER_PROFILE);
                                Glide.with(context)
                                        .load(Server.URL + photo_profile)
                                        .asBitmap()
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(civPhotoProfile);
                                Glide.with(context)
                                        .load(Server.URL + cover_profile)
                                        .asBitmap()
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(ivCoverProfile);
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
                    params.put(LoginActivity.TAG_USER_ID, String.valueOf(userId));
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, context);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            hideProgressDialog();
        }
    }

    private boolean isEmailValid(@NonNull String emailAddress) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (emailAddress.matches(emailPattern)) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Alamat email tidak valid", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
