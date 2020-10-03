package com.aravi.dotpro.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aravi.dotpro.BuildConfig;
import com.aravi.dotpro.R;
import com.aravi.dotpro.Utils;
import com.aravi.dotpro.manager.SharedPreferenceManager;
import com.aravi.dotpro.service.DotService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    private boolean TRIGGERED_START = false;
    private SwitchMaterial mainSwitch, vibrateSwitch;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        init();

    }

    private void init() {
        mainSwitch = findViewById(R.id.mainSwitch);
        vibrateSwitch = findViewById(R.id.vibrationSwitch);
        MaterialButton submitFeedback = findViewById(R.id.submitFeedback);
        MaterialButton rateApp = findViewById(R.id.rateApp);
        RadioGroup align = findViewById(R.id.align);
        ((TextView) findViewById(R.id.versionText)).setText("VERSION - " + BuildConfig.VERSION_NAME);

        mainSwitch.setChecked(sharedPreferenceManager.isServiceEnabled() && checkAccessiblity());
        vibrateSwitch.setChecked(sharedPreferenceManager.isVibrationEnabled());
        mainSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        vibrateSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        submitFeedback.setOnClickListener(view -> sendFeedbackEmail());
        align.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.topLeft:
                    sharedPreferenceManager.setPosition(0);
                    break;
                case R.id.topRight:
                    sharedPreferenceManager.setPosition(1);
                    break;
                default:
                    break;
            }
        });

        rateApp.setOnClickListener(view -> {
            String url = "https://play.google.com/store/apps/details?id=com.aravi.dotpro";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });


        findViewById(R.id.logsClickable).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LogsActivity.class);
            startActivity(intent);
        });
    }

    private SwitchMaterial.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case R.id.mainSwitch:
                    if (b) {
                        checkForAccessibilityAndStart();
                        TRIGGERED_START = true;
                    } else {
                        stopService();
                    }
                    break;
                case R.id.vibrationSwitch:
                    sharedPreferenceManager.setVibrationEnabled(b);
                    break;
                default:
                    break;

            }
        }
    };

    private void checkForAccessibilityAndStart() {
        if (!accessibilityPermission(getApplicationContext(), DotService.class)) {
            mainSwitch.setChecked(false);
            Utils.showPermissionsDialog(MainActivity.this);
        } else {
            mainSwitch.setChecked(true);
            sharedPreferenceManager.setServiceEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startService(new Intent(MainActivity.this, DotService.class));
            } else {
                startService(new Intent(MainActivity.this, DotService.class));
            }
        }
    }


    private void stopService() {
        sharedPreferenceManager.setServiceEnabled(false);
        stopService(new Intent(MainActivity.this, DotService.class));
    }

    public static boolean accessibilityPermission(Context context, Class<?> cls) {
        ComponentName componentName = new ComponentName(context, cls);
        String string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
        if (string == null) {
            return false;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(simpleStringSplitter.next());
            if (unflattenFromString != null && unflattenFromString.equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TRIGGERED_START) {
            checkForAccessibilityAndStart();
            TRIGGERED_START = false;
        }
        if (accessibilityPermission(getApplicationContext(), DotService.class)) {
            Utils.dismissPermissionDialog();
        }
    }

    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "kamaravichow@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for SafeDot");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Device Information : \n----- Don't clear these ----\n "
                + Build.DEVICE + " ,\n " + Build.BOARD + " ,\n " + Build.BRAND + " , " + Build.MANUFACTURER + " ,\n " + Build.MODEL + "\n ------ ");
        startActivity(Intent.createChooser(emailIntent, "Send feedback..."));
    }

    private boolean checkAccessiblity() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        return manager.isEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}