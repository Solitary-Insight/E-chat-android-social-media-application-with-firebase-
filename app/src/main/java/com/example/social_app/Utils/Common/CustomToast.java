package com.example.social_app.Utils.Common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.social_app.R;

public class CustomToast {


  public   static  void ShowCustomToast(String message, String heading, String type, Context context){

//        Toast.makeText(context,message, Toast.LENGTH_LONG).show();



        if(type.equals("success")){

              createToast(context,R.color.success,R.drawable.success,message,heading);

        }
        else if(type.equals("error")){
              createToast(context,R.color.error,R.drawable.error,message,heading);

        }
        else if(type.equals("warning")){
              createToast(context,R.color.warning,R.drawable.warn,message,heading);

        }
        else if(type.equals("info")) {
              createToast(context,R.color.info,R.drawable.info,message,heading);

        }
        else{
              createToast(context,R.color.warning,R.drawable.warn,message,heading);

        }

    }

   static void createToast(Context context,int color,int icon,String message,String heading_text){
          Toast toast = new Toast(context);
          View customView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null);

          TextView heading = customView.findViewById(R.id.textView1);
          TextView message_tv = customView.findViewById(R.id.textView2);
          CardView toastCard= customView.findViewById(R.id.snackbar_car);
          ImageView iconIv= customView.findViewById(R.id.imageView);

          iconIv.setImageDrawable(ContextCompat.getDrawable(context,icon));
          toastCard.setCardBackgroundColor(color);




         heading.setText(heading_text);
          message_tv.setText(message);



          toast.setView(customView);
          toast.setDuration(Toast.LENGTH_SHORT);
          toast.show();
    }

}
