package com.debarunlahiri.burnabvideo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.debarunlahiri.burnabvideo.Utils.IPAddress;
import com.debarunlahiri.burnabvideo.Utils.Variables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private Button loginbutton;
    private ProgressBar loginPB;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        loginbutton = findViewById(R.id.loginbutton);
        loginPB = findViewById(R.id.loginPB);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-video.appspot.com");

        loginPB.setVisibility(View.GONE);

        if (currentUser != null) {
            sendToMain();
        }

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        loginPB.setVisibility(View.VISIBLE);
        loginbutton.setVisibility(View.GONE);
        email = etLoginEmail.getText().toString();
        password = etLoginPassword.getText().toString();

        if (email.isEmpty()) {
            loginPB.setVisibility(View.GONE);
            loginbutton.setVisibility(View.VISIBLE);
            etLoginEmail.setError("Please enter your email");
        } else if (password.isEmpty()) {
            loginPB.setVisibility(View.GONE);
            loginbutton.setVisibility(View.VISIBLE);
            etLoginPassword.setError("Please enter your password");
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String login_id = mDatabase.child("users").child(task.getResult().getUser().getUid()).push().getKey();
                        String user_id = task.getResult().getUser().getUid();
                        String version_name = null;
                        long version_code = 0;
                        try {
                            PackageInfo pInfo = LoginActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                            version_name = pInfo.versionName;
                            version_code = pInfo.getLongVersionCode();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        HashMap<String, Object> mLoginInfoDataMap = new HashMap<>();
                        mLoginInfoDataMap.put("login_id", login_id);
                        mLoginInfoDataMap.put("user_id", user_id);
                        mLoginInfoDataMap.put("timestamp", System.currentTimeMillis());
                        mLoginInfoDataMap.put("version_name", version_name);
                        mLoginInfoDataMap.put("version_code", version_code);

                        mDatabase.child("users").child(user_id).child("login").child(login_id).updateChildren(mLoginInfoDataMap);

                        setUserDeviceDetail(user_id, login_id);
                        setUserIPAddressAndMACAddress(user_id, login_id);
                        sendToMain();
                        loginPB.setVisibility(View.GONE);
                    } else {
                        loginPB.setVisibility(View.GONE);
                        loginbutton.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loginPB.setVisibility(View.GONE);
                    loginbutton.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setUserDeviceDetail(String uid, String login_id) {
        Log.i("TAG", "SERIAL: " + Build.SERIAL);
        Log.i("TAG","MODEL: " + Build.MODEL);
        Log.i("TAG","ID: " + Build.ID);
        Log.i("TAG","Manufacture: " + Build.MANUFACTURER);
        Log.i("TAG","brand: " + Build.BRAND);
        Log.i("TAG","type: " + Build.TYPE);
        Log.i("TAG","user: " + Build.USER);
        Log.i("TAG","BASE: " + Build.VERSION_CODES.BASE);
        Log.i("TAG","INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.i("TAG","SDK  " + Build.VERSION.SDK_INT);
        Log.i("TAG","BOARD: " + Build.BOARD);
        Log.i("TAG","BRAND " + Build.BRAND);
        Log.i("TAG","HOST " + Build.HOST);
        Log.i("TAG","FINGERPRINT: "+Build.FINGERPRINT);
        Log.i("TAG","Version Code: " + Build.VERSION.RELEASE);

        HashMap<String, Object> mUserDeviceDetail = new HashMap<>();
        mUserDeviceDetail.put("SERIAL: ", Build.SERIAL);
        mUserDeviceDetail.put("MODEL: ", Build.MODEL);
        mUserDeviceDetail.put("ID: ", Build.ID);
        mUserDeviceDetail.put("MANUFACTURER: ", Build.MANUFACTURER);
        mUserDeviceDetail.put("BRAND: ", Build.BRAND);
        mUserDeviceDetail.put("TYPE: ", Build.TYPE);
        mUserDeviceDetail.put("USER: ", Build.USER);
        mUserDeviceDetail.put("BASE: ", Build.VERSION_CODES.BASE);
        mUserDeviceDetail.put("INCREMENTAL ", Build.VERSION.INCREMENTAL);
        mUserDeviceDetail.put("SDK  ", Build.VERSION.SDK_INT);
        mUserDeviceDetail.put("BOARD: ", Build.BOARD);
        mUserDeviceDetail.put("BRAND ", Build.BRAND);
        mUserDeviceDetail.put("HOST ", Build.HOST);
        mUserDeviceDetail.put("FINGERPRINT: ", Build.FINGERPRINT);
        mUserDeviceDetail.put("VERSION CODE: ", Build.VERSION.RELEASE);

        mDatabase.child("users").child(uid).child("login").child(login_id).child("user_device_detail").updateChildren(mUserDeviceDetail);
    }

    private void setUserIPAddressAndMACAddress(String uid, String login_id) {
        Variables.user_ip_address_ipv4 = IPAddress.getIPAddress(true);
        Variables.user_ip_address_ipv6 = IPAddress.getIPAddress(false);
        Variables.user_mac_address_eth0 = IPAddress.getMACAddress("eth0");
        Variables.user_mac_address_wlan0 = IPAddress.getMACAddress("wlan0");

        HashMap<String, Object> mUserIPandMACDataMap = new HashMap<>();
        mUserIPandMACDataMap.put("ip_address_ipv4", Variables.user_ip_address_ipv4);
        mUserIPandMACDataMap.put("ip_address_ipv6", Variables.user_ip_address_ipv6);
        mUserIPandMACDataMap.put("user_mac_address_eth0", Variables.user_mac_address_eth0);
        mUserIPandMACDataMap.put("user_mac_address_wlan0", Variables.user_mac_address_wlan0);

        mDatabase.child("users").child(uid).child("login").child(login_id).child("ip_and_mac_add").updateChildren(mUserIPandMACDataMap);
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
