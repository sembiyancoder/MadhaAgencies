package com.sembiyan.madhaagencies.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sembiyan.madhaagencies.R;
import com.sembiyan.madhaagencies.adapter.HomeAdapter;
import com.sembiyan.madhaagencies.ui.sales.AddSalesActivity;
import com.sembiyan.madhaagencies.ui.sales.ViewSalesActivity;
import com.sembiyan.madhaagencies.utilities.Constants;
import com.sembiyan.madhaagencies.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements HomeAdapter.onItemSelectedListener {

    private HomeAdapter mDashboardAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inflateXMLView();
        setAdapterData();
    }

    private void inflateXMLView() {
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    private void setAdapterData() {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(Utils.readJSONFromAsset(this, Constants.HOME_MENU_FILE_NAME));
            mDashboardAdapter = new HomeAdapter(this, jsonArray, this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setAdapter(mDashboardAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(JSONObject jsonObject) {
        Intent intent = null;
        if (jsonObject.optString("name").equals("Add Sales")) {
            intent = new Intent(this, AddSalesActivity.class);
        }{
            intent = new Intent(this, ViewSalesActivity.class);
        }
        startActivity(intent);
    }
}