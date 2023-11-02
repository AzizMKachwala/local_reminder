package com.example.localreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    ImageView imgLock;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    UpcomingFragment upcomingFragment;
    CompletedFragment completedFragment;
    SwitchMaterial switchLock;
    LinearLayout lyt;
    private SharedPreferences sharedPreferences;
    private static final String SWITCH_STATE_KEY = "biometricSwitchState";
    private boolean isBiometricEnabled = false;
    private boolean userLoggedIn = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabMode);
        viewPager2 = findViewById(R.id.viewPager);
        switchLock = findViewById(R.id.switchLock);
        lyt = findViewById(R.id.lyt);
        imgLock = findViewById(R.id.imgLock);

        upcomingFragment = new UpcomingFragment();
        completedFragment = new CompletedFragment();

        viewPager2.setAdapter(new MVPAdapter(MainActivity.this));

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Upcoming");
                } else if (position == 1) {
                    tab.setText("Completed");
                }
            }
        }).attach();

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isBiometricEnabled = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false);
        switchLock.setChecked(isBiometricEnabled);

        switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isBiometricEnabled = isChecked;
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, isBiometricEnabled).apply();

            if (isChecked) {
                imgLock.setImageResource(R.drawable.baseline_lock_24);
                enableBiometricAuthentication();
            } else {
                imgLock.setImageResource(R.drawable.baseline_unlock_24);
                disableBiometricAuthentication();
            }
        });

        if (isBiometricEnabled) {
            lyt.setVisibility(View.GONE);
            enableBiometricAuthentication();
        }
    }

    private void disableBiometricAuthentication() {
        Toast.makeText(this, "App Lock Deactivated", Toast.LENGTH_SHORT).show();
    }

    private void enableBiometricAuthentication() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            Executor executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(MainActivity.this, "App Lock Activated", Toast.LENGTH_SHORT).show();
                    lyt.setVisibility(View.VISIBLE);
                    switchLock.setChecked(true);
                    userLoggedIn = true;
                    imgLock.setImageResource(R.drawable.baseline_lock_24);
                }

                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(MainActivity.this, errString, Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Authentication");
                    builder.setMessage("Authentication is Required. \nDo you want to continue without Security?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            lyt.setVisibility(View.GONE);
                            imgLock.setImageResource(R.drawable.baseline_unlock_24);
                            dialogInterface.dismiss();
                            enableBiometricAuthentication();
                            lyt.setVisibility(View.VISIBLE);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!userLoggedIn) {
                                switchLock.setChecked(true);
                                finish();
                            } else {
                                switchLock.setChecked(false); // Keep the switch in the checked state
                                lyt.setVisibility(View.VISIBLE);
                                imgLock.setImageResource(R.drawable.baseline_unlock_24);
                                dialogInterface.dismiss();
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Unlock Lock Reminder")
                    .setDescription("Use PIN, PATTERN or PASSWORD to unlock")
                    .setDeviceCredentialAllowed(true)
                    .setNegativeButtonText(null)
                    .build();
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "Device Doesn't Support Biometric Authentication", Toast.LENGTH_SHORT).show();
            switchLock.setChecked(false);
            imgLock.setImageResource(R.drawable.baseline_unlock_24);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (biometricPrompt != null) {
            biometricPrompt.cancelAuthentication();
        }
    }
}
