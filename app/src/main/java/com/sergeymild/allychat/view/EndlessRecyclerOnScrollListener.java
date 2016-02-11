package com.sergeymild.allychat.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by sergeyMild on 10/12/15.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int VISIBLE_THRESHOLD = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int totalItemCount;
    private boolean isStackFromBottom;

    private int currentPage = 0;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
        isStackFromBottom = mLinearLayoutManager.getStackFromEnd();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        totalItemCount = mLinearLayoutManager.getItemCount();

        if (isStackFromBottom) {
            stackFromBottom();
        }
    }

    protected void stackFromBottom() {
        if (previousTotal == 0) {
            previousTotal = totalItemCount - 1;
        }


        if (loading) {
            if (previousTotal < totalItemCount) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        //check for last item
        if (!loading && mLinearLayoutManager.findFirstVisibleItemPosition() < VISIBLE_THRESHOLD) {
            loading = true;

            currentPage++;
            onLoadMore(currentPage);
        }
    }

    public abstract void onLoadMore(int currentPage);
}
