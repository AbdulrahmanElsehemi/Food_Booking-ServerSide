package com.example.abdulrahman.serverside.Remote;

import com.example.abdulrahman.serverside.model.MyResponse;
import com.example.abdulrahman.serverside.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Abdulrahman on 12/18/2017.
 */

public interface APiService {
    @Headers(

            {
                    "Content-Type:application/json",
                    "Authentication:key=AAAAeQTkEAc:APA91bFXYI-WClhA8qwprCXQfCHzgIfY-m8zsnRoFA7SRNdSk55CCFqKs2GNDkElyTs3K13McG7ZvaO43neffCRDYeA3_ZsRaPMgmAQXMtzaf0OUhOO9f0PjaZMDtlrYkC5cU3DTosA7"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

