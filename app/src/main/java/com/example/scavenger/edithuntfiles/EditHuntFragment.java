package com.example.scavenger.edithuntfiles;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.scavenger.Checkpoint;
import com.example.scavenger.Hint;
import com.example.scavenger.Hunt;
import com.example.scavenger.R;
import com.example.scavenger.databinding.FragmentEditHuntBinding;
import com.example.scavenger.edithuntfiles.CheckpointRVAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class EditHuntFragment extends Fragment implements OnMapReadyCallback {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;

    private GoogleMap mMap;
    private MapView mapView;
    private LatLng latlng;

    private FragmentEditHuntBinding binding;

    // the current hunt that the user is editing
    public static Hunt hunt;

    private CheckpointRVAdapter adapter;

    private RecyclerView recyclerView;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditHuntBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // map stuff
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        // checkpoint list stuff
        adapter = new CheckpointRVAdapter((ArrayList<Checkpoint>) hunt.getCheckpoints().clone(), getActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = binding.checkpointRV;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // code from https://www.youtube.com/watch?v=cT9w4T9FCSQ
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int positionDragged = viewHolder.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();
                Collections.swap(adapter.getItems(), positionDragged, positionTarget);
                adapter.notifyItemMoved(positionDragged, positionTarget);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);

        // create a new checkpoint and add it to this hunt
        binding.newcheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocationForCheckpoint();
            }
        });

        binding.savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        binding.zoominbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        binding.zoomoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    private void createCheckpoint() {
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.yellowflag);
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 140, 168, false);
        mMap.addMarker(
                new MarkerOptions()
                        .position(latlng)
                        .title("Checkpoint")
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .anchor(0f, 0f));

        Checkpoint checkpoint = new Checkpoint(hunt, latlng.latitude, latlng.longitude,
                "", "desc", adapter.getItemCount(), Checkpoint.YELLOW);
        ArrayList<Hint> hints = new ArrayList<>();
        hints.add(new Hint("Sample Description"));
        hints.add(new Hint("Sample Description"));
        hints.add(new Hint("Sample Description"));
        checkpoint.setHints(hints);
        //int checkpointPos = hunt.addCheckpoint(checkpoint);
        //if (checkpointPos < 0) {
        // maximum checkpoint capacity reached
        //    return;
        //}
        // refresh checkpoint display
        adapter.add(checkpoint);
        recyclerView.setAdapter(adapter);
    }

    /**
     * When a checkpoint is deleted, the recyclerView needs to be refreshed so
     * that it removes the deleted checkpoint and continues displaying the others
     * @param adapter The updated adapter without the deleted checkpoint
     * @param removedCpPos The position of the deleted checkpoint so that the
     *                     display number on the other checkpoints can be updated
     */
    public void refresh(CheckpointRVAdapter adapter, int removedCpPos) {
        for (Checkpoint cp : adapter.getItems()) {
            if (cp.getPosition() > removedCpPos) {
                cp.setPosition(cp.getPosition() - 1);
            }
        }
        recyclerView.setAdapter(adapter);
        mMap.clear(); // clear all markers from the map
        showCurrentCheckpoints(); // add all remaining markers back
    }

    /**
     * Save the created checkpoints and their information to the database so that
     * when the user logs out and logs back in their progress will be saved
     */
    public void save() {
        FirebaseFirestore.getInstance().collection("Hunts")
                .document(hunt.getName())
                .update("checkpoints", adapter.getItems());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getLocationPermission();
        mMap = googleMap;
        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(20.0f);
        LatLngBounds wheatonBounds = new LatLngBounds(
                new LatLng(41.867290, -88.101175), // SW bounds
                new LatLng(41.873652, -88.093206)  // NE bounds
        );
        mMap.setLatLngBoundsForCameraTarget(wheatonBounds);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wheatonBounds.getCenter(), 17));
        showCurrentCheckpoints();
    }

    public void showCurrentCheckpoints() {
        // for each checkpoint, add a marker on the map for it
        for (Checkpoint cp : hunt.getCheckpoints()) {
            latlng = new LatLng(cp.getLatitude(),cp.getLongitude());
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(cp.getColor());
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 140, 168, false);
            mMap.addMarker(
                    new MarkerOptions()
                            .position(latlng)
                            .title("Checkpoint")
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                            .anchor(0f, 0f));
        }
    }

    private void getDeviceLocationForCheckpoint() {
        System.out.println("getting device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (mLocationPermissionsGranted) {
                mMap.setMyLocationEnabled(true);
                Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
                locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            System.out.println("got location!");
                            Location currentLocation = (Location) task.getResult();
                            latlng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            //latlng = new LatLng(41.8695436,-88.0960761); // mey sci location
                            createCheckpoint();
                            mMap.setMyLocationEnabled(false);
                        } else {
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                System.out.println("permissions not set");
            }
        } catch (SecurityException e) {
            System.out.println("security exception");
        }
    }

    private void getLocationPermission(){
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}