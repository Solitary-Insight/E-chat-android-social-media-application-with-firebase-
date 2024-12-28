package com.example.social_app.social_Repo.Activites;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.Callbacks.OnUserDownloadListener;
import com.example.social_app.UserRepository.Controllers.FirebaseUserController;
import com.example.social_app.UserRepository.Controllers.LoadingDialogue;
import com.example.social_app.UserRepository.Models.PostModel;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnCommmentListner;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnLikedListner;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnPostDeleteListener;
import com.example.social_app.social_Repo.Adapters.CommentsViewAdapter;
import com.example.social_app.social_Repo.Adapters.FeedRecyclerViewAdapter;
import com.example.social_app.social_Repo.Adapters.PickedMediaViewAdapter;
import com.example.social_app.social_Repo.Controllers.PostInteractionController;
import com.example.social_app.social_Repo.MediaUri;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post_detail_activity extends AppCompatActivity {
    TextView username,post,likes_count,comment_counts;
    LinearLayoutCompat like_llc;
    ImageView like_icon,comment_button,profile;
    CardView post_delete_btn;
    EditText comment_et;
    RecyclerView comments_recycler_view,media_recycler_view;
    FirebaseUser curr_user;
    CommentsViewAdapter commentsViewAdapter;
    Map<String, PostModel.Comment> comments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username=findViewById(R.id.post_username_);
        post_delete_btn=findViewById(R.id.post_delete_btn);
        post=findViewById(R.id.post_text_);
        like_llc=findViewById(R.id.like_LLC_);
        like_icon=findViewById(R.id.like_iv_);
        likes_count=findViewById(R.id.like_tv_);
        comment_counts=findViewById(R.id.comment_tv_);
        comment_et=findViewById(R.id.et_comment);
        comments_recycler_view=findViewById(R.id.comment_recycler_view_);
        media_recycler_view=findViewById(R.id.media_recycler_view_);
        comment_button=findViewById(R.id.comment_btn);
        profile=findViewById(R.id.profile);
        comments_recycler_view.setLayoutManager( new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        curr_user= FirebaseAuth.getInstance().getCurrentUser();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if(getActionBar() !=null){
            getActionBar().hide();
        }



        // Receiving Activity
        String jsonObject = getIntent().getStringExtra("CLICKED-POST");
        PostModel clicked_post = new Gson().fromJson(jsonObject, PostModel.class);
        if(clicked_post!=null){
            Log.d("POST-PARSE",clicked_post.toString());
            setPostOnView(clicked_post);

        }else{
            CustomToast.ShowCustomToast("Something went wrong! try again","Post loading failed!","error",this);
            finish();
        }
        like_llc.setOnClickListener(view -> {

            Log.d("LIKE","like clicked");
            PostInteractionController.onPostLiked(clicked_post.getPostid(), curr_user.getUid(), clicked_post.getLikes(), new OnLikedListner() {
                @Override
                public void onLiked(int likeCount, List<String> updatedLikes) {
                    clicked_post.setLikes(updatedLikes);


                    likes_count.setText( updatedLikes.size()<2?updatedLikes.size()+" like ":updatedLikes.size()+" likes");

                    like_icon.setImageDrawable(ContextCompat.getDrawable(Post_detail_activity.this,R.drawable.thumb_filled));
                }

                @Override
                public void onDislike(int count, List<String> updatedLikes) {
                    clicked_post.setLikes(updatedLikes);

                    likes_count.setText( updatedLikes.size()<2?updatedLikes.size()+" like ":updatedLikes.size()+" likes");

                    like_icon.setImageDrawable(ContextCompat.getDrawable(Post_detail_activity.this,R.drawable.thumb_empty));

                }

                @Override
                public void onFailed() {
                    CustomToast.ShowCustomToast("Unable to like!  like again","Like failed","error",Post_detail_activity.this);

                }
            });
        });


        FirebaseUserController.loadUserFromFirebase(clicked_post.getUserId(), new OnUserDownloadListener() {
            @Override
            public void onUserDownloaded(UserModel user) {
                Glide.with(Post_detail_activity.this).load(user.getProfilePictureUrl())  // URL or URI of the image
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.user)   // Optional placeholder while loading
                        .error(R.drawable.user)               // Optional error image
                        .into(profile);
            }

            @Override
            public void onFailed() {

            }
        });

        post_delete_btn.setOnClickListener(view -> {
            Dialog dialog= LoadingDialogue.createLoadingDialog(Post_detail_activity.this,"Deleting post",R.raw.loading_lottie);

            dialog.show();
            PostInteractionController.deletePost(clicked_post.getPostid(), new OnPostDeleteListener() {
                @Override
                public void onPostDeleted() {
                    CustomToast.ShowCustomToast("Post deletion successful.", "Post deleted", "success", Post_detail_activity.this);
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onDeleteFailed(String error) {
                    dialog.dismiss();

                    CustomToast.ShowCustomToast(error, "Post deletion failed!", "error", Post_detail_activity.this);

                }
            });

        });

        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=comment_et.getText().toString();
                if(comment.trim().equals("")){
                    CustomToast.ShowCustomToast("Your comment is blank. Write something in comment.","Blank comment!","warn",Post_detail_activity.this);
                }else{
                    PostInteractionController.OnComment(clicked_post.getPostid(),curr_user.getUid(),curr_user.getDisplayName(), comment, new OnCommmentListner() {


                        @Override
                        public void onCommented(String commentId, PostModel.Comment updated_comments) {
                            CustomToast.ShowCustomToast("You commented on the post","Commented","success",Post_detail_activity.this);


                            comments.put(commentId,updated_comments);
                            commentsViewAdapter.update(comments);

                            clicked_post.setComments(comments);

                            comment_counts.setText( comments.size()<2?comments.size()+" comment ":comments.size()+" comments");


                            comment_et.setText("");

                        }

                        @Override
                        public void onCommentFailure(String error) {
                            CustomToast.ShowCustomToast(error,"Commenting failed!",error,Post_detail_activity.this);

                        }
                    });
                }
            }
        });




    }

    private void setPostOnView(PostModel clickedPost) {

        if(curr_user != null && curr_user.getUid().equals(clickedPost.getUserId())){
            post_delete_btn.setVisibility(View.VISIBLE);
        }



        username.setText(clickedPost.getUsername());
        post.setText(clickedPost.getCaption());
        List<String> likes = clickedPost.getLikes();
        comments= clickedPost.getComments();
        if(comments==null){
            comments=new HashMap<>();
        }

        commentsViewAdapter=new CommentsViewAdapter(this,clickedPost.getPostid(),curr_user.getUid(),clickedPost.getUserId(),comments);
        comments_recycler_view.setAdapter(commentsViewAdapter);


        likes_count.setText(
                (likes == null || likes.size() == 0)
                        ? "0 like"
                        : likes.size() + " like" + (likes.size() > 1 ? "s" : "")
        );
        comment_counts.setText(
                (comments == null || comments.size() == 0)
                        ? "0 comment"
                        : comments.size() + " comment" + (comments.size() > 1 ? "s" : "")
        );



        media_recycler_view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        ArrayList<MediaUri> mediaUris = new ArrayList<>();

        if(curr_user!=null && likes!=null){

            boolean alreadyLiked = likes.contains(curr_user.getUid());

            if(alreadyLiked){
                like_icon.setImageDrawable(ContextCompat.getDrawable(Post_detail_activity.this,R.drawable.thumb_filled));

            }
        }else{
            if(curr_user==null){
                CustomToast.ShowCustomToast("Something went wrong with your profile, please relogin","Profile problem!","error",Post_detail_activity.this);

            }

        }


        media_recycler_view.setAdapter(new PickedMediaViewAdapter(this,clickedPost.getMediaUrls(),true));
    }
}