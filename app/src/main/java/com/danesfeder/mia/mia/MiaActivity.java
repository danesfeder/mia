package com.danesfeder.mia.mia;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danesfeder.mia.R;
import com.danesfeder.mia.chatview.ChatAdapter;
import com.danesfeder.mia.chatview.dataobjects.ChatList;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.Constants;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import java.util.List;

import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiaActivity extends AppCompatActivity implements MiaContract.View {

    private static final int MIA_PERMISSIONS_REQUEST = 100;
    private final String LOG_TAG = MiaActivity.class.getSimpleName();

    private MiaServices mia;
    private MiaPresenter presenter;

    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;

    private RecyclerView chatRecyclerView;
    private ChatAdapter mAdapter;
    private FloatingActionButton searchBtn;
    private RelativeLayout chatLayout;
    private EditText chatEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        requestPermissions();

        // Setup Mapbox and API.AI access
        MapboxAccountManager.start(this, getResources().getString(R.string.mapbox_access_token));
        // Set LayoutManager and ItemAnimator
        setupAIConfiguration();

        // Create list of objects and construct chat adapter
        ChatList chatList = createChatList();

        // Create presenter and give it access to API.AI services
        this.presenter = new MiaPresenter(mia, chatList);
        presenter.attachView(this);

        chatLayout = (RelativeLayout) findViewById(R.id.rl_chat_layout);
        chatRecyclerView = (RecyclerView) findViewById(R.id.rv_chat);
        setupRecyclerView();

        searchBtn = (FloatingActionButton) findViewById(R.id.search_fab);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatLayout.setVisibility(View.VISIBLE);
                searchBtn.hide();
            }
        });

        ImageView clearBtn = (ImageView) findViewById(R.id.iv_hide_chat);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatLayout.setVisibility(View.GONE);
                searchBtn.show();
                hideKeyboard();
            }
        });

        chatEditText = (EditText) findViewById(R.id.et_chat_box);
        chatEditText.setOnEditorActionListener(chatBoxListener);

        mapView = (MapView) findViewById(R.id.mv_map);
        mapView.onCreate(savedInstanceState);
        mapView.setStyle(Style.MAPBOX_STREETS);


        // Alhambra landmark in Granada, Spain.
        final Position origin = Position.fromCoordinates(-3.588098, 37.176164);

        // Plaza del Triunfo in Granada, Spain.
        final Position destination = Position.fromCoordinates(-3.601845, 37.184080);

        // Once map is ready, set marker to current location
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // Add the marker to the map
                map = mapboxMap;

                // Add origin and destination to the map
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(origin.getLatitude(), origin.getLongitude()))
                        .title("Origin")
                        .snippet("Alhambra"));
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(destination.getLatitude(), destination.getLongitude()))
                        .title("Destination")
                        .snippet("Plaza del Triunfo"));

                // Get route from API
                try {
                    getRoute(origin, destination);
                } catch (ServicesException servicesException) {
                    servicesException.printStackTrace();
                }
            }
        });

        // Add initial greeting message
        presenter.addMiaGreeting();
    }

    /**
     * ChatList is an ArrayList of BotObjects which populate responses and
     * user input view in the RecyclerView
     */
    private ChatList createChatList() {
        ChatList chatList = new ChatList();
        this.mAdapter = new ChatAdapter(chatList);
        return chatList;
    }

    /**
     * Set up API.AI configuration and give it to MiaServices class so we
     * can query our bot and receive responses
     */
    private void setupAIConfiguration() {
        final AIConfiguration aiConfig = new AIConfiguration(getString(R.string.apiai_access_token),
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        this.mia = new MiaServices(new AIDataService(getApplicationContext(), aiConfig));
    }

    private void setupRecyclerView() {
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.setAdapter(mAdapter);
    }

    private EditText.OnEditorActionListener chatBoxListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.sendQueryRequest(textView.getText().toString());
                chatEditText.getText().clear();
                return true;
            }
            return false;
        }
    };

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chatLayout.getWindowToken(), 0);
    }

    @Override
    public void hideChatView() {
        chatLayout.setVisibility(View.GONE);
    }

    @Override
    public void showSearchBtn() {
        searchBtn.show();
    }

    @Override
    public void scrollToBottom() {
        if (mAdapter.getItemCount() > 0) {
            chatRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void notifyAdapterItemInserted(int position) {
        this.mAdapter.notifyItemInserted(position);
    }

    private void getRoute(Position origin, Position destination) throws ServicesException {

        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_CYCLING)
                .setAccessToken(MapboxAccountManager.getInstance().getAccessToken())
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(LOG_TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(LOG_TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    Log.e(LOG_TAG, "No routes found");
                    return;
                }

                // Print some info about the route
                currentRoute = response.body().getRoutes().get(0);
                Log.d(LOG_TAG, "Distance: " + currentRoute.getDistance());
                Toast.makeText(
                        MiaActivity.this,
                        "Route is " + currentRoute.getDistance() + " meters long.",
                        Toast.LENGTH_SHORT).show();

                // Draw the route on the map
                drawRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(LOG_TAG, "Error: " + throwable.getMessage());
                Toast.makeText(MiaActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.getGeometry(), Constants.OSRM_PRECISION_V5);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude(),
                    coordinates.get(i).getLongitude());
        }

        // Draw Points on MapView
        map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MiaActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,},
                MIA_PERMISSIONS_REQUEST);
        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
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
