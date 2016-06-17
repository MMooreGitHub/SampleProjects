
package com.a_corp.rp_imagelocation.util;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.a_corp.rp_imagelocation.R;
import com.bumptech.glide.Glide;



/**
 * Purpose: Managers the creation of a new ImageView for each item referenced
 *          by the Image Manager Adapter
 *
 * @author Magela Moore
 *         <p/>
 *         ©2016 a Company. All rights reserved.
 *
 * @history
 * 06.08.2016    - Initial Creation
 * 06.16.2016    - Additional Code documentation and cleanup
 */
public class ImageManager extends BaseAdapter{

    private static final int VIEW_PADDING =8;

    public final Context mContext;
    private String[] list;


    // Constructors
    public ImageManager(Context c) {

        mContext = c;
    }

    public ImageManager(Context c, String[] list) {
        mContext = c;
        this.list = list;
    }

    // Getters
    public int getCount() {
        return list.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    /**
     * Create a new ImageView for each item referenced
     * by the Image Manager Adapter
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

            imageView.setLayoutParams(new GridView.LayoutParams(
                    (int) mContext.getResources().getDimension(R.dimen.grid_width),
                    (int) mContext.getResources().getDimension(R.dimen.grid_height)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(VIEW_PADDING, VIEW_PADDING, VIEW_PADDING, VIEW_PADDING);
        } else {
            imageView = (ImageView) convertView;
        }
        // Use glide to load the image into the view
        Glide.with(mContext).load(list[position]).into(imageView);

        return imageView;
    }

}
