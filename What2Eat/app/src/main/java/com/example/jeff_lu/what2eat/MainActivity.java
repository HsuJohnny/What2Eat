package com.example.jeff_lu.what2eat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import java.util.List;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;

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
    Context mContext;

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


    }
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
