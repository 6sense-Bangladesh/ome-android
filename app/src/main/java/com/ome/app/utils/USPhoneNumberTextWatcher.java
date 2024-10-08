package com.ome.app.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class USPhoneNumberTextWatcher implements TextWatcher {

    private final int PHONE_NUMBER_LENGTH = 10;
    private final int PHONE_FORMAT_TRIGGER_LENGTH = PHONE_NUMBER_LENGTH + 1;
    private final String PHONE_FORMAT = "%s-%s-%s";

    private EditText mEditText;
    private boolean mIsDeleting;
    private int mOnTextChangedCount, mBeforeTextChangedCount;

    public USPhoneNumberTextWatcher(EditText editText) {
        this.mEditText = editText;
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mOnTextChangedCount = count;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mBeforeTextChangedCount = count;
    }

    public void afterTextChanged(Editable s) {
        mIsDeleting = mOnTextChangedCount < mBeforeTextChangedCount;
        if(mIsDeleting && s.length() == PHONE_FORMAT_TRIGGER_LENGTH) {
            mEditText.removeTextChangedListener(this);
            mEditText.setText(toPlainNumbers(s.toString()));
            mEditText.setSelection(mEditText.getText().toString().length());
            mEditText.addTextChangedListener(this);
        }
        else if(!mIsDeleting && s.length() == PHONE_NUMBER_LENGTH) {
            mEditText.removeTextChangedListener(this);
            mEditText.setText(toFormatNumbers(s.toString()));
            mEditText.setSelection(mEditText.getText().toString().length());
            mEditText.addTextChangedListener(this);
        }
    }

    private String toFormatNumbers(String s) {
        return String.format(PHONE_FORMAT, s.substring(0,3), s.substring(3,6), s.substring(6));
    }

    private String toPlainNumbers(String s) {
        return s.replace("-", "");
    }
}
