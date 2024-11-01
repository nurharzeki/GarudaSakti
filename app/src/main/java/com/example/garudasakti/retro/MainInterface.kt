package com.example.garudasakti.retro

import com.example.garudasakti.ApiModels.Authentication.LoginResponse
import com.example.garudasakti.models.LapanganHome
import com.example.garudasakti.models.MemberResponse
import com.example.garudasakti.models.PesananSaya
import com.example.garudasakti.models.ProfilResponse
import com.example.garudasakti.models.RegisterRequest
import com.example.garudasakti.models.RegisterResponse
import com.example.garudasakti.models.UpdatePasswordResponse
import com.example.garudasakti.models.UpdateProfilResponse
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

    @POST("register")
    fun registerUser(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @GET("member")
    fun getMemberData(
        @Header("Authorization") token: String
    ): Call<MemberResponse>

    @PATCH("update-profil")
    fun updateProfil(
        @Header("Authorization") token: String,
        @Body profilData: Map<String, String>
    ): Call<UpdateProfilResponse>

    @FormUrlEncoded
    @PATCH("update-password")
    fun updatePassword(
        @Header("Authorization") token: String,
        @FieldMap passwordData: Map<String, String>
    ): Call<UpdatePasswordResponse>

}