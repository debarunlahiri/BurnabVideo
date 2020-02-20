package com.debarunlahiri.burnabvideo.Home.Categories;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.debarunlahiri.burnabvideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    private Context mContext;
    private RecyclerView categoryNameRV;
    private List<CategoryName> categoryNameList = new ArrayList<>();
    private CategoryNameAdapter categoryNameAdapter;
    private LinearLayoutManager linearLayoutManager;


    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        mContext = getActivity();

        categoryNameRV = view.findViewById(R.id.categoryNameRV);
        categoryNameAdapter = new CategoryNameAdapter(mContext, categoryNameList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        categoryNameRV.setAdapter(categoryNameAdapter);
        categoryNameRV.setLayoutManager(linearLayoutManager);
        categoryNameRV.setHasFixedSize(true);

        categoryNameList.clear();

        initCategoryNames();



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void initCategoryNames() {
        CategoryName categoryNameEntertainment = new CategoryName("Entertainment");
        CategoryName categoryNameComedy = new CategoryName("Comedy");
        CategoryName categoryNameMusic = new CategoryName("Music");
        CategoryName categoryNameGaming = new CategoryName("Gaming");
        CategoryName categoryNameSports = new CategoryName("Sports");
        CategoryName categoryNameHowToStyle = new CategoryName("How to & Style");
        CategoryName categoryNameTechnology = new CategoryName("Technology");
        CategoryName categoryNameAutosVehicles = new CategoryName("Autos & Vehicles");
        CategoryName categoryNameEducation = new CategoryName("Education");

        categoryNameList.add(categoryNameEntertainment);
        categoryNameList.add(categoryNameComedy);
        categoryNameList.add(categoryNameMusic);
        categoryNameList.add(categoryNameGaming);
        categoryNameList.add(categoryNameSports);
        categoryNameList.add(categoryNameHowToStyle);
        categoryNameList.add(categoryNameTechnology);
        categoryNameList.add(categoryNameAutosVehicles);
        categoryNameList.add(categoryNameEducation);

        categoryNameAdapter.notifyDataSetChanged();
    }
}
