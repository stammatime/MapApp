package mapapp.cody.com.mapapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This shows how to place markers on a map.
 */
public class MapActivity extends FragmentActivity
        implements
        OnMarkerClickListener,
        OnInfoWindowClickListener
         {

    //locations
    private static final LatLng COLUMBUS_N = new LatLng(40.015178, -82.998993);
    private static final LatLng COLUMBUS_S = new LatLng(39.916109, -82.995560);
    private static final LatLng COLUMBUS_E = new LatLng(39.976383, -83.119156);
    private static final LatLng COLUMBUS_W = new LatLng(39.967832, -82.950585);

    private static final LatLng OUTLOOKHQ = new LatLng(39.979377, -83.004706);
    private static final LatLng MELT = new LatLng(39.979426, -83.003687);

    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements InfoWindowAdapter {
        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {

            //TODO maybe change color based on attraction type

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, snippet.length(), 0);
                snippetText.setSpan(new BackgroundColorSpan(Color.RED), 0, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }

    private GoogleMap mMap;

    private Marker mPerth;
    private Marker mSydney;
    private Marker mBrisbane;
    private Marker mAdelaide;
    private Marker mMelbourne;

    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */
    private Marker mLastSelectedMarker;

    //TODO get map locations and add to marker list array

    private final List<Marker> mMarkerList = new ArrayList<Marker>();

    private RadioGroup mOptions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_demo);



        //TODO make OnCheckedChange go thru cities

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);

        // Add lots of markers to the map.
        addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();

        //TODO add selector statement for different locations

        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {

                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(OUTLOOKHQ)
                            //.include(COLUMBUS_E)
                            //.include(COLUMBUS_W)
                            //.include(COLUMBUS_S)
                            //.include(MELT)
                            .build();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(13);
                    mMap.animateCamera(zoom);

                }
            });
        }
    }



    private void addMarkersToMap() {
        // Uses a colored icon.
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(OUTLOOKHQ)
                .title("Outlook Headquarters")
                .snippet("The people who bring you such wonderful media"));

        // Creates a draggable marker. Long press to drag.
        mMelbourne = mMap.addMarker(new MarkerOptions()
                .position(MELT)
                .title("Melt")
                .snippet("Delicious grilled cheese, most dining options deep fried"));

    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** Called when the Columbus button is clicked. */
    public void onColumbusMap(View view) {
        if (!checkReady()) {
            return;
        }
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(39.974796, -82.999755));

        mMap.moveCamera(center);
    }

     /** Called when the Cincinnati button is clicked. */
     public void onCincinnatiMap(View view) {
             if (!checkReady()) {
                 return;
             }
             CameraUpdate center=
                     CameraUpdateFactory.newLatLng(new LatLng(39.103178, -84.514309));

             mMap.moveCamera(center);
         }

     /** Called when the Cleveland button is clicked. */
     public void onClevelandMap(View view) {
         if (!checkReady()) {
             return;
         }
         CameraUpdate center=
                 CameraUpdateFactory.newLatLng(new LatLng(41.495071, -81.695560));

         mMap.moveCamera(center);
     }

     public void onToledoMap(View view) {
         if (!checkReady()) {
             return;
         }
         CameraUpdate center=
                 CameraUpdateFactory.newLatLng(new LatLng(41.660531, -83.555932));

         mMap.moveCamera(center);
     }


    /** Called when the Reset button is clicked. */


    //
    // Marker related listeners.
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {

        mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle() + " was clicked", Toast.LENGTH_SHORT).show();
    }



}