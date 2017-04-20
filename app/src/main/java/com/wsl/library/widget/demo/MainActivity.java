package com.wsl.library.widget.demo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wsl.library.widget.DdLoadListener;
import com.wsl.library.widget.DdRefreshListener;
import com.wsl.library.widget.DdRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.dd_refresh_content)
    RecyclerView rvContent;

    @BindView(R.id.refresh_layout)
    DdRefreshLayout refreshLayout;

    private TestAdapter testAdapter;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    testAdapter.add("pull item ");
                    refreshLayout.setRefresh(false);
                    break;
                case 1:
                    testAdapter.add("load item ");
                    refreshLayout.setLoad(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        rvContent.setHasFixedSize(true);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setItemAnimator(new DefaultItemAnimator());

        testAdapter = new TestAdapter(20);
        rvContent.setAdapter(testAdapter);

        refreshLayout.setRefreshListener(new DdRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(0, 2000);
            }
        });

        refreshLayout.setLoadListener(new DdLoadListener() {
            @Override
            public void onLoadMore() {
                mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        });
    }
}
