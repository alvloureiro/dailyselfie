package course.labs.dailyselfie;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private static final String ALBUM_NAME = "DailySelfie/Album";
    private static final String PHOTOFILE_KEY = "PHOTOFILE_KEY";
    private static final String PHOTOALBUMDIR_KEY = "PHOTOALBUMDIR_KEY";
    public static final String PHOTOFILENAME = "PHOTOFILENAME";
    public static final String PHOTOFILEPATH = "PHOTOFILEPATH";
    private String mAbsPhotoFileDirPath = null;
    private String mPhotoPath = null;
    private static final String PREF_NAME = "SELFIE_PREF";
    private SharedPreferences mPreferences = null;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (savedInstanceState != null) {
            mPhotoPath = savedInstanceState.getString(PHOTOFILEPATH);
            mAbsPhotoFileDirPath = savedInstanceState.getString(PHOTOALBUMDIR_KEY);
            Log.d(TAG, "restoring the photo file path: " + mPhotoPath);
            Log.d(TAG, "restoring the photo album dir path: " + mAbsPhotoFileDirPath);
            loadPhotosFromDir();
        } else {
            initPhotoFileDir();
        }
        setContentView (R.layout.activity_daily_selfie);

        Toolbar toolbar = (Toolbar) findViewById (R.id.daily_selfie_toolbar);
        setSupportActionBar (toolbar);

        mPhotoListView = (ListView) findViewById (R.id.photo_listview);
        mAdapter = new PhotoViewAdapter (getApplicationContext ());
        mPhotoListView.setAdapter (mAdapter);
        mPhotoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: " + position);
                PhotoMetaData photo = (PhotoMetaData) mAdapter.getItem(position);
                Log.d(TAG, "Photo Object: " + photo.toString());
                Intent intent = new Intent(getApplicationContext(), ShowPhotoActivity.class);
                intent.putExtra(PHOTOFILENAME, photo.getmName());
                intent.putExtra(PHOTOFILEPATH, photo.getmPath());
                startActivity(intent);
            }
        });

        mPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
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
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                PhotoMetaData photo = new PhotoMetaData();
                String filename = new File(mPhotoPath).getName();
                photo.setmName(filename);
                photo.setmPath(mPhotoPath);

                Bitmap bm = BitmapFactory.decodeFile(mPhotoPath);
                float ratio = Math.min((float) bm.getHeight() / 160, (float) bm.getWidth() / 120);
                Bitmap thumb = Bitmap.createScaledBitmap(bm,160, (int)(120*ratio), false);
                photo.setmThumbnail(thumb);

                //TODO need to get the thumbpath and store the photo in a file
                File img = new File(mPhotoPath);
                String path = img.getAbsolutePath();
                Log.d(TAG, "Photo absolute path: " + path);
                String fileWithoutExtension = img.getName().split("\\.(?=[^\\.]+$)")[0];
                Log.d(TAG, "File without extension: " + fileWithoutExtension);
                String thumbPath = mAbsPhotoFileDirPath + fileWithoutExtension + "_thumb.jpg";
                photo.setmThumbPath(thumbPath);

                writePhotoToFile(thumb, thumbPath);
                mAdapter.add(photo);

//                thumb.recycle();
//                bm.recycle();
            } else if (resultCode == RESULT_CANCELED) {
                new File(mPhotoPath).delete();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mPhotoPath)
            outState.putString(PHOTOFILE_KEY, mPhotoPath);
        if (null != mAbsPhotoFileDirPath)
            outState.putString(PHOTOALBUMDIR_KEY, mAbsPhotoFileDirPath);
    }



    private void initPhotoFileDir () {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                String root = getExternalFilesDir(null)
                        .getCanonicalPath();
                if (null != root) {
                    File bitmapStorageDir = new File(root, ALBUM_NAME);
                    bitmapStorageDir.mkdirs();
                    mAbsPhotoFileDirPath = bitmapStorageDir.getCanonicalPath();
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString(PHOTOALBUMDIR_KEY, mAbsPhotoFileDirPath);
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createPhotoFile () throws IOException {
        // Create an image file name
        String filetimestamp_ = new SimpleDateFormat ("yyyyMMdd_HHmmss").format(new Date ());
        String filename = PHOTO_FILE_PREFIX + filetimestamp_;
        File photoFile = new File(mAbsPhotoFileDirPath+"/"+filename+PHOTO_FILE_SUFFIX);
        photoFile.createNewFile();
        return photoFile;
    }

    private void callTakePhotoIntent () {
        Intent takePictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity (getPackageManager ()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile ();

            } catch (IOException e) {
                e.printStackTrace ();
            }

            if (null != photoFile) {
                mPhotoPath = photoFile.getAbsolutePath ();
                takePictureIntent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult (takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void writePhotoToFile(Bitmap bm, String filepath) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filepath));
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "Fail when try to write bitmap to file");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "Fail when try to write bitmap to file");
                e.printStackTrace();
            }
        }
    }

    private void loadPhotosFromDir () {
        File storageDir = new File(mAbsPhotoFileDirPath);
        String[] files = storageDir.list();
        for (String file: files) {
            Log.d(TAG, file);
        }
    }
}
