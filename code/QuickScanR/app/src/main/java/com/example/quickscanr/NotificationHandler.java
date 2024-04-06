package com.example.quickscanr;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * This class handles notifications that is triggered by Firebase's Cloud Function (androidAnnouncements)
 * and handles it accordingly.
 *
 * @see Announcement
 *
 * Resources used:
 * 1) For Notification Channel & Heads-Up Information: https://developer.android.com/develop/ui/views/notifications/channels#java, Android
 * 2) The connection of Android App to Push Notifications (Automated) : https://www.youtube.com/watch?v=If2eDphtutI&t=408s, Codeible
 */
public class NotificationHandler extends FirebaseMessagingService {
    final String CHANNEL_ID = "headsup"; // NOTE: If you make changes to the notification channel, change CHANNEL_ID for the saves to change.

    /**
     * This creates a notification channel
     * The code below is inspired by Android's official documentation for NotificationChannel
     * Link: https://developer.android.com/develop/ui/views/notifications/channels#java, Android
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel (only on API 26+ due to differences in libraries)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH; // The impportance is what allows us to make a heads-up/drop down notification
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Announcements", importance);
            channel.setDescription("All announcements.");
            // Register the channel with the app; you can't change the importance or any other notification behaviour after.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("PushNotif", "Notification channel has been created");
        }
    }

    /**
     * This is called everytime we receive a notification from Firebase
     * and the app is in the foreground (the application is open)
     * @param remoteMessage Remote message that has been received from Cloud Functions
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        // Create the notification channel
        createNotificationChannel();

        // Check if the message has data (or payload)
        if (remoteMessage.getNotification() != null) {
            // Retrieve the notification body
            String notificationBody = remoteMessage.getNotification().getBody(); // I dont think this will be used anymore
            String notificationTitle = remoteMessage.getNotification().getTitle();
            String user = remoteMessage.getData().get("user"); // Extract announcer from notification data


            // Create a notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.bell) // Change notification icon here
                    .setContentTitle("QuickScanR")
                    .setContentText(user + ": " + notificationTitle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH); // Makes this heads-up/drop down notification

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return; // No permission = no notification

            }

            notificationManagerCompat.notify(12,builder.build()); // The notification is pushed, the id can be any number, helps make notification unique.

        } else {
            // Handle the message with only data payload here if needed
            Log.d("PushNotif", "Message data payload: " + remoteMessage.getData());
        }
    }


    /**
     * Required method, deals with token instantiation for FCM.
     * @param token The token used for sending messages to this application instance. This token is
     *     the same as the one retrieved by {@link FirebaseMessaging#getToken()}.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}


