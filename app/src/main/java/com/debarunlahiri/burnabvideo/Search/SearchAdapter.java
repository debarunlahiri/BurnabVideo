package com.debarunlahiri.burnabvideo.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.debarunlahiri.burnabvideo.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context mContext;
    private List<Search> searchList;
    private String searchType;

    private static final int HISTORY_SEARCH = 1;
    private static final int SEARCH = 2;

    public SearchAdapter(Context mContext, List<Search> searchList, String searchType) {
        this.mContext = mContext;
        this.searchList = searchList;
        this.searchType = searchType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HISTORY_SEARCH) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_history_search_layout, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_videos_layout, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Search search = searchList.get(position);
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (searchType.equals("history")) {
            return HISTORY_SEARCH;
        } else {
            return SEARCH;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHistorySearchName;
        private CardView cvHistorySearch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            if (searchType.equals("history")) {
                tvHistorySearchName = itemView.findViewById(R.id.tvHistorySearchName);
                cvHistorySearch = itemView.findViewById(R.id.cvHistorySearch);
            }


        }
    }
}
