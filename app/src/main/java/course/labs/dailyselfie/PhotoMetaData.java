package course.labs.dailyselfie;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by loureiro on 19/07/16.
 */
public class PhotoMetaData {
    private static final String TAG = PhotoMetaData.class.getSimpleName ();

    private String mPath;
    private String mName;
    private String mThumbPath;
    private Bitmap mThumbnail;

    public PhotoMetaData() {}

    public PhotoMetaData(String mPath, String mName, String mThumbPath) {
        this.mPath = mPath;
        this.mName = mName;
        this.mThumbPath = mThumbPath;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmThumbPath() {
        return mThumbPath;
    }

    public void setmThumbPath(String mThumbPath) {
        this.mThumbPath = mThumbPath;
    }

    public Bitmap getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(Bitmap mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    @Override
    public String toString() {
        return "PhotoMetaData{" +
                "mPath='" + mPath + '\'' +
                ", mName='" + mName + '\'' +
                ", mThumbPath='" + mThumbPath + '\'' +
                '}';
    }
}
