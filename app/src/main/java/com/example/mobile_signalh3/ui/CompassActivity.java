package com.example.mobile_signalh3.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.pojo.SignalData;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private Location myLocation = new Location("A");
    private Location target = new Location("B");


    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        image = (ImageView) findViewById(R.id.compass);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        target.setLatitude(31.319123);
        target.setLongitude(30.058744);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (location != null ) {
                    myLocation=location;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this); // to stop the listener and save battery
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        GeomagneticField geoField = new GeomagneticField( Double
                .valueOf( myLocation.getLatitude() ).floatValue(), Double
                .valueOf( myLocation.getLongitude() ).floatValue(),
                Double.valueOf( myLocation.getAltitude() ).floatValue(),
                System.currentTimeMillis() );

        degree += geoField.getDeclination();

        float bearing = myLocation.bearingTo(target);
        degree = (bearing - degree) * -1;
        degree = normalizeDegree(degree);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }
}

