package com.example.bechat.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.SimpleDateFormat

object Util{
    @SuppressLint("ClickableViewAccessibility")
    fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }

    fun hideKeyboard(context: Context, view: View){
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun String.timeToList(): String{
        val day = this.subSequence(4,15).toString()
        val today = SimpleDateFormat("DD MMM yyyy").format(java.util.Date()).toString()
        if (day == today){
            return this.subSequence(16,21).toString()
        }else{
            return this.subSequence(4,10).toString()
        }
    }

    fun String.timeToDetail(): String{
        val day = this.subSequence(4,15).toString()
        val today = SimpleDateFormat("DD MMM yyyy").format(java.util.Date()).toString()
        if (day == today){
            return this.subSequence(15,21).toString()
        }else{
            return this.subSequence(4,10).toString() + " AT " +this.subSequence(16,21).toString()
        }
    }
    fun convertTime(time: String, format: String): Long {
        val sdf = SimpleDateFormat(format)
        val mDate = sdf.parse(time)
        val timeInMilliseconds = mDate.time
        return timeInMilliseconds
    }
}