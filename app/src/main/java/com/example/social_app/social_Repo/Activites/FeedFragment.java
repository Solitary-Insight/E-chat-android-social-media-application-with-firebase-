package com.example.social_app.social_Repo.Activites;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.social_app.R;
import com.example.social_app.UserRepository.Activities.Post_Search_Result;
import com.example.social_app.UserRepository.Controllers.Callbacks.FeedCallbacks.PostDownloadCallback;
import com.example.social_app.UserRepository.Controllers.FirebasePostLoadingController;
import com.example.social_app.UserRepository.Models.PostModel;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.social_Repo.Adapters.FeedRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    RecyclerView feed_recycler_view;
    FirebasePostLoadingController controller;
    FirebaseUser user;
    SearchView searchView;
    TextView noFeedTV,loading_feed_tv;
    public FeedFragment() {
        // Required empty public constructor
    }



    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller=new FirebasePostLoadingController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View feed_layout= inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        feed_recycler_view=feed_layout.findViewById(R.id.feed_recycler_view);
        searchView=feed_layout.findViewById(R.id.searchView_posts);
        noFeedTV=feed_layout.findViewById(R.id.no_feed_tv);
        loading_feed_tv=feed_layout.findViewById(R.id.loading_feed_tv);
        feed_recycler_view.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));


        return feed_layout;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user= FirebaseAuth.getInstance().getCurrentUser();
        // Get the current user
        user = FirebaseAuth.getInstance().getCurrentUser();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Log.d("Search",s);

                SharedPreferences sharedPreferences= getContext().getSharedPreferences("APP", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();

                editor.putString("SEARCHED-TEXT",s);
                editor.commit();

                ActivityNavigation.Navigate(getActivity(), Post_Search_Result.class,false,false);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("Search.....",s);

                return false;
            }
        });

        // Check if the user is null
        if (user != null) {
            // Load posts from Firebase

            loading_feed_tv.setVisibility(View.VISIBLE);
            controller.loadFeedPostsFromFirebase(user.getUid(), new PostDownloadCallback() {
                @Override
                public void OnPostDownloaded(List<PostModel> posts) {
                    // Check if context is valid before showing Toast
                    loading_feed_tv.setVisibility(View.INVISIBLE);
                    Log.d("Firebase-POST", "Loaded post: " + new Gson().toJson(posts));

                    if(posts.size()==0){
                        noFeedTV.setText("Sorry! There is no post for you, start with making some friends.");
                        noFeedTV.setVisibility(View.VISIBLE);
                    }else{
                        noFeedTV.setVisibility(View.INVISIBLE);

                    }


                    feed_recycler_view.setAdapter(new FeedRecyclerViewAdapter(getActivity(),posts,user.getUid()));


                }

                @Override
                public void OnPostDownloadFailure(String error) {
                    // Check if context is valid before showing Toast
                    CustomToast.ShowCustomToast(error,"Feed download failed!","error",getActivity());
                    noFeedTV.setText("Sorry! There is no post for you, start with making some friends.");

                    noFeedTV.setVisibility(View.VISIBLE);
                    loading_feed_tv.setVisibility(View.INVISIBLE);



                }
            });
        } else {
            // User is null
            Log.e("FeedFragment", "User is null. Cannot load posts.");
            if (getContext() != null) {
                CustomToast.ShowCustomToast("User not authenticated","Identification failed!","error",getContext());
            }
        }
    }
}