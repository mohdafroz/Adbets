package com.example.adbets.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import com.example.adbets.R;
import com.example.adbets.database.AppDatabase;
import com.example.adbets.helper.DownloadHelper;
import com.example.adbets.model.VideoUrl;
import com.example.adbets.service.BroadcastDownloadReceiver;
import com.example.adbets.utility.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<VideoUrl> modelVideoUrlList;
    private ExoPlayer player;
    private PlayerView playerView;
    private ArrayList<MediaItem> videoList;
    private ArrayList<MediaItem> modelMediaList;
    private ConnectivityManager.NetworkCallback networkCallback;
    private final String[] videoUrls = {"https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_7378692246785011771DesignAsset_1707124198604_Ads%20offers.mp4", "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_10997764465025613630DesignAsset_1707128707418_dantam%20dental%20suite-2.mp4", "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/86/ASSETS/asset_14975988839848876724DesignAsset_1711686900898_World+Kidney+Day+Campaign_Digital+Screen+-+V1+(1).mp4", "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_6890123940375361769DesignAsset_1709667781043_Dantam%20Ad_01%20copy.mp4", "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_7360241592401237427DesignAsset_1709667785000_Dantam%20Ad_02%20copy.mp4"};
    private final String[] filename = {"adBeetsVideos1.mp4", "adBeetsVideos2.mp4", "adBeetsVideos3.mp4", "adBeetsVideos4.mp4", "adBeetsVideos5.mp4"};
    private BroadcastReceiver downloadReceiver;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        FloatingActionButton downloadVideos = findViewById(R.id.fab);
        videoList = new ArrayList<>();
        ArrayList<String> tempList = new ArrayList<>();
        modelVideoUrlList = new ArrayList<>();
        modelMediaList = new ArrayList<>();
        DownloadHelper downloadHelper = new DownloadHelper(this);
        downloadReceiver = new BroadcastDownloadReceiver();

        for (String tempVideoUrls : videoUrls) {
            videoList.add(MediaItem.fromUri(tempVideoUrls));
            tempList.add(tempVideoUrls);
        }

        if (!Util.checkInternetStatus(getApplicationContext())) {
            playOfflineVideos();
        }

        // Get ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Define the NetworkCallback
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (player != null) {
                            player.release();
                        }

                        // Initialize ExoPlayer
                        player = new ExoPlayer.Builder(getApplicationContext()).build();
                        playerView.setPlayer(player);

                        player.addMediaItems(videoList);
                        player.prepare();
                        player.setRepeatMode(player.REPEAT_MODE_ALL);
                        player.setPlayWhenReady(true);
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TAG", "onLost");
                        if (player != null) {
                            player.release();
                        }
                        playOfflineVideos();
                    }
                });
            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities capabilities) {
                super.onCapabilitiesChanged(network, capabilities);
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    //  runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected via cellular", Toast.LENGTH_SHORT).show());
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    //  runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected via Wi-Fi", Toast.LENGTH_SHORT).show());
                }
            }
        };

        // Create a NetworkRequest
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        NetworkRequest networkRequest = builder.build();

        // Register the NetworkCallback with the ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);

        downloadVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.checkInternetStatus(getApplicationContext())) {
                    downloadHelper.downloadVideo(videoUrls, filename);
                }
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    public void playOfflineVideos() {

        new Thread(() -> {
            modelVideoUrlList = AppDatabase.getDataBase(this).videoUrlDao().getAllVideoUrl();

            if (!modelVideoUrlList.isEmpty()) {
                runOnUiThread(() -> {
                    for (VideoUrl videoUrl : modelVideoUrlList) {
                        modelMediaList.add(MediaItem.fromUri(videoUrl.getName()));
                        Log.e("TAG", "URL " + videoUrl.getName());
                    }
                    if (player != null) {
                        player.release();
                    }

                    player = new ExoPlayer.Builder(this).build();
                    player.setMediaItems(modelMediaList);
                    player.prepare();
                    player.setRepeatMode(player.REPEAT_MODE_ALL);
                    playerView.setPlayer(player);
                    player.setPlayWhenReady(true);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "No Video's available", Toast.LENGTH_SHORT).show();
                });

            }

        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCallback != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
        player.release();
        unregisterReceiver(downloadReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.setPlayWhenReady(true);
        }

    }
}

