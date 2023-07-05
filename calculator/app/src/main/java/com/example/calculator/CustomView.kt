package com.example.calculator


import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout


class CustomView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {
    init {
        inflate(context, R.layout.custom_view, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomView).apply {
            getString(R.styleable.CustomView_textResult)?.let { setTextResult(it) }
            setOnError(getBoolean(R.styleable.CustomView_onError, true))

        }
        attributes.recycle()
    }


    fun setTextResult(result: String) {
        findViewById<TextView>(R.id.text_result).text = result
    }

    fun setOnError(error: Boolean) {
        if (error) {
            findViewById<TextView>(R.id.OnError).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.OnError).visibility = View.GONE
        }
    }
}