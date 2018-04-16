package adammccarthy.flyontime;

/**
 * Created by morganbeaty on 4/12/18.
 */

//package com.example.locuslabs.locuslab;

        import android.Manifest;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.ActivityCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;

        import com.locuslabs.sdk.configuration.LocusLabs;
        import com.locuslabs.sdk.maps.model.Airport;
        import com.locuslabs.sdk.maps.model.AirportDatabase;
        import com.locuslabs.sdk.maps.model.Floor;
        import com.locuslabs.sdk.maps.model.Map;
        import com.locuslabs.sdk.maps.model.Marker;
        import com.locuslabs.sdk.maps.model.POI;
        import com.locuslabs.sdk.maps.model.POIDatabase;
        import com.locuslabs.sdk.maps.model.Position;
        import com.locuslabs.sdk.maps.view.MapView;


public class MapActivity extends AppCompatActivity {

    // Static
    private static final int PERMISSIONS_REQUEST_CODE = 1000;

    // Var
    private AirportDatabase airportDatabase;
    private MapView mapView;
    private Airport airport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Individual permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE);
        }
        // Global permissions (Android versions prior to m)
        else {

            initializeLocusLabsDatabase();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {

            // Assume all permissions were granted (in practice you would need to check each permission)
            if (grantResults.length > 0) {

                initializeLocusLabsDatabase();
            }
        }
    }

// *************
// CUSTOM METHODS
// *************

    private void initializeLocusLabsDatabase() {

        LocusLabs.registerOnReadyListener(new LocusLabs.OnReadyListener() {

            @Override
            public void onReady() {

                airportDatabase = new AirportDatabase();
                loadVenueAndMap("lax", "");
            }
        });
    }

    private void loadVenueAndMap(final String venueId, final String venueName) {

        final RelativeLayout rl = new RelativeLayout(this);

        AirportDatabase.OnLoadAirportAndMapListeners listeners = new AirportDatabase.OnLoadAirportAndMapListeners();
        listeners.loadedInitialViewListener = new AirportDatabase.OnLoadedInitialViewListener() {
            @Override
            public void onLoadedInitialView(View view) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null) {

                    parent.removeView(view);
                }
                view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                rl.addView(view);
                setContentView(rl);
                airportDatabase.resumeLoadAirportAndMap();
            }
        };

        listeners.loadCompletedListener = new AirportDatabase.OnLoadCompletedListener() {
            @Override
            public void onLoadCompleted(Airport _airport, Map _map, final MapView _mapView, Floor floor, Marker marker) {

                mapView = _mapView;
                mapView.setPositioningEnabled(_airport, true);
            }
        };

        // The second parameter is an initial search option, if any
        airportDatabase.loadAirportAndMap(venueId, null, listeners);
    }

    private void loadAirport(String venueId) {
        final RelativeLayout rl = new RelativeLayout( this );

        AirportDatabase.OnLoadAirportAndMapListeners listeners = new AirportDatabase.OnLoadAirportAndMapListeners();
        listeners.loadedInitialViewListener = new AirportDatabase.OnLoadedInitialViewListener() {
            @Override public void onLoadedInitialView(View view) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null) {
                    parent.removeView(view);
                }

                view.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                rl.addView(view);
                setContentView(rl);
            }
        };
        listeners.loadCompletedListener = new AirportDatabase.OnLoadCompletedListener() {

            @Override public void onLoadCompleted(Airport _airport, Map _map, final MapView _mapView,
                                                  Floor floor, Marker marker) {
                airport = _airport;
                mapView = _mapView;
                showNavigation();
            }
        };

        airportDatabase.loadAirportAndMap(venueId, "", listeners);
    }

    private void showNavigation() {
        final POIDatabase poiDatabase = airport.poiDatabase();

        poiDatabase.loadPOI("15", new POIDatabase.OnLoadPoiListener() {
            @Override
            public void onLoadPoi(final POI startPOI) {
                poiDatabase.loadPOI("126", new POIDatabase.OnLoadPoiListener() {
                    @Override
                    public void onLoadPoi(final POI endPOI) {

                        //Use the Position.Builder to create a new Position based off the startPOIs Position.
                        Position startPosition = new Position.Builder(startPOI.getPosition())
                                //Assign a name to the Position startPosition by getting the POIs name.
                                .name(startPOI.getName())
                                //Create the Position startPosition, so it can be called from MapView > showNavigation
                                .createPosition();

                        //Use the Position.Builder to create a new Position based off the endPOIs Position.
                        Position endPosition = new Position.Builder(endPOI.getPosition())
                                //Assign a name to the Position endPosition by getting the POIs name.
                                .name(endPOI.getName())
                                //Create the Position endPosition, so it can be called from MapView > showNavigation
                                .createPosition();

                        mapView.showNavigation(startPosition,endPosition);
                    }
                });
            }
        });
    }
}



