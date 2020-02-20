package com.debarunlahiri.burnabvideo.Search;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.debarunlahiri.burnabvideo.R;

public class SearchActivity extends AppCompatActivity {

    private ImageView ivSearchBack, tvSearchClear;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ivSearchBack = findViewById(R.id.ivSearchBack);
        tvSearchClear = findViewById(R.id.tvSearchClear);
        etSearch = findViewById(R.id.etSearch);

        tvSearchClear.setVisibility(View.INVISIBLE);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 3) {
                    tvSearchClear.setVisibility(View.VISIBLE);
                } else {
                    tvSearchClear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        });

        tvSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
