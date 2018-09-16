package com.droidmentor.purchasehelper.Util;

import android.app.Activity;

import com.droidmentor.purchasehelper.Model.ListingResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Jaison.
 */
public class JSONHelper
{
    // To return the specific JSON file from the assets folder
    private static String loadJSONFromAsset(Activity context, String filename) {
        String json;
        try {
            InputStream is = context.getAssets().open(filename + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
           // Log.d("", "loadJSONFromAsset: " + json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // To return the JSOn file response as specific Array of objects
    public static ArrayList<ListingResponse.City_listing> getCityListings(Activity activity, String fileName) {
        Gson gson = new Gson();
        ListingResponse response;
        response = gson.fromJson(loadJSONFromAsset(activity, fileName), ListingResponse.class);
        return response.getData().getCity_listing();
    }
}
