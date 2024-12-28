package com.example.social_app.UserRepository.Controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cloudinary.Cloudinary;
import com.example.social_app.CloudResources.CloudinaryHelper;
import com.example.social_app.UserRepository.Controllers.Callbacks.OnMediaUploadListener;
import com.example.social_app.UserRepository.Controllers.Callbacks.OnPostListener;
import com.example.social_app.UserRepository.Models.PostModel;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.NetworkManager;
import com.example.social_app.social_Repo.MediaUri;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebasePostController {
    Context context;
    public  FirebasePostController(Context context){
        this.context=context;
    }
    public void Post(String post , UserModel user, ArrayList<MediaUri> medias, OnPostListener listener){
        if(!NetworkManager.isInternetAvailable()){
            listener.onPostFailure("Network error, please try again later.");
            return;
        }
        if(user==null){
            listener.onPostFailure("Unidentified user, please login again.");
            return;
        }

        UploadMedia(medias, new OnMediaUploadListener() {
            @Override
            public void onUploadFailure(String error) {
                listener.onPostFailure(error);
            }

            @Override
            public void onUploadSuccessful(ArrayList<String> urls) {

                PostModel newPost = new PostModel(
                        FirebaseAuth.getInstance().getCurrentUser().getUid(), // User ID
                        user.getUsername(), // User ID
                        urls,  // Media URLs
                        post,  // Caption
                        System.currentTimeMillis(), // Timestamp
                        new ArrayList<>(),  // Likes (empty list, you can add user IDs later)
                        new HashMap<>()     // Comments (empty map, you can add comments later)
                );

                // Save post to Firebase Database
                DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
                String postId = postsRef.push().getKey();
                postsRef.child(postId).setValue(newPost)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Handle post save success
                                listener.onPosted("Posted");


                            } else {
                                // Handle post save failure
                                listener.onPostFailure("Something went wrong while posting1");


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                listener.onPostFailure("Something went wrong while posting1");
                                Log.d("POST",e.getLocalizedMessage());
                            }
                        });
            }
        });
    }





    void UploadMedia(ArrayList<MediaUri> medias, OnMediaUploadListener listener) {


        // Check network availability before starting the upload
        if (!NetworkManager.isInternetAvailable()) {
            listener.onUploadFailure("Network error, please try again later.");
            return;
        }

        // Initialize Cloudinary instance
        Cloudinary cloudinary = CloudinaryHelper.getInstance();
        ArrayList<String> downloadableUrls = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (MediaUri mediaUri : medias) {
                        Uri uri = mediaUri.getMedia_uri();

                        // Convert URI to File
                        File file = createFileFromUri(uri);
                        if (file == null) {
                            Log.e("MEDIA_INTAKE", "Failed to convert URI to file: " + uri);
                            continue;
                        }
                        Map<String, Object> uploadOptions = new HashMap<>();
                        uploadOptions.put("resource_type", "auto"); // Automatically detects the resource type (image, video, raw, etc.)


                        // Upload to Cloudinary
                        Map uploadResult = cloudinary.uploader().upload(file,uploadOptions);
                        String url = (String) uploadResult.get("url");

                        // Add the uploaded URL to the list
                        downloadableUrls.add(url);
                        Log.d("MEDIA_INTAKE--download-postPost", url);
                    }

                    // Notify listener about the successful upload on the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        listener.onUploadSuccessful(downloadableUrls);
                    });
                    Log.d("MEDIA_INTAKE---postPOST", "Media uploaded: size = " + downloadableUrls.size());

                } catch (Exception e) {
                    // Handle exceptions during upload
                    Log.d("MEDIA_INTAKE--Exception", "Media upload failed: " + e.getMessage());

                    // Notify listener about the failure on the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        listener.onUploadFailure("Upload failed: " + e.getMessage());
                    });
                }
            }
        }).start();
    }


    private File createFileFromUri(Uri uri) {
        try {
            // Open input stream and create a temporary file from the URI
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("upload", ".tmp", context.getCacheDir());

            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return tempFile;
        } catch (Exception e) {
            Log.e("MEDIA_INTAKE", "Error creating file from URI: " + uri, e);
            return null;
        }
    }

}
