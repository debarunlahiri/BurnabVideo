package com.debarunlahiri.burnabvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.debarunlahiri.burnabvideo.Channel.Channel;
import com.debarunlahiri.burnabvideo.Channel.ChannelFragment;
import com.debarunlahiri.burnabvideo.Channel.SelectChannelAdapter;
import com.debarunlahiri.burnabvideo.Discover.DiscoverFragment;
import com.debarunlahiri.burnabvideo.Home.HomeFragment;
import com.debarunlahiri.burnabvideo.Profile.ProfileFragment;
import com.debarunlahiri.burnabvideo.SetupAccount.SetupAccountImageActivity;
import com.debarunlahiri.burnabvideo.SetupAccount.SetupAccountNameActivity;
import com.debarunlahiri.burnabvideo.SetupAccount.SetupAccountUsernameActivity;
import com.debarunlahiri.burnabvideo.Utils.IPAddress;
import com.debarunlahiri.burnabvideo.Utils.Variables;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mainFrameLayout;
    private BottomNavigationView mainBottomNavigationView;

    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private DiscoverFragment discoverFragment;
    private ChannelFragment channelFragment;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String channel_id;
    private String user_id;
    private boolean hasChannel = false;
    private boolean isChannelSelected = false;
    private static String PREF_SELECT_CHANNEL = "select_channel";

    private Dialog dialog;

    private Context mContext;
    private List<Channel> channelList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        mainFrameLayout = findViewById(R.id.mainFrameLayout);
        mainBottomNavigationView = findViewById(R.id.mainBottomNavigationView);

        mainBottomNavigationView.setBackgroundColor(Color.WHITE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        //Getting user's IP Address and MAC Address
        setUserIPAddressAndMACAddress();

        //Getting user's Device details
        setUserDeviceDetail();




        if (currentUser != null) {
            user_id = currentUser.getUid();

            checkUserHasNameOrNot();
            checkUserHasChannelOrNot();

            SharedPreferences sharedPreferencesSelectChannel = getSharedPreferences(PREF_SELECT_CHANNEL, MODE_PRIVATE);
            channel_id = sharedPreferencesSelectChannel.getString("channel_id", null);
            Variables.selected_channel_id = channel_id;


        }

        homeFragment = new HomeFragment();
        discoverFragment = new DiscoverFragment();
        profileFragment = new ProfileFragment();
        channelFragment = new ChannelFragment();

        loadFragment(homeFragment);

        final Bundle bundle = new Bundle();
        bundle.putString("channel_id", channel_id);

        mainBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {


                    case R.id.home_main_bottom_menu_item:
                        homeFragment.setArguments(bundle);
                        loadFragment(homeFragment);
                        return true;

                    case R.id.discover_main_bottom_menu_item:
                        discoverFragment.setArguments(bundle);
                        loadFragment(discoverFragment);
                        return true;

                    case R.id.profile_main_bottom_menu_item:
                        if (hasChannel == false) {
                            loadFragment(profileFragment);
                        } else {
                            channelFragment.setArguments(bundle);
                            loadFragment(channelFragment);
                        }

                        return true;
                }
                return false;
            }
        });


    }

    private void setUserDeviceDetail() {
        Log.i("TAG", "SERIAL: " + Build.SERIAL);
        Log.i("TAG","MODEL: " + Build.MODEL);
        Log.i("TAG","ID: " + Build.ID);
        Log.i("TAG","Manufacture: " + Build.MANUFACTURER);
        Log.i("TAG","brand: " + Build.BRAND);
        Log.i("TAG","type: " + Build.TYPE);
        Log.i("TAG","user: " + Build.USER);
        Log.i("TAG","BASE: " + Build.VERSION_CODES.BASE);
        Log.i("TAG","INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.i("TAG","SDK  " + Build.VERSION.SDK);
        Log.i("TAG","BOARD: " + Build.BOARD);
        Log.i("TAG","BRAND " + Build.BRAND);
        Log.i("TAG","HOST " + Build.HOST);
        Log.i("TAG","FINGERPRINT: "+Build.FINGERPRINT);
        Log.i("TAG","Version Code: " + Build.VERSION.RELEASE);
    }

    private void setUserIPAddressAndMACAddress() {
        Variables.user_ip_address_ipv4 = IPAddress.getIPAddress(true);
        Variables.user_ip_address_ipv6 = IPAddress.getIPAddress(false);
        Variables.user_mac_address_eth0 = IPAddress.getMACAddress("eth0");
        Variables.user_mac_address_wlan0 = IPAddress.getMACAddress("wlan0");
    }

    private void checkUserHasChannelOrNot() {
        mDatabase.child("users").child(user_id).child("hasChannel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mDatabase.child("users").child(user_id).child("hasChannel").setValue(false);
                    hasChannel = false;
                } else {
                    hasChannel = (Boolean) dataSnapshot.getValue();

                    if (hasChannel) {
                        SharedPreferences sharedPreferencesSelectChannel = getSharedPreferences(PREF_SELECT_CHANNEL, MODE_PRIVATE);
                        if (sharedPreferencesSelectChannel.contains("channel_id")) {
                            channel_id = sharedPreferencesSelectChannel.getString("channel_id", null);
                        } else {
                            dialogSelectChannel();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void dialogSelectChannel() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_select_channel);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);;
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        LocalBroadcastManager.getInstance(this).registerReceiver(selectChannelBrodcastReceiver,
                new IntentFilter("selectChannelBroadcast"));

        RecyclerView rvDialogSelectChannel = dialog.findViewById(R.id.rvDialogSelectChannel);
        final SelectChannelAdapter selectChannelAdapter = new SelectChannelAdapter(mContext, channelList, dialog);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvDialogSelectChannel.setAdapter(selectChannelAdapter);
        rvDialogSelectChannel.setLayoutManager(linearLayoutManager);

        Button bDialogSelectChannel = dialog.findViewById(R.id.bDialogSelectChannel);

        bDialogSelectChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChannelSelected) {
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Select Channel", Toast.LENGTH_LONG).show();
                }
            }
        });

        mDatabase.child("users").child(user_id).child("channels").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Channel channel = dataSnapshot.getValue(Channel.class);
                channelList.add(channel);
                selectChannelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public BroadcastReceiver selectChannelBrodcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isChannelSelected = intent.getBooleanExtra("selectChannelClick", false);
        }
    };


    private void checkUserHasUsernameOrNot() {
        mDatabase.child("users").child(user_id).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Intent userUsernameIntent = new Intent(MainActivity.this, SetupAccountUsernameActivity.class);
                    startActivity(userUsernameIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserHasProfilePicOrNot() {
        mDatabase.child("users").child(user_id).child("profile_image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Intent userProfilePicIntent = new Intent(MainActivity.this, SetupAccountImageActivity.class);
                    startActivity(userProfilePicIntent);
                    finish();
                } else {
                    checkUserHasUsernameOrNot();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserHasNameOrNot() {
        mDatabase.child("users").child(user_id).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Intent userNameIntent = new Intent(MainActivity.this, SetupAccountNameActivity.class);
                    startActivity(userNameIntent);
                    finish();
                } else {
                    checkUserHasProfilePicOrNot();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (hasChannel && Variables.selected_channel_id.equals("")) {
            checkUserHasChannelOrNot();
        } else {
            super.onBackPressed();
        }

    }
}
