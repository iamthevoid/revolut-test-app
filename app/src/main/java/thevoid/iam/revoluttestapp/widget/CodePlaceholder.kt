package thevoid.iam.revoluttestapp.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import thevoid.iam.revoluttestapp.R

/**
 * Created by alese_000 on 28.02.2018.
 */
class CodePlaceholder constructor(context : Context, private val currencyCode: String) : Drawable() {


    /**
     * Used "rainbow" palette for empty placeholders. If color depends on any constant value, that
     * depends on string(as example we can convert hashcode into hex and cut unwanted digits), but
     * it produce unwanted and non beautiful colors (black, brown, lime and each other), than
     * we can also use hashcode, non as it is, but using reminder of dividing it by pallette array
     * length. It let us use that reminder as index of this array and take wanted colors.
     */
    private val palette = arrayOf(
            R.color.rainbow_red,
            R.color.rainbow_orange,
            R.color.rainbow_yellow,
            R.color.rainbow_green,
            R.color.rainbow_light_blue,
            R.color.rainbow_blue,
            R.color.rainbow_purple
    )

    private val TEXT_PERCENT = 0.35F

    private val textPaint: Paint = Paint()
    private val backgroundPaint: Paint
    private var placeholderBounds: RectF? = null

    private var textStartXPoint: Float = 0F
    private var textStartYPoint: Float = 0f

    private var width = -1
    private var height = -1

    init {
        textPaint.isAntiAlias = true
        textPaint.color = Color.parseColor("#fafafa")
        textPaint.isFakeBoldText = true

        backgroundPaint = Paint()
        backgroundPaint.isAntiAlias = true
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = getColor(context, currencyCode)
    }

    private fun getColor(context: Context, currencyCode: String): Int {
        return ContextCompat.getColor(context, palette[Math.abs(currencyCode.hashCode() % palette.size)])
    }

    override fun draw(canvas: Canvas) {
        if (placeholderBounds == null || width != bounds.width() || height != bounds.height()) {
            width = bounds.width()
            height = bounds.height()
            placeholderBounds = RectF(0f, 0f, bounds.width().toFloat(), bounds.height().toFloat())
            textPaint.textSize = bounds.width().toFloat() * TEXT_PERCENT
            setAvatarTextValues()
        }

        canvas.drawOval(placeholderBounds!!, backgroundPaint)
        canvas.drawText(currencyCode, textStartXPoint, textStartYPoint, textPaint)
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
        backgroundPaint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        backgroundPaint.colorFilter = colorFilter
    }

    private fun setAvatarTextValues() {
        textStartXPoint = calculateTextStartXPoint()
        textStartYPoint = calculateTextStartYPoint()
    }

    private fun calculateTextStartXPoint(): Float {
        val stringWidth = textPaint.measureText(currencyCode)
        return bounds.width() / 2f - stringWidth / 2f
    }

    private fun calculateTextStartYPoint(): Float {
        return bounds.height() / 2f - (textPaint.ascent() + textPaint.descent()) / 2f
    }
}

