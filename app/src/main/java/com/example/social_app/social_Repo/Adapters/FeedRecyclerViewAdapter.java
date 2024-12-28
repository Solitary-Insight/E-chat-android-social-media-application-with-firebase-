//package com.example.social_app.social_Repo.Adapters;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.LinearLayoutCompat;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.social_app.R;
//import com.example.social_app.UserRepository.Models.PostModel;
//import com.example.social_app.Utils.Common.CustomToast;
//import com.example.social_app.social_Repo.Adapters.Callbacks.OnLikedListner;
//import com.example.social_app.social_Repo.Controllers.PostInteractionController;
//import com.example.social_app.social_Repo.MediaUri;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder> {
//    Context context;
//    List<PostModel> posts;
//    String curr_userId;
//    public FeedRecyclerViewAdapter(Context context, List<PostModel> posts,String curr_userId) {
//        this.context = context;
//        this.posts=posts;
//        this.curr_userId=curr_userId;
//    }
//
//    @NonNull
//    @Override
//    public FeedRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView=LayoutInflater.from(context).inflate(R.layout.feed_recycler_view_item,null);
//        return new ViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull FeedRecyclerViewAdapter.ViewHolder holder, int position) {
//        PostModel post=posts.get(position);
//        holder.post_textview.setText(post.getCaption());
//        holder.username.setText(post.getUsername());
//       List<String> likes_list= post.getLikes();
//        if(likes_list!=null){
//            holder.likes.setText(likes_list.size()+"");
//
//            for(String id:likes_list){
//                if (id.equals(curr_userId)){
//                    holder.like_iv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.thumb_filled));
//                }
//            }
//        }else {
//            holder.likes.setText("0");
//
//        }
//
//        holder.media_recycler_view.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
//        ArrayList<MediaUri> mediaUris=new ArrayList<>();
//       List<String> uris= post.getMediaUrls();
//       for (String uristr:uris){
//           Uri uri=Uri.parse(uristr);
//           mediaUris.add(new MediaUri(holder.getMediaType(uri),uri ));
//       }
//        holder.media_recycler_view.setAdapter(new PickedMediaViewAdapter(context,mediaUris));
//
//
//       holder.likes.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               PostInteractionController.onPostLiked(post.getPostid(), post.getUserId(), likes_list, new OnLikedListner() {
//                   @Override
//                   public void onLiked(int likeCount) {
//                       holder.like_iv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.thumb_filled));
//                       holder.likes.setText(likeCount+"");
//                   }
//
//                   @Override
//                   public void onDislike(int count) {
//                       holder.like_iv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.thumb_empty));
//                       holder.likes.setText(count+"");
//                   }
//
//                   @Override
//                   public void onFailed() {
//                       CustomToast.ShowSnackBar("Failed liking post","error",context);
//                   }
//               });
//           }
//       });
//    }
//
//    @Override
//    public int getItemCount() {
//        return posts.size();
//    }
//
//    public class ViewHolder  extends  RecyclerView.ViewHolder{
//        ImageView comment_button,like_iv;
//        TextView post_textview,username,likes;
//        LinearLayoutCompat like_btn;
//        RecyclerView media_recycler_view;
//
//
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            comment_button=itemView.findViewById(R.id.comment_iv);
//            post_textview=itemView.findViewById(R.id.post_text);
//            username=itemView.findViewById(R.id.post_username);
//            like_btn=itemView.findViewById(R.id.like_llc);
//            likes=itemView.findViewById(R.id.like_tv);
//            like_iv=itemView.findViewById(R.id.like_iv);
//            media_recycler_view=itemView.findViewById(R.id.media_recycler_view);
//        }
//
//        //Get media Type
//        private String getMediaType(Uri uri) {
//            ContentResolver resolver = context.getContentResolver();
//            String type = resolver.getType(uri);
//
//            if (type != null) {
//                if (type.startsWith("video")) {
//                    return "video";
//                } else if (type.startsWith("image")) {
//                    return "image";
//                }
//            }
//            return "unknown";
//        }
//    }
//
//}
package com.example.social_app.social_Repo.Adapters;

