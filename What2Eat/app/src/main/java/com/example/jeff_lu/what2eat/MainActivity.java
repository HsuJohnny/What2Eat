package com.example.jeff_lu.what2eat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.text.TextUtils;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStreamReader;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FoldableLayout mFoldableLayout;
    private MyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private static final String[] RESTAURANTS = new String[]{
            "test",
            "test1",
            "test2",
            "test3",
            "test4",
            "test5",
            "test6",
            "test7",
    };
    //private GoogleApiClient mGoogleApiClient;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    double latitude;
    double longitude;
    URL url;
    Thread th;
    Context mContext;
    private static final int restaurantNumber = 5;
    String[] restaurantTitleFromWeb = new String[restaurantNumber];
    String[] restaurantRatingFromWeb = new String[restaurantNumber];
    public void getRestaurantInfo(double latitude, double longitude){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.recyclerview);
        List<Restaurant> myDataset = new ArrayList<>();
        for(String name: RESTAURANTS ){
            myDataset.add(new Restaurant(name, name));
        }
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                //outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        //get location info, send it to getRestaurantInfo for information needed for list
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getRestaurantInfo(latitude, longitude);
        }
        th = new Thread(r0);
        th.start();
    }

    // crawl the google map web page and get the restaurant title and rating
    // data saved in MainActivity two string array
    // restaurantTitleFromWeb and restaurantRatingFromWeb
    private Runnable r0=new Runnable() {
        @Override
        public void run() {
            try{
                url = new URL("https://www.google.com.tw/maps/search/restaurant+/@latitude,longitude");
                Document doc = Jsoup.parse(url, 5000);
                Elements titles = doc.select("h3[class=section-result-title]");
                Elements ratings = doc.select("span[class=cards-rating-score]");
                for(int i=0;i<5;i++) {
                    restaurantTitleFromWeb[i] = titles.get(i).text();
                    restaurantRatingFromWeb[i] = ratings.get(i).text();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /*******************************************************************
    interface CrawlingCallback{
        void onPageCrawlingComplete();
        void onPageCrawlingFailed();
        void onCrawlingComplete();
    }

    private class CrawlerRunnable implements Runnable {
        CrawlingCallback mCallback;
        String mUrl;

        public CrawlerRunnable(CrawlingCallback callback, String Url) {
            this.mCallback = callback;
            this.mUrl = Url;
        }

        @Override
        public void run() {
            String pageContent = retrieveHtmlContent(mUrl);

            if (!TextUtils.isEmpty(pageContent.toString())) {
                //get the restaurant in google map page
                Document doc = Jsoup.parse(pageContent.toString());
                Elements title = doc.select(".section-result-title");
                Elements rating = doc.select(".cards-rating-score");

            }
        }

        private String retrieveHtmlContent(String Url) {
            URL httpUrl = null;
            try {
                httpUrl = new URL(Url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            int responseCode = HttpURLConnection.HTTP_OK;
            StringBuilder pageContent = new StringBuilder();
            try {
                if (httpUrl != null) {
                    HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    responseCode = conn.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        throw new IllegalAccessException("connection failed.");
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        pageContent.append(line);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
                mCallback.onPageCrawlingFailed();
            }catch (IllegalAccessException e) {
                e.printStackTrace();
                mCallback.onPageCrawlingFailed();
            }
            return pageContent.toString();
        }
    }
    ***********************************************************************/
    /***********************************************************************
    private class RunnableManager{
        private static final int KEEP_ALIVE_ITEM = 1;
        private final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        private static final int CORE_POOL_SIZE = 1;
        private static final int MAXIMUM_POOL_SIZE = 1;
        private final BlockingQueue<Runnable> mCrawlingQueue;

        private final ThreadPoolExecutor mCrawlingThreadPool;

        public RunnableManager() {
            mCrawlingQueue = new LinkedBlockingQueue<>();
            mCrawlingThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
                                                         MAXIMUM_POOL_SIZE,
                                                         KEEP_ALIVE_ITEM,
                                                         KEEP_ALIVE_TIME_UNIT,
                                                         mCrawlingQueue);
        }

        private void addToCrawlingQueue(Runnable runnable) {
            mCrawlingThreadPool.execute(runnable);
        }

        private void cancelAllRunnable() {
            mCrawlingThreadPool.shutdownNow();
        }

        private int getUnusedPoolSize() {
            return MAXIMUM_POOL_SIZE - mCrawlingThreadPool.getActiveCount();
        }

        private boolean isShuttingDown() {
            return mCrawlingThreadPool.isShutdown()
                    || mCrawlingThreadPool.isTerminating();
        }

    }
    ************************************************************************************/
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Restaurant> mData;

        public class ViewHolder extends RecyclerView.ViewHolder {
            View cover_view;
            View detail_view;
            ImageView cover_pic;
            ImageView detail_pic;
            TextView cover_text;
            public ViewHolder(FoldableLayout v) {
                super(v);
                cover_view = v.getCoverView();
                detail_view = v.getDetailView();
                cover_pic = (ImageView) cover_view.findViewById(R.id.imageview_cover);
                cover_text = (TextView) cover_view.findViewById(R.id.textview_cover);
                detail_pic = (ImageView) detail_view.findViewById(R.id.imageview_detail);
            }
        }

        public MyAdapter(List<Restaurant> data) {
            mData = data;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mFoldableLayout = new FoldableLayout(mContext);
            mFoldableLayout.setupViews(R.layout.small_view,R.layout.big_view,R.dimen.card_cover_height,mContext);
            ViewHolder vh = new ViewHolder(mFoldableLayout);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            int id = getResources().getIdentifier(mData.get(position).getImg(), "drawable", getPackageName());
            holder.cover_text.setText(mData.get(position).getName());
            holder.cover_pic.setImageResource(id);
            holder.detail_pic.setImageResource(id);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

}
