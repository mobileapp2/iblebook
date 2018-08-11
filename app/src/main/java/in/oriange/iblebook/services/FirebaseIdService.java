package in.oriange.iblebook.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import in.oriange.iblebook.utilities.UserSessionManager;

/**
 * Created by priyeshp on 02-01-18.
 */

public class FirebaseIdService  extends FirebaseInstanceIdService {

    private Context context;
//    private UserSessionManager session;

    @Override
    public void onTokenRefresh() {
        //Get hold of the registration token
        context = FirebaseIdService.this;

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("TokenID", "" + token);

//        session = new UserSessionManager(context);
//
//        if(token != null)
//            session.createAndroidToken(token);
    }

    private void sendRegistrationToServer(String token) {
        //Implement this method if you want to store the token on your server
    }
}
