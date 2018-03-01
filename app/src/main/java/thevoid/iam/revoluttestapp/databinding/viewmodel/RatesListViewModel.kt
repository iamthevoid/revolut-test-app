package thevoid.iam.revoluttestapp.databinding.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableFloat
import android.databinding.ObservableList
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.LinearInterpolator
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

    /**
     * Default values for init base nominal
     */

    private var nominal = ObservableFloat(DEFAULT_NOMINAL)

    private var baseRate = ObservableField(CurrencyRate(DEFAULT_CODE, DEFAULT_RATE))

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
                itemBinding?.set(BR.rate, if (position == 0) R.layout.rate_item else R.layout.rate_item_text)?.
                        bindExtra(BR.nominal, nominal)?.
                        bindExtra(BR.relay, valueRelay)?.
                        bindExtra(BR.isBaseRate, list.isFirst(item))
            }

    private fun subscribeRates() {
        dispose(ratesDisposable)
        ratesDisposable = Observable.defer({ Api.get().latest(baseRate.get().code) })
                .repeatWhen { it.delay(1, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.rates }
                .subscribe { items.update(it.sortedBy { it.code }) }
    }

    /**
     * On rate click recycler view scroll to top for let editable item place in focus
     *
     * 15 is a magic number. Can be each other for regulate scroll speed. Animation duration always
     * the same and we can speed up scroll by increasing scroll distance
     */

    var onRateClick = object : ItemClickSupport.OnItemClick<CurrencyRate> {
        override fun onItemClicked(recyclerView: RecyclerView, itemView: View, position: Int, item: CurrencyRate) {
            selectItem(position, item)
            recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            recyclerView.smoothScrollBy(0, -recyclerView.measuredHeight * 15, LinearInterpolator())
        }
    }

    /**
     * Lifecycle methods
     */

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
        bundle.putFloat(EXTRA_NOMINAL, nominal.get())
        bundle.putString(EXTRA_CODE, baseRate.get().code)
    }

    override fun restoreState(savedInstantState: Bundle) {
        nominal.set(savedInstantState.getFloat(EXTRA_NOMINAL, DEFAULT_NOMINAL))
        baseRate.set(CurrencyRate(savedInstantState.getString(EXTRA_CODE, DEFAULT_CODE), DEFAULT_RATE))
    }

    /**
     * On item selecting we must swap list before new data will be loaded.
     */
    private fun selectItem(position: Int, item: CurrencyRate) {
        if (position != 0) {

            // storing current base rate in temp var
            val oldHeadRate = headRate[0]

            // New list based on old, but old base rate inserts in list, and new base rate
            // removing from list
            updateList(item, oldHeadRate)

            // setting selected item as base rate
            replaceBaseItem(item)

            // after all unsubscribe from current Api subscription and resubscribe on it with new
            // base rate
            subscribeRates()
        }
    }

    private fun replaceBaseItem(item: CurrencyRate) {
        // setting nominal as multiplying current nominal and selected item rate
        // It let us use multiplier "1" for this rate for recalculating it for other
        nominal.set(nominal.get() * item.rate)

        // New base rate becomes "1"
        item.rate = DEFAULT_RATE

        baseRate.set(item)

        // Replace CurrencyRate in headRates "singleton" list
        headRate[0] = item
    }


    private fun updateList(item: CurrencyRate, oldBase: CurrencyRate) {
        val newItems = mutableListOf<CurrencyRate>()
        newItems.addAll(items)
        newItems.remove(item)
        newItems.add(oldBase)

        // Recalculate rates
        newItems.forEach { it.rate /= item.rate }

        // Update in Diff Observable List create insertion animation because sort order always the
        // and DiffCallback is the guarantee of new item will be animated inserted at correct position
        items.update(newItems.sortedBy { it.code })
    }
}