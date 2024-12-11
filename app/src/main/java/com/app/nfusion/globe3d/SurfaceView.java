package com.app.nfusion.globe3d;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class SurfaceView extends GLSurfaceView {

    private com.app.nfusion.globe3d.Renderer renderer;
    private ScaleGestureDetector scaleGestureDetector;
    private float previousX;
    private float previousY;

    public SurfaceView(Context context) {
        super(context);
        init(context, null);
    }

    public SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        renderer = new com.app.nfusion.globe3d.Renderer(context);
        setEGLContextClientVersion(3);
        setRenderer(renderer);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        scaleGestureDetector.onTouchEvent(e);
        e.getX();
        e.getY();
//        return true;

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - previousX;
                float deltaY = y - previousY;

                // Tăng giảm góc quay theo di chuyển của ngón tay
                renderer.setEarthAngle(renderer.getEarthAngle() + deltaX * 0.2f);
                renderer.setEarthTilt(renderer.getEarthTilt() + deltaY * 0.1f);

                requestRender(); // Vẽ lại màn hình với góc mới
                break;
        }

        previousX = x;
        previousY = y;

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            renderer.scaleSphere(scaleFactor);
            // Request render to update the view
            requestRender();
            return true;
        }
    }
}
