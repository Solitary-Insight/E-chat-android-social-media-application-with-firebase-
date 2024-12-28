

package com.example.social_app.social_Repo.Adapters;

import android.content.Context;
import android.util.Log;
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
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnFriendRequestActionListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.viewHolder> {
    Context context;
    List<UserModel>  intrested_friends;
    String current_user_id;




    public FriendRequestsAdapter(Context context, List<UserModel> intrested_friends, String current_user_id) {
        this.context = context;
        this.intrested_friends = intrested_friends;
        this.current_user_id = current_user_id;
    }

    @NonNull
    @Override
    public FriendRequestsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(context).inflate(R.layout.friend_request_recycler_item,null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestsAdapter.viewHolder holder, int position) {



        Glide.with(context).load(intrested_friends.get(position).getProfilePictureUrl())  // URL or URI of the image
                .transform(new CircleCrop())
                .placeholder(R.drawable.user)   // Optional placeholder while loading
                .error(R.drawable.user)               // Optional error image
                .into(holder.user_profile);
        holder.username.setText(intrested_friends.get(position).getUsername());
//        TODO:   Add profileImage
        holder.accept_req_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFriendsController.acceptFriendRequest(current_user_id, intrested_friends.get(holder.getAdapterPosition()).getUserId(), new OnFriendRequestActionListener() {
                    @Override
                    public void onSuccess(String message) {
                        CustomToast.ShowCustomToast("You accepted friend request.","Request accepted","info",context);
                        intrested_friends.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        CustomToast.ShowCustomToast("Something went wrong while accepting request.","Request accept failed!","error",context);
                        Log.d("FRIEND_REQ",errorMessage);

                    }
                });
            }
        });




    }

    @Override
    public int getItemCount() {
        return intrested_friends.size();
    }

    public class viewHolder  extends  RecyclerView.ViewHolder{
        CircleImageView user_profile;
        TextView username;
        Button accept_req_button;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile=itemView.findViewById(R.id.profile_image_friend_req_Circular_Image);
            username=itemView.findViewById(R.id.friend_req_username);
            accept_req_button=itemView.findViewById(R.id.friend_req_accept_button);
        }
    }

   public void  updateRecycler(List<UserModel> updated_list){

        this.intrested_friends=updated_list;
        notifyDataSetChanged();
    }
}
