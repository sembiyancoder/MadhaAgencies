package com.sembiyan.madhaagencies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sembiyan.madhaagencies.BaseActivity;
import com.sembiyan.madhaagencies.R;
import com.sembiyan.madhaagencies.network.AbstractGrant;
import com.sembiyan.madhaagencies.network.HttpRequest;
import com.sembiyan.madhaagencies.network.Password;
import com.sembiyan.madhaagencies.network.RequestFinishListener;
import com.sembiyan.madhaagencies.ui.home.HomeActivity;
import com.sembiyan.madhaagencies.utilities.Constants;
import com.sembiyan.madhaagencies.utilities.PreferencesManager;
import com.sembiyan.madhaagencies.utilities.Utils;
import com.sembiyan.madhaagencies.utilities.WebserviceEndpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private MaterialButton mLoginButton;
    private TextInputEditText mUsernameEditText, mPasswordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inflateXMLView();
    }

    private void inflateXMLView() {
        mUsernameEditText = findViewById(R.id.edit_username);
        mPasswordEditText = findViewById(R.id.edit_password);
        mLoginButton = findViewById(R.id.btn_login);

        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                oAuthAuthenticationService(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString());
                break;
            default:
                break;
        }
    }


    private void oAuthAuthenticationService(String username, String password) {
        //url parameters in authentication
        HashMap<String, String> urlParameters = new HashMap<>();
        urlParameters.put("username", username);
        urlParameters.put("password", password);
        urlParameters.put("client_id", Constants.CLIENT_ID);
        urlParameters.put("client_secret", Constants.CLIENT_SECRET);
        AbstractGrant grant = new Password();

        showProgress();

        //set url parameters in body
        urlParameters = grant.prepareRequestParameters(urlParameters);
        String strAuthenticationURL = WebserviceEndpoints.LOGIN_AUTHENTICATION_URL;

        new HttpRequest(strAuthenticationURL, "POST", urlParameters, new RequestFinishListener() {
            @Override
            public void onFinishRequest(String response) {
                hideProgress();
                Log.d(LoginActivity.class.getSimpleName(), response);
                try {
                    if (Utils.validateResponseBody(response) != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.optInt("code");
                        if (code != 500) {
                            PreferencesManager manager = PreferencesManager.getInstance(LoginActivity.this);
                            manager.setStringValue(Constants.LOGIN_BASIC_DETAILS, jsonObject.toString());
                            startHomeIntent();
                        } else {
                            Toast.makeText(LoginActivity.this, Utils.getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeRequest();
    }

    private void startHomeIntent() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}