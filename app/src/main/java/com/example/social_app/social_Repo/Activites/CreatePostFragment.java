package com.example.social_app.social_Repo.Activites;

import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.social_app.R;
import com.example.social_app.UserRepository.HelperCallbacks.MediaPickerCallbacks.OnCreatePostClickListener;
import com.example.social_app.UserRepository.HelperCallbacks.MediaPickerCallbacks.OnMediaSelectedListener;
import com.example.social_app.UserRepository.HelperCallbacks.MediaPickerCallbacks.OnUploadMediaClickListener;
import com.example.social_app.social_Repo.Adapters.PickedMediaViewAdapter;
import com.example.social_app.social_Repo.MediaUri;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;


public class CreatePostFragment extends Fragment {

    MaterialButton  postButton,uploadMediaButton,next,previous;

    View createPostFragmentView;
    OnCreatePostClickListener postClickListener;
    OnUploadMediaClickListener mediaClickListener;
    RecyclerView mediaRecycler_view;
    TextView item_count_tv;
    ImageView imageView;
    VideoView videoView ;
    ArrayList<MediaUri> media_uris;
    TextInputEditText post_et;

    public CreatePostFragment(OnCreatePostClickListener onCreatePostClickListener, OnUploadMediaClickListener onUploadMediaClickListener) {
       this.postClickListener=onCreatePostClickListener;
       this.mediaClickListener=onUploadMediaClickListener;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        mediaRecycler_view.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false
        ));

        postButton.setOnClickListener(v -> {
            try {
                postClickListener.onCreatePostClick(media_uris,post_et.getText().toString());

            }catch (Exception e){
                Log.d("POST",e.getLocalizedMessage());
            }

        });

        uploadMediaButton.setOnClickListener(v -> {
            mediaClickListener.uploadMediaClicked(new OnMediaSelectedListener() {
                @Override
                public void OnSingleMediaSelected(Uri uri) {
//                    mediaRecycler_view.setAdapter( new PickedMediaViewAdapter(getContext(),new ArrayList<Uri>()));
                }

                @Override
                public void OnMultipleMediaSelected(ArrayList<MediaUri> mediaUris) {
                    media_uris = mediaUris;
                    List<String> uris=new ArrayList<>();
                    for(MediaUri uri:mediaUris){
                        uris.add(uri.getMedia_uri().toString());
                    }

                    item_count_tv.setText(Integer.toString(mediaUris.size()) + " - items");
                    mediaRecycler_view.setAdapter(new PickedMediaViewAdapter(getActivity(),uris,false));



                }
            });
        });



    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        createPostFragmentView=inflater.inflate(R.layout.fragment_create_post, container, false);
        uploadMediaButton=createPostFragmentView.findViewById(R.id.upload_media_btn);
        postButton=createPostFragmentView.findViewById(R.id.post_button);
        mediaRecycler_view=createPostFragmentView.findViewById(R.id.media_recycler_view);
        post_et=createPostFragmentView.findViewById(R.id.post_input_et);


//         imageView = createPostFragmentView.findViewById(R.id.item_image);
//         videoView = createPostFragmentView.findViewById(R.id.item_video);
//         next = createPostFragmentView.findViewById(R.id.next);
//         previous = createPostFragmentView.findViewById(R.id.previous);

        item_count_tv=createPostFragmentView.findViewById(R.id.item_counts);



        return createPostFragmentView;
    }


    public void reset() {
        post_et.setText("");
        media_uris=new ArrayList<>();
        mediaRecycler_view.setAdapter(new PickedMediaViewAdapter(getActivity(),new ArrayList<>(),false));
        item_count_tv.setText("");
    }
}