package thevoid.iam.revoluttestapp.data

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import thevoid.iam.revoluttestapp.BuildConfig
import java.util.concurrent.TimeUnit

/**
 * Created by alese_000 on 21.02.2018.
 */
object Api {

    private val ENDPOINT = BuildConfig.ENDPOINT

    private var apiInterface: ApiInterface? = null
    private lateinit var mClient: OkHttpClient
    private lateinit var mRetrofit: Retrofit

    fun get(): ApiInterface {

        if (apiInterface != null)
            return apiInterface as ApiInterface

        mClient = getOkHttpClient()
        mRetrofit = getRetrofit(mClient)
        apiInterface = mRetrofit.create(ApiInterface::class.java)
        return apiInterface!!
    }

    private fun getOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .followSslRedirects(true)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()


        return client
    }

    private fun getRetrofit(client: OkHttpClient): Retrofit {
        val retrofit = Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()


        return retrofit
    }
}