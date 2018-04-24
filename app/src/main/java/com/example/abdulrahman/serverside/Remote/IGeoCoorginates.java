package com.example.abdulrahman.serverside.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Abdulrahman on 11/29/2017.
 */

public interface IGeoCoorginates {
    @GET("maps/api/geocode/json")
    Call<String>getGeoCode(@Query("address")String address);
    @GET("maps/api/directions/json")
    Call<String>getDirection(@Query("origin")String origin,@Query("destination") String destination);
}
