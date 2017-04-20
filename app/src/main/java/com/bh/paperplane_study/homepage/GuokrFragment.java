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
import com.bh.paperplane_study.adapter.GuokrNewsAdapter;
import com.bh.paperplane_study.bean.GuokrHandpickNews;
import com.bh.paperplane_study.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuokrFragment extends Fragment implements GuokrContract.View {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private GuokrNewsAdapter adapter;
    private GuokrContract.Presenter presenter;
    private boolean isFragmentVisiable = false;
    private boolean isFail = false;
    private Snackbar snackbar = null;

    public GuokrFragment() {
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

    public static GuokrFragment newInstance() {
        return new GuokrFragment();
    }

    @Override
    public void showResults(ArrayList<GuokrHandpickNews.result> list) {
        isFail = false;
        if(adapter==null) {
            adapter = new GuokrNewsAdapter(getContext(),list);
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
            snackbar = Snackbar.make(recyclerView, "加载失败", Snackbar.LENGTH_INDEFINITE)
                    .setAction("重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.refresh();
                        }
                    });
            snackbar.show();
        }

    }

    @Override
    public void setPresenter(GuokrContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
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
    }

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
}
