package bobkallepalle.wya;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by bhavadeep on 4/1/2017.
 */

public class WYAFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TOKEN_BROADCAST = "wyatokenBrodcast";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FiidS", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
      //  sendRegistrationToServer(refreshedToken);
        storeToken(refreshedToken);
        getApplicationContext().sendBroadcast(new Intent(TOKEN_BROADCAST));
    }

    public void storeToken(String refreshedToken){

    }
}
