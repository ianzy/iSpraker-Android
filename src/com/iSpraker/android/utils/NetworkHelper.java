package com.iSpraker.android.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class NetworkHelper {
	public static Bitmap fetchImage(String fileUrl){
        try {
            URL myFileUrl = new URL(fileUrl);
            HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(is);
            is.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
