package com.cita.myapplication.ui.diagnoses;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.R;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.model.Child;
import com.cita.myapplication.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateDiagnosesFragment extends Fragment {
    private ProgressDialog progressDialog;

    private TextInputLayout tilChildName, tilWeightChild, tilHeightChild;

    private TextInputEditText tietWeightChild, tietHeightChild;

    private AutoCompleteTextView dropdownChildName;

    private ArrayList<Child> children;

    private static final String URL = Server.URL + "user/store_diagnoses.php";
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_CHILD_ID = "child_id";
    private static final String TAG_CHILD_NAME = "child_name";
    private static final String TAG = CreateDiagnosesFragment.class.getSimpleName();
    private static final String TAG_JSON_OBJ = "json_obj_req";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DIAGNOSES_ID = "diagnoses_id";
    private static final String TAG_WEIGHT_CHILD = "weight_child";
    private static final String TAG_HEIGHT_CHILD = "height_child";

    private static int userId;
    private static int childId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_diagnoses, container, false);

        assert getArguments() != null;
        userId = CreateDiagnosesFragmentArgs.fromBundle(getArguments()).getUserId();

        tilChildName = root.findViewById(R.id.til_child_name);
        tilWeightChild = root.findViewById(R.id.til_weight_child);
        tilHeightChild = root.findViewById(R.id.til_height_child);
        dropdownChildName = root.findViewById(R.id.dropdown_child_name);
        tietWeightChild = root.findViewById(R.id.tiet_weight_child);
        tietHeightChild = root.findViewById(R.id.tiet_height_child);

        initDropdownChildName();

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
                if (Objects.requireNonNull(dropdownChildName.getText()).toString().isEmpty()) {
                    tilChildName.setError("Kolom tidak boleh kosong");
                } else {
                    tilChildName.setError(null);
                }
                if (Objects.requireNonNull(tietWeightChild.getText()).toString().isEmpty()) {
                    tilWeightChild.setError("Kolom tidak boleh kosong");
                } else {
                    tilWeightChild.setError(null);
                }
                if (Objects.requireNonNull(tietHeightChild.getText()).toString().isEmpty()) {
                    tilHeightChild.setError("Kolom tidak boleh kosong");
                } else {
                    tilHeightChild.setError(null);
                }

                textChangedListener(tietWeightChild, tilWeightChild);
                textChangedListener(tietHeightChild, tilHeightChild);

                String weightChild = tietWeightChild.getText().toString();
                String heightChild = tietHeightChild.getText().toString();


                if (weightChild.trim().length() > 0 && heightChild.trim().length() > 0 && childId > 0) {
                    double weightChildDouble = Double.parseDouble(tietWeightChild.getText().toString());
                    double heightChildDouble = Double.parseDouble(tietHeightChild.getText().toString());
                    store(view, childId, weightChildDouble, heightChildDouble);

                }

            }
        });

        return root;
    }

    private void store(final View view, final int childId, final double weightChild, final double heightChild) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memproses ...");
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e(TAG, "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt(TAG_SUCCESS);
                            if (success == 1) {
                                Toast.makeText(getActivity(),
                                        jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                CreateDiagnosesFragmentDirections.ActionNavCreateDiagnosesToNavResultDiagnoses actionNavCreateDiagnosesToNavResultDiagnoses =
                                        CreateDiagnosesFragmentDirections.actionNavCreateDiagnosesToNavResultDiagnoses();
                                actionNavCreateDiagnosesToNavResultDiagnoses.setDiagnosesId(jsonObject.getInt(TAG_DIAGNOSES_ID));
                                Navigation.findNavController(view).navigate(actionNavCreateDiagnosesToNavResultDiagnoses);

                            } else {
                                Toast.makeText(getActivity(),
                                        jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
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
                params.put(TAG_CHILD_ID, String.valueOf(childId));
                params.put(TAG_WEIGHT_CHILD, String.valueOf(weightChild));
                params.put(TAG_HEIGHT_CHILD, String.valueOf(heightChild));
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
    }

    //
    private void initDropdownChildName() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu sebentar ...");
        showProgressDialog();

        children = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.URL + "user/child_name.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e(TAG, "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArrayChildId = jsonObject.getJSONArray(TAG_CHILD_ID);
                            JSONArray jsonArrayChildName = jsonObject.getJSONArray(TAG_CHILD_NAME);

                            for (int i = 0; i < jsonArrayChildId.length(); i++) {
                                Child child = new Child();
                                child.setChildId(jsonArrayChildId.getInt(i));
                                child.setChildName(jsonArrayChildName.getString(i));
                                children.add(child);
                            }

                            String[] listChildName = new String[children.size()];
                            final int[] listChildId = new int[children.size()];
                            for (int i = 0; i < children.size(); i++) {
                                listChildId[i] = children.get(i).getChildId();
                                listChildName[i] = children.get(i).getChildName();
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            Objects.requireNonNull(getContext()),
                                            R.layout.dropdown_menu_popup_child_name,
                                            listChildName);

                            dropdownChildName.setAdapter(adapter);
                            dropdownChildName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    childId = listChildId[i];
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(TAG_USER_ID, String.valueOf(userId));
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
