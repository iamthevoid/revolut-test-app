package thevoid.iam.revoluttestapp

import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import thevoid.iam.revoluttestapp.databinding.viewmodel.CurrencyNominal

/**
 * Created by alese_000 on 26.02.2018.
 */
// Observable List

fun diffCallback() : DiffObservableList.Callback<CurrencyNominal> {
    return object : DiffObservableList.Callback<CurrencyNominal> {
        override fun areItemsTheSame(oldItem: CurrencyNominal?, newItem: CurrencyNominal?): Boolean {
            return oldItem?.rate?.code == newItem?.rate?.code
        }

        override fun areContentsTheSame(oldItem: CurrencyNominal?, newItem: CurrencyNominal?): Boolean {
            return areItemsTheSame(oldItem, newItem) && oldItem?.rate?.rate == newItem?.rate?.rate
        }
    }
}

fun <T> add(list: MutableList<T>, vararg item : T) : List<T> {
    list.addAll(item)
    return list
}