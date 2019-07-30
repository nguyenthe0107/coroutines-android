package android.support.driver.camera;

import android.view.View;

import java.util.Set;

abstract class ICamera {

    protected final Callback mCallback;

    protected final Preview mPreview;

    ICamera(Callback callback, Preview preview) {
        mCallback = callback;
        mPreview = preview;
    }

    View getView() {
        return mPreview.getView();
    }

    /**
     * @return {@code true} if the implementation was able to start the camera session.
     */
    abstract boolean start();

    abstract void resume();

    abstract void pause();

    abstract void stop();

    abstract boolean isCameraOpened();

    abstract void setFacing(int facing);

    abstract int getFacing();

    abstract Set<AspectRatio> getSupportedAspectRatios();

    /**
     * @return {@code true} if the aspect ratio was changed.
     */
    abstract boolean setAspectRatio(AspectRatio ratio);

    abstract AspectRatio getAspectRatio();

    abstract void setAutoFocus(boolean autoFocus);

    abstract boolean getAutoFocus();

    abstract void setFlash(int flash);

    abstract int getFlash();

    abstract void takePicture();

    abstract void setDisplayOrientation(int displayOrientation);

    interface Callback {

        void onCameraOpened();

        void onCameraClosed();

        void onPictureTaken(byte[] data);

    }

}
