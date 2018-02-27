package thevoid.iam.revoluttestapp.databinding

import android.databinding.BindingAdapter
import android.databinding.BindingConversion
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.jakewharton.rxrelay2.BehaviorRelay
import me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
import thevoid.iam.revoluttestapp.R
import java.text.ParseException


@BindingAdapter("visibile", "type", requireAll = false)
fun setVisibility(view: View, boolean: Boolean, type: Int) {
    if (!boolean && type != View.VISIBLE) {
        view.visibility = type
    } else {
        view.visibility = if (boolean) View.VISIBLE else View.GONE
    }
}

@BindingAdapter("onChange")
fun onChange(view: EditText, relay: BehaviorRelay<Float>) {
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            var value: Float
            try {
                value = s.toString().toFloat()
            } catch (e: ParseException) {
                value = 0F
            }
            relay.accept(value)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })
}


@BindingAdapter("flag")
fun setFlag(imageView: ImageView, currencyCode: String) {
    Glide.with(imageView.context)
            .load("http://s.xe.com/themes/xe/images/flags/big/${currencyCode.toLowerCase()}.png")
            .into(imageView)
}

@BindingAdapter("itemClick")
fun <T> setItemClickListener(view: RecyclerView, listener: ItemClickSupport.OnItemClick<T>) {
    ItemClickSupport.addTo(view).setOnItemClickListener(itemClick(listener))
}

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

    private fun refreshAllChildren() {
        (0 until mRecyclerView.childCount)
                .map { mRecyclerView.getChildAt(it) }
                .forEach { mAttachListener.onChildViewAttachedToWindow(it) }
    }

    private fun detach(view: RecyclerView) {
        view.removeOnChildAttachStateChangeListener(mAttachListener)
        view.setTag(R.id.item_click_support, null)
    }

    interface OnItemClickListener {

        fun onItemClicked(recyclerView: RecyclerView, itemView: View, position: Int)
    }

    interface OnItemClick<T> {
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

        fun removeFrom(view: RecyclerView): ItemClickSupport? {
            val support = view.getTag(R.id.item_click_support) as ItemClickSupport
            support.detach(view)
            return support
        }
    }
}