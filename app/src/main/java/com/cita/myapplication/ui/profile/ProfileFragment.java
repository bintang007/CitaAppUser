package com.cita.myapplication.ui.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.LoginActivity;
import com.cita.myapplication.R;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.utils.Server;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final String TAG_JSON_OBJ = "json_obj_req";
    private static final String TAG_FULL_NAME = "full_name";
    private static final String TAG_EMAIL_ADDRESS = "email_address";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String URL = Server.URL + "user/read_profile.php";
    private final static String TAG_USER_ID = "user_id";


    private int userId;
    private TextView tvFullName, tvEmailAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(TAG_USER_ID, 0);

        MaterialCardView mcvFullName = root.findViewById(R.id.mcv_full_name);
        MaterialCardView mcvEmailAddress = root.findViewById(R.id.mcv_email_address);
        MaterialCardView mcvPassword = root.findViewById(R.id.mcv_password);
        tvFullName = root.findViewById(R.id.tv_full_name);
        tvEmailAddress = root.findViewById(R.id.tv_email_address);

        showProfile();

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
                                            showProfile();

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
                                            showProfile();

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
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });
        mcvPassword.setOnClickListener(new View.OnClickListener() {
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
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                input.setLayoutParams(params);
                container.addView(input);
                alert.setTitle("Kata Sandi");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String password = input.getText().toString();
                        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                                Server.URL + "user/update_profile_password.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e(TAG, response);
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Toast.makeText(getActivity(),
                                                    jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                    Toast.LENGTH_SHORT).show();
                                            showProfile();

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
                                params.put(TAG_PASSWORD, password);
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
        return root;
    }

    private void showProfile() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            tvFullName.setText(jsonObject.getString(TAG_FULL_NAME));
                            tvEmailAddress.setText(jsonObject.getString(TAG_EMAIL_ADDRESS));

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
                params.put(LoginActivity.TAG_USER_ID, String.valueOf(userId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());

    }
}
