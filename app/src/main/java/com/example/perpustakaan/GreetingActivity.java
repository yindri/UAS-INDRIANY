package com.example.perpustakaan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GreetingActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        tvGreeting = findViewById(R.id.tv_greeting);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_REQUEST_CODE);
        } else {
            getLocationAndGreet();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndGreet();
            } else {
                Toast.makeText(this, "Permission Denied. Defaulting to Indonesia.", Toast.LENGTH_SHORT).show();
                showGreeting("Indo");
            }
        }
    }

    private void getLocationAndGreet() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        tvGreeting.setText("Detecting Location...");

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            determineGreeting(location);
                        } else {
                            // If LastLocation is null, try requesting a fresh location
                            requestCurrentLocation();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        requestCurrentLocation();
                    }
                });
    }

    private void requestCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showGreeting("Indo");
            return;
        }

        // Create a request with high accuracy and a short duration to try hard to get a
        // fix
        com.google.android.gms.location.CurrentLocationRequest request = new com.google.android.gms.location.CurrentLocationRequest.Builder()
                .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
                .setDurationMillis(10000) // Wait up to 10 seconds for a fix
                .setMaxUpdateAgeMillis(0) // Don't accept old location
                .build();

        fusedLocationClient.getCurrentLocation(request, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            determineGreeting(location);
                        } else {
                            // Retry with lower accuracy if High Accuracy failed
                            fallbackToCoarseLocation();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GreetingActivity.this, "Gagal/Timeout: " + e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        showGreeting("Indo");
                    }
                });
    }

    private void fallbackToCoarseLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showGreeting("Indo");
            return;
        }

        com.google.android.gms.location.CurrentLocationRequest request = new com.google.android.gms.location.CurrentLocationRequest.Builder()
                .setPriority(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setDurationMillis(5000)
                .build();

        fusedLocationClient.getCurrentLocation(request, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            determineGreeting(location);
                        } else {
                            Toast.makeText(GreetingActivity.this, "Lokasi tidak ditemukan (Cek GPS)", Toast.LENGTH_LONG)
                                    .show();
                            showGreeting("Indo");
                        }
                    }
                });
    }

    private void determineGreeting(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String adminArea = addresses.get(0).getAdminArea(); // Provinsi
                if (adminArea != null) {
                    Toast.makeText(GreetingActivity.this, "Provinsi: " + adminArea, Toast.LENGTH_LONG).show();
                    String lowerAdmin = adminArea.toLowerCase();
                    if (lowerAdmin.contains("jawa barat") || lowerAdmin.contains("west java")
                            || lowerAdmin.contains("banten")) {
                        showGreeting("Sunda");
                    } else if (lowerAdmin.contains("jakarta") || lowerAdmin.contains("dki")) {
                        showGreeting("Betawi");
                    } else if (lowerAdmin.contains("jawa") || lowerAdmin.contains("java")
                            || lowerAdmin.contains("yogyakarta")) {
                        // Tengah (Central Java) or Timur (East Java) or DIY
                        showGreeting("Jawa");
                    } else {
                        showGreeting("Indo");
                    }
                } else {
                    Toast.makeText(GreetingActivity.this, "Provinsi tidak terdeteksi (Null)", Toast.LENGTH_SHORT)
                            .show();
                    showGreeting("Indo");
                }
            } else {
                showGreeting("Indo");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showGreeting("Indo");
        }
    }

    private void showGreeting(String type) {
        String message = "Selamat Datang";
        switch (type) {
            case "Sunda":
                message = "Wilujeng Sumping";
                break;
            case "Betawi":
                message = "Selamat Datang, Nyok!";
                break;
            case "Jawa":
                message = "Sugeng Rawuh";
                break;
            default:
                message = "Selamat Datang";
                break;
        }
        tvGreeting.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Delay and go to Main
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(GreetingActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);
    }
}
