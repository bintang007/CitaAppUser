package com.cita.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextInputEditText tietFullName, tietEmailAddress, tietPassword, tietConfirmPassword;
    private TextInputLayout tilFullName, tilEmailAddress, tilPassword, tilConfirmPassword;

    private static final String URL = Server.URL + "user/register.php",
            TAG = RegisterActivity.class.getSimpleName(), TAG_FULL_NAME = "full_name",
            TAG_EMAIL_ADDRESS = "email_address", TAG_PASSWORD = "password", TAG_SUCCESS = "success",
            TAG_MESSAGE = "message", TAG_JSON_OBJ = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Daftar Akun");

        TextView tvLogin = findViewById(R.id.tv_login);
        tvLogin.setPaintFlags(tvLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView tvForgotPassword = findViewById(R.id.tv_reset_password);
        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "On the way", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialButton btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MaterialButton btnNext = findViewById(R.id.btn_next);
        tietFullName = findViewById(R.id.tiet_full_name);
        tietEmailAddress = findViewById(R.id.tiet_email_address);
        tietPassword = findViewById(R.id.tiet_password);
        tietConfirmPassword = findViewById(R.id.tiet_confirm_password);
        tilFullName = findViewById(R.id.til_full_name);
        tilEmailAddress = findViewById(R.id.til_email_address);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Cek kolom tidak boleh kosong

                if (Objects.requireNonNull(tietFullName.getText()).toString().isEmpty()) {
                    tilFullName.setError("Kolom tidak boleh kosong");
                } else {
                    tilFullName.setError(null);
                }

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

                if (Objects.requireNonNull(tietConfirmPassword.getText()).toString().isEmpty()) {
                    tilConfirmPassword.setError("Kolom tidak boleh kosong");
                } else {
                    tilConfirmPassword.setError(null);
                }

                textChangedListener(tietFullName, tilFullName);
                textChangedListener(tietEmailAddress, tilEmailAddress);
                textChangedListener(tietPassword, tilPassword);
                textChangedListener(tietConfirmPassword, tilConfirmPassword);

                String fullName = tietFullName.getText().toString();
                String emailAddress = tietEmailAddress.getText().toString();
                String password = tietPassword.getText().toString();
                String confirmPassword = tietConfirmPassword.getText().toString();


                if (emailAddress.trim().length() > 0) {
                    if (isEmailValid(emailAddress, tilEmailAddress)) {
                        if (fullName.trim().length() > 0 && password.trim().length() > 0
                                && confirmPassword.trim().length() > 0) {
                            if (password.equalsIgnoreCase(confirmPassword)) {
                                checkRegister(fullName, emailAddress, password);
                            } else {
                                tilPassword.setError("Kolom Password dan Ulangi Password harus sama");
                                tilConfirmPassword.setError("Kolom Password dan Ulangi Password harus sama");
                            }
                        }


                    }
                }


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

    private void checkRegister(final String fullName, final String emailAddress, final String password) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Daftar ...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt(TAG_SUCCESS);

                    // Check untuk error node di JSON
                    if (success == 1) {
                        Log.e("Successfully Register!", jsonObject.toString());
                        Toast.makeText(getApplicationContext(), jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting paramater ke register url
                Map<String, String> params = new HashMap<>();
                params.put(TAG_FULL_NAME, fullName);
                params.put(TAG_EMAIL_ADDRESS, emailAddress);
                params.put(TAG_PASSWORD, password);
                return params;
            }
        };

        // Menambahkan request ke daftar request
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getApplicationContext());
    }

    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    private boolean isEmailValid(@NonNull String emailAddress, TextInputLayout textInputLayout) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (emailAddress.matches(emailPattern)) {
            textInputLayout.setError(null);
            return true;
        } else {
            textInputLayout.setError("Alamat email tidak valid");
            return false;
        }
    }
}
