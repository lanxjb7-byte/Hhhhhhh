package com.lixxzy.tool;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View floatingIcon, menuView, hsButton;
    private WindowManager.LayoutParams iconParams, menuParams, hsParams;
    private boolean isMenuOpen = false;
    private boolean isStealth = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel("sys_helper", "System Service", NotificationManager.IMPORTANCE_MIN);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
            Notification n = new Notification.Builder(this, "sys_helper").setContentTitle("System Service").setSmallIcon(android.R.drawable.ic_menu_compass).build();
            startForeground(1, n);
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        setupFloatingIcon();
        setupMenuView();
        setupHsButton();
    }

    private void setupFloatingIcon() {
        TextView tv = new TextView(this);
        tv.setText("L");
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(24);
        tv.setGravity(Gravity.CENTER);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#FF0033"));
        tv.setBackground(shape);
        tv.setLayoutParams(new LinearLayout.LayoutParams(120, 120));

        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        iconParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, layoutFlag, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        iconParams.gravity = Gravity.TOP | Gravity.START;
        iconParams.x = 0; iconParams.y = 200;

        tv.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY; private float initialTouchX, initialTouchY; private boolean isDragging = false;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = iconParams.x; initialY = iconParams.y;
                        initialTouchX = event.getRawX(); initialTouchY = event.getRawY();
                        isDragging = false; return true;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getRawX() - initialTouchX) > 10 || Math.abs(event.getRawY() - initialTouchY) > 10) {
                            isDragging = true;
                            iconParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            iconParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(floatingIcon, iconParams);
                        } return true;
                    case MotionEvent.ACTION_UP:
                        if (!isDragging) toggleMenu(); return true;
                } return false;
            }
        });
        floatingIcon = tv;
        windowManager.addView(floatingIcon, iconParams);
    }

    private void setupHsButton() {
        TextView hsBtn = new TextView(this);
        hsBtn.setText("HS");
        hsBtn.setTextColor(Color.WHITE);
        hsBtn.setTextSize(18);
        hsBtn.setTypeface(null, android.graphics.Typeface.BOLD);
        hsBtn.setGravity(Gravity.CENTER);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#00FFCC"));
        hsBtn.setBackground(shape);
        hsBtn.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        hsBtn.setVisibility(View.GONE);

        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        hsParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, layoutFlag, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        hsParams.gravity = Gravity.TOP | Gravity.START;
        hsParams.x = 300; hsParams.y = 800;

        hsBtn.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY; private float initialTouchX, initialTouchY; private boolean isDragging = false;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = hsParams.x; initialY = hsParams.y;
                        initialTouchX = event.getRawX(); initialTouchY = event.getRawY();
                        isDragging = false; return true;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getRawX() - initialTouchX) > 10 || Math.abs(event.getRawY() - initialTouchY) > 10) {
                            isDragging = true;
                            hsParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            hsParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(hsButton, hsParams);
                        } return true;
                    case MotionEvent.ACTION_UP:
                        if (!isDragging) {
                            if (MacroService.instance != null) {
                                MacroService.instance.executeDragHS();
                            } else {
                                Toast.makeText(OverlayService.this, "Aksesibilitas Belum Nyala!", Toast.LENGTH_SHORT).show();
                            }
                        } return true;
                } return false;
            }
        });
        hsButton = hsBtn;
        windowManager.addView(hsButton, hsParams);
    }

    private void setupMenuView() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#EE111111")); bg.setCornerRadius(30f); bg.setStroke(4, Color.parseColor("#FF0033"));
        layout.setBackground(bg); layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("LIXXZY TOOL"); title.setTextColor(Color.parseColor("#FF0033")); title.setTextSize(22); title.setGravity(Gravity.CENTER);
        layout.addView(title);

        addSwitch("TAMPILKAN TOMBOL HS", layout, isChecked -> {
            if (isChecked) { hsButton.setVisibility(View.VISIBLE); } else { hsButton.setVisibility(View.GONE); }
        });

        addSwitch("STEALTH MODE (Hide L)", layout, isChecked -> {
            isStealth = isChecked;
            if (isChecked) {
                floatingIcon.setAlpha(0.0f);
                Toast.makeText(this, "Menu L disembunyikan", Toast.LENGTH_SHORT).show();
            } else {
                floatingIcon.setAlpha(1.0f);
            }
        });

        Button btnFF = new Button(this); btnFF.setText("START FREEFIRETH"); btnFF.setBackgroundColor(Color.parseColor("#FF0033")); btnFF.setTextColor(Color.WHITE);
        btnFF.setOnClickListener(v -> launchGame("com.dts.freefireth")); layout.addView(btnFF);

        Button btnFFMax = new Button(this); btnFFMax.setText("START FREEFIREMAX"); btnFFMax.setBackgroundColor(Color.parseColor("#FF0033")); btnFFMax.setTextColor(Color.WHITE);
        btnFFMax.setOnClickListener(v -> launchGame("com.dts.freefiremax")); layout.addView(btnFFMax);

        Button btnClose = new Button(this); btnClose.setText("X TUTUP MENU"); btnClose.setTextColor(Color.GRAY); btnClose.setBackgroundColor(Color.TRANSPARENT);
        btnClose.setOnClickListener(v -> toggleMenu()); layout.addView(btnClose);

        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        menuParams = new WindowManager.LayoutParams(700, WindowManager.LayoutParams.WRAP_CONTENT, layoutFlag, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        menuParams.gravity = Gravity.CENTER;
        menuView = layout;
    }

    private void addSwitch(String text, LinearLayout parent, java.util.function.Consumer<Boolean> callback) {
        Switch sw = new Switch(this); sw.setText(text); sw.setTextColor(Color.WHITE);
        sw.setOnCheckedChangeListener((v, isChecked) -> callback.accept(isChecked));
        parent.addView(sw);
    }

    private void launchGame(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i != null) { i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i); }
        else Toast.makeText(this, "Game tidak ada!", Toast.LENGTH_SHORT).show();
    }

    private void toggleMenu() {
        if (!isMenuOpen) { windowManager.addView(menuView, menuParams); isMenuOpen = true; floatingIcon.setVisibility(View.GONE); }
        else { windowManager.removeView(menuView); isMenuOpen = false; floatingIcon.setVisibility(View.VISIBLE); if(isStealth) floatingIcon.setAlpha(0.0f); }
    }

    @Override public void onDestroy() { 
        super.onDestroy(); 
        if (floatingIcon != null) windowManager.removeView(floatingIcon); 
        if (hsButton != null) windowManager.removeView(hsButton); 
        if (isMenuOpen) windowManager.removeView(menuView); 
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}