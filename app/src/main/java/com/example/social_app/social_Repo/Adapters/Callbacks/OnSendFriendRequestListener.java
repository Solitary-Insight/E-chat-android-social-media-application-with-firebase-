package com.example.social_app.social_Repo.Adapters.Callbacks;

public interface OnSendFriendRequestListener {
    public void onRequestSent(String Sender,String Receiver) ;

    public  void onRequestSendFailure(String error);
}
