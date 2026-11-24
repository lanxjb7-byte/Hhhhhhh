package com.lixxzy.tool;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 100, 50, 50);
        layout.setGravity(android.view.Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("SYSTEM SERVICE\nPersiapkan Izin:");
        title.setTextSize(20);
        title.setGravity(android.view.Gravity.CENTER);
        layout.addView(title);

        Button btnOverlay = new Button(this);
        btnOverlay.setText("1. IZINKAN OVERLAY");
        btnOverlay.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });
        layout.addView(btnOverlay);

        Button btnAcc = new Button(this);
        btnAcc.setText("2. IZINKAN AKSESIBILITAS");
        btnAcc.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Cari 'System Service' dan nyalakan!", Toast.LENGTH_LONG).show();
        });
        layout.addView(btnAcc);

        Button btnStart = new Button(this);
        btnStart.setText("3. JALANKAN SERVICE");
        btnStart.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Izinkan Overlay dulu!", Toast.LENGTH_SHORT).show();
                return;
            }
            startForegroundService(new Intent(this, OverlayService.class));
            finish();
        });
        layout.addView(btnStart);

        setContentView(layout);
    }
  }
