package thevoid.iam.revoluttestapp.data.model

import android.databinding.BaseObservable

        /**
         * Created by alese_000 on 23.02.2018.
         */

typealias CurrencyCode = String

class CurrencyRate(val code: CurrencyCode, rate: Float) : BaseObservable() {

    var rate: Float = rate
        set(value) {
            field = value
            notifyChange()
        }

}