package com.example.sns_project.fragment;

import com.example.sns_project.Notifications.MyResponse;
import com.example.sns_project.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAYUrg6jk:APA91bGRijQtSQeHAUR1zz7riS3U4Ztnnp-UhcApkOddkGa4-Fa9_lWA3Q5CEey0lSkSh9Ja8UIiljehMwrcSzOX4RR3dRCG-4kg7seVQ8uPxjh_qpqvTyr62l-__7B1QbiqnEaFf6H8"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Sender body);
}
