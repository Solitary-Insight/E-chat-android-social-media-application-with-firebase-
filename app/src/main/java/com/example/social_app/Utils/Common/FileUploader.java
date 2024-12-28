package com.example.social_app.Utils.Common;

import android.content.Context;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.cloudinary.utils.ObjectUtils;
import com.example.social_app.CloudResources.CloudinaryHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUploader {



    private static File createFileFromUri(Context context, Uri uri) throws Exception {
        if (uri == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Cannot open URI: " + uri);
            }

            // Create a temp file in the app's cache directory
            File tempFile = File.createTempFile("upload", ".tmp", context.getCacheDir());

            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            Log.d("MEDIA_UPTAKE", "Temp file created for URI: " + uri + " at: " + tempFile.getAbsolutePath());
            return tempFile;
        }
    }


    public static String determineMediaType(String url) {
        if (url == null || url.isEmpty()) {
            return "unknown";
        }

        // Extract the file extension from the URL
        String extension = null;
        int lastDotIndex = url.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < url.length() - 1) {
            extension = url.substring(lastDotIndex + 1);
        }

        // Default to "unknown" if no valid extension is found
        if (extension == null) {
            return "unknown";
        }

        // Determine the media type based on the extension
        switch (extension.toLowerCase()) {
            case "mp3":
            case "wav":
            case "flac":
                return "audio";
            case "mp4":
            case "avi":
            case "mov":
                return "video";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return "image";
            default:
                return "unknown";
        }
    }

}
