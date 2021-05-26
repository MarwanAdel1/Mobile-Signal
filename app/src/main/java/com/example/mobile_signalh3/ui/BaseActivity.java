package com.example.mobile_signalh3.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;

public class BaseActivity extends AppCompatActivity {
    private ConnectivityManager connect;
    private NetworkInfo networkInfo;

    private static final int RequestPermissionCode = 1;

    @Override
    public void onBackPressed() {
        onBackButtonPressed();
    }

    public void onBackButtonPressed() {
        new AlertDialog
                .Builder(this)
                .setTitle("Exit")
                .setMessage("Are you want to exit?")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                        System.exit(0);
                    }
                }).setNegativeButton("No", null)
                .show();
    }

    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ||ActivityCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
        ||ActivityCompat.checkSelfPermission(getApplicationContext(), INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
    }

    public boolean isConnected() {
        connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connect.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        ACCESS_FINE_LOCATION,
                        READ_PHONE_STATE,
                        ACCESS_COARSE_LOCATION,
                        INTERNET
                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean FineLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean Phone_StatePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean Coarse_LocationPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean InternetPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    if (FineLocationPermission && Phone_StatePermission && Coarse_LocationPermission && InternetPermission) {
                        Toast.makeText(this, "Permissions Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permissions Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }
}
