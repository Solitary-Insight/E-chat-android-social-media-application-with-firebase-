package com.example.social_app.UserRepository.Controllers.Callbacks;

public interface OnPostListener {
    public  void onPosted(String message);
    public void onPostFailure(String error);
    public  void onPostProgress(int progress);
}
