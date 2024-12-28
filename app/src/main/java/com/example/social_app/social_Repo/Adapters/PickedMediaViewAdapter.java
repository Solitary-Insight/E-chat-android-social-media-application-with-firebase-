package com.example.social_app.social_Repo.Adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Activities.ContentViewActivity;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.example.social_app.Utils.Common.FileUploader;
import com.example.social_app.social_Repo.Activites.Post_detail_activity;
import com.example.social_app.social_Repo.MediaUri;

import java.util.ArrayList;
import java.util.List;

public class PickedMediaViewAdapter extends RecyclerView.Adapter<PickedMediaViewAdapter.ViewHolder> {
    Activity context;
    String video = "video";
    String image = "image";
    String undefined = "undefined";
    List<String> media_list=new ArrayList<>();
    Boolean isOnline;
    public PickedMediaViewAdapter(Activity context, List<String> medias,Boolean isOnline){
        this.context=context;
        media_list=medias;
        this.isOnline=isOnline;
    }

    @NonNull
    @Override
    public PickedMediaViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.picked_media_recycler_view,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String curr_media_uri=media_list.get(position);


        holder.mediaImage.setVisibility(View.VISIBLE);
        holder.media_unsupported.setVisibility(View.INVISIBLE);


        holder.media_item_layout.setOnClickListener(view -> {

            SharedPreferences sharedPreferences= context.getSharedPreferences("APP", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("MEDIA-URI",curr_media_uri);
            editor.putString("MEDIA-TYPE",holder.media_type.getText().toString());
            editor.putBoolean("IS-ONLINE",isOnline);
            editor.apply();
            ActivityNavigation.Navigate(context, ContentViewActivity.class,false  ,   false);

        });
        if(isOnline){
        holder.media_type.setText(FileUploader.determineMediaType(curr_media_uri));

        }else{
            holder.media_type.setText(FeedRecyclerViewAdapter.getMediaType(Uri.parse(curr_media_uri),context));
        }

        try {
            Glide.with(context)
                    .load(curr_media_uri)  // URL or URI of the image
                    .apply(RequestOptions.centerCropTransform())
                    .override(200, 200) // Set the width and height for cropping
                    .placeholder(R.drawable.baseline_image_24)   // Optional placeholder while loading
                    .error(R.drawable.error)               // Optional error image
                    .into(holder.mediaImage);// Target ImageView


        }
        catch (Exception e){


            holder.media_unsupported.setVisibility(View.VISIBLE);
            holder.mediaImage.setVisibility(View.INVISIBLE);
        }




    }

    @Override
    public int getItemCount() {
        return media_list.size();
    }
    class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView mediaImage;
        TextView media_unsupported,media_type;
        LinearLayout media_item_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mediaImage=itemView.findViewById(R.id.media_image);
            media_unsupported=itemView.findViewById(R.id.media_undefined);
            media_type=itemView.findViewById(R.id.media_type);
            media_item_layout=itemView.findViewById(R.id.media_item_layout);
        }
    }



//    public void loadVideoThumbnail(String videoPath, ImageView imageView) {
//        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
//        imageView.setImageBitmap(thumbnail);
//    }
    public void loadVideoThumbnail(String videoPath, ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .load(videoPath)
                .apply(RequestOptions.centerCropTransform()) // Ensures square cropping
                .override(200,200)
                .into(imageView);

    }
}