import static androidx.core.graphics.RectKt.transform;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.Callbacks.OnUserDownloadListener;
import com.example.social_app.UserRepository.Controllers.FirebaseUserController;
import com.example.social_app.UserRepository.Models.PostModel;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.social_Repo.Activites.Post_detail_activity;
import com.example.social_app.social_Repo.MediaUri;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder> {
    Activity context;
    List<PostModel> posts;
    String curr_userId;

    public FeedRecyclerViewAdapter(Activity context, List<PostModel> posts, String curr_userId) {
        this.context = context;
        this.posts = posts;
        this.curr_userId = curr_userId;
    }

    @NonNull
    @Override
    public FeedRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.feed_recycler_view_item, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecyclerViewAdapter.ViewHolder holder, int position) {
        PostModel post = posts.get(position);

        holder.post_textview.setText(post.getCaption());

        holder.username.setText(post.getUsername());
        List<String> likesList = post.getLikes();
        holder.likes.setText(
                (likesList == null || likesList.size() == 0)
                        ? "0 like"
                        : likesList.size() + " like" + (likesList.size() > 1 ? "s" : "")
        );
        if (likesList != null) {


            holder.like_iv.setImageDrawable(likesList.contains(curr_userId)
                    ? ContextCompat.getDrawable(context, R.drawable.thumb_filled)
                    : ContextCompat.getDrawable(context, R.drawable.thumb_empty));
        } else {

            holder.like_iv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.thumb_empty));
        }
        Map<String, PostModel.Comment> comment_list = post.getComments();
        holder.comments.setText(
                (comment_list == null || comment_list.size() == 0)
                        ? "0 comment"
                        : comment_list.size() + " comment" + (comment_list.size() > 1 ? "s" : "")
        );


        FirebaseUserController.loadUserFromFirebase(post.getUserId(), new OnUserDownloadListener() {
            @Override
            public void onUserDownloaded(UserModel user) {
                holder.username.setText(user.getUsername());

                Glide.with(context).load(user.getProfilePictureUrl())  // URL or URI of the image
                .transform(new CircleCrop())
                        .placeholder(R.drawable.user)   // Optional placeholder while loading
                        .error(R.drawable.user)               // Optional error image
                        .into(holder.profile);

            }

            @Override
            public void onFailed() {

            }
        });




        holder.media_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//

        holder.feed_layout.setOnClickListener(view -> {
            // Passing Activity

            Intent intent = new Intent(context, Post_detail_activity.class);
            String post_json = new Gson().toJson(post);
            intent.putExtra("CLICKED-POST", post_json);
            context.startActivity(intent);



        });


        holder.media_recycler_view.setAdapter(new PickedMediaViewAdapter(context, post.getMediaUrls(),true));


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updateAdapter(List<PostModel> posts) {
        this.posts=posts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView comment_button, like_iv,profile;
        TextView post_textview, username, likes,comments;
        LinearLayoutCompat like_btn,feed_layout;
        RecyclerView media_recycler_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment_button = itemView.findViewById(R.id.comment_iv);
            post_textview = itemView.findViewById(R.id.post_text);
            username = itemView.findViewById(R.id.post_username);
            like_btn = itemView.findViewById(R.id.like_llc);
            likes = itemView.findViewById(R.id.like_tv);
            profile = itemView.findViewById(R.id.profile);
            comments = itemView.findViewById(R.id.comment_tv);
            like_iv = itemView.findViewById(R.id.like_iv);
            media_recycler_view = itemView.findViewById(R.id.media_recycler_view);
            feed_layout = itemView.findViewById(R.id.feed_layout);
        }


    }
    // Get media type from URI
    public static String getMediaType(Uri uri,Context context) {
        ContentResolver resolver = context.getContentResolver();
        String type = resolver.getType(uri);
        if (type != null) {
            if (type.startsWith("video")) return "video";
            if (type.startsWith("image")) return "image";
        }
        return "unknown";
    }
}
