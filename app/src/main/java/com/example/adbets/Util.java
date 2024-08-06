package com.example.adbets;

import androidx.annotation.NonNull;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Util {

    @NonNull
    public static String getUniqueID() {
        long time = System.currentTimeMillis();
        int no = rand(100000, 999999);
        return datechange_gmttt_format() + "" + time + no;
    }

    public static int rand(int min, int max) {
        Random random = new Random();
        int randomNum = random.nextInt(max) + min;
        Log.e("Lak", "Rand Num: " + randomNum);
        return randomNum;
    }

    //2017-09-23T21:00:00.000+0000
    public static String datechange_gmttt_format() {

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatGmt.setTimeZone(TimeZone
                .getTimeZone("GMT"));
        String gmt_date = dateFormatGmt
                .format(new Date());

        dateFormatGmt = new SimpleDateFormat(
                "HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone
                .getTimeZone("GMT"));

        String gmt_time = dateFormatGmt
                .format(new Date());

        String gmt_date_time = gmt_date + "T"
                + gmt_time;
        System.out.println("gmt_date_time "
                + gmt_date_time);
        return gmt_date_time;
    }

    public static boolean checkInternetStatus(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean status;
        assert conMgr != null;
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null) {
            status = false;
        } else if (!i.isConnected()) {
            status = false;
        } else status = i.isAvailable();

        if (!status) {
            Toast.makeText(context, "Please Check your Internet", Toast.LENGTH_SHORT).show();
        }
        return status;
    }

}
