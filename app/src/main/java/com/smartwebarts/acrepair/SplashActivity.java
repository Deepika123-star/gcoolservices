package com.smartwebarts.acrepair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smartwebarts.acrepair.dashboard.DashboardActivity;
import com.smartwebarts.acrepair.intro.Session;
import com.smartwebarts.acrepair.intro.WelcomeActivity;
import com.smartwebarts.acrepair.shared_preference.AppSharedPreferences;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2500l;
    private static final int REQUEST_LOCATION = 202;
    private ImageView mAppLogoView;
    private TextView description;
    private String user;

    Animation topAnim, bottomAnim;

    private Handler mDelayHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
//
//            user = preferences.getLoginDetails();
//            if (user == null || user.isEmpty()) {
//                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                finish();
//                overridePendingTransition(R.anim.enter, R.anim.exit);
//                startActivity(intent);
//            }
//            else {
//                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
//                finish();
//                overridePendingTransition(R.anim.enter, R.anim.exit);
//                startActivity(intent);
//            }

            startNextActivity();
        }
    };
    private GoogleApiClient googleApiClient;

    private void startNextActivity() {


        // Checking for first time launch - before calling setContentView()
        Session session = new Session(this);
        if (session.isFirstTimeLaunch()) {
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
            startActivity(intent);
        }
        else {

            checkPermission();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        FirebaseMessaging.getInstance().subscribeToTopic("all");
        super.onCreate(savedInstanceState);

        //Remove Title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        init();

        mDelayHandler.postDelayed(runnable, SPLASH_DELAY);

        startAnimation();
    }

    private void init() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animator);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animator);
        mAppLogoView = findViewById(R.id.logo);
        description = findViewById(R.id.description);
    }

    private void startAnimation() {
        mAppLogoView.startAnimation(topAnim);
        description.startAnimation(bottomAnim);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    private void checkPermission() {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // You can use the API that requires the permission.
            turnongps();
        }  else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    private void turnongps() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
//            Toast.makeText(getApplicationContext(),"Gps already enabled",Toast.LENGTH_SHORT).show();
            //getActivity().finish();
            next();
        }
        // Todo Location Already on  ... end

        if(!hasGPSDevice(this)){
//            Toast.makeText(getApplicationContext(),"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            // Log.e("Neha","Gps already enabled");
//            Toast.makeText(getApplicationContext(),"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            // Log.e("Neha","Gps already enabled");
//            Toast.makeText(getApplicationContext(),"Gps already enabled",Toast.LENGTH_SHORT).show();
        }
    }

    public void next(){
        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());

        user = preferences.getLoginDetails();
        if (user == null || user.isEmpty()) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
            startActivity(intent);
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(7 * 1000);  //30 * 1000
            locationRequest.setFastestInterval(5 * 1000); //5 * 1000
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient,builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(SplashActivity.this,REQUEST_LOCATION);

                                // getActivity().finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_LOCATION == requestCode) {
            next();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 101:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    turnongps();
                }  else {
                    turnongps();
                }
                return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDelayHandler.removeCallbacks(runnable);
    }
}