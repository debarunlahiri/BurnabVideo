package com.debarunlahiri.burnabvideo.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.debarunlahiri.burnabvideo.R;

public class SettingsChangeProfilePicActivity extends AppCompatActivity {

    private Toolbar settingschangeprofilepictoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_change_profile_pic);

        settingschangeprofilepictoolbar = findViewById(R.id.settingschangeprofilepictoolbar);
        settingschangeprofilepictoolbar.setTitle("Change profile pic");
        setSupportActionBar(settingschangeprofilepictoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        settingschangeprofilepictoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        settingschangeprofilepictoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
