package thevoid.iam.revoluttestapp.widget

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.text.TextWatcher
import android.util.AttributeSet
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

    fun setText(text: String?) {
        disableWatcher()
        super.setText(text)
        super.setSelection(getText().length)
        enableWatcher()
    }

    fun disableWatcher() {
        if (textWatcher != null) {
            super.removeTextChangedListener(textWatcher)
        }
    }

    fun enableWatcher() {
        if (textWatcher != null) {
            super.addTextChangedListener(textWatcher)
        }
    }

}