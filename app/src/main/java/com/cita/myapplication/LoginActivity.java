package com.cita.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextInputEditText tietEmailAddress, tietPassword;
    private TextInputLayout tilEmailAddress, tilPassword;
    private CheckBox cbRememberMe;
    private int success;

    private static final String URL = Server.URL + "user/login.php",
            TAG = LoginActivity.class.getSimpleName(),
            TAG_SUCCESS = "success",
            TAG_JSON_OBJ = "json_obj_req";

    public static final String TAG_USER_ID = "user_id",
            TAG_EMAIL_ADDRESS = "email_address", TAG_PASSWORD = "password", TAG_MESSAGE = "message";

    private SharedPreferences sharedPreferences;

    public static final String MY_SHARED_PREFERENCES = "my_shared_preferences";
    public static final String SESSION_STATUS = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Masuk Akun");

        cbRememberMe = findViewById(R.id.cb_remember_me);
        MaterialButton btnNext = findViewById(R.id.btn_next);
        TextView tvRegister = findViewById(R.id.tv_register);
        TextView tvForgotPassword = findViewById(R.id.tv_reset_password);
        tietEmailAddress = findViewById(R.id.tiet_email_address);
        tietPassword = findViewById(R.id.tiet_password);
        tilEmailAddress = findViewById(R.id.til_email_address);
        tilPassword = findViewById(R.id.til_password);

        tvRegister.setPaintFlags(tvRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Cek session login jika bernilai true maka lanjut ke UserMainActivity
        sharedPreferences = getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean session = sharedPreferences.getBoolean(SESSION_STATUS, false);
        int userId = sharedPreferences.getInt(TAG_USER_ID, 0);

        if (session) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(TAG_USER_ID, userId);
            finish();
            startActivity(intent);

        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Cek kolom tidak boleh kosong
                if (Objects.requireNonNull(tietEmailAddress.getText()).toString().isEmpty()) {
                    tilEmailAddress.setError("Kolom tidak boleh kosong");
                } else {
                    tilEmailAddress.setError(null);
                }

                if (Objects.requireNonNull(tietPassword.getText()).toString().isEmpty()) {
                    tilPassword.setError("Kolom tidak boleh kosong");
                } else {
                    tilPassword.setError(null);
                }

                textChangedListener(tietEmailAddress, tilEmailAddress);
                textChangedListener(tietPassword, tilPassword);

                String emailAddress = tietEmailAddress.getText().toString();
                String password = tietPassword.getText().toString();

                if (emailAddress.trim().length() > 0 && password.trim().length() > 0) {
                    checkLogin(emailAddress, password);
                }
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "On the way", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void textChangedListener(TextInputEditText textInputEditText, final TextInputLayout textInputLayout) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateEditText(editable, textInputLayout);
            }
        });

        textInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText((((EditText) view).getText()), textInputLayout);
                }
            }
        });
    }

    private void validateEditText(Editable editable, TextInputLayout textInputLayout) {
        if (TextUtils.isEmpty((editable))) {
            textInputLayout.setError("Kolom tidak boleh kosong");
        } else {
            textInputLayout.setError(null);
        }
    }

    private void checkLogin(final String emailAddress, final String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Masuk ...");
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Login Response: " + response);
                        hideProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getInt(TAG_SUCCESS);

                            // Check untuk error node di JSON
                            if (success == 1) {
                                int userId = jsonObject.getInt(TAG_USER_ID);

                                Toast.makeText(getApplicationContext(), jsonObject.getString(TAG_MESSAGE),
                                        Toast.LENGTH_LONG).show();

                                // Menyimpan login ke session
                                if (cbRememberMe.isChecked()) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(SESSION_STATUS, true);
                                    editor.putInt(TAG_USER_ID, userId);
                                    editor.apply();
                                }


                                // Memanggil UserMainActivity
                                Intent intent = new Intent(LoginActivity.this,
                                        MainActivity.class);
                                finish();
                                startActivity(intent);


                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString(TAG_MESSAGE),
                                        Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                        hideProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //Posting paramater ke login url
                Map<String, String> params = new HashMap<>();
                params.put(TAG_EMAIL_ADDRESS, emailAddress);
                params.put(TAG_PASSWORD, password);
                return params;
            }
        };

        //Menambahkan request ke daftar request
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getApplicationContext());
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
