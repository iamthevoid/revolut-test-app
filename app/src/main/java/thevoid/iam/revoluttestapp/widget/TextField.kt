package thevoid.iam.revoluttestapp.widget

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by alese_000 on 28.02.2018.
 */
class TextField @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        EditText(context, attrs, defStyleAttr) {

    private var textWatcher: TextWatcher? = null

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (textWatcher == null) super.addTextChangedListener(watcher)
        textWatcher = watcher
    }

    /**
     * setText calls "afterTextChanged" in TextWatcher, than we must disable Watcher before it
     * and enable after, for prevent recursion.
     */
    fun setText(text: String?) {
        disableWatcher()
        super.setText(text)
        setSelection(getText().length)
        post { requestFocus() }
        enableWatcher()
    }

    private fun disableWatcher() {
        if (textWatcher != null) {
            super.removeTextChangedListener(textWatcher)
        }
    }

    private fun enableWatcher() {
        if (textWatcher != null) {
            super.addTextChangedListener(textWatcher)
        }
    }

}