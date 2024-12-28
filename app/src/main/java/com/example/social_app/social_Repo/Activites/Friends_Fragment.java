package com.example.social_app.social_Repo.Activites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.Callbacks.FriendsCallback.OnFriendsSuggestListener;
import com.example.social_app.UserRepository.Controllers.FirebaseFriendsController;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnActiveFriendsLoadingListener;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnFriendRequestsLoadedListener;
import com.example.social_app.social_Repo.Adapters.FriendRequestsAdapter;
import com.example.social_app.social_Repo.Adapters.FriendsAdapter;
import com.example.social_app.social_Repo.Adapters.SuggestedFriendsAdapter;

import java.util.ArrayList;
import java.util.List;


public class Friends_Fragment extends Fragment {
    UserModel current_User;
    TextView no_friend_tv,no_request_tv,no_suggestion_tv;
    List<String> friends;
    RecyclerView friends_recycler_view,suggestd_friends_recycler,friend_request_recycler;
    SuggestedFriendsAdapter suggestedFriendsAdapter;
    FriendRequestsAdapter friendRequestsAdapter;
    FriendsAdapter friendsAdapter;

    public Friends_Fragment(UserModel current_User) {
        // Required empty public constructor
        this.current_User=current_User;



    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout=inflater.inflate(R.layout.fragment_friends_, container, false);

        friends_recycler_view=layout.findViewById(R.id.friends_recycler_view);
        suggestd_friends_recycler=layout.findViewById(R.id.suggested_friends_recycler_view);
        friend_request_recycler=layout.findViewById(R.id.friend_request_recycler);
        no_friend_tv=layout.findViewById(R.id.no_friend_tv);
        no_suggestion_tv=layout.findViewById(R.id.no_suggestion_tv);
        no_request_tv=layout.findViewById(R.id.no_request_tv);
        // Inflate the layout for this fragment
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        suggestd_friends_recycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        friends_recycler_view.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        friend_request_recycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        suggestedFriendsAdapter=new SuggestedFriendsAdapter(getContext(),new ArrayList<>(),current_User.getUserId());
        suggestd_friends_recycler.setAdapter(suggestedFriendsAdapter);


        friendRequestsAdapter=new FriendRequestsAdapter(getContext(),new ArrayList<>(),current_User.getUserId());
        friend_request_recycler.setAdapter(friendRequestsAdapter);



        friendsAdapter=new FriendsAdapter(getContext(),new ArrayList<>(),current_User.getUserId());
        friends_recycler_view.setAdapter(friendsAdapter);

        no_friend_tv.setText("Loading friends...");
        no_friend_tv.setVisibility(View.VISIBLE);
        FirebaseFriendsController.loadFriends(current_User.getUserId(), new OnActiveFriendsLoadingListener() {
            @Override
            public void onFriendsLoaded(ArrayList<UserModel> friends) {
                friendsAdapter.updateAdapter(friends);
                friendsAdapter.notifyDataSetChanged();
                if(friendsAdapter.getItemCount()==0){
                    no_friend_tv.setText("You have no friend till now");
                    no_friend_tv.setVisibility(View.VISIBLE);

                }else{
                    no_friend_tv.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(String errorMessage) {
                no_friend_tv.setText(errorMessage);
            }
        });

        no_request_tv.setText("Friend requests loading...");
        no_request_tv.setVisibility(View.VISIBLE);
        FirebaseFriendsController.loadFriendRequestsWithDetails(current_User.getUserId(), new OnFriendRequestsLoadedListener() {
            @Override
            public void onFriendRequestsLoaded(ArrayList<UserModel> receivedRequests) {
                friendRequestsAdapter.updateRecycler(receivedRequests);

                if(friendRequestsAdapter.getItemCount()==0){
                    no_request_tv.setText("No friend requests");
                    no_request_tv.setVisibility(View.VISIBLE);
                }
                else{
                    no_request_tv.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(String errorMessage) {
            no_request_tv.setText(errorMessage);
            }
        });



        no_suggestion_tv.setText("Loading friends suggestions...");
        no_suggestion_tv.setVisibility(View.VISIBLE);
        FirebaseFriendsController.suggestFriend(current_User, new OnFriendsSuggestListener() {
            @Override
            public void OnFriendSuggestSucces(List<UserModel> suggestedFriends) {
                suggestedFriendsAdapter.Update(suggestedFriends);

                if(suggestedFriendsAdapter.getItemCount()==0){
                    no_suggestion_tv.setText("No friend suggestions.");
                    no_suggestion_tv.setVisibility(View.VISIBLE);
                }
                else{
                    no_suggestion_tv.setVisibility(View.GONE);


                }
            }

            @Override
            public void OnFriendsSuggestionFailure(String error) {
            no_suggestion_tv.setText(error);
            }
        });
    }
}