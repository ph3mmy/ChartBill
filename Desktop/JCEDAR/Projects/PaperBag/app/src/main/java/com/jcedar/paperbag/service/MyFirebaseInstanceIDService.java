package com.jcedar.paperbag.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by OLUWAPHEMMY on 4/15/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    FirebaseAuth mAuth;
    String uuid, roles;

    @Override
    public void onCreate() {
        super.onCreate();
       /* mAuth = FirebaseAuth.getInstance();
        uuid = mAuth.getCurrentUser().getUid();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("User");
        mRef.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("TAG", "user role  inside LoGIN async " + dataSnapshot);
                if (dataSnapshot != null) {
                    roles = dataSnapshot.child("role").getValue(String.class);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    static void notifyActivity(String data, Context mContext){
        Intent intent = new Intent("token-event");
        intent.putExtra("token", data);
        intent.putExtra("onRefreshed", "TRUE");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        notifyActivity(refreshedToken, this);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        Log.e(TAG, "notification token = " + token);

/*        if (roles.equalsIgnoreCase("1")) {
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("Stores").child(uuid).child("storeSellerToken").setValue(token);
        }*/
    }
}
