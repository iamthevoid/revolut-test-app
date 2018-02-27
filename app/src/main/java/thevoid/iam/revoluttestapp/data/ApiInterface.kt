package thevoid.iam.revoluttestapp.data

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import thevoid.iam.revoluttestapp.data.model.RatesResponse


/**
 * Created by alese_000 on 21.02.2018.
 */

interface ApiInterface {

    @GET("latest")
    fun latest(@Query("base") base: String): Observable<RatesResponse>

}