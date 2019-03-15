package android.support.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.support.R;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(14)
class TexturePreview extends Preview {

    private final TextureView mTextureView;

    private int mDisplayOrientation;

    TexturePreview(Context context, ViewGroup parent) {
        final View view = View.inflate(context, R.layout.texture_view, parent);
        mTextureView = view.findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                setSize(width, height);
                configureTransform();
                dispatchSurfaceChanged();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                setSize(width, height);
                configureTransform();
                dispatchSurfaceChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                setSize(0, 0);
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    // This method is called only from Camera2.
    @TargetApi(15)
    @Override
    void setBufferSize(int width, int height) {
        mTextureView.getSurfaceTexture().setDefaultBufferSize(width, height);
    }

    @Override
    Surface getSurface() {
        return new Surface(mTextureView.getSurfaceTexture());
    }

    @Override
    SurfaceTexture getSurfaceTexture() {
        return mTextureView.getSurfaceTexture();
    }

    @Override
    View getView() {
        return mTextureView;
    }

    @Override
    Class getOutputClass() {
        return SurfaceTexture.class;
    }

    @Override
    void setDisplayOrientation(int displayOrientation) {
        mDisplayOrientation = displayOrientation;
        configureTransform();
    }

    @Override
    boolean isReady() {
        return mTextureView.getSurfaceTexture() != null;
    }

    /**
     * Configures the transform matrix for TextureView based on {@link #mDisplayOrientation} and
     * the surface size.
     */
    void configureTransform() {
        Matrix matrix = new Matrix();

        final int width = getWidth();
        final int height = getHeight();
        if (mDisplayOrientation % 180 == 90) {
            // Rotate the camera preview when the screen is landscape.
            matrix.setPolyToPoly(
                    new float[]{
                            0.f, 0.f, // top left
                            width, 0.f, // top right
                            0.f, height, // bottom left
                            width, height, // bottom right
                    }, 0,
                    mDisplayOrientation == 90 ?
                            // Clockwise
                            new float[]{
                                    0.f, height, // top left
                                    0.f, 0.f, // top right
                                    width, height, // bottom left
                                    width, 0.f, // bottom right
                            } : // mDisplayOrientation == 270
                            // Counter-clockwise
                            new float[]{
                                    width, 0.f, // top left
                                    width, height, // top right
                                    0.f, 0.f, // bottom left
                                    0.f, height, // bottom right
                            }, 0,
                    4);
        } else if (mDisplayOrientation == 180) {
            matrix.postRotate(180, (float) width / 2, (float) height / 2);
        }
        int previewHeight = ((ViewGroup) mTextureView.getParent()).getMeasuredHeight();
        matrix.postTranslate(0, (float) (previewHeight - height) / 2);
        mTextureView.setTransform(matrix);
    }

}
