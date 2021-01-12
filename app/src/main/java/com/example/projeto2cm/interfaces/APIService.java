package com.example.projeto2cm.interfaces;

import com.example.projeto2cm.notifications.MyResponse;
import com.example.projeto2cm.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:Key=AAAAqZmd0dY:APA91bH1bP8bjRF6RsLTf9014i-NXhqWuAdutcykvv_jziQedA2roCrLL5CapO-4c6kfp68vBt9gpXrKnGRdfKewir-Ka0rHVGKbauVhJLZNgTy4ZhJpAbwufJxNPHKi2TC6q95urYZs"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}