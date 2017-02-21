package com.danesfeder.mia.map;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.danesfeder.mia.R;
import com.danesfeder.mia.chat.BotAdapter;
import com.danesfeder.mia.chat.dataobjects.BotList;
import com.danesfeder.mia.chat.dataobjects.InputObject;
import com.danesfeder.mia.chat.dataobjects.ResponseObject;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MapActivity extends AppCompatActivity {

    private BotAdapter mAdapter;

    private MapView mapView;
    private RecyclerView chatList;
    private FloatingActionButton searchBtn;
    private RelativeLayout chatLayout;
    private ImageView clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getResources().getString(R.string.access_token));
        setContentView(R.layout.activity_map);

        final BotList botList = new BotList();
        ResponseObject object = new ResponseObject();
        object.setText("Hi, I'm MIA!");
        InputObject inputObject = new InputObject();
        inputObject.setText("User inputted text here that ends up being super long and lengthy");
        botList.add(object);
        botList.add(inputObject);

        chatLayout = (RelativeLayout) findViewById(R.id.rl_chat_layout);
        chatList = (RecyclerView) findViewById(R.id.rv_chat);
        mAdapter = new BotAdapter(botList);
        chatList.setAdapter(mAdapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        chatList.setItemAnimator(new DefaultItemAnimator());

        searchBtn = (FloatingActionButton) findViewById(R.id.search_fab);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatLayout.setVisibility(View.VISIBLE);
                searchBtn.hide();
            }
        });

        clearBtn = (ImageView) findViewById(R.id.iv_hide_chat);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatLayout.setVisibility(View.GONE);
                searchBtn.show();
            }
        });

        mapView = (MapView) findViewById(R.id.mv_map);
        mapView.onCreate(savedInstanceState);
        mapView.setStyle(Style.MAPBOX_STREETS);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // Add the marker to the map
                mapboxMap.addMarker(new MarkerViewOptions()
                        .position(new LatLng(40.6258, -75.3708)));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
