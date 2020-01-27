package com.example.petlocket2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.io.IOException;
import java.util.List;

public class LocationFragment extends Fragment  implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker ;
    private static final int Request_User_Location_Code = 99;
    private double longitude , latitude;
    private int ProximityRadius = 5000;
    ImageButton imgbt,imgbt2;
    EditText addressField;
    View view;
    String stores = "pet_store";
    Object transferData[];
    GetNearbyClasses getNearByPlaces;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view= inflater.inflate(R.layout.activity_location_fragment,container,false);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
       mapFragment.getMapAsync(this);
       imgbt=(ImageButton)view.findViewById(R.id.search_button);
        imgbt2=(ImageButton)view.findViewById(R.id.stores);


        addressField = (EditText) view.findViewById(R.id.location_search);



        imgbt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               transferData=new Object[2];
               getNearByPlaces=new GetNearbyClasses();
               String address = addressField.getText().toString();
               MarkerOptions userMarkerOptions = new MarkerOptions();
                mMap.clear();

               List<Address> addressList = null;
               if(!TextUtils.isEmpty(address)){
                   Geocoder geocoder = new Geocoder(getActivity());
                   try {

                       addressList = geocoder.getFromLocationName(address,6);
                       if(addressList!=null) {
                           for (int i = 0; i < addressList.size(); i++) {
                               Address userAddress = addressList.get(i);
                               LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                               latitude=userAddress.getLatitude();
                               longitude= userAddress.getLongitude();
                               userMarkerOptions.position(latLng);
                               userMarkerOptions.title(address);
                               userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                               mMap.addMarker(userMarkerOptions);

                               mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                               mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                           }
                       }
                       else{
                           Toast.makeText(getActivity(),"Location not found" , Toast.LENGTH_SHORT);
                       }
                   } catch (IOException e) {
                       e.printStackTrace();
                   }

               }
               else{
                   Toast.makeText(getActivity(),"Please write any location name" , Toast.LENGTH_SHORT);
               }


           }
       });

        imgbt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transferData=new Object[2];
                getNearByPlaces=new GetNearbyClasses();
                mMap.clear();
                String url = getUrl(latitude,longitude,stores);
                transferData[0] = mMap;
                transferData[1] = url;
                getNearByPlaces.execute(transferData);
            }
        });

        return view;
    }

    private String getUrl(double latitude,double longitude,String nearByPlace){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearByPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key="+ "AIzaSyCl_jPs5WTl8SGRIs_INEZBo_Nqr_AZRWM");

        Log.d("GoogleMapsActivity" , "url = " + googleURL.toString() );
        return googleURL.toString();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }

    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;

        if(currentUserLocationMarker!=null){
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentUserLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if(googleApiClient!= null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

        public boolean checkUserLocationPermission(){
            if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                    ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);

                }
                return false;
            }
            else {
                return true;
            }

        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(getActivity(),"permission denied", Toast.LENGTH_LONG).show();
                }
        }
    }
    protected synchronized void buildGoogleApiClient(){

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }
}
























