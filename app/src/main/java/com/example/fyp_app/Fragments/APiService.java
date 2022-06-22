package com.example.fyp_app.Fragments;

import com.example.fyp_app.Notifications.MyResponse;
import com.example.fyp_app.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorisation:key=AAAAgHZ91E8:APA91bETiAVz6gsoNLlx4knTi5SZF8VVO0mTOehHP3-qwBfLN69ktxzbZhuWqzvOwKUU8NXcgNnJ1OMYUgjy91rZn45MEG3PaTgn03q0XnVp1_YBF5uEgjhR_f9mRMw4okLvrFlasRjS"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
