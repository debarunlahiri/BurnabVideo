package com.debarunlahiri.burnabvideo.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.debarunlahiri.burnabvideo.Channel.ChannelActivity;
import com.debarunlahiri.burnabvideo.MainActivity;
import com.debarunlahiri.burnabvideo.R;
import com.debarunlahiri.burnabvideo.StartActivity;
import com.debarunlahiri.burnabvideo.Utils.Variables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar settingstoolbar;

    private CardView settingsMyChannelCV, cvSettingsLogout;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private static String PREF_SELECT_CHANNEL = "select_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingstoolbar = findViewById(R.id.settingstoolbar);
        settingstoolbar.setTitle("Settings");
        setSupportActionBar(settingstoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingstoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        settingstoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cvSettingsLogout = findViewById(R.id.cvSettingsLogout);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");


        cvSettingsLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(PREF_SELECT_CHANNEL, MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                Variables.selected_channel_id = "";
                mAuth.signOut();
                Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout_settings_menu_item:
                sendToStart();
                mAuth.signOut();

        }
        return super.onOptionsItemSelected(item);
    }

    public void sendToStart() {
        Intent startIntent = new Intent(SettingsActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }
}
