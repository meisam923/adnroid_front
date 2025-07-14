// STEP 1: Create the DTOs
// Create a new package in your Android project, e.g., 'com.yourname.app.dto'
// Inside that package, create a new file, e.g., 'UserDto.kt'


```

```java
// STEP 2: Create the API Service Interface
// Create a new package, e.g., 'com.yourname.app.network'
// Inside that package, create a new file: 'ApiService.kt'

package com.yourname.app.network // Use your actual package name

import com.yourname.app.dto.UserDto // Import your DTOs
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * This interface defines all the API endpoints your app will call, using Retrofit annotations.
 */
interface ApiService {

    @POST("auth/login") // The path of your endpoint
    suspend fun loginUser(
        @Body loginRequest: UserDto.LoginRequestDTO
    ): Response<UserDto.LoginResponseDTO>

    // You will add other endpoints here later, like register, getVendors, etc.
    // @POST("auth/register")
    // suspend fun registerUser(...)
}
```

```java
// STEP 3: Create the Retrofit Instance
// In the same 'network' package, create a new file: 'RetrofitInstance.kt'

package com.yourname.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // This is the special IP address to access your computer's
    // localhost from the Android Emulator.
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
