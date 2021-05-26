package com.example.mobile_signalh3.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.ui.CellTowersMaps;

public class CompassFragment extends BaseFragment implements SensorEventListener, View.OnClickListener {
    private TextView mOperatorName;
    private ImageView mImage;
    private Button mCellTowerButton;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compass,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOperatorName = (TextView) view.findViewById(R.id.operator_name_compass);
        mOperatorName.setText(StatisticsFragments.simOperatorName);
        mImage = (ImageView) view.findViewById(R.id.compass);
        mCellTowerButton = (Button) view.findViewById(R.id.cell_towers_map_bt);
        mCellTowerButton.setOnClickListener(this);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);

        float bearing = StatisticsFragments.MY_LOCATION.bearingTo(StatisticsFragments.TARGET_LOCATION);
//bearTo = The angle from true north to the destination location from the point we're your currently standing
        //The angle that you've rotated your phone from true north
        GeomagneticField geoField = new GeomagneticField(
                Double.valueOf( StatisticsFragments.MY_LOCATION.getLatitude() ).floatValue(),
                Double.valueOf( StatisticsFragments.MY_LOCATION.getLongitude() ).floatValue(),
                Double.valueOf( StatisticsFragments.MY_LOCATION.getAltitude() ).floatValue(),
                System.currentTimeMillis());

        degree -= geoField.getDeclination();



        if (bearing < 0) {
            bearing = bearing + 360;
            //bearTo = -100 + 360  = 260;
        }

//This is where we choose to point it
        float direction = bearing - degree;

// If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                direction,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        mImage.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cell_towers_map_bt){
            Intent intent = new Intent(getActivity().getApplication(), CellTowersMaps.class);
            startActivity(intent);
        }
    }
}