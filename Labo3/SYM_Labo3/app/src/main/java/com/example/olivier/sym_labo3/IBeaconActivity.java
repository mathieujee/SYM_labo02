package com.example.olivier.sym_labo3;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

public class IBeaconActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "IBeacon activity";
    private BeaconManager beaconManager;
    private List<String> beaconsInfos = new ArrayList<>();
    private ListView listView = null;
    private ArrayAdapter<String> adapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibeacon);
        context = getApplicationContext();

        adapter = new ArrayAdapter<>(this, R.layout.ibeacon_list_view, R.id.beaconsInfosTextView, beaconsInfos);
        listView = findViewById(R.id.IBeaconListView);
        listView.setAdapter(adapter);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        beaconManager.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                beaconsInfos.clear();
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        beaconsInfos.add("RSSI : " + Integer.toString(beacon.getRssi()) + "\nMAJOR ID : " + beacon.getId2() + "\nMINOR ID : " + beacon.getId3());
                    }

                } else {
                    beaconsInfos.add("There is no beacon in this area");
                }
                adapter.notifyDataSetChanged();
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

}