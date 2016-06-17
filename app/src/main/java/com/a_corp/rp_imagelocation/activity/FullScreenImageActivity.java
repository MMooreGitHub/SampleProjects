package com.a_corp.rp_imagelocation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.a_corp.rp_imagelocation.util.ImageManager;
import com.a_corp.rp_imagelocation.R;
import com.bumptech.glide.Glide;

/**
 * Purpose: Activity which renders the selected image full
 * screen following invocation
 *
 * @author Magela Moore
 *         <p/>
 *         ©2016 a Company. All rights reserved.
 *
 * @history
 * 06.15.2016    - Initial Creation
 * 06.16.2016    - Additional Code documentation and cleanup
 */

public class FullScreenImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //load the layout
        setContentView(R.layout.activity_full_screen_image_view);

        // get intent data
        Intent i = getIntent();

        // Retrieve the position and selected URI from Intent Extras
        String selectedURIImage = i.getExtras().getString("selectedURIImage");
        ImageManager imageManager = new ImageManager(this);
        ImageView imageView = (ImageView) findViewById(R.id.full_screen_image);

        // Use glide to load the image into the view
       Glide.with(imageManager.mContext).load(selectedURIImage).into(imageView);

    }

}
