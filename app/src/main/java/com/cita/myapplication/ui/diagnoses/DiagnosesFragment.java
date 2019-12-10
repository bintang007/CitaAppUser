package com.cita.myapplication.ui.diagnoses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.LoginActivity;
import com.cita.myapplication.R;
import com.cita.myapplication.adapter.DiagnosesAdapter;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.model.Diagnoses;
import com.cita.myapplication.utils.Server;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiagnosesFragment extends Fragment {

    private ProgressDialog progressDialog;

    private static final String URL = Server.URL + "user/diagnoses.php", TAG = DiagnosesFragment.class.getSimpleName(),
            TAG_USER_ID = "user_id", TAG_JSON_OBJ = "json_obj_req", TAG_MESSAGE = "message",
            TAG_SUCCESS = "success", TAG_DIAGNOSES_ID = "diagnoses_id", TAG_CHILD_NAME = "child_name",
            TAG_CHILD_AGE = "child_age", TAG_GENDER = "gender", TAG_WEIGHT_CHILD = "weight_child",
            TAG_HEIGHT_CHILD = "height_child", TAG_DIAGNOSES_RESULT = "diagnoses_result",
            TAG_DESCRIPTION = "description", TAG_DIAGNOSES_DATE = "diagnoses_date";
    private ArrayList<Diagnoses> diagnosesArrayList;
    private int userId;
    private TextView tvEmptyChild;
    private DiagnosesAdapter diagnosesAdapter;
    private FloatingActionButton fabCreateChild;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_diagnoses, container, false);
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(TAG_USER_ID, 0);
        tvEmptyChild = root.findViewById(R.id.tv_empty_child);

        getAllDiagnoses();

        RecyclerView rvDiagnoses = root.findViewById(R.id.rv_diagnoses);
        diagnosesAdapter = new DiagnosesAdapter(diagnosesArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDiagnoses.setLayoutManager(layoutManager);
        rvDiagnoses.setAdapter(diagnosesAdapter);


        fabCreateChild = root.findViewById(R.id.fab_create_diagnoses);
        fabCreateChild.setSupportBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fabCreateChild.setSupportImageTintList(getResources().getColorStateList(R.color.colorWhite));
        fabCreateChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiagnosesFragmentDirections.ActionNavDiagnosesToNavCreateDiagnoses actionNavDiagnosesToNavCreateDiagnoses =
                        DiagnosesFragmentDirections.actionNavDiagnosesToNavCreateDiagnoses();
                actionNavDiagnosesToNavCreateDiagnoses.setUserId(userId);
                Navigation.findNavController(view).navigate(actionNavDiagnosesToNavCreateDiagnoses);
            }
        });
        return root;
    }

    private void getAllDiagnoses() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu sebentar ...");
        showProgressDialog();

        diagnosesArrayList = new ArrayList<>();

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
                                JSONArray jsonArrayDiagnosesId = jsonObject.getJSONArray(TAG_DIAGNOSES_ID);
                                JSONArray jsonArrayChildName = jsonObject.getJSONArray(TAG_CHILD_NAME);
                                JSONArray jsonArrayDiagnosesResult = jsonObject.getJSONArray(TAG_DIAGNOSES_RESULT);
                                JSONArray jsonArrayDiagnosesDate = jsonObject.getJSONArray(TAG_DIAGNOSES_DATE);
                                for (int i = 0; i < jsonArrayChildName.length(); i++) {
                                    Diagnoses diagnoses = new Diagnoses();
                                    diagnoses.setDiagnosesId(jsonArrayDiagnosesId.getInt(i));
                                    diagnoses.setChildName(jsonArrayChildName.getString(i));
                                    diagnoses.setDiagnosesResult(jsonArrayDiagnosesResult.getString(i));
                                    diagnoses.setDiagnosesDate(jsonArrayDiagnosesDate.getString(i));
                                    diagnosesArrayList.add(diagnoses);
                                    diagnosesAdapter.setItems(diagnosesArrayList);
                                }
                            } else if (success == 0){
                                tvEmptyChild.setText(jsonObject.getString(TAG_MESSAGE));
                            } else {
                                tvEmptyChild.setText(jsonObject.getString(TAG_MESSAGE));
                                fabCreateChild.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(getActivity(), "Anda belum memiliki data anak", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

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
                params.put(TAG_USER_ID, String.valueOf(userId));
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
