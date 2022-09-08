package com.sample.quotesapp.data.sources.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.sample.quotesapp.BuildConfig
import com.sample.quotesapp.R
import com.sample.quotesapp.data.models.QuotesApiResponse
import com.sample.quotesapp.data.models.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import javax.inject.Inject

class QuotesInternetDataSource @Inject constructor(
    private val quotesApi: QuotesApi,
    @ApplicationContext private val context: Context,
) : QuotesNetworkDataSource {

    override suspend fun getQuotes(page: Int): Resource<QuotesApiResponse> =
        quotesApi.getQuotes(page).callWithExceptionHandling()

    /**
     * This method will execute the `Call<T>` and handle the exceptions
     * that can happen. It returns either `Success<T>` or `Failure<T>`.
     *
     * Consumers of the API calls can check the result using this style:
     *
     *      result = MealzApi.someApiCall()
     *
     *      when (result){
     *          is Success -> doSomething(result.contents)
     *          is Failure -> showError(result.throwable.message)
     *      }
     *
     */
    private fun <T : Any> Call<T>.callWithExceptionHandling(): Resource<T> {

        val serverErrorMsg = context.getString(R.string.server_error_message)

        var responseCode = 101
        var responseMsg = serverErrorMsg


        if (!isNetworkAvailable()) {
            return Resource.Error(
                Throwable(context.getString(R.string.offline_message))
            )
        }


        return try {

            val response = execute()

            if (response.isSuccessful && response.body() != null) {

                Resource.Success(response.body() as T)

            } else {

                responseCode = response.code()

                /*
                Retrofit currently doesn't convert its `errorBody` to JSON. We need to do that
                manually. Ref: https://github.com/square/retrofit/issues/1321
                */
                val errorBody = response.errorBody()?.string()

                val errorResponse = runCatching {
                    Gson().fromJson(
                        errorBody,
                        QuotesApiResponse::class.java
                    )
                }.getOrNull()

                responseMsg = errorResponse?.statusMessage ?: response.message()

                when (errorResponse) {
                    null -> throw java.lang.Exception(
                        if (BuildConfig.DEBUG)
                            context.getString(R.string.error_exception_message, errorBody)
                        else serverErrorMsg
                    )
                    else -> throw java.lang.Exception(
                        if (BuildConfig.DEBUG)
                            responseMsg.ifBlank {
                                context.getString(
                                    R.string.exception_message,
                                    errorBody
                                )
                            }
                        else serverErrorMsg
                    )
                }
            }

        } catch (e: Exception) {

            if (e is JsonSyntaxException || e is MalformedJsonException || e is IllegalStateException) {
                responseCode = 100
                responseMsg = serverErrorMsg
            }

            if (responseMsg.isEmpty()) {
                responseMsg = serverErrorMsg
            }

            Resource.Error(
                Throwable(
                    if (BuildConfig.DEBUG)
                        "$responseCode: ${responseMsg.ifBlank { e.localizedMessage }}"
                    else serverErrorMsg
                )
            )
        }

    }

    /**
     * To check if internet connection is available or not
     * @return True on internet availability else false
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


}