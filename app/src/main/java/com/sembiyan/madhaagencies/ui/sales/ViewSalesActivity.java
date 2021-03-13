package com.sembiyan.madhaagencies.ui.sales;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sembiyan.madhaagencies.BaseActivity;
import com.sembiyan.madhaagencies.R;
import com.sembiyan.madhaagencies.utilities.AppController;
import com.sembiyan.madhaagencies.utilities.Constants;
import com.sembiyan.madhaagencies.utilities.Utils;
import com.sembiyan.madhaagencies.utilities.WebserviceEndpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewSalesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sales);
        getSalesList();
    }


    public void getSalesList() {
        showProgress();
        String strUrl = WebserviceEndpoints.READ_RECORDS_I_SEARCH;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, strUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                try {
                    if (Utils.validateResponseBody(response) != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.optInt("code");
                        if (code != 500) {
                            JSONArray jsonArray = jsonObject.optJSONArray("data");
                        } else {
                            Toast.makeText(ViewSalesActivity.this, Utils.getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(AddSalesActivity.class.getSimpleName(), response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //set authorization token in with the type via Bearer token
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + Utils.getLoginDetails(ViewSalesActivity.this, Constants.ACCESS_TOKEN));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("model", Constants.SALES_ORDER);
                params.put("limit", Constants.LIMITS);
                params.put("fields", getFields());
                //params.put("domain", "[[\"rfid_number\",\"=\"," + "\"" + strDomain + "\"" + "]]");
                return params;
            }
        };

        int socketTimeout = 300000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String getFields() {
        return "[\n" +
                "      \"message_needaction\",\n" +
                "      \"name\",\n" +
                "      \"confirmation_date\",\n" +
                "      \"commitment_date\",\n" +
                "      \"expected_date\",\n" +
                "      \"partner_id\",\n" +
                "      \"user_id\",\n" +
                "      \"amount_total\",\n" +
                "      \"currency_id\",\n" +
                "      \"invoice_status\",\n" +
                "      \"state\"\n" +
                "    ]";
    }

}