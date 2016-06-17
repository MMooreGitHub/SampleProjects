package com.a_corp.rp_imagelocation.net;

/**
 * Purpose: Access global api variables
 *          via a singleton Design Pattern utilization
 *
 * @author Magela Moore
 *         <p/>
 *         ©2016 a Company. All rights reserved.
 *
 * @history
 * 06.14.2016    - Initial Creation
 * 06.16.2016    - Additional Code documentation and cleanup
 */

public class ApiUtilities {

    private static ApiUtilities mInstance= null;
    //Define the relevant api-related key and uris
    public final String API_IMAGE_CLIENT_KEY = "3f9751dbeada15de3694e5b4b324840b";
    public final String BASE_URL_IMAGES= "https://api.flickr.com/services/rest/";
    public final String ENDPOINT_SEARCH_IMAGES="flickr.photos.search";

    private ApiUtilities(){}

   /**
    * Return the current instance
    */
    public static synchronized ApiUtilities getInstance(){
        if(null == mInstance){
            mInstance = new ApiUtilities();
        }
        return mInstance;
    }

}
