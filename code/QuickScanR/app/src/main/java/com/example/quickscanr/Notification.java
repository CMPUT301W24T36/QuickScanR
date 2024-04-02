package com.example.quickscanr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.checkerframework.common.returnsreceiver.qual.This;


public class Notification extends FirebaseMessagingService {



    /**
     * Creating a token for each user
    @param token The token used for sending messages to this application instance. This token is
    the same as the one retrieved by {@link FirebaseMessaging#getToken()}.*/
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);}

    /**
     * To respond to a notification
    @param message Remote message that has been received.*/@Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);}
}