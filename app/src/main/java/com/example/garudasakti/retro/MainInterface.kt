package com.example.garudasakti.retro

import com.example.garudasakti.ApiModels.Authentication.LoginResponse
import com.example.garudasakti.models.JamResponse
import com.example.garudasakti.models.KetentuanMembershipResponse
import com.example.garudasakti.models.LapanganHome
import com.example.garudasakti.models.MemberResponse
import com.example.garudasakti.models.PemesananLangsungRequest
import com.example.garudasakti.models.PemesananLangsungResponse
import com.example.garudasakti.models.PemesananPoinResponse
import com.example.garudasakti.models.PemesananSaldoResponse
import com.example.garudasakti.models.PesananSaya
import com.example.garudasakti.models.ProfilResponse
import com.example.garudasakti.models.RegisterRequest
import com.example.garudasakti.models.RegisterResponse
import com.example.garudasakti.models.TanggalResponse
import com.example.garudasakti.models.UpdatePasswordResponse
import com.example.garudasakti.models.UpdateProfilResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MainInterface {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<Void>

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

    @GET("ketentuan-membership")
    fun getKetentuanMembership(
        @Header("Authorization") token: String
    ): Call<List<KetentuanMembershipResponse>>

    @FormUrlEncoded
    @POST("tanggal-tersedia")
    fun getTanggal(
        @Header("Authorization") token: String,
        @Field("lapangan_id") lapangan_id: Int
    ): Call<List<TanggalResponse>>

    @FormUrlEncoded
    @POST("jam-tersedia")
    fun getJam(
        @Header("Authorization") token: String,
        @Field("lapangan_id") lapangan_id: Int,
        @Field("tanggal") tanggal: String
    ): Call<List<JamResponse>>

    @FormUrlEncoded
    @POST("pemesanan-saldo")
    fun pemesananSaldo(
        @Header("Authorization") token: String,
        @Field("lapangan_id") lapanganId: Int,
        @Field("tanggal") tanggal: String,
        @Field("jam[]") jamList: List<String>,
        @Field("nama_tim") namaTim: String
    ): Call<PemesananSaldoResponse>

    @FormUrlEncoded
    @POST("pemesanan-poin")
    fun pemesananPoin(
        @Header("Authorization") token: String,
        @Field("lapangan_id") lapanganId: Int,
        @Field("tanggal") tanggal: String,
        @Field("jam[]") jamList: List<String>,
        @Field("nama_tim") namaTim: String
    ): Call<PemesananPoinResponse>

    @POST("pemesanan-langsung")
    fun pemesananLangsung(
        @Header("Authorization") token: String,
        @Body requestBody: PemesananLangsungRequest
    ): Call<PemesananLangsungResponse>

}