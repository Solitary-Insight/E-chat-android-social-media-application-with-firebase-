package com.example.social_app.UserRepository.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.Callbacks.FeedCallbacks.PostDownloadCallback;
import com.example.social_app.UserRepository.Controllers.FirebasePostController;
import com.example.social_app.UserRepository.Controllers.FirebasePostLoadingController;
import com.example.social_app.UserRepository.Models.PostModel;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.social_Repo.Adapters.FeedRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Post_Search_Result extends AppCompatActivity {
String Search_data;

RecyclerView searched_post_recycler;
FeedRecyclerViewAdapter adapter;
FirebaseUser user;
TextView textView,no_search_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_search_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user= FirebaseAuth.getInstance().getCurrentUser();



        SharedPreferences sharedPreferences= getSharedPreferences("APP", Context.MODE_PRIVATE);
      Search_data=sharedPreferences.getString("SEARCHED-TEXT","");


      searched_post_recycler=findViewById(R.id.searched_post_recycler_view);
      textView=findViewById(R.id.searched_text_tv);
      no_search_tv=findViewById(R.id.no_seach_tv);
        textView.setText("Searching result for \'"+Search_data+"\'...");

      searched_post_recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
      if(user!=null){
          adapter=new FeedRecyclerViewAdapter(this,new ArrayList<>(),user.getUid());
          searched_post_recycler.setAdapter(adapter);
      }
      else{
          CustomToast.ShowCustomToast("User identification error, please relogin","User identification failed!","error",this);
          finish();
      }

        FirebasePostLoadingController.loadSearchPost(Search_data, new PostDownloadCallback() {
            @Override
            public void OnPostDownloaded(List<PostModel> posts) {
                adapter.updateAdapter(posts);
                textView.setText("Search results for \'"+Search_data+"\'");
                if(adapter.getItemCount()==0){
                    no_search_tv.setVisibility(View.VISIBLE);
                    no_search_tv.setText("Sorry! there is no post related to \'"+Search_data+"\'");
                }


            }

            @Override
            public void OnPostDownloadFailure(String error) {

                CustomToast.ShowCustomToast(error,"Loading error!","error",getApplicationContext());

            }
        });










    }
}