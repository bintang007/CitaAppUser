package com.cita.myapplication.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.cita.myapplication.utils.LruBitmapCache;

/**
 * Created by Bintang on 01/10/2019
 */

public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * Menginstiasikan obyek AppController
     *
     * @return Obyek AppController
     */
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /**
     * Mendapatkan queue sesuai dengan contextnya
     *
     * @param context Conntext dari queue yang ingin dibuat
     * @return Obyek queue
     */
    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> tRequest, Context context) {
        tRequest.setTag(TAG);
        getRequestQueue(context).add(tRequest);
    }

    /**
     * Menambahkan request kedalam RequestQueue
     *
     * @param tRequest Request
     * @param tag      Tag dari request yang telah dibuat
     * @param context  Context dari request dibuat
     * @param <T>      Tipe paramater
     */
    public <T> void addToRequestQueue(Request<T> tRequest, String tag, Context context) {
        tRequest.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue(context).add(tRequest);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ImageLoader getImageLoader() {
        getRequestQueue(getApplicationContext());
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }


}

