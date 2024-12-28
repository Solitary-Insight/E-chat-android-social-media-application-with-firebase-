package com.example.social_app.CloudResources;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryHelper {
    final static  String CLOUD_NAME="dwmma0hgy";
    final static  String API_KEY="256415131825947";
    final static  String API_SECRET="YAseKLFYqx72SyfHbfHifoHcJbg";
    final static  String CLOUDINARY_URL="CLOUDINARY_URL=cloudinary://256415131825947:YAseKLFYqx72SyfHbfHifoHcJbg@dwmma0hgy";
    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            config.put("api_key", API_KEY);
            config.put("api_secret", API_SECRET);
            cloudinary = new Cloudinary(config);
        }
        return cloudinary;
    }

}
