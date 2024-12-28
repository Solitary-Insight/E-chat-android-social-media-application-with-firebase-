package com.example.social_app.UserRepository.HelperCallbacks.MediaPickerCallbacks;

import com.example.social_app.social_Repo.MediaUri;

import java.util.ArrayList;

@FunctionalInterface
public interface OnCreatePostClickListener {
  void onCreatePostClick(ArrayList<MediaUri> mediaUris,String post);

}
