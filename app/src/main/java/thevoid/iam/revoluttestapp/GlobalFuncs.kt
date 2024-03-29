package thevoid.iam.revoluttestapp

import io.reactivex.disposables.Disposable
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

// Util

fun setPrecision(float: Double, precision : Int) : Double {
    return BigDecimal(float).setScale(precision, RoundingMode.HALF_EVEN).toDouble()
}


// RX

fun isSubscribed(disposable: Disposable?) = disposable != null && !disposable.isDisposed

fun dispose(disposable: Disposable?) {
    disposable?.dispose()
}

fun dispose(disposable: Disposable?, vararg disposables: Disposable?) {
    disposable?.dispose()
    disposables.forEach { it?.dispose() }
}