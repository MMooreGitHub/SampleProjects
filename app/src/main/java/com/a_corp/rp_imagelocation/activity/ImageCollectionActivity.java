package com.a_corp.rp_imagelocation.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import com.a_corp.rp_imagelocation.util.ImageManager;
import com.a_corp.rp_imagelocation.R;

/**
 * Purpose: Activity which renders the returned images
 *          in a gridview
 *
 * @author Magela Moore
 *         <p/>
 *         ©2016 a Company. All rights reserved.
 *
 * @history
 * 06.08.2016    - Initial Creation
 * 06.16.2016    - Additional Code documentation and cleanup
 */

public class ImageCollectionActivity  extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load the layout
        setContentView(R.layout.activity_image_collection);

        //Return the intent that started this activity
        Intent intent = getIntent();

        //Load the images returned into the GridView
        final String[] imageURIs = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        assert gridView != null;
        gridView.setAdapter(new ImageManager(this, imageURIs));

        /**
         * On Click event for Single Gridview Item
         * Pass the image position and selected uri
         * in intent extras.
         **/
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {




                // Sending image id to FullScreenImageActivity
                Intent i = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                // passing array index
                i.putExtra("id", position);
                i.putExtra("selectedURIImage",imageURIs[position]);
                startActivity(i);
            }
        });
    }
}
