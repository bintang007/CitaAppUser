package com.cita.myapplication.ui.diagnoses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.R;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.model.Child;
import com.cita.myapplication.utils.Server;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResultDiagnosesFragment extends Fragment {
    private TextView tvChilcName, tvGender, tvChildAge, tvWeightChild, tvHeightChild, tvDiagnosesResult,
            tvDescription, tvDiagnosesDate;

    private static final String TAG = ResultDiagnosesFragment.class.getSimpleName(),
            URL = Server.URL + "user/result_diagnoses.php", TAG_DIAGNOSES_ID = "diagnoses_id",
            TAG_JSON_OBJ = "json_obj_req", TAG_CHILD_NAME = "child_name",
            TAG_CHILD_AGE = "child_age", TAG_WEIGHT_CHILD = "weight_child", TAG_HEIGHT_CHILD = "height_child",
            TAG_DIAGNOSES_RESULT = "diagnoses_result", TAG_DESCRIPTION = "description", TAG_DIAGNOSES_DATE = "diagnoses_date",
            TAG_GENDER = "gender";

    private static int diagnosesId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_result_diagnoses, container, false);

        assert getArguments() != null;
        diagnosesId = ResultDiagnosesFragmentArgs.fromBundle(getArguments()).getDiagnosesId();

        tvChilcName = root.findViewById(R.id.tv_child_name);
        tvGender = root.findViewById(R.id.tv_gender);
        tvChildAge = root.findViewById(R.id.tv_child_age);
        tvWeightChild = root.findViewById(R.id.tv_weight_child);
        tvHeightChild = root.findViewById(R.id.tv_height_child);
        tvDiagnosesResult = root.findViewById(R.id.tv_diagnoses_result);
        tvDescription = root.findViewById(R.id.tv_description);
        tvDiagnosesDate = root.findViewById(R.id.tv_diagnoses_date);

        diagnosesResult();

        MaterialButton btnBack = root.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        return root;
    }

    private void diagnosesResult() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            tvChilcName.setText(jsonObject.getString(TAG_CHILD_NAME));
                            tvGender.setText(jsonObject.getString(TAG_GENDER));
                            tvChildAge.setText(jsonObject.getString(TAG_CHILD_AGE));
                            tvWeightChild.setText(jsonObject.getInt(TAG_WEIGHT_CHILD));
                            tvHeightChild.setText(jsonObject.getString(TAG_HEIGHT_CHILD));
                            tvDiagnosesResult.setText(jsonObject.getString(TAG_DIAGNOSES_RESULT));
                            tvDescription.setText(jsonObject.getString(TAG_DESCRIPTION));
                            tvDiagnosesDate.setText(jsonObject.getString(TAG_DIAGNOSES_DATE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
}

