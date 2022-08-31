package com.example.students;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.students.data.Resturant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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

public class ResturantsMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleMap mMap;
    AlertDialog alertDialog1;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private GoogleApiClient mGoogleApiClient1;
    private Marker MyMarker;

    //array of restaurants
    private String [][]Rest= new String[24][5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resturants_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderApi = LocationServices.FusedLocationApi;        mGoogleApiClient1 = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (mGoogleApiClient1 != null) {
            mGoogleApiClient1.connect();
        }
        ResturantsView();

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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        //clear the map
        mMap.clear();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //if
                if (!(marker.equals(MyMarker))&&!marker.getTitle().equals(Rest[0][3])) {
                    CharSequence[] values = {"more informations","cancle"};
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ResturantsMapsActivity.this);
                    builder1.setTitle(marker.getTitle());
                    builder1.setCancelable(true);
                    builder1.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    email = email.replace(".", "_");
                                    StringBuilder str=new StringBuilder();
                                    for (int i=0;i<marker.getSnippet().length();i++){
                                        if(marker.getSnippet().charAt(i)!='\n')
                                            str.insert(i,marker.getSnippet().charAt(i));
                                        else
                                            break;
                                    }
                                    Resturant r=new Resturant();
                                    r.setName(marker.getTitle());
                                    r.setNearby(str.toString());
                                    reference.child("users").child(email).child("Search").setValue(r, new DatabaseReference.CompletionListener() {
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                // Toast.makeText(getContext(), "save ok", Toast.LENGTH_LONG).show();
                                                Intent myIntent = new Intent(ResturantsMapsActivity.this, RestaurantsShowActivity.class);
                                                startActivity(myIntent);
                                            } else {
                                                Toast.makeText(ResturantsMapsActivity.this, "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                databaseError.toException().printStackTrace();
                                            }
                                        }
                                    });
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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResturantsMapsActivity.this, StudentsMain.class);
        startActivity(intent);
        finish();
        return;
    }
   @Override
    public void onLocationChanged(@NonNull Location location) {
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
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient1, locationRequest, (com.google.android.gms.location.LocationListener) ResturantsMapsActivity.this);



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //function to get name of  restaurant from database
    public  void ResturantsView(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email=FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");
        reference.child("users").child(email).child("myProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name academy the user study in
                final String AcademyName = dataSnapshot.child("lemod").getValue(String.class);
                reference.child("academics").child(AcademyName).child("Resturants").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i=0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = ds.getKey();

                            //function to get more information about restaurant
                            func(AcademyName,name,i);
                            i++;
                        }
                        //function to make marker for academy
                        AcademicMarker(AcademyName);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    public void AcademicMarker(String AcademyName){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("academics").child(AcademyName).child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String loc1= dataSnapshot.child("lat").getValue(String.class);
                String loc2 = dataSnapshot.child("lan").getValue(String.class);

                double lat = Double.parseDouble(loc1);
                double lang = Double.parseDouble(loc2);
                LatLng loc = new LatLng(lat, lang);
                mMap.addMarker(new MarkerOptions().position(loc).title(AcademyName).icon(BitmapDescriptorFactory.defaultMarker(100)));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 15);
                mMap.animateCamera(cameraUpdate);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    public void func(String AcademyName,String name,int i){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email=FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");
        reference.child("academics").child(AcademyName).child("Resturants").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Rest[i][0] = dataSnapshot.child("lat").getValue(String.class);
                Rest[i][1] = dataSnapshot.child("lan").getValue(String.class);
                Rest[i][2] = dataSnapshot.child("name").getValue(String.class);
                Rest[i][3] = dataSnapshot.child("nearby").getValue(String.class);
                Rest[i][4] = dataSnapshot.child("phone").getValue(String.class);

                double lat = Double.parseDouble(Rest[i][0]);
                double lang = Double.parseDouble(Rest[i][1]);
                LatLng loc = new LatLng(lat, lang);

                mMap.addMarker(new MarkerOptions().position(loc).title(Rest[i][2]).snippet(Rest[i][3]+"\n"+Rest[i][4]).icon(BitmapDescriptorFactory.defaultMarker(200)));
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(ResturantsMapsActivity.this);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(ResturantsMapsActivity.this);
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(ResturantsMapsActivity.this);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
}