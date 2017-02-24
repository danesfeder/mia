package com.danesfeder.mia.mia;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danesfeder.mia.R;
import com.danesfeder.mia.chatview.ChatAdapter;
import com.danesfeder.mia.chatview.dataobjects.ChatList;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;

public class MiaActivity extends AppCompatActivity implements MiaContract.View {

    private MiaServices mia;
    private MiaPresenter presenter;

    private MapView mapView;
    private RecyclerView chatRecyclerView;
    private ChatAdapter mAdapter;

    private FloatingActionButton searchBtn;
    private RelativeLayout chatLayout;
    private EditText chatEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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

        // Once map is ready, set marker to current location
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // Add the marker to the map
                mapboxMap.addMarker(new MarkerViewOptions()
                        .position(new LatLng(40.6258, -75.3708)));
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
