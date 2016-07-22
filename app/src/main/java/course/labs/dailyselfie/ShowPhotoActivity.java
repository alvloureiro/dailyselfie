package course.labs.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class ShowPhotoActivity extends Activity {
    private static final String TAG = ShowPhotoActivity.class.getSimpleName();
    private ImageView mPhotoBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_photo);
        mPhotoBitmap = (ImageView) findViewById(R.id.photo_imageview);
        Intent intent = getIntent();
        String filename = intent.getStringExtra("FILENAME");
        String filepath = intent.getStringExtra("FILEPATH");

        setTitle(filename);
        new LoadBitmapPhoto(this, mPhotoBitmap).execute(filepath);
        setProgressBarIndeterminateVisibility(true);
    }

    private static class LoadBitmapPhoto extends AsyncTask<String, Void, Bitmap> {
        private Activity mActivity = null;
        private ImageView mPhoto = null;

        public LoadBitmapPhoto (Activity activity, ImageView photo) {
            mActivity = activity;
            mPhoto = photo;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String filepath = params[0];

            return loadImage(filepath);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mPhoto.setImageBitmap(bitmap);
            mActivity.setProgressBarIndeterminateVisibility(false);
            super.onPostExecute(bitmap);
        }

        public Bitmap loadImage(String filepath) {
            Bitmap bm = BitmapFactory.decodeFile(filepath);
            int nh = (int) ( bm.getHeight() * (1024.0 / bm.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bm, 1024, nh, true);
            return scaled;
        }
    }
}
