package com.debarunlahiri.burnabvideo.Home.Categories;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.debarunlahiri.burnabvideo.R;

import java.util.List;

public class CategoryNameAdapter extends RecyclerView.Adapter<CategoryNameAdapter.ViewHolder> {

    private Context mContext;
    private List<CategoryName> categoryNameList;


    public CategoryNameAdapter(Context mContext, List<CategoryName> categoryNameList) {
        this.mContext = mContext;
        this.categoryNameList = categoryNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_name_layout_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CategoryName categoryName = categoryNameList.get(position);

        holder.tvCategoryName.setText(categoryName.getCategory_name());

        final String category_name = categoryName.getCategory_name();

        holder.categoriesNameCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(mContext, CategoriesActivity.class);
                categoryIntent.putExtra("category_name", category_name);
                mContext.startActivity(categoryIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView categoriesNameCV;
        private TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoriesNameCV = itemView.findViewById(R.id.categoriesNameCV);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
