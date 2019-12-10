package com.cita.myapplication.ui.diagnoses;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ShowDiagnosesFragment extends Fragment {
    private static final Locale LOCALE_ID = new Locale("in", "ID");
    private ProgressDialog progressDialog;
    private static final String URL = Server.URL + "user/show_diagnoses.php";
    private static final String TAG = ShowDiagnosesFragment.class.getSimpleName();
    private static final String TAG_JSON_OBJ = "json_obj_req";
    private static final String TAG_DIAGNOSES_ID = "diagnoses_id";
    private static final String TAG_CHILD_NAME = "child_name";
    private static final String TAG_CHILD_AGE = "child_age";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_WEIGHT_CHILD = "weight_child";
    private static final String TAG_HEIGHT_CHILD = "height_child";
    private static final String TAG_DIAGNOSES_RESULT = "diagnoses_result";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DIAGNOSES_DATE = "diagnoses_date";

    private TextView tvChildName, tvGender, tvChildAge, tvWeightChild, tvHeightChild,
            tvDiagnoseResult, tvDescription, tvDiagnosesDate;

    private int diagnosesId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_show_diagnoses, container, false);

        assert getArguments() != null;
        diagnosesId = ShowDiagnosesFragmentArgs.fromBundle(getArguments()).getDiagnosesId();

        tvChildName = root.findViewById(R.id.tv_child_name);
        tvGender = root.findViewById(R.id.tv_gender);
        tvChildAge = root.findViewById(R.id.tv_child_age);
        tvWeightChild = root.findViewById(R.id.tv_weight_child);
        tvHeightChild = root.findViewById(R.id.tv_height_child);
        tvDiagnoseResult = root.findViewById(R.id.tv_diagnoses_result);
        tvDescription = root.findViewById(R.id.tv_description);
        tvDiagnosesDate = root.findViewById(R.id.tv_diagnoses_date);
        MaterialButton btnBack = root.findViewById(R.id.btn_back);

        setTextTvDiagnoses();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        return root;
    }


    private void setTextTvDiagnoses() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu sebentar ...");
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e(TAG, "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            tvChildName.setText(jsonObject.getString(TAG_CHILD_NAME));
                            tvGender.setText(jsonObject.getString(TAG_GENDER));
                            tvChildAge.setText(jsonObject.getString(TAG_CHILD_AGE));
                            tvWeightChild.setText(jsonObject.getString(TAG_WEIGHT_CHILD));
                            tvHeightChild.setText(jsonObject.getString(TAG_HEIGHT_CHILD));
                            tvDiagnoseResult.setText(jsonObject.getString(TAG_DIAGNOSES_RESULT));
                            tvDescription.setText(jsonObject.getString(TAG_DESCRIPTION));
                            String diagnosesDateTimestamp = jsonObject.getString(TAG_DIAGNOSES_DATE);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", LOCALE_ID);

                            try {
                                Date dateTime = formatter.parse(diagnosesDateTimestamp);
                                formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", LOCALE_ID);
                                assert dateTime != null;
                                String diagnosesDate = formatter.format(dateTime);
                                tvDiagnosesDate.setText(diagnosesDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


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
                params.put(TAG_DIAGNOSES_ID, String.valueOf(diagnosesId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());

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
