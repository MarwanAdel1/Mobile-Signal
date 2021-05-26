package com.example.mobile_signalh3.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.ui.fragments.CompassFragment;
import com.example.mobile_signalh3.ui.fragments.SignalMapFragment;
import com.example.mobile_signalh3.ui.fragments.StatisticsFragments;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends BaseActivity {
    private BottomNavigationView mBottomNavigationView;
    private CoordinatorLayout parentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(R.id.mycoordinator);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        prepareBottomMenu();
        mBottomNavigationView.setSelectedItemId(R.id.statistics_item);

        checkPermissions();

        isConnected();
    }

    private void prepareBottomMenu() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                int id = item.getItemId();
                switch (id) {
                    case R.id.statistics_item:
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new StatisticsFragments()).commit();
                        return true;
                    case R.id.coverage_item:
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new SignalMapFragment()).commit();
                        return true;
                    case R.id.cell_location_item:
                        if (StatisticsFragments.MY_LOCATION != null && StatisticsFragments.TARGET_LOCATION != null) {
                            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new CompassFragment()).commit();
                            return true;
                        }
                        else {
                            Snackbar.make(parentLayout,"Calibrating... Just Wait a Second",Snackbar.LENGTH_SHORT).show();
                        }
                        return false;
                    default:
                        return false;
                }
            }
        });
    }
}
