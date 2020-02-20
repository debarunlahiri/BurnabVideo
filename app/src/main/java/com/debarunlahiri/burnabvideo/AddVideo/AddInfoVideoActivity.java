package com.debarunlahiri.burnabvideo.AddVideo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnabvideo.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;

public class AddInfoVideoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar addinfovideotoolbar;

    private ImageView ivAddInfoVideoThumbnail;
    private Button addinfovideoreplacethumbbutton, addinfovideouploadbutton;
    private TextView etAddInfoVideoTitle, etAddInfoVideoDesc;
    private Spinner videoAddInfoSpinner;

    private Uri videoUri, videoThumbnailUri;
    private Bitmap videoThumbnailBitmap;
    private String videoTitle, videoDesc, videoDate;
    private int videoHeight, videoWidth;
    private long videoDuration;
    private String categoryItem;
    private Bitmap mCompressedVideoThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info_video);

        Bundle bundle = getIntent().getExtras();
        videoUri = Uri.parse(bundle.getString("videoUri"));
        videoThumbnailUri = Uri.parse(bundle.getString("videoThumbnail"));
        videoHeight = bundle.getInt("videoHeight");
        videoWidth = bundle.getInt("videoWidth");
        videoDuration = bundle.getLong("videoDuration");

        addinfovideotoolbar = findViewById(R.id.addinfovideotoolbar);
        addinfovideotoolbar.setTitle("Add info");
        setSupportActionBar(addinfovideotoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addinfovideotoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        addinfovideotoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivAddInfoVideoThumbnail = findViewById(R.id.ivAddInfoVideoThumbnail);;
        addinfovideoreplacethumbbutton = findViewById(R.id.addinfovideoreplacethumbbutton);
        etAddInfoVideoTitle = findViewById(R.id.etAddInfoVideoTitle);
        etAddInfoVideoDesc = findViewById(R.id.etAddInfoVideoDesc);
        addinfovideouploadbutton = findViewById(R.id.addinfovideouploadbutton);
        videoAddInfoSpinner = findViewById(R.id.videoAddInfoSpinner);

        videoAddInfoSpinner.setOnItemSelectedListener(this);

        List<String> categoriesList = new ArrayList<>();
        categoriesList.add("Select Category");
        categoriesList.add("Entertainment");
        categoriesList.add("Comedy");
        categoriesList.add("Music");
        categoriesList.add("Gaming");
        categoriesList.add("Sports");
        categoriesList.add("How to & Style");
        categoriesList.add("Technology");
        categoriesList.add("Autos & Vehicles");
        categoriesList.add("Education");
        categoriesList.add("Politics");

        ArrayAdapter<String> categoriesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        videoAddInfoSpinner.setAdapter(categoriesDataAdapter);


        Glide.with(getApplicationContext()).load(videoThumbnailUri).into(ivAddInfoVideoThumbnail);

        addinfovideoreplacethumbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(16, 9)
                        .start(AddInfoVideoActivity.this);
            }
        });

        addinfovideouploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                videoTitle = etAddInfoVideoTitle.getText().toString();
                videoDesc = etAddInfoVideoDesc.getText().toString();

                if (videoUri == null) {
                    Toast.makeText(getApplicationContext(), "Video missing", Toast.LENGTH_SHORT).show();
                } else if (categoryItem.equals("Select Category")) {
                    Toast.makeText(getApplicationContext(), "Please select a video category", Toast.LENGTH_LONG).show();
                } else {
                    if (videoTitle.isEmpty()) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
                        videoTitle = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                        Intent uploadVideoIntent = new Intent(AddInfoVideoActivity.this, VideoUploadActivity.class);
                        uploadVideoIntent.putExtra("videoUri", videoUri.toString());
                        uploadVideoIntent.putExtra("videoThumbnailUri", videoThumbnailUri.toString());
                        uploadVideoIntent.putExtra("videoTitle", videoTitle);
                        uploadVideoIntent.putExtra("videoDesc", videoDesc);
                        uploadVideoIntent.putExtra("videoHeight", videoHeight);
                        uploadVideoIntent.putExtra("videoWidth", videoWidth);
                        uploadVideoIntent.putExtra("videoDuration", videoDuration);
                        uploadVideoIntent.putExtra("videoCategory", categoryItem);
                        startActivity(uploadVideoIntent);
                        finish();
                    } else {
                        Intent uploadVideoIntent = new Intent(AddInfoVideoActivity.this, VideoUploadActivity.class);
                        uploadVideoIntent.putExtra("videoUri", videoUri.toString());
                        uploadVideoIntent.putExtra("videoThumbnailUri", videoThumbnailUri.toString());
                        uploadVideoIntent.putExtra("videoTitle", videoTitle);
                        uploadVideoIntent.putExtra("videoDesc", videoDesc);
                        uploadVideoIntent.putExtra("videoHeight", videoHeight);
                        uploadVideoIntent.putExtra("videoWidth", videoWidth);
                        uploadVideoIntent.putExtra("videoDuration", videoDuration);
                        uploadVideoIntent.putExtra("videoCategory", categoryItem);
                        startActivity(uploadVideoIntent);
                        finish();
                    }


                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                videoThumbnailUri = result.getUri();

                Glide.with(getApplicationContext()).load(videoThumbnailUri).into(ivAddInfoVideoThumbnail);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categoryItem = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
