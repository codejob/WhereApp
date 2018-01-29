package com.where.prateekyadav.myapplication.Util

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.where.prateekyadav.myapplication.R

/**
 * Created by Infobeans on 2/13/2017.
 */
class GenericTextWatcher(val textInputLayout: TextInputLayout, var view: View, val mContext: Context, val mDrawableClear: Drawable) : TextWatcher {
    private val mEditText: EditText
    private var mBeforeTextChangeLength: Int = 0

    init {
        mEditText = view as EditText


    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        val legth = charSequence.toString().length
        mBeforeTextChangeLength = legth
        Log.d(AppConstant.TAG_KOTLIN_DEMO_APP, legth.toString() + "")
    }

    /**
     * OnText change
     * @param charSequence
     * @param i
     * @param i1
     * @param i2
     */
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        if (mEditText.text.toString().trim { it <= ' ' }.length > 0) {
            mEditText.setCompoundDrawables(null, null, mDrawableClear, null)
            removeError(charSequence.toString())

            mEditText.background.setColorFilter(mContext.resources.getColor(R.color.edittext_hintcolor), PorterDuff.Mode.SRC_ATOP)
            /*mEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
               if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getX() > (view.getWidth() - view.getPaddingRight())) {
                        ((EditText) view).setText("");
                    }
                }
                return false;
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP && mEditText.getText().toString().trim().length()>0) {
                        mEditText.setCompoundDrawables(null, null, mDrawableClear, null);
                        if (motionEvent.getRawX() >= (mEditText.getRight() - (mEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()+50))) {
                            // your action here
                            ((EditText) view).setText("");
                            mEditText.setText("");
                            return true;
                        }
                    }
                    return false;
                }
            });*/
        } else {
            mEditText.setCompoundDrawables(null, null, null, null)
            mEditText.background.setColorFilter(mContext.resources.getColor(R.color.edittext_fontcolor), PorterDuff.Mode.SRC_ATOP)
            // setError("This is a required field");
            // mEditText.getBackground().setColorFilter(mContext.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }
    }


    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        when (view.id) {
           /* R.id.edt_first_name ->
                //    model.setName(text);
                removeError(text)
            R.id.edt_last_name ->
                // model.setEmail(text);
                removeError(text)
            R.id.edt_organisation_name -> removeError(text)
            R.id.edt_password ->
                //  model.setPhone(text);
                removeError(text)
            R.id.edt_cnf_password ->
                //  model.setPhone(text);
                removeError(text)*/
        }
    }

    internal fun setError(msg: String) {
        //textInputLayout.setErrorEnabled(true);
        textInputLayout.error = msg
    }

    internal fun removeError(text: String) {

        if (text.trim { it <= ' ' }.length > 0) {
            textInputLayout.isErrorEnabled = false
            textInputLayout.error = null
            /*
            mEditText.getBackground().setColorFilter(mContext.getResources().getColor(R.color.edittext_fontcolor), PorterDuff.Mode.SRC_ATOP);
            // setError(null);
            if (textInputLayout.getChildCount() == 2)
                textInputLayout.getChildAt(1).setVisibility(View.GONE);*/
        }

    }

    /**
     *
     * @param s
     */
    private fun numberFormatting(s: Editable) {
        var isBackSpace: Boolean? = false
        var value: String? = s.toString()
        if (value!!.length < mBeforeTextChangeLength) {
            Log.d(AppConstant.TAG_KOTLIN_DEMO_APP, "Char is deleted")
            isBackSpace = true
        }
        var a: String? = ""
        var b: String? = ""
        var c: String? = ""
        if (value != null && value.length > 0) {
            value = value.replace("-", "")
            if (value.length > 4) {
                a = value.substring(0, 4)
            } else if (value.length <= 4) {
                a = value.substring(0, value.length)
            }
            if (value.length > 7) {
                b = value.substring(4, 7)
                c = value.substring(7, value.length)
            } else if (value.length > 4 && value.length <= 7) {
                b = value.substring(4, value.length)
            }
            val stringBuffer = StringBuffer()
            if (a != null && a.length > 0) {
                stringBuffer.append(a)
                if (a.length == 4 && b != null && b.length > 0) {
                    // TODO
                    stringBuffer.append("-")
                }
            }
            if (b != null && b.length > 0) {
                stringBuffer.append(b)
                if (b.length == 3 && c != null && c.length > 0) {
                    stringBuffer.append("-")
                }
            }
            if (c != null && c.length > 0) {
                stringBuffer.append(c)
            }
            val cursorLastPosition = mEditText.selectionStart
            mEditText.removeTextChangedListener(this)
            mEditText.setText(stringBuffer.toString())
            //mEditText.setSelection(mEditText.getText().toString().length());
            setPositionOfCursor(isBackSpace, cursorLastPosition)
            mEditText.addTextChangedListener(this)
        } else {
            mEditText.removeTextChangedListener(this)
            mEditText.setText("")
            mEditText.addTextChangedListener(this)
        }

    }

    /**
     *
     * @param isBackSpace
     */
    private fun setPositionOfCursor(isBackSpace: Boolean?, lastPosition: Int) {
        if (isBackSpace!!) {
            try {
                mEditText.setSelection(lastPosition)
            } catch (ex: Exception) {
                mEditText.setSelection(lastPosition - 1)
            }

        } else {
            val length = mEditText.text.length
            if (length == lastPosition) {
                mEditText.setSelection(lastPosition)
            } else {
                val newPos = lastPosition + 1
                if (lastPosition == 5) {
                    // newPos++;
                    mEditText.setSelection(newPos)
                } else if (lastPosition == 9) {
                    mEditText.setSelection(newPos)
                } else {
                    mEditText.setSelection(lastPosition)
                }
            }
        }

    }

}