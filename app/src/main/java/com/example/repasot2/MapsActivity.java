package com.example.repasot2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mi_mapa;
    private Button btn_seguir, btn_reset;
    private PolylineOptions ruta= new PolylineOptions();
    private boolean mapa_centrado= false;
    LocationListener oyente;
    LocationManager lc;

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResult) {
        if (requestCode == 99) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                // pedirActualizaciones();
                btn_seguir.setEnabled(true);
            }
        }

    }

    public void pedirActualizaciones() {
        lc = (LocationManager) getSystemService(LOCATION_SERVICE);
        oyente = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                meterNuevoPuntoEnRuta(location);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, oyente);

    }

    private void meterNuevoPuntoEnRuta(Location location) {
        LatLng punt = new LatLng(location.getLatitude(),location.getLongitude());
        ruta.add(punt);
        mi_mapa.addPolyline(ruta);
        if (mapa_centrado==false)
        {
            mi_mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(punt,8));
            mapa_centrado=true;
        }
        //Centramos el mapa en el punto que me ha llegado

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_seguir = findViewById(R.id.btn_seguir);
        btn_reset = findViewById(R.id.reset);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://webscrappingciudades-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Ciudades");
        

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetearRuta();
            }
        });
        btn_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirActualizaciones();
            }
        });
        chekearPermiso();
    }

    private void resetearRuta() {
        lc.removeUpdates(oyente);
        ruta= new PolylineOptions();
        mi_mapa.clear();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void chekearPermiso() {
        // si no tengo permiso lo pido
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            { requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            }// ya tengo permiso leocalizo despues de
        else{
            //
                btn_seguir.setEnabled(true);

        }


    }

};


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mi_mapa = googleMap;

        //habilitar boton

        btn_seguir.setEnabled(true);



    }
}