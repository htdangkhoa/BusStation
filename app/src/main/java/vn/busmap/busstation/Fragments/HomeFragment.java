package vn.busmap.busstation.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import vn.busmap.busstation.MainActivity;
import vn.busmap.busstation.R;
import vn.busmap.busstation.Utils.AsyncHttp;
import vn.busmap.busstation.Utils.MapUtils;
import vn.busmap.busstation.Utils.ServiceUtil;
import vn.busmap.busstation.Utils.Services;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by dangkhoa on 12/8/17.
 */

public class HomeFragment extends Fragment implements OnCompleteListener<Location>, OnSuccessListener<LocationSettingsResponse> {
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.nFabAction)
    FloatingActionsMenu nFabAction;

    @OnClick(R.id.sFabCurrentLocation)
    void onClick() {
        nFabAction.collapse();
        initLocation();
    }

    int REQUEST_PERMISSION_LOCATION = 11111;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initialize(view);
        mapView.onCreate(null);

        return view;
    }

    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private SettingsClient settingsClient;
    private Location location;
    FusedLocationProviderClient client;

    private void initialize(View view) {
        MainActivity.actionBar.setVisibility(View.GONE);
        ButterKnife.bind(this, view);
    }

    private void initLocation() {
        client = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnCompleteListener(getActivity(), this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();

        settingsClient = LocationServices.getSettingsClient(getContext());

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this);
    }

    private void initMap(final Double lat, final Double lng, final String address) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                LatLng khoaHouse = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions()
                        .position(khoaHouse)
                        .title("Current location")
                        .snippet(address));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(khoaHouse, 16.0f));
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Services.Navigate(getContext(), getFragmentManager(), new BusListFragment(), "BUS_LIST", true, null, Services.FROM_RIGHT_TO_LEFT);
                    }
                });

                requestToServer(googleMap, lat, lng);
            }
        });
    }

    private void requestToServer(final GoogleMap googleMap, final Double lat, final Double lng) {
        RequestParams params = new RequestParams();
        params.put("lat", lat);
        params.put("lng", lng);
        AsyncHttp.GET("/stations/near", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Double> distances = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        Log.i("DATA", response.getJSONObject(i).toString());
                        JSONObject object = response.getJSONObject(i);

                        LatLng stationId = new LatLng(object.getDouble("Lat"), object.getDouble("Lng"));
                        googleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station))
                                .position(stationId)
                                .snippet("Address: " + object.getString("Address"))
                                .title("Station Id: " + object.getString("StationId")));
                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                String title = marker.getTitle();

                                if (!title.toString().equals("Current location")) {
                                    String stationId = title.replace("Station Id: ", "");
                                    Log.i("DATA", stationId);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("STATION_ID", stationId);
                                    Services.Navigate(getContext(), getFragmentManager(), new BusListFragment(), "BUS_LIST", true, bundle, Services.FROM_RIGHT_TO_LEFT);
                                }
                            }
                        });

                        Double distance = MapUtils.distance(lat, lng, object.getDouble("Lat"), object.getDouble("Lng"));
                        Log.i("DISTANCE", String.valueOf(distance));
                        distances.add(distance);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (distances.size() > 0) {
                    int index = MapUtils.getMinimumDistance(distances);
                    Log.i("MINIMUM", String.valueOf(index));
                    try {
                        final String sId = response.getJSONObject(index).getString("StationId");

                        Services.FuckingDialog(getContext(),
                                "You are located\nnear the " + sId + " station.",
                                "Would you like to see more details about the " + sId + " station?",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Services.DismissFuckingDialog();
                                    }
                                },
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("STATION_ID", sId);
                                        Services.Navigate(getContext(), getFragmentManager(), new BusListFragment(), "BUS_LIST", true, bundle, Services.FROM_RIGHT_TO_LEFT);

                                        Services.DismissFuckingDialog();
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void requestToInitLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        initLocation();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
            Services.ShowDialog(getContext(),
                    "",
                    getResources().getString(R.string.rationale_location),
                    "CANCEL",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    },
                    "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                System.exit(0);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            System.exit(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int servStatus = ServiceUtil.checkServices(getContext());
        switch (servStatus) {
            case ServiceUtil.SUCCESS:
                break;
            case ServiceUtil.FAILURE_INTERNET:
                ServiceUtil.guideInternet(getContext());
                break;
            case ServiceUtil.FAILURE_LOCATION:
                ServiceUtil.guideLocation(getContext());
                break;
        }

        mapView.onResume();
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        requestToInitLocation();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onComplete(@NonNull Task<Location> task) {
        if (task.isSuccessful() && task.getResult() != null) {
            location = task.getResult();

            Double lat, lng;
            lat = location.getLatitude();
            lng = location.getLongitude();

            Log.d("LAT", String.valueOf(lat));
            Log.d("LNG", String.valueOf(lng));

            Geocoder geocoder = new Geocoder(getContext());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    Log.i("ADDRESS", String.valueOf(address));
                    initMap(lat, lng, address);
                }
                initMap(lat, lng, null);
            } catch (IOException e) {
                e.printStackTrace();
                initMap(lat, lng, null);
            }
        }
    }

    @Override
    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
        client.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                location = locationResult.getLastLocation();

                Double lat, lng;
                lat = location.getLatitude();
                lng = location.getLongitude();

                Log.i("UPDATE LAT", String.valueOf(location.getLatitude()));
                Log.i("UPDATE LNG", String.valueOf(location.getLongitude()));

                Geocoder geocoder = new Geocoder(getContext());
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        Log.i("ADDRESS", String.valueOf(address));
                        initMap(lat, lng, address);
                    }
                    initMap(lat, lng, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    initMap(lat, lng, null);
                }
            }
        }, Looper.myLooper());
    }
}
