package com.debarunlahiri.burnabvideo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.FrameLayout;

public class CreateAccountPopupActivity extends AppCompatActivity {

    FrameLayout createaccountpopupheaderlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_popup);

        createaccountpopupheaderlayout = findViewById(R.id.createaccountpopupheaderlayout);
        createaccountpopupheaderlayout.setBackgroundColor(0x00000000);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
