package com.example.web

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.lang.Exception

abstract class BaseApiResponse {
    fun <T> safeApiCall(apiCall:suspend () -> Response<T>): Flow<NetworkResult<T>> {
        return flow {
            emit(NetworkResult.Loading<T>() as NetworkResult<T>)
            val response = apiCall()
            emit(
                try {
                    if (response.isSuccessful) {
                        val body = response.body()
                        NetworkResult.Success(body) as NetworkResult<T>
                    } else {
                        error("${response.code()} ${response.message()}")
                    }
                } catch (e: Exception) {
                    error(e.message ?: e.toString())
                }
            )
        }
    }
    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Api call failed: $errorMessage")
}