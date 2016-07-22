package course.labs.dailyselfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by loureiro on 19/07/16.
 */
public class PhotoViewAdapter extends BaseAdapter {
    private final static String TAG = PhotoViewAdapter.class.getSimpleName ();
    private ArrayList<PhotoMetaData> mPhotoList = new ArrayList<PhotoMetaData> ();
    private Context mContext = null;
    private static LayoutInflater mInflater = null;

    public PhotoViewAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from (mContext);
    }
    @Override
    public int getCount () {
        return mPhotoList.size ();
    }

    @Override
    public Object getItem (int i) {
        if (i > getCount ()) return null;

        return mPhotoList.get (i);
    }

    @Override
    public long getItemId (int i) {
        return i;
    }

    @Override
    public View getView (int i, View view, ViewGroup viewGroup) {
        View newView = view;
        ViewHolder holder;

        PhotoMetaData curr = mPhotoList.get (i);
        if(null == view) {
            holder = new ViewHolder ();
            newView = mInflater.inflate (R.layout.photo_view, viewGroup, false);
            holder.photo = (ImageView) newView.findViewById (R.id.photo);
            holder.timestamp = (TextView) newView.findViewById (R.id.photo_timestamp);
            newView.setTag (holder);
        } else {
            holder = (ViewHolder) newView.getTag ();
        }
        holder.photo.setImageBitmap (curr.getmThumbnail());
        holder.timestamp.setText (curr.getmName());
        return newView;
    }

    public void add(PhotoMetaData photoMetaData) {
        mPhotoList.add (photoMetaData);
        notifyDataSetChanged ();
    }

    static class ViewHolder {
        ImageView photo;
        TextView timestamp;
    }

}
