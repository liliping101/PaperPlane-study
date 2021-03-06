package com.bh.paperplane_study.homepage;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bh.paperplane_study.R;
import com.bh.paperplane_study.adapter.DoubanMomentAdapter;
import com.bh.paperplane_study.bean.DoubanMomentNews;
import com.bh.paperplane_study.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoubanMomentFragment extends Fragment implements DoubanMomentContract.View {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private DoubanMomentContract.Presenter presenter;
    private DoubanMomentAdapter adapter;
    private boolean isFragmentVisiable = false;
    private boolean isFail = false;
    private Snackbar snackbar = null;

    public DoubanMomentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        initViews();
        presenter.start();
        return view;
    }

    public static DoubanMomentFragment newInstance() {
        return new DoubanMomentFragment();
    }

    @Override
    public void showResults(ArrayList<DoubanMomentNews.posts> list) {
        isFail = false;
        if(adapter==null) {
            adapter = new DoubanMomentAdapter(getContext(), list);
            adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
                @Override
                public void OnItemClick(View v, int position) {
                    presenter.startReading(position);
                }
            });
            recyclerView.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setPresenter(DoubanMomentContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void showLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void stopLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showError() {
        isFail = true;
        if(isFragmentVisiable) {
            snackbar = Snackbar.make(recyclerView, R.string.loaded_failed, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.refresh();
                        }
                    });
            snackbar.show();
        }
    }


    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void initViews() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refresh();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if(newState==RecyclerView.SCROLL_STATE_IDLE) {
                    // 获取最后一个完全显示的item position
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemNum = manager.getItemCount();

                    if(lastVisibleItem==(totalItemNum-1)&& isSlidingToLast) {
                        presenter.loadMore();
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0;
            }
        });
    }

    //需要注意以下方法并不完美，Fragment不可见的信号会被多次发送。目前还没找到一个只发送一次的方法。
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {
            onResume();
        } else if (!isVisibleToUser) {
            onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            isFragmentVisiable = true;
            if(isFail) {
                presenter.refresh();
                if(snackbar!=null) {
                    snackbar.dismiss();
                    snackbar = null;
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisiable = false;
        if(snackbar!=null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }
    //需要注意以上方法并不完美，Fragment不可见的信号会被多次发送。目前还没找到一个只发送一次的方法。
}
