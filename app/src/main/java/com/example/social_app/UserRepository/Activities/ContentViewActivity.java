package com.example.social_app.UserRepository.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.LoadingDialogue;
import com.example.social_app.Utils.Common.CustomToast;

public class ContentViewActivity extends AppCompatActivity {

    private static final String TYPE_IMAGE = "image";
    private static final String TYPE_VIDEO = "video";
    Dialog dialog;
    private String mediaUrl, mediaType;
    private boolean isOnline;

    private ImageView imageView;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_content_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Access SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("APP", Context.MODE_PRIVATE);
        mediaUrl = sharedPreferences.getString("MEDIA-URI", null);
        mediaType = sharedPreferences.getString("MEDIA-TYPE", null);
        isOnline = sharedPreferences.getBoolean("IS-ONLINE", false);

        // Initialize views
        videoView = findViewById(R.id.content_video);
        imageView = findViewById(R.id.content_image);
        dialog= LoadingDialogue.createLoadingDialog(this,"Loading! please wait.",R.raw.loading_lottie);


        if (mediaUrl == null || mediaType == null) {
            Toast.makeText(this, "Media data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaType.equals(TYPE_VIDEO)) {
            // Set up the VideoView
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            videoView.setVideoURI(Uri.parse(mediaUrl));
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            dialog.show();
            videoView.setOnPreparedListener(mp -> videoView.start());
            videoView.setOnErrorListener((mp, what, extra) -> {
                dialog.dismiss();
                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
                CustomToast.ShowCustomToast("Error loading video","Video error!","error",this);

                return true;
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    dialog.dismiss();
                    mediaPlayer.start();
                }
            });
            videoView.setOnErrorListener((mp, what, extra) -> {
                dialog.dismiss();
                CustomToast.ShowCustomToast("Error loading video","Video error!","error",this);
                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
                return true;
            });

        } else if (mediaType.equals(TYPE_IMAGE)) {
            // Load the image using Glide
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            Glide.with(this)
                    .load(mediaUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.baseline_image_24)
                            .error(R.drawable.error))
                    .into(imageView);

        } else {
            Toast.makeText(this, "Unsupported media type", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        dialog.dismiss();

        super.onBackPressed();
    }
}
