package thevoid.iam.revoluttestapp.data.model

import android.databinding.BaseObservable

/**
 * Created by alese_000 on 23.02.2018.
 */


/**
 * CurrensyRate extends by BaseObservable. It let us notify changes in list
 */
class CurrencyRate(val code: String, rate: Float) : BaseObservable() {

    var rate: Float = rate
        set(value) {
            field = value
            notifyChange()
        }

}