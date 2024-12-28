package com.example.social_app.UserRepository.Controllers.Callbacks;

import java.util.ArrayList;

public interface OnMediaUploadListener {
 public    void onUploadFailure(String error);
 public    void onUploadSuccessful(ArrayList<String> urls);

}
