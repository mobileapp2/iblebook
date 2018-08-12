package in.oriange.iblebook.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import in.oriange.iblebook.utilities.UserSessionManager;

/**
 * Created by priyeshp on 02-01-18.
 */

public class FirebaseIdService extends FirebaseInstanceIdService {

    private Context context;
    private UserSessionManager session;

    @Override
    public void onTokenRefresh() {
        context = FirebaseIdService.this;
        session = new UserSessionManager(context);

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("TokenID", "" + token);

        if (token != null) {
            session.createAndroidToken(token);
        }
    }
}
