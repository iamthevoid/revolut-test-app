package thevoid.iam.revoluttestapp.databinding

import android.databinding.BindingAdapter
import android.databinding.BindingConversion
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxrelay2.BehaviorRelay
import me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
import thevoid.iam.revoluttestapp.R
import thevoid.iam.revoluttestapp.removeZeroesString
import thevoid.iam.revoluttestapp.setCurrencyCode
import thevoid.iam.revoluttestapp.widget.TextField

/**
 * Binding methods for views
 */

/**
 * ==== VIEW =====
 */

@BindingAdapter("visibile", "type", requireAll = false)
fun setVisibility(view: View, boolean: Boolean, type: Int) {
    if (!boolean && type != View.VISIBLE) {
        view.visibility = type
    } else {
        view.visibility = if (boolean) View.VISIBLE else View.GONE
    }
}

/**
* ==== TEXT VIEW =====
*/


@BindingAdapter("doubleValue", "precision", requireAll = false)
fun setTextViewText(view: TextView, float: Double, precision: Int?) {
    view.text = float.removeZeroesString(precision)
}

/**
* ==== TEXT FIELD =====
*/



@BindingAdapter("doubleValue", "precision", requireAll = false)
fun setFieldText(view: TextField, float: Double, precision: Int?) {
    view.setText(float.removeZeroesString(precision))
}

@BindingAdapter("editable")
fun setEditable(view: TextField, editable: Boolean) {
    view.isFocusable = editable
    view.isFocusableInTouchMode = editable
}

@BindingAdapter("onChange")
fun onChange(view: TextField, relay: BehaviorRelay<Double>) {
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            relay.accept(try {
                s.toString().toDouble()
            } catch (e: NumberFormatException) {
                0.toDouble()
            })
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })
}

/**
* ==== IMAGE VIEW =====
*/

// Call glide for each Currency Code and load flag image from remote or use placeholder
// if image not found
@BindingAdapter("flag")
fun setFlag(imageView: ImageView, currencyCode: String) {
    imageView.setCurrencyCode(currencyCode)
}

/**
 * ===== RECYCLER VIEW ====
 */

@BindingAdapter("itemClick")
fun <T> setItemClickListener(view: RecyclerView, listener: ItemClickSupport.OnItemClick<T>) {
    ItemClickSupport.addTo(view).setOnItemClickListener(itemClick(listener))
}

// Disables "blinking" on notify data set changed
@BindingAdapter("supportChangeAnimations")
fun setSupportChangeAnimations(view: RecyclerView, support: Boolean) {
    val itemAnimator = view.itemAnimator
    if (itemAnimator is SimpleItemAnimator)
        itemAnimator.supportsChangeAnimations = support
}

@Suppress("UNCHECKED_CAST")
@BindingConversion
fun <T> itemClick(listener: ItemClickSupport.OnItemClick<T>): ItemClickSupport.OnItemClickListener {
    return object : ItemClickSupport.OnItemClickListener {
        override fun onItemClicked(recyclerView: RecyclerView, itemView: View, position: Int) {
            if (position >= 0 && position < recyclerView.adapter.itemCount) {
                val adapterItem = (recyclerView.adapter as BindingCollectionAdapter<*>).getAdapterItem(position) as T
                listener.onItemClicked(recyclerView, itemView, position, adapterItem)
            }
        }
    }
}


class ItemClickSupport private constructor(private val mRecyclerView: RecyclerView) {
    private var mOnItemClickListener: OnItemClickListener? = null
    private val mOnClickListener = View.OnClickListener { v ->
        if (mOnItemClickListener != null) {
            val holder = mRecyclerView.getChildViewHolder(v)
            mOnItemClickListener!!.onItemClicked(mRecyclerView, v, holder.adapterPosition)
        }
    }

    /**
     *  Use attach listener for set onClickListener for each item in recycler
     */
    private val mAttachListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener)
            } else {
                view.setOnClickListener(null)
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {}
    }

    init {
        // pass Item Click support as tag
        mRecyclerView.setTag(R.id.item_click_support, this)
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?): ItemClickSupport {
        if (mOnItemClickListener == null && listener != null) {
            mOnItemClickListener = listener
            refreshAllChildren()
        } else if (mOnItemClickListener != null && listener == null) {
            mOnItemClickListener = null
            refreshAllChildren()
        } else {
            mOnItemClickListener = listener
        }
        return this
    }

    /**
     * After setting listener refresh all items in recycler and call "attachListener" for each
     */
    private fun refreshAllChildren() {
        (0 until mRecyclerView.childCount)
                .map { mRecyclerView.getChildAt(it) }
                .forEach { mAttachListener.onChildViewAttachedToWindow(it) }
    }

    interface OnItemClickListener {
        fun onItemClicked(recyclerView: RecyclerView, itemView: View, position: Int)
    }

    interface OnItemClick<in T> {
        fun onItemClicked(recyclerView: RecyclerView, itemView: View, position: Int, item: T)
    }

    companion object {

        fun addTo(view: RecyclerView): ItemClickSupport {
            var support: ItemClickSupport? = view.getTag(R.id.item_click_support) as? ItemClickSupport
            if (support == null) {
                support = ItemClickSupport(view)
            }
            return support
        }
    }
}