package com.example.social_app.social_Repo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.FirebaseFriendsController;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnFriendRequestActionListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.viewHolder> {
    Context context;
    ArrayList<UserModel> friends;
    String curr_user_id;

    public FriendsAdapter(Context context  ,ArrayList<UserModel> friends,String curr_user_id) {
        this.friends =friends;
        this.context = context;
        this.curr_user_id=curr_user_id;
    }

    @NonNull
    @Override
    public FriendsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.friends_recycler_item_view,null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.viewHolder holder, int position) {
        UserModel friend=friends.get(position);
        holder.friend_username__.setText(friend.getUsername());

        Glide.with(context).load(friend.getProfilePictureUrl())  // URL or URI of the image
                .transform(new CircleCrop())
                .placeholder(R.drawable.user)   // Optional placeholder while loading
                .error(R.drawable.user)               // Optional error image
                .into(holder.friend_profile__);
//        todo:  implement profile image

        holder.friend_unfriend_button__.setOnClickListener(view -> {
            FirebaseFriendsController.unfriend(curr_user_id, friend.getUserId(), new OnFriendRequestActionListener() {
                @Override
                public void onSuccess(String message) {
                    friends.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        });


    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class viewHolder  extends  RecyclerView.ViewHolder{
        CircleImageView friend_profile__;
        TextView friend_username__;
        Button friend_unfriend_button__;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            friend_profile__=itemView.findViewById(R.id.__friend_profile_image);
            friend_unfriend_button__=itemView.findViewById(R.id.__friend_unfriend_btn);
            friend_username__=itemView.findViewById(R.id.__friend_username);
        }
    }


    public  void updateAdapter( ArrayList<UserModel> updated_friend_list){
        this.friends =updated_friend_list;
        notifyDataSetChanged();
    }



}
