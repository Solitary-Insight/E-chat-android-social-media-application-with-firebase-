package com.example.social_app.social_Repo.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.UserRepository.Models.PostModel;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnCommentDeleteListner;
import com.example.social_app.social_Repo.Controllers.PostInteractionController;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CommentsViewAdapter  extends RecyclerView.Adapter<CommentsViewAdapter.viewHolder> {
Context context;
Map<String, PostModel.Comment> commentsMap;
    List<String> commentKeys;
    String current_user_id;
    String post_owner_id;
    String post_id;


    public CommentsViewAdapter(Context context,String post_id,String current_user_id,String post_owner_id, Map<String, PostModel.Comment> commentsMap) {
        this.context = context;
        this.commentsMap = commentsMap;
         commentKeys= new ArrayList<>(commentsMap.keySet());
         this.current_user_id=current_user_id;
         this.post_owner_id=post_owner_id;
         this.post_id=post_id;
    }

    @NonNull
    @Override
    public CommentsViewAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View comment_layout= LayoutInflater.from(context).inflate(R.layout.comment_recycler_unified_view,null);
        return new viewHolder(comment_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewAdapter.viewHolder holder, int position) {
       PostModel.Comment comment=commentsMap.get( commentKeys.get(position));
        setCommentLayout(current_user_id,comment.getUserId(),holder.comment_card);
        holder.comment.setText(comment.getText());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault());
        holder.date.setText(sdf.format(comment.getTimestamp()));
        holder.username.setText(comment.getUsername());
        Log.d("DELETE-COMMENT",String.valueOf(comment.isDelete()));


          if(!comment.isDelete().equals("deleted")){
              if(post_owner_id.equals(current_user_id) || comment.getUserId().equals(current_user_id)){
                  holder.delete_card_view.setVisibility(View.VISIBLE);


              }
          }



        holder.delete_card_view.setOnClickListener(view -> {
           PostInteractionController.DeleteComment(post_id,comment.getCommentId(),comment.getUserId(),current_user_id,post_owner_id,comment.getUsername(), new OnCommentDeleteListner() {


               @Override
               public void OnCommentDeleted(String updated_text) {
                    holder.comment.setText(updated_text);
                    holder.delete_card_view.setVisibility(View.INVISIBLE);
               }

               @Override
               public void OnCommentDeleteFailure(String error) {

               }
           });
       });



    }

    @Override
    public int getItemCount() {
        return commentsMap.size();
    }



    public void update(Map<String, PostModel.Comment> comments) {
        this.commentsMap=comments;
        this.commentKeys=new ArrayList<>(commentsMap.keySet());
        notifyDataSetChanged();
    }

    class viewHolder extends  RecyclerView.ViewHolder {
        CardView comment_card;
        TextView comment,username,date;
        MaterialCardView delete_card_view;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            comment_card=itemView.findViewById(R.id.comment_card);
            comment=itemView.findViewById(R.id.comment_text);
            username=itemView.findViewById(R.id.comment_username_);
            date=itemView.findViewById(R.id.comment_date);
            delete_card_view=itemView.findViewById(R.id.delete_card);
        }
    }


    void setCommentLayout(String current_user_id,String commenter_id,CardView comment_card){

        // Get LayoutParams dynamically
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) comment_card.getLayoutParams();

        // Set margins (in pixels)
        if(commenter_id.equals(current_user_id)){
            params.setMargins(100, 10, 0, 10);
            comment_card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.my_comment));

        }else{
            params.setMargins(0, 10, 100, 10);
            comment_card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.others_comment));

        }

        // Apply the updated LayoutParams
        comment_card.setLayoutParams(params);
    }
}
