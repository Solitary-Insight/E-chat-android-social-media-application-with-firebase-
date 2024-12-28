package com.example.social_app.social_Repo.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.social_app.R;
import com.example.social_app.UserRepository.Controllers.FirebaseFriendsController;
import com.example.social_app.UserRepository.Controllers.UserUtils;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnSendFriendRequestListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestedFriendsAdapter extends RecyclerView.Adapter<SuggestedFriendsAdapter.viewHolder> {
    Context context;
    List<UserModel> friends;
    String curr_user_id;

    public SuggestedFriendsAdapter(Context context,List<UserModel> friends,String curr_user_id) {
        this.context=context;
        this.friends=friends;
        this.curr_user_id=curr_user_id;
    }

    @NonNull
    @Override
    public SuggestedFriendsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View layout= LayoutInflater.from(context).inflate(R.layout.suggested_friend_recycler_item,null);

        return new viewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedFriendsAdapter.viewHolder holder, int position) {


            UserModel suggested_friend=friends.get(position);
            holder.username.setText(suggested_friend.getUsername());
            holder.user_bio.setText(UserUtils.trimText(suggested_friend.getBio(),150)+"...");
            Map<String,Boolean> requested_ids=suggested_friend.getReceivedFriendRequests();
            if(requested_ids==null){
                requested_ids=new HashMap<>();

            }
        Glide.with(context).load(suggested_friend.getProfilePictureUrl())  // URL or URI of the image
                .transform(new CircleCrop())
                .placeholder(R.drawable.user)   // Optional placeholder while loading
                .error(R.drawable.user)               // Optional error image
                .into(holder.leading_profile);

        holder.send_request_button.setOnClickListener(view -> {


                FirebaseFriendsController.sendRequest(curr_user_id, suggested_friend.getUserId(), new OnSendFriendRequestListener() {
                    @Override
                    public void onRequestSent(String Sender, String Receiver) {
                        CustomToast.ShowCustomToast("You sent a friend request.","Friendship requested","info",context);
                        friends.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onRequestSendFailure(String error) {
                        CustomToast.ShowCustomToast("Something went wrong while sending friend request, try again.","Friend request failed","error",context);
                        Log.d("FRIEND_REQUEST",error);

                    }
                });
            });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void Update(List<UserModel> suggestedFriends) {
        friends=suggestedFriends;
        notifyDataSetChanged();
    }

    public class viewHolder  extends  RecyclerView.ViewHolder{
        CircleImageView leading_profile;
        TextView username,user_bio;
        AppCompatImageButton send_request_button;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            leading_profile=itemView.findViewById(R.id.profile_image);
            username=itemView.findViewById(R.id.username);
            send_request_button=itemView.findViewById(R.id.send_friend_req);
            user_bio=itemView.findViewById(R.id.user_bio);




        }
    }
}
