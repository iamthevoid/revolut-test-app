package thevoid.iam.revoluttestapp

import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import thevoid.iam.revoluttestapp.data.model.CurrencyRate
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by alese_000 on 26.02.2018.
 */
// Observable List

fun diffCallback() : DiffObservableList.Callback<CurrencyRate> {
    return object : DiffObservableList.Callback<CurrencyRate> {
        override fun areItemsTheSame(oldItem: CurrencyRate?, newItem: CurrencyRate?): Boolean {
            return oldItem?.code == newItem?.code
        }

        override fun areContentsTheSame(oldItem: CurrencyRate?, newItem: CurrencyRate?): Boolean {
            return areItemsTheSame(oldItem, newItem) && oldItem?.rate == newItem?.rate
        }
    }
}

fun <T> add(list: MutableList<T>, vararg item : T) : List<T> {
    list.addAll(item)
    return list
}

fun setPrecision(float: Float, precision : Int) : Float {
    var value = BigDecimal(float.toDouble())
    value = value.setScale(precision, RoundingMode.HALF_EVEN) // here the value is correct (625.30)
    return value.toFloat()
}