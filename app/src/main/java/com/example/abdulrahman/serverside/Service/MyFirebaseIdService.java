package com.example.abdulrahman.serverside.Service;

import com.example.abdulrahman.serverside.Common.Common;
import com.example.abdulrahman.serverside.model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Abdulrahman on 12/17/2017.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        updateToServer(refreshedToken);
    }

    private void updateToServer(String refreshedToken) {
        FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference tokens=database.getReference("Tokens");
        Token  data=new Token(refreshedToken,true);
        tokens.child(Common.currentuser.getPhone()).setValue(data);
    }
}
