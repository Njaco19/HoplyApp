package com.example.hoplyapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface that defines methods to interact with the remote JSON database.
 */

public interface RemoteDB {

    // String containing the bearer token.
    String bearer = "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMCJ9.PZG35xIvP9vuxirBshLunzYADEpn68wPgDUqzGDd7ok";

    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("comments")
    Call<List<Comment>> getComments();

    @GET("users")
    Call<List<User>> getUsers();

    @Headers(bearer)
    @POST("users")
    @FormUrlEncoded
    Call<User> insertUser(@Field("id") String id,
                          @Field("name") String name,
                          @Field("stamp") String stamp);

    @Headers(bearer)
    @POST("comments")
    @FormUrlEncoded
    Call<Comment> insertComment(@Field("user_id") String user_id,
                                @Field("post_id") int post_id,
                                @Field("content") String content,
                                @Field("stamp") String stamp);

    @Headers(bearer)
    @POST("posts")
    @FormUrlEncoded
    Call<Post> insertPost(@Field("id") int id,
                          @Field("user_id") String user_id,
                          @Field("content") String content,
                          @Field("stamp") String stamp);

    @Headers(bearer)
    @PATCH("users")
    Call<User> updateUsername(@Query("id") String id,
                              @Body User user);

}
