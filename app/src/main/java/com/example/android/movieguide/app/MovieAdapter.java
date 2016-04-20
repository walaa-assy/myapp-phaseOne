package com.example.android.movieguide.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Administrator on 4/8/2016.
 */
public class MovieAdapter extends BaseAdapter{
  ArrayList<MovieInfo> resultMovie;

    private Context mContext;
    private LayoutInflater inflater;

    public MovieAdapter(Context c, ArrayList<MovieInfo> data) {
        mContext = c;
        resultMovie = data;
        // inflater = LayoutInflater.from(mContext);
        //c=this.context;

    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return resultMovie.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return resultMovie.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
// Gets the object from the customAdapter at the appropriate position
        MovieInfo movie = (MovieInfo) getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        String posterURL = movie.getPosterPath();

        convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movie,null);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_image);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(0,0,0,0);
        Picasso.with(mContext).load(posterURL).into(imageView);
        return convertView;

    }

    public void clear() {
        resultMovie = null;
    }

    public void add(ArrayList<MovieInfo> path) {
//       resultMovie.add(path);
    }

    class ViewHolder{
        ImageView imageView;
    }

}
