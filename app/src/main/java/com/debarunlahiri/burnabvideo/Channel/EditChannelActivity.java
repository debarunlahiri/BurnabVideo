package com.debarunlahiri.burnabvideo.Channel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.debarunlahiri.burnabvideo.R;

public class EditChannelActivity extends AppCompatActivity {

    private Toolbar channeledittoolbar;

    private EditText etEditChannelPersonName;

    private String profile_image, banner_image, person_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_channel);

        channeledittoolbar = findViewById(R.id.channeledittoolbar);
        channeledittoolbar.setTitle("Edit Channel");
        setSupportActionBar(channeledittoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        channeledittoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        channeledittoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        });

        etEditChannelPersonName = findViewById(R.id.etEditChannelPersonName);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channel_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.channel_edit_save_menu_item:
                person_name = etEditChannelPersonName.getText().toString();

                if (person_name.isEmpty()) {
                    etEditChannelPersonName.setError("Name cannot be empty");
                } else {
                    Intent channelSaveIntent = new Intent(EditChannelActivity.this, ChannelActivity.class);
                    channelSaveIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(channelSaveIntent);
                }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(
                0,
                R.anim.play_panel_close_background
        );
    }
}
