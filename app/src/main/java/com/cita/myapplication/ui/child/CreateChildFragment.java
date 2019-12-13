package com.cita.myapplication.ui.child;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.R;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateChildFragment extends Fragment {

    private ProgressDialog progressDialog;

    private TextInputLayout tilChildName, tilGender, tilDateOfBirth;
    private TextInputEditText tietChildName, tietDateOfBirth;
    private AutoCompleteTextView dropdownGender;

    private final static String URL = Server.URL + "user/store_child.php", TAG = CreateChildFragment
            .class.getSimpleName(), TAG_MESSAGE = "message", TAG_JSON_OBJ = "json_obj_req",
            TAG_USER_ID = "user_id", TAG_CHILD_NAME = "child_name", TAG_GENDER = "gender",
            TAG_DATE_OF_BIRTH = "date_of_birth";

    private static int userId;

    public CreateChildFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_child, container, false);

        assert getArguments() != null;
        userId = CreateChildFragmentArgs.fromBundle(getArguments()).getUserId();

        tilChildName = root.findViewById(R.id.til_child_name);
        tilGender = root.findViewById(R.id.til_gender);
        tilDateOfBirth = root.findViewById(R.id.til_date_of_birth);
        tietChildName = root.findViewById(R.id.tiet_child_name);
        tietDateOfBirth = root.findViewById(R.id.tiet_date_of_birth);

        tietDateOfBirth.setKeyListener(null);

        String[] COUNTRIES = new String[]{"Laki - Laki", "Perempuan"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        Objects.requireNonNull(getContext()),
                        R.layout.dropdown_menu_popup_gender,
                        COUNTRIES);

        dropdownGender = root.findViewById(R.id.dropdown_gender);
        dropdownGender.setAdapter(adapter);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(tietDateOfBirth, myCalendar);
            }

        };
        tietDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(Objects.requireNonNull(getActivity()), date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - (DateUtils.YEAR_IN_MILLIS * 5));
                datePickerDialog.show();
            }
        });

        MaterialButton btnCancel = root.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        MaterialButton btnNext = root.findViewById(R.id.btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(tietChildName.getText()).toString().isEmpty()) {
                    tilChildName.setError("Kolom tidak boleh kosong");
                } else {
                    tilChildName.setError(null);
                }
                if (Objects.requireNonNull(dropdownGender.getText()).toString().isEmpty()) {
                    tilGender.setError("Kolom tidak boleh kosong");
                } else {
                    tilGender.setError(null);
                }
                if (Objects.requireNonNull(tietDateOfBirth.getText()).toString().isEmpty()) {
                    tilDateOfBirth.setError("Kolom tidak boleh kosong");
                } else {
                    tilDateOfBirth.setError(null);
                }

                textChangedListener(tietChildName, tilChildName);
//                textChangedListener(dropdownGender, tilGender);
                textChangedListener(tietDateOfBirth, tilDateOfBirth);

                String fullName = tietChildName.getText().toString();
                String gender = dropdownGender.getText().toString();
                String dateOfBirth = tietDateOfBirth.getText().toString();

                if (fullName.trim().length() > 0 && gender.trim().length() > 0
                        && dateOfBirth.trim().length() > 0) {
                    store(fullName, gender, dateOfBirth);

                }

            }
        });

        return root;
    }

    private void updateLabel(TextInputEditText textInputEditText, Calendar calendar) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        textInputEditText.setText(sdf.format(calendar.getTime()));
    }

    private void store(final String childName, final String gender, final String dateOfBirth) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memproses ...");
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response);
                        hideProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getActivity(),
                                    jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getActivity()).onBackPressed();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Store Error: " + error.getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(TAG_USER_ID, String.valueOf(userId));
                params.put(TAG_CHILD_NAME, childName);
                params.put(TAG_GENDER, gender);
                params.put(TAG_DATE_OF_BIRTH, dateOfBirth);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
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

    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
