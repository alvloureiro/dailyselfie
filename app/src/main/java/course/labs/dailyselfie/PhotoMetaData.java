package course.labs.dailyselfie;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by loureiro on 19/07/16.
 */
public class PhotoMetaData {
    private static final String TAG = PhotoMetaData.class.getSimpleName ();
    private String mTimestamp;
    private String mCurrentPhotoPath;
    private Uri mPhotoUri;
    private String mImageFileName;
    private Bitmap mPhotoBitmap;

    public PhotoMetaData() {}

    public PhotoMetaData (String mTimestamp, String mPhoto, Uri mPhotoUri, String mImageFileName) {
        this.mTimestamp = mTimestamp;
        this.mCurrentPhotoPath = mPhoto;
        this.mPhotoUri = mPhotoUri;
        this.mImageFileName = mImageFileName;
    }

    public String getmTimestamp () {
        return mTimestamp;
    }

    public void setmTimestamp (String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public String getmCurrentPhotoPath () {
        return mCurrentPhotoPath;
    }

    public void setmPhoto (String mPhoto) {
        this.mCurrentPhotoPath = mPhoto;
    }

    public Uri getmPhotoUri () {
        return mPhotoUri;
    }

    public void setmPhotoUri (Uri mPhotoUri) {
        this.mPhotoUri = mPhotoUri;
    }

    public String getmImageFileName () {
        return mImageFileName;
    }

    public void setmImageFileName (String mImageFileName) {
        this.mImageFileName = mImageFileName;
    }

    public Bitmap getmPhotoBitmap () {
        return mPhotoBitmap;
    }

    public void setmPhotoBitmap (Bitmap mPhotoBitmap) {
        this.mPhotoBitmap = mPhotoBitmap;
    }

    @Override
    public String toString () {
        return "PhotoMetaData{" +
                "mTimestamp='" + mTimestamp + '\'' +
                ", mCurrentPhotoPath=" + mCurrentPhotoPath +
                ", mPhotoUri=" + mPhotoUri +
                ", mImageFileName='" + mImageFileName + '\'' +
                '}';
    }
}
