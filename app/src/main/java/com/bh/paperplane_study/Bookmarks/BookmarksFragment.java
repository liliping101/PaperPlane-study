package com.bh.paperplane_study.Bookmarks;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bh.paperplane_study.R;
import com.bh.paperplane_study.adapter.BookmarksAdapter;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.DoubanMomentNews;
import com.bh.paperplane_study.bean.GuokrHandpickNews;
import com.bh.paperplane_study.bean.ZhihuDailyNews;
import com.bh.paperplane_study.interfaze.OnRecyclerViewOnClickListener;
import com.bh.paperplane_study.search.SearchActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment implements BookmarksContract.View {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    BookmarksContract.Presenter presenter;
    BookmarksAdapter adapter;

    public BookmarksFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }

    @Override
    public void showResults(ArrayList<ZhihuDailyNews.Question> zhihuList,
                            ArrayList<GuokrHandpickNews.result> guokrList,
                            ArrayList<DoubanMomentNews.posts> doubanList,
                            ArrayList<Integer> types) {
        if(adapter==null) {
            adapter = new BookmarksAdapter(getActivity(), zhihuList, guokrList, doubanList, types);
            adapter.setItemListener(
                    new OnRecyclerViewOnClickListener() {
                        @Override
                        public void OnItemClick(View v, int position) {
                            int type = recyclerView.findViewHolderForLayoutPosition(position).getItemViewType();
                            if(type == BookmarksAdapter.TYPE_ZHIHU_NORMAL) {
                                presenter.startReading(BeanType.TYPE_ZHIHU, position);
                            } else if(type == BookmarksAdapter.TYPE_DOUBAN_NORMAL) {
                                presenter.startReading(BeanType.TYPE_DOUBAN, position);
                            } else if(type == BookmarksAdapter.TYPE_GUOKR_NORMAL) {
                                presenter.startReading(BeanType.TYPE_GUOKR, position);
                            }
                        }
                    }
            );
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void notifyDataChanged() {
        presenter.loadResults(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void stopLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void setPresenter(BookmarksContract.Presenter presenter) {
        if(presenter != null) {
            this.presenter = presenter;
        }
    }

    @Override
    public void initViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);

        setHasOptionsMenu(true);
        presenter.loadResults(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadResults(true);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bookmarks, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case  R.id.action_feel_lucky:
                presenter.feelLucky();
                break;
        }
        return true;
    }
}
