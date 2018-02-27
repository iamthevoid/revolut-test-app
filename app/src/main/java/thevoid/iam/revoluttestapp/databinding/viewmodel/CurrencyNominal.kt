package thevoid.iam.revoluttestapp.databinding.viewmodel

import android.databinding.BaseObservable
import thevoid.iam.revoluttestapp.data.model.CurrencyRate

/**
 * Created by alese_000 on 25.02.2018.
 */
class CurrencyNominal(val rate: CurrencyRate, baseNominal: Float = 0F) : BaseObservable() {

    var baseNominal: Float = baseNominal
        set(value) {
            field = value
            notifyChange()
        }

    val stringValue : String
    get() {
        val nominal = baseNominal * rate.rate
        return if(nominal == nominal.toLong().toFloat())
            String.format("%d", nominal.toLong());
        else
            String.format("%.2f", nominal);
    }
}