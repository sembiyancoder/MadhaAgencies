package com.sembiyan.madhaagencies.ui.sales;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.sembiyan.madhaagencies.ui.customer.AddCustomerActivity;
import com.sembiyan.madhaagencies.utilities.AppController;
import com.sembiyan.madhaagencies.utilities.Constants;
import com.sembiyan.madhaagencies.utilities.PreferencesManager;
import com.sembiyan.madhaagencies.utilities.Utils;
import com.sembiyan.madhaagencies.utilities.WebserviceEndpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSalesActivity extends BaseActivity {

    private PreferencesManager mPreferencesManager;
    private ArrayList<String> mCustomerNameList = new ArrayList<String>();
    private ArrayList<String> mCustomerIdList = new ArrayList<String>();
    private ArrayList<String> mProductNameList = new ArrayList<String>();
    private ArrayList<String> mProductIdList = new ArrayList<String>();
    private AutoCompleteTextView mAutoCompleteTextView, mProductAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales);
        mPreferencesManager = PreferencesManager.getInstance(this);
        getBasicDetails();
        inflateXMLView();
    }

    private void inflateXMLView() {
        mAutoCompleteTextView = findViewById(R.id.auto_complete_textView);
        mProductAutoCompleteTextView = findViewById(R.id.auto_complete_product_textView);

        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString();
                if (name != null && !name.isEmpty() && name.equalsIgnoreCase("Create New Customer")) {
                    Intent intent = new Intent(AddSalesActivity.this, AddCustomerActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mProductAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPriceList();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void prepareCustomerMenuList(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject object = jsonArray.optJSONObject(index);
                JSONArray partner_id = object.optJSONArray("partner_id");
                String name = partner_id.optString(1);
                String id = partner_id.optString(0);
                mCustomerNameList.add(name);
                mCustomerIdList.add(id);
            }
            mCustomerNameList.add("Create New Customer");
            mCustomerIdList.add("0");

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, mCustomerNameList);
        mAutoCompleteTextView.setAdapter(adapter);

        getProductList();
    }


    private void prepareProductList(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject object = jsonArray.optJSONObject(index);
                String name = object.optString("display_name");
                String id = object.optString("id");
                mProductNameList.add(name);
                mProductIdList.add(id);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, mProductNameList);
        mProductAutoCompleteTextView.setAdapter(adapter);
    }


    /*
     * Get customer name list
     * */

    public void getBasicDetails() {

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
                            prepareCustomerMenuList(jsonArray);
                        } else {
                            Toast.makeText(AddSalesActivity.this, Utils.getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
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
                params.put("Authorization", "Bearer " + Utils.getLoginDetails(AddSalesActivity.this, Constants.ACCESS_TOKEN));
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
        return "[\"message_needaction\",\"name\",\"date_order\",\"commitment_date\",\"expected_date\",\"partner_id\",\"user_id\",\"amount_total\",\"currency_id\",\"state\"]";
    }


    /*
     * Get product list
     * */
    public void getProductList() {
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
                            prepareProductList(jsonArray);
                        } else {
                            Toast.makeText(AddSalesActivity.this, Utils.getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
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

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //set authorization token in with the type via Bearer token
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + Utils.getLoginDetails(AddSalesActivity.this, Constants.ACCESS_TOKEN));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("model", Constants.PRODUCT_LIST);
                params.put("limit", Constants.LIMITS);
                params.put("fields", getProductFields());
                //params.put("domain", "[[\"rfid_number\",\"=\"," + "\"" + strDomain + "\"" + "]]");
                return params;
            }
        };

        int socketTimeout = 300000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private String getProductFields() {
        return "[\"display_name\"]";
    }


    public void getPriceList() {

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

                        } else {
                            Toast.makeText(AddSalesActivity.this, Utils.getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
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
                params.put("Authorization", "Bearer " + Utils.getLoginDetails(AddSalesActivity.this, Constants.ACCESS_TOKEN));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("model", Constants.SALES_ORDER);
                params.put("limit", Constants.LIMITS);
                //params.put("fields", getFields());
               // params.put("domain", "[[\"partner_id\",\"=\"," + "\"" + 19 + "\"" + "]]");
                return params;
            }
        };

        int socketTimeout = 300000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}