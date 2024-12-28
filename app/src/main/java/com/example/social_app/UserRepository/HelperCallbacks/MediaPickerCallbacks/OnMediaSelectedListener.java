package com.example.social_app.UserRepository.HelperCallbacks.MediaPickerCallbacks;


import android.net.Uri;

import com.example.social_app.social_Repo.MediaUri;

import java.util.ArrayList;

public interface OnMediaSelectedListener {
    void OnSingleMediaSelected(Uri uri);
    void OnMultipleMediaSelected(ArrayList<MediaUri> mediaUris);

}
