package com.example.students;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.students.data.location;
import com.example.students.sql.dbAcademics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UniversityMapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener , GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleMap mMap;
    private Spinner spType;
    private Button btnNearby,btnSearch;
    private EditText etSearchAcademic;
    AlertDialog alertDialog1;
    AlertDialog alertDialog;
    private LinearLayout validity;

    private LocationRequest locationRequest;
    private Marker MyMarker;
    String str;
    LatLng templocation;
    private GoogleApiClient mGoogleApiClient1;
    private Location mLastLocation;
    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
    private String value;
    private dbAcademics db;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Allow LocationApp to access this device's location?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(UniversityMapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_maps);
        checkLocationPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //connecting xml to code
        spType=(Spinner)findViewById(R.id.spType1);
        btnNearby=(Button) findViewById(R.id.btnNearby);
        btnSearch=(Button) findViewById(R.id.btnSearch);
        etSearchAcademic=(EditText) findViewById(R.id.etSearchAcademic);
        validity=(LinearLayout)findViewById(R.id.validity);
        db=new dbAcademics(UniversityMapsActivity.this);

        //add values to spinner
        spType.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("All");
        categories.add("University");
        categories.add("College");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spType.setAdapter(dataAdapter);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //fusedLocationProviderApi = LocationServices.FusedLocationApi;
        mGoogleApiClient1 = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (mGoogleApiClient1 != null) {
            mGoogleApiClient1.connect();
        }

        btnNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call function to show all academics in map
                selectTypeAcademy("All");

                //check if have location user
                  if (MyMarker!=null){
                      //add values to dialog
                CharSequence[] values = {"15.000", "30.0000"};
                AlertDialog.Builder builder = new AlertDialog.Builder(UniversityMapsActivity.this);
                builder.setTitle("Select Max distance");
                builder.setCancelable(true);
                builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            //if select :"15.000"
                            case 0:
                                for (Marker marker : mMarkerArray) {
                                    //call function to get distance between the user and academics
                                    double x = distance(MyMarker.getPosition().latitude, MyMarker.getPosition().longitude, marker.getPosition().latitude, marker.getPosition().longitude);
                                    if (x > 15000)
                                        //remove markers that have distance more than 15000
                                        marker.remove();

                                }
                                break;
                            case 1:
                                //if select :"30.000"
                                for (Marker marker : mMarkerArray) {
                                    //call function to get distance between the user and academics
                                    double x = distance(MyMarker.getPosition().latitude, MyMarker.getPosition().longitude, marker.getPosition().latitude, marker.getPosition().longitude);
                                    if (x > 30000)
                                        //remove markers that have distance more than 30.000
                                        marker.remove();
                                }
                                break;

                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();

            }
                  else {
                      Toast.makeText(UniversityMapsActivity.this,"Check Permission Location or Refresh Page ",Toast.LENGTH_LONG).show();
                  }
        }

        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //getting value from last academy
             value = extras.getString("KEY");
             etSearchAcademic.setText(value);
        }
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });
        etSearchAcademic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UniversityMapsActivity.this,SelectAcademyActivity.class);
                //reset HOME to know this is Map Fragment
                intent.putExtra("HOME","");

                startActivity(intent);

            }
        });



    }

    public void Search(){
        try {
            for (Marker marker : mMarkerArray) {
                if (marker.getTitle().equals(etSearchAcademic.getText().toString())){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),14));
                }
            }

        }catch (Exception ex){}
    }



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
        
        mMap = googleMap;
        Bundle extras = getIntent().getExtras();

        if (extras!= null&&extras.getString("GUEST")!=null&&extras.getString("GUEST").equals("GUEST")) {
            //if user is guest remove the search and distance
            btnSearch.setX(99999);
            etSearchAcademic.setX(9999);
            btnNearby.setX(9999);
            spType.setX(99999);
            validity.setX(9999);

        }
        else {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (!(marker.equals(MyMarker))) {
                    CharSequence[] values = {"more informations", "cancle"};
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(UniversityMapsActivity.this);
                    builder1.setTitle(marker.getTitle());
                    builder1.setCancelable(true);
                    builder1.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    /*
                                    move user from current activity to UniverstyViewActivity
                                    to show information about it
                                     */
                                    Intent myIntent = new Intent(UniversityMapsActivity.this, UniverstyViewActivity.class);
                                    myIntent.putExtra("KEY", marker.getTitle());

                                    startActivity(myIntent);
                                    break;
                                case 1:
                                    break;

                            }
                            alertDialog1.dismiss();
                        }
                    });
                    alertDialog1 = builder1.create();
                    alertDialog1.show();
                }
                return false;
            }

        });
    }
    }

    //calculate the distance between 2 points
    public double distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;


        return (distance * meterConversion);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(UniversityMapsActivity.this,item,Toast.LENGTH_LONG).show();
        mMarkerArray.clear();
        mMap.clear();

        /*
        call function to show:
         1) all academics in map
         2)only university
         3)only college
         */
        selectTypeAcademy(item);

        }

        public void selectTypeAcademy(String item) {
            mMap.clear();
            Bundle extras = getIntent().getExtras();
            //if user is guest
            if (extras!= null&&extras.getString("GUEST")!=null&&extras.getString("GUEST").equals("GUEST")) {
                double lat[]=db.getLat();

                double lan[]=db.getLan();
                for (int i=0;i<lan.length;i++){
                    //show academics that in array (demo mode)
                    LatLng loc = new LatLng(lat[i], lan[i]);
                    Marker m = mMap.addMarker(new MarkerOptions().position(loc).title(db.getName(i)));
                    mMarkerArray.add(m);
                }

            }else{

            if (item.equals("All")) {
                mMap.clear();
                reference.child("academics").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get all academics from database
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //getting information about academy
                            String name = ds.getKey();
                            String loc1 = dataSnapshot.child(name).child("location").child("lan").getValue(String.class);
                            String loc2 = dataSnapshot.child(name).child("location").child("lat").getValue(String.class);
                            String type = dataSnapshot.child(name).child("informations").child("type").getValue(String.class);
                            double lat = Double.parseDouble(loc2);
                            double lang = Double.parseDouble(loc1);
                            LatLng loc = new LatLng(lat, lang);
                            Marker m = mMap.addMarker(new MarkerOptions().position(loc).title(ds.getKey()).snippet(type));

                            //change icon marker
                            if (type.equals("Universty"))
                                m.setIcon(BitmapDescriptorFactory.defaultMarker(100));
                            else
                                m.setIcon(BitmapDescriptorFactory.defaultMarker(300));

                            mMarkerArray.add(m);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                if (templocation != null)
                    //add user marker to map
                    MyMarker = mMap.addMarker(new MarkerOptions().position(templocation).icon(BitmapDescriptorFactory.defaultMarker(200)).title(str));
            } else if (item.equals("University")) {
                mMap.clear();
                reference.child("academics").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = ds.getKey();
                            String type = dataSnapshot.child(name).child("informations").child("type").getValue(String.class);
                            //show only University
                            if (type.equals("Universty")) {
                                String loc1 = dataSnapshot.child(name).child("location").child("lan").getValue(String.class);
                                String loc2 = dataSnapshot.child(name).child("location").child("lat").getValue(String.class);
                                double lat = Double.parseDouble(loc2);
                                double lang = Double.parseDouble(loc1);
                                LatLng loc = new LatLng(lat, lang);
                                Marker m = mMap.addMarker(new MarkerOptions().position(loc).title(ds.getKey()).snippet(type));
                                m.setIcon(BitmapDescriptorFactory.defaultMarker(100));

                                mMarkerArray.add(m);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                if (templocation != null)
                    //add user marker to map
                    MyMarker = mMap.addMarker(new MarkerOptions().position(templocation).icon(BitmapDescriptorFactory.defaultMarker(200)).title(str));
            } else if (item.equals("College")) {
                mMap.clear();
                reference.child("academics").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = ds.getKey();
                            String type = dataSnapshot.child(name).child("informations").child("type").getValue(String.class);
                            //show only College
                            if (type.equals("College")) {
                                String loc1 = dataSnapshot.child(name).child("location").child("lan").getValue(String.class);
                                String loc2 = dataSnapshot.child(name).child("location").child("lat").getValue(String.class);
                                double lat = Double.parseDouble(loc2);
                                double lang = Double.parseDouble(loc1);
                                LatLng loc = new LatLng(lat, lang);
                                Marker m = mMap.addMarker(new MarkerOptions().position(loc).title(ds.getKey()).snippet(type));
                                m.setIcon(BitmapDescriptorFactory.defaultMarker(300));

                                mMarkerArray.add(m);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                if (templocation != null)
                    //add user marker to map
                    MyMarker = mMap.addMarker(new MarkerOptions().position(templocation).icon(BitmapDescriptorFactory.defaultMarker(200)).title(str));
            }
        }
        }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();
        //move from current activity to main activity
        Intent intent = new Intent(UniversityMapsActivity.this, NotStudentActivity.class);
        if (extras != null&&extras.getString("GUEST").equals("GUEST")) {
            intent.putExtra("GUEST","GUEST");
        }

        startActivity(intent);
        finish();

        return;
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Bundle extras = getIntent().getExtras();

        if (extras == null || extras.getString("GUEST")==null||!extras.getString("GUEST").equals("GUEST")) {
            boolean isok = false;
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient1);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        double stLocation11 = mLastLocation.getLatitude();
        double stLocation12 = mLastLocation.getLongitude();
        //set location user to class
        location l = new location();
        l.setLat(stLocation11);
        l.setLng(stLocation12);
        email = email.replace(".", "_");
        reference.child("users").child(email).child("Location").child("loc").setValue(l, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("p").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //getting name of user
                            String name = dataSnapshot.child("fullname").getValue(String.class);
                            str = name;
                            //set location of user
                            LatLng MyLoction = new LatLng(l.getLat(), l.getLng());
                            templocation = MyLoction;
                            //add marker user to map
                            MyMarker = mMap.addMarker(new MarkerOptions().position(MyLoction).icon(BitmapDescriptorFactory.defaultMarker(200)).title(str));
                            if (etSearchAcademic.getText().length() == 0)
                                //move camera to user
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLoction, 12));
                            else {
                                Toast.makeText(UniversityMapsActivity.this, etSearchAcademic.getText().toString(), Toast.LENGTH_LONG).show();
                                Search();

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                } else {

                    Toast.makeText(getBaseContext(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();

                    databaseError.toException().printStackTrace();
                }
            }
        });

        isok = true;


        if (isok == false) {
            //show dialog to turn on gps
            Toast.makeText(UniversityMapsActivity.this, "please trun on Gps and restart the application", Toast.LENGTH_LONG).show();
            CharSequence[] values = {" Exit "};
            AlertDialog.Builder builder1 = new AlertDialog.Builder(UniversityMapsActivity.this);
            builder1.setTitle("Please trun on your GPS");
            builder1.setCancelable(false);
            builder1.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch (item) {
                        case 0:
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            break;
                    }
                    alertDialog1.dismiss();
                }
            });
            alertDialog1 = builder1.create();
            alertDialog1.show();


        }
    }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}