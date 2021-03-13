package com.sembiyan.madhaagencies;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    AlertDialog alertDialogProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showProgress() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_loading, null);
        adb.setView(view);
        alertDialogProgressBar = adb.create();
        alertDialogProgressBar.setCancelable(true);
        alertDialogProgressBar.show();
    }

    public void hideProgress() {
        if (alertDialogProgressBar == null) {
            return;
        }
        hideSoftKeyboard();
        alertDialogProgressBar.hide();

    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}