package com.example.adbets;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.OptIn;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import com.example.adbets.database.AppDatabase;
import com.example.adbets.model.VideoUrl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExoPlayer offlinePlayer;
    private VideoUrl modelVideoUrl;
    private List<VideoUrl> modelVideoUrlList;
    private ExoPlayer player;
    private PlayerView playerView;
    private ArrayList<MediaItem> videoList;
    private ArrayList<MediaItem> modelMediaList;
    private FloatingActionButton downloadVideos;
    private long downloadID;
    private DownloadManager downloadManager;
    private String uriString = "";
    private ArrayList<String> tempList;
    private ConnectivityManager.NetworkCallback networkCallback;
    private final String[] videoUrls = {"https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_7378692246785011771DesignAsset_1707124198604_Ads%20offers.mp4", "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_10997764465025613630DesignAsset_1707128707418_dantam%20dental%20suite-2.mp4", "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/86/ASSETS/asset_14975988839848876724DesignAsset_1711686900898_World+Kidney+Day+Campaign_Digital+Screen+-+V1+(1).mp4", "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_6890123940375361769DesignAsset_1709667781043_Dantam%20Ad_01%20copy.mp4", "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_7360241592401237427DesignAsset_1709667785000_Dantam%20Ad_02%20copy.mp4"};

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        downloadVideos = findViewById(R.id.fab);
        videoList = new ArrayList<>();
        tempList = new ArrayList<>();
        modelVideoUrl = new VideoUrl();
        modelVideoUrlList = new ArrayList<>();
        modelMediaList = new ArrayList<>();

        String videoUrl1 = "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_7378692246785011771DesignAsset_1707124198604_Ads%20offers.mp4";
        String videoUrl2 = "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_10997764465025613630DesignAsset_1707128707418_dantam%20dental%20suite-2.mp4";
        String videoUrl3 = "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/86/ASSETS/asset_14975988839848876724DesignAsset_1711686900898_World+Kidney+Day+Campaign_Digital+Screen+-+V1+(1).mp4";
        String videoUrl4 = "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_6890123940375361769DesignAsset_1709667781043_Dantam%20Ad_01%20copy.mp4";
        String videoUrl5 = "https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_7360241592401237427DesignAsset_1709667785000_Dantam%20Ad_02%20copy.mp4";

        videoList.add(MediaItem.fromUri(videoUrl1));
        videoList.add(MediaItem.fromUri(videoUrl2));
        videoList.add(MediaItem.fromUri(videoUrl3));
        videoList.add(MediaItem.fromUri(videoUrl4));
        videoList.add(MediaItem.fromUri(videoUrl5));

        tempList.add(videoUrl1);
        tempList.add(videoUrl2);
        tempList.add(videoUrl3);
        tempList.add(videoUrl4);
        tempList.add(videoUrl5);


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
                        if (offlinePlayer != null) {
                            offlinePlayer.release();
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
                downloadVideos();
            }
        });
    }


    @OptIn(markerClass = UnstableApi.class)
    public void playOfflineVideos() {

        new Thread(() -> {
            modelVideoUrlList = AppDatabase.getDataBase(this).videoUrlDao().getAllVideoUrl();
            Log.e("TAG", "PLAY OFFLINE modelVideoUrlList  " + modelVideoUrlList.size());

            if (!modelVideoUrlList.isEmpty()) {
                runOnUiThread(() -> {
                    for (VideoUrl videoUrl : modelVideoUrlList) {
                        modelMediaList.add(MediaItem.fromUri(videoUrl.getName()));
                        Log.e("TAG", "URL " + videoUrl.getName());
                    }


                    offlinePlayer = new ExoPlayer.Builder(this).build();

                    // MediaItem mediaItem = new MediaItem.Builder()
                    //        .setUri(Uri.parse(uri))
                    // .setDataSourceFactory(factory)
                    //      .build();
                    offlinePlayer.setMediaItems(modelMediaList);
                    //  simpleExoPlayer.setMediaItem(mediaItem);
                    offlinePlayer.prepare();
                    offlinePlayer.setRepeatMode(player.REPEAT_MODE_ALL);
                    playerView.setPlayer(offlinePlayer);
                    offlinePlayer.setPlayWhenReady(true);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "No Video's available", Toast.LENGTH_SHORT).show();
                });

            }

        }).start();
    }


    private void downloadVideos() {
        if (Util.checkInternetStatus(getApplicationContext())) {
            Toast.makeText(MainActivity.this, "Download Started", Toast.LENGTH_SHORT).show();

            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            //  DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl1));
            DownloadManager.Request request = null;
            for (String temp : videoUrls) {
                Uri uri = Uri.parse(temp);
                Log.e("TAG", "URI  " + uri);
                new DownloadManager.Request(Uri.parse(String.valueOf(uri)));
                Log.e("TAG", "TESTING" + temp.toString());
                request = new DownloadManager.Request(Uri.parse(temp));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setTitle("Video Download");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                // Set the local destination for the downloaded file to a path within the application's internal storage
                request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_MOVIES, "adBeetsVideos.mp4");
                // request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "adBeetsVideos.mp4");

                // Enqueue the download and save the ID
                Log.e("TAG", "DOWNLOAD request " + request.toString());
                downloadID = downloadManager.enqueue(request);
                Log.e("TAG", "DOWNLOAD ID " + downloadID);
                // Set BroadcastReceiver to receive when download is complete
                registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                Log.e("TAG", "TESTING 333");

            }

        }
    }


    // BroadcastReceiver to receive when download is complete
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.e("TAG", "onReceive 22  " + id + "  downloadID  " + downloadID);
            if (id > 0) {
                Log.e("TAG", "onReceive downloadID " + downloadID);
                // Toast.makeText(MainActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();
                // Perform operations on successful download
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // Get the local URI of the downloaded file
                        String localUri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));

                        // Convert URI to File path
                        String filePath = Uri.parse(localUri).getPath();

                        // Now filePath contains the path of the downloaded video file
                        // You can move or copy this file to any other directory as needed
                        // For example, move it to a specific directory
                        File sourceFile = new File(filePath);
                        File destinationDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "adBeetsVideos");
                        //   File destinationDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "adBeetsVideos");
                        if (!destinationDir.exists()) {
                            destinationDir.mkdirs();
                        }

                        File destinationFile = new File(destinationDir, sourceFile.getName());

                        // Perform move operation
                        if (sourceFile.renameTo(destinationFile)) {
                            // File moved successfully
                            uriString = destinationFile.getAbsolutePath();
                            Log.d("TAG", "File moved to: " + destinationFile.getAbsolutePath());
                            modelVideoUrl.setName(uriString);
                            // AppDatabase.getDataBase(getApplicationContext()).videoUrlDao().insert(modelVideoUrl);
                        } else {
                            // Failed to move file
                            Log.e("TAG", "Failed to move file");
                        }
                        // AppDatabase.getDataBase(getApplicationContext()).videoUrlDao().insert(modelVideoUrl);
                        Log.e("TAG", "ELSE");
                    }
                }
                new Thread(() -> {
                    AppDatabase.getDataBase(getApplicationContext()).videoUrlDao().insert(modelVideoUrl);
                }).start();
                cursor.close();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        if (offlinePlayer != null) {
            offlinePlayer.setPlayWhenReady(false);
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
        offlinePlayer.release();
        unregisterReceiver(receiver);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
        if (offlinePlayer != null) {
            offlinePlayer.setPlayWhenReady(true);
        }

    }
}
