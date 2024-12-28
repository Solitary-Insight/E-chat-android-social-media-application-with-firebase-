package com.example.social_app.Utils.Common;

import android.app.Activity;
import android.content.Intent;

public class ActivityNavigation {
    public static void Navigate(Activity from, Class<?> to, boolean pop, boolean clearStack) {
        Intent intent = new Intent(from, to);

        // Clear stack if required
        if (clearStack) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        // Start the target activity
        from.startActivity(intent);

        // Finish the current activity if `pop` is true
        if (pop) {
            from.finish();
        }
    }

}
