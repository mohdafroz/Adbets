package com.example.adbets.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.example.adbets.database.AppDatabase;
import com.example.adbets.model.VideoUrl;
import java.io.File;

public class BroadcastDownloadReceiver extends BroadcastReceiver {

    private VideoUrl modelVideoUrl;

    @Override
    public void onReceive(Context context, Intent intent) {
        modelVideoUrl = new VideoUrl();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != -1) {
                Log.e("TAG", "onReceive id " + id);
                // Perform operations on successful download
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

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
                            String uriString = destinationFile.getAbsolutePath();
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
                    AppDatabase.getDataBase(context).videoUrlDao().insert(modelVideoUrl);
                }).start();
                cursor.close();
            }
        }
    }
}
