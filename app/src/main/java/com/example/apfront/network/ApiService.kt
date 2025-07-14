package com.example.apfront.network

import com.example.apfront.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    @POST("auth/login")
    suspend fun loginUser(
        @Body loginRequest: UserDto.LoginRequestDTO
    ): Response<UserDto.LoginResponseDTO>


}