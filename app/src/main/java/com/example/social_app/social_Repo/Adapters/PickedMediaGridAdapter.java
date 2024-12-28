package com.example.social_app.social_Repo.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.social_app.R;

import java.util.List;

public class PickedMediaGridAdapter extends BaseAdapter {
    private Context context;
    private List<Uri> mediaList; // List of URIs for images and videos

    public PickedMediaGridAdapter(Context context, List<Uri> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public int getCount() {
        return mediaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.item_image);
        VideoView videoView = convertView.findViewById(R.id.item_video);

        Uri mediaUri = mediaList.get(position);
        String mimeType = context.getContentResolver().getType(mediaUri);

        if (mimeType != null && mimeType.startsWith("image/")) {
            // It's an image
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            imageView.setImageURI(mediaUri);
        } else if (mimeType != null && mimeType.startsWith("video/")) {
            // It's a video
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVideoURI(mediaUri);
            videoView.start();
        }

        return convertView;
    }
}
