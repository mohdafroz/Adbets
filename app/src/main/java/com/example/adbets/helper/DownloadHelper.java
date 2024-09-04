package com.example.adbets.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.SparseArray;
import android.widget.Toast;

public class DownloadHelper {

    private Context context;
    private SparseArray<Long> downloadIds = new SparseArray<>();
    private int downloadCount = 0;

    public DownloadHelper(Context context) {
        this.context = context;
    }

    public void downloadVideo(String[] urls, String[] filenames) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            String filename = filenames[i];
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                    .setTitle("Downloading Video " + (i + 1))
                    .setDescription("Please wait...")
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, "adBeetsVideos.mp4");
            //  .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

            long downloadId = downloadManager.enqueue(request);
            downloadIds.put((int) downloadId, downloadId);
            downloadCount++;
        }

        Toast.makeText(context, "Downloading " + downloadCount + " videos", Toast.LENGTH_SHORT).show();
    }

    public SparseArray<Long> getDownloadIds() {
        return downloadIds;
    }

}
