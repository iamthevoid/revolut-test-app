package thevoid.iam.revoluttestapp.data.model

import com.google.gson.annotations.JsonAdapter
import thevoid.iam.revoluttestapp.data.adapter.RatesAdapter
import thevoid.iam.revoluttestapp.data.adapter.StringDateAdapter
import java.util.*

/**
 * Created by alese_000 on 21.02.2018.
 */
data class RatesResponse(@JsonAdapter(StringDateAdapter::class)
                         val date: Date,
                         val base : String,
                         @JsonAdapter(RatesAdapter::class)
                         val rates : List<CurrencyRate>)