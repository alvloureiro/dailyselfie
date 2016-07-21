package course.labs.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailySelfieActivity extends AppCompatActivity {

    private static final String TAG = DailySelfieActivity.class.getSimpleName ();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String PHOTO_FILE_PREFIX = "IMG_";
    private static final String PHOTO_FILE_SUFFIX = ".jpg";
    private ListView mPhotoListView = null;
    private PhotoViewAdapter mAdapter = null;
    private static final String ALBUM_NAME = "DailySelfieAlbum";
    private String mAbsPhotoFilePath = null;
    private File mPhotoFile = null;
    private String mFileName = null;
    private String mFileTimeStamp = null;
    private Bitmap mPhotoFullBitmap = null;
    private int mTargetW = 0;
    private int mTargetH = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_daily_selfie);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mTargetW = metrics.widthPixels;
        mTargetH = metrics.heightPixels;

        Toolbar toolbar = (Toolbar) findViewById (R.id.daily_selfie_toolbar);
        setSupportActionBar (toolbar);

        mPhotoListView = (ListView) findViewById (R.id.photo_listview);
        mAdapter = new PhotoViewAdapter (getApplicationContext ());
        mPhotoListView.setAdapter (mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_daily_selfie, menu);

        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_photo:
                Log.d (TAG, "Loading the camera app");
                callTakePhotoIntent ();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected (item);
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (setPic()) {
                new CreateThumbnails().execute(mPhotoFullBitmap);
            }
        }
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        PhotoMetaData photoMetaData = new PhotoMetaData ();
        photoMetaData.setmImageFileName (mPhotoFile.getName ());
        photoMetaData.setmTimestamp (mFileTimeStamp);
        photoMetaData.setmPhotoUri (Uri.fromFile (mPhotoFile));
        photoMetaData.setmPhoto (mAbsPhotoFilePath);
        photoMetaData.setmPhotoBitmap(photoBitmap);
        mAdapter.add (photoMetaData);
    }

    private class CreateThumbnails extends AsyncTask<Bitmap, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap photoBitmap = bitmaps[0];
            if (photoBitmap != null) {
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(photoBitmap, 160, 120);
                if (thumbnail != null) return thumbnail;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            setPhotoBitmap(bitmap);
        }
    }

    private File getPhotoAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_PICTURES);

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createPhotoFile () throws IOException {
        // Create an image file name
        mFileTimeStamp = new SimpleDateFormat ("yyyyMMdd_HHmmss").format(new Date ());
        mFileName = PHOTO_FILE_PREFIX + mFileTimeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(mFileName, PHOTO_FILE_SUFFIX, storageDir);
        return imageFile;
    }

    private void callTakePhotoIntent () {
        Intent takePictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity (getPackageManager ()) != null) {
            try {
                mPhotoFile = createPhotoFile ();

            } catch (IOException e) {
                e.printStackTrace ();
                mPhotoFile = null;
                mAbsPhotoFilePath = null;
            }

            if (null != mPhotoFile) {
                mAbsPhotoFilePath = mPhotoFile.getAbsolutePath ();
                Uri fileUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider",
                        mPhotoFile);
                takePictureIntent.putExtra (MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult (takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private boolean setPic() {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mAbsPhotoFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/mTargetW == 0 ? 1 : mTargetW,
                photoH/mTargetH == 0 ? 1 : mTargetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        mPhotoFullBitmap = BitmapFactory.decodeFile(mAbsPhotoFilePath, bmOptions);
        if (mPhotoFullBitmap != null) return true;

        return false;
    }
}
