package com.app.nfusion.globe3d;

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {

    private final Context context;
    private Sphere sphere;
    private int earthTexture;
    private final float[] mvpMatrix = new float[16];
    private final float[] earthRotationMatrix = new float[16];
    private final float[] earthViewMatrix = new float[16];
    private final float[] earthProjectionMatrix = new float[16];
    private final float[] earthModelMatrix = new float[16];
    private float earthAngle;
    private float zoomScale = 1.0f; // Zoom default scale
    private static final float minScale = 0.5f; // Zoom min scale
    private static final float maxScale = 2.0f; // Zoom max scale
    public Renderer(Context context) {
        this.context = context;
        Matrix.setIdentityM(earthRotationMatrix, 0);
    }
    private float earthTilt = 0.0f; // Góc nghiêng của Trái Đất

    public float getEarthAngle() {
        return earthAngle;
    }

    public void setEarthAngle(float angle) {
        this.earthAngle = angle;
    }

    public float getEarthTilt() {
        return earthTilt;
    }

    public void setEarthTilt(float tilt) {
        this.earthTilt = tilt;
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);

//        updateEarthRotation();
        // Set the view matrix
        Matrix.setLookAtM(earthViewMatrix, 0,
                0, 0, -5,
                0, 0, 0,
                0, 1, 0);



        // Compute the projection and view transformation
        Matrix.multiplyMM(mvpMatrix, 0, earthProjectionMatrix, 0, earthViewMatrix, 0);

        // Draw the Earth
        drawEarth();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);        // Set the clear color to black with full opacity
        GLES32.glEnable(GLES32.GL_DEPTH_TEST);                                    // Enable depth testing for accurate 3D rendering
        GLES32.glEnable(GLES32.GL_BLEND);                                         // Enable blending for transparency effects
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA);   // Set the blending function to use the source alpha value for transparency blending

        sphere = new Sphere();
        sphere.init();

        earthTexture = TextureHelper.loadTexture(context, R.drawable.earth_texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES32.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(earthProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }


    private void drawEarth() {
        // Compute the rotation for the Earth
        Matrix.setRotateM(earthRotationMatrix, 0, earthAngle, 0.0f, 1.0f, 0.0f);
        Matrix.setIdentityM(earthRotationMatrix, 0);
        Matrix.rotateM(earthRotationMatrix, 0, earthAngle, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(earthRotationMatrix, 0, earthTilt, 1.0f, 0.0f, 0.0f);


        // Compute the final MVP matrix for the Earth
        Matrix.multiplyMM(mvpMatrix, 0, earthProjectionMatrix, 0, earthViewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, earthRotationMatrix, 0);

        // Set the model matrix and apply scaling for Earth
        Matrix.setIdentityM(earthModelMatrix, 0);
        Matrix.scaleM(earthModelMatrix, 0, zoomScale, zoomScale, zoomScale);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, earthModelMatrix, 0);



        // Draw the Earth
        sphere.drawEarth(earthTexture, mvpMatrix);
    }

    public void scaleSphere(float scaleFactor) {
        zoomScale *= scaleFactor;
        if (zoomScale < minScale) {
            zoomScale = minScale;
        } else if (zoomScale > maxScale) {
            zoomScale = maxScale;
        }
    }
}
