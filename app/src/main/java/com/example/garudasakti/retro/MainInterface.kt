package com.example.garudasakti.retro

import com.example.garudasakti.ApiModels.Authentication.LoginResponse
import com.example.garudasakti.models.LapanganHome
import com.example.garudasakti.models.PesananSaya
import com.example.garudasakti.models.ProfilResponse
import retrofit2.Call
import retrofit2.http.*

interface MainInterface {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("home")
    fun getLapanganList(
        @Header("Authorization") token: String
    ): Call<List<LapanganHome>>

    @GET("profil")
    fun getProfil(
        @Header("Authorization") token: String
    ): Call<ProfilResponse>


    @GET("pesanan-saya")
    fun getPesananSaya(
        @Header("Authorization") token: String
    ): Call<List<PesananSaya>>


}