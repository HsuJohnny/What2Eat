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
import android.util.Log;
import android.view.LayoutInflater;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity implements LocationListener{

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
    String stringLatitude = "25.0266686";
    String stringLogitude = "121.5371623";
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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.v("location", "succeed");
                }
            }else {
                Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
                Log.v("location manager","fail");
                latitude = 25.0266686;
                longitude = 121.5371623;
            }
        }else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
        }
        stringLatitude = String.valueOf(latitude);
        stringLogitude = String.valueOf(longitude);

        th = new Thread(r0);
        th.start();

    }

    @Override
    public void onLocationChanged(Location arg0){
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //status=OUT_OF_SERVICE
        //status=TEMPORARILY_UNAVAILABLE
    }

    // crawl the google map web page and get the restaurant title and rating
    // data saved in MainActivity two string array
    // restaurantTitleFromWeb and restaurantRatingFromWeb
    private Runnable r0=new Runnable() {
        @Override
        public void run() {
            try{
                url = new URL(String.format("https://www.google.com.tw/maps/search/restaurant+/@ %s,%s", stringLatitude, stringLogitude));
                Document doc = Jsoup.parse(url, 5000);
                Elements titles = doc.select("h3[class=section-result-title]");
                Elements ratings = doc.select("span[class=cards-rating-score]");
                for(int i=0;i<5;i++) {
                    restaurantTitleFromWeb[i] = titles.get(i).text();
                    restaurantRatingFromWeb[i] = ratings.get(i).text();
                }
                Log.v("into try","into try");
            }catch (Exception e) {
                e.printStackTrace();
                Log.v("didn't connect","fail");
            }
        }
    };

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
