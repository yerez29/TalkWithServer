package com.example.talkwithserver;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MyServerInreface {

    @GET("/users/{user_name}/token")
    Call<TokenResponse> getUserToken(@Path("user_name") String userName);

    @GET("/user")
    Call<UserResponse> getUserResponse(@Header("Authorization") String token);

    @Headers({
            "Content-Type:application/json"
    })
    @POST("/user/edit/")
    Call<UserResponse> postPrettyName(@Body SetUserPrettyNameRequest request, @Header("Authorization") String token);

    @Headers({
            "Content-Type:application/json"
    })
    @POST("/user/edit/")
    Call<UserResponse> chooseProfileImage(@Body SetUserProfileImageRequest request, @Header("Authorization") String token);

}
