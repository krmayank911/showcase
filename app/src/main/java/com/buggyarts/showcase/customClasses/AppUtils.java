package com.buggyarts.showcase.customClasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AppUtils {

    public static final String STORAGE_PATH_UPLOADS = "images/";
    public static final String DATABASE_PATH_UPLOADS = "images";

    public static final String IMAGE_PLACE_HOLDER = "http://placehold.jp/300x300.png";

    private static Gson gson = new Gson();
    private static String TAG = AppUtils.class.getSimpleName();

    private static void saveValue(String key, String value,
                                  Context context) {

        SharedPreferences sp = context.getSharedPreferences("preferences",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getValue(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences("preferences",
                Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static ArrayList<String> getList(Context context) {

        String json = getValue("list", context);

        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return null;
    }

    public static void addToList(String article, Context context){

        ArrayList<String> imagesList;
        imagesList = getList(context);

        if(imagesList != null){
            imagesList.add(0, article);
        }else {
            imagesList = new ArrayList<>();
            imagesList.add(article);
        }

        saveImages(imagesList,context);

    }

    public static void saveImages(ArrayList<String> images, Context context) {
        Gson gson = new Gson();
        String jsonOptions = gson.toJson(images);
        saveValue("list", jsonOptions, context);
    }

}
