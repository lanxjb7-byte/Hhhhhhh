package com.lixxzy.tool;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;

import java.util.Random;

public class MacroService extends AccessibilityService {
    public static MacroService instance;

    @Override
    public void onServiceConnected() {
        instance = this;
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}

    public void executeDragHS() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Random rand = new Random();

        // ANTI-DETECT JITTER: Koordinat X dan Y digeser acak dikit biar kayak tangan manusia
        float x = (dm.widthPixels / 2f) + (rand.nextInt(60) - 30);
        float startY = (dm.heightPixels * 0.6f) + (rand.nextInt(80) - 40);
        
        // Jarak tarikan ke atas (18% - 25% tinggi layar)
        float pullDistance = dm.heightPixels * (0.18f + rand.nextFloat() * 0.07f);
        float endY = startY - pullDistance;

        // Kecepatan swipe acak (40ms - 80ms)
        int duration = 40 + rand.nextInt(40);

        Path path = new Path();
        path.moveTo(x, startY);
        
        // Jalur sedikit melengkung
        path.quadTo(x + (rand.nextInt(20) - 10), (startY + endY) / 2, x, endY);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, duration)); 
        dispatchGesture(builder.build(), null, null);
    }
}
