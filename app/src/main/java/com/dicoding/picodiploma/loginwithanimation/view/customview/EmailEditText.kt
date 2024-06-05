package com.dicoding.picodiploma.loginwithanimation.view.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.loginwithanimation.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText (context, attrs) {

    init {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                if (!isValid) {
                    error = context.getString(R.string.emailWarning)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //do Nothing
            }

        })
    }

}