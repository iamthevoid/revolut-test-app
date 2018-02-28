package thevoid.iam.revoluttestapp.databinding.viewmodel

import android.databinding.*
import android.os.Bundle
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

    private val NOMINAL_EXTRA = "NOMINAL_EXTRA"
    private val CODE_EXTRA = "CODE_EXTRA"

    private var ratesDisposable: Disposable? = null
    private var editValueDisposable: Disposable? = null

    /**
     * Default values for init base nominal
     */

    private var nominal = ObservableFloat(100F)

    private var baseRate = ObservableField(CurrencyRate("EUR", 1F))

    private var needRequestFocus = ObservableBoolean(false)

    /**
     * BehaviourRelay for observe text editing.
     */

    private var valueRelay: BehaviorRelay<Float> = BehaviorRelay.create()

    /**
     * Using MergeObservableList for handle items replacements. It filled with two lists,
     * first - single element list - it will be replaced on each click
     * second - for other rates.
     */

    var list = MergeObservableList<CurrencyRate>()

    private var items: DiffObservableList<CurrencyRate> = DiffObservableList<CurrencyRate>(diffCallback())

    private var headRate: ObservableList<CurrencyRate> = ObservableArrayList()

    /**
     * fill list in constructor
     */

    init {
        list.insertList(headRate)
                .insertList(items)
    }

    /**
     * Variables binds for each item in recycler
     */

    val itemBinding: OnItemBindClass<CurrencyRate> = OnItemBindClass<CurrencyRate>()
            .map(CurrencyRate::class.java) { itemBinding, position, item ->
                itemBinding?.set(BR.rate, if (position == 0) R.layout.rate_item else R.layout.rate_item_text)?.bindExtra(BR.nominal, nominal)?.bindExtra(BR.relay, valueRelay)?.bindExtra(BR.needRequestFocus, needRequestFocus)?.bindExtra(BR.isBaseRate, list.isFirst(item))
            }

    private fun subscribeRates() {
        dispose(ratesDisposable)
        ratesDisposable = Observable.interval(0, 1, TimeUnit.MINUTES)
                .flatMap { Observable.defer({ Api.get().latest(baseRate.get().code) }) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.rates }
                .subscribe { items.update(it.sortedBy { it.code }) }
    }

    override fun initialize() {
        if (isSubscribed(ratesDisposable)) return
        headRate.add(baseRate.get())
        subscribeRates()
        editValueDisposable = valueRelay.subscribe {
            nominal.set(it)
        }
    }

    override fun deinitialize() {
        dispose(ratesDisposable, editValueDisposable)
    }

    override fun saveState(bundle: Bundle) {
        bundle.putFloat(NOMINAL_EXTRA, nominal.get())
        bundle.putString(CODE_EXTRA, baseRate.get().code)
    }

    override fun restoreState(savedInstantState: Bundle) {
        nominal.set(savedInstantState.getFloat(NOMINAL_EXTRA))
        baseRate.set(CurrencyRate(savedInstantState.getString(CODE_EXTRA), 1F))
    }


    var onRateClick = object : ItemClickSupport.OnItemClick<CurrencyRate> {
        override fun onItemClicked(recyclerView: RecyclerView, itemView: View, position: Int, item: CurrencyRate) {
            selectItem(position, item)
        }
    }

    /**
     * On item selecting we must swap list before new data will be loaded.
     */
    private fun selectItem(position: Int, item: CurrencyRate) {
        if (position != 0) {
            val oldHeadRate = headRate[0]
            baseRate.set(item)
            nominal.set(nominal.get() * item.rate)
            val newItems = makeNewList(item, oldHeadRate)
            recalculateRates(newItems, item)
            replaceBaseItem(item)
            items.update(newItems.sortedBy { it.code })
            needRequestFocus.set(true)
            subscribeRates()
        }
    }

    private fun replaceBaseItem(item: CurrencyRate) {
        item.rate = 1F
        headRate[0] = item
    }

    private fun recalculateRates(newItems: MutableList<CurrencyRate>, item: CurrencyRate) {
        newItems.forEach {
            it.rate /= item.rate
        }
    }

    private fun makeNewList(item: CurrencyRate, oldBase: CurrencyRate): MutableList<CurrencyRate> {
        val newItems = mutableListOf<CurrencyRate>()
        newItems.addAll(items)
        newItems.remove(item)
        newItems.add(oldBase)
        return newItems
    }
}