package thevoid.iam.revoluttestapp.databinding.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableFloat
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import thevoid.iam.revoluttestapp.*
import thevoid.iam.revoluttestapp.data.Api
import thevoid.iam.revoluttestapp.data.model.CurrencyRate
import thevoid.iam.revoluttestapp.databinding.ItemClickSupport
import java.util.concurrent.TimeUnit

/**
 * Created by alese_000 on 23.02.2018.
 */
class RatesListViewModel : ViewModel() {

    private var ratesDisposable: Disposable? = null
    private var editValueDisposable: Disposable? = null

    var items: DiffObservableList<CurrencyNominal> = DiffObservableList<CurrencyNominal>(diffCallback())

    var headRate: ObservableList<CurrencyRate> = ObservableArrayList()

    var list: MergeObservableList<Any> = MergeObservableList()

    private var currency: ObservableField<CurrencyRate> = ObservableField(CurrencyRate("EUR", 1F))

    private var nominal: ObservableFloat = ObservableFloat(100F)

    private var valueRelay: BehaviorRelay<Float> = BehaviorRelay.create()

    init {
        list.insertList(headRate)
                .insertList(items)
    }

    val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
            .map(CurrencyNominal::class.java) { itemBinding, _, item ->
                itemBinding?.set(BR.item, R.layout.nominal_item)?.bindExtra(BR.dividerVisible, !items.isLast(item))
            }
            .map(CurrencyRate::class.java) { itemBinding, _, _ ->
                itemBinding?.set(BR.rate, R.layout.rate_item)?.bindExtra(BR.nominal, nominal)?.bindExtra(BR.relay, valueRelay)
            }

    val focusChange = View.OnFocusChangeListener { v, hasFocus -> }

    override fun initialize() {
        if (isSubscribed(ratesDisposable)) return
        headRate.add(currency.get())
        subscribeRates()
        editValueDisposable = valueRelay.subscribe {
            items.forEach { currencyNominal: CurrencyNominal? ->
                currencyNominal?.baseNominal = it
            }
        }
    }

    private fun subscribeRates() {
        dispose(ratesDisposable)
        ratesDisposable = Observable.interval(0, 1, TimeUnit.MINUTES)
                .flatMap { Observable.defer({ Api.get().latest(currency.get().code) }) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.rates }
                .map { it.map { currencyRate -> CurrencyNominal(currencyRate, nominal.get()) } }
                .subscribe { items.update(it.sortedBy { it.rate.code }) }
    }

    override fun deinitialize() {
        dispose(ratesDisposable, editValueDisposable)
    }

    var onRateClick = object : ItemClickSupport.OnItemClick<Any> {
        override fun onItemClicked(recyclerView: RecyclerView, itemView: View, position: Int, item: Any) {
            if (item is CurrencyNominal) {
                val oldHeadRate = headRate[0]
                currency.set(item.rate)
                nominal.set(item.baseNominal * item.rate.rate)
                val newItems = makeNewList(item, oldHeadRate)
                recalculateRates(newItems, item)
                replaceBaseItem(item)
                items.update(newItems.sortedBy { it.rate.code })
                subscribeRates()
            }
        }
    }

    private fun replaceBaseItem(item: CurrencyNominal) {
        item.rate.rate = 1F
        headRate[0] = item.rate
    }

    private fun recalculateRates(newItems: MutableList<CurrencyNominal>, item: CurrencyNominal) {
        newItems.forEach {
            it.rate.rate /= item.rate.rate
            it.baseNominal = item.baseNominal
        }
    }

    private fun makeNewList(item: CurrencyNominal, oldBase: CurrencyRate): MutableList<CurrencyNominal> {
        val newItems = mutableListOf<CurrencyNominal>()
        newItems.addAll(items)
        newItems.remove(item)
        newItems.add(CurrencyNominal(oldBase, nominal.get()))
        return newItems
    }
}