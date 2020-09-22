package com.threehundredpercentbears.quiktrak.addtransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.threehundredpercentbears.quiktrak.utils.formatters.CurrencyFormatter;

import java.lang.ref.WeakReference;

public class CurrencyEditTextWatcher implements TextWatcher {

    private final WeakReference<EditText> editTextWeakReference;

    public CurrencyEditTextWatcher(EditText editText) {
        editTextWeakReference = new WeakReference<>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();

        if (editText == null) {
            return;
        }

        String s = editable.toString();

        if (s.isEmpty()) {
            return;
        }

        String nonFormattedValue = s.replaceAll("[$,.a-zA-Z]", "");
        nonFormattedValue = nonFormattedValue.trim();
        try {
            String formattedValue = CurrencyFormatter.createCurrencyFormattedString(Integer.parseInt(nonFormattedValue));

            editText.removeTextChangedListener(this);
            editText.setText(formattedValue);
            editText.setSelection(formattedValue.length());
            editText.addTextChangedListener(this);
        } catch (NumberFormatException e) {
            Log.e("CurrencyEditTextWatcher", "Error parsing " + nonFormattedValue + ", was " + s, e);
        }

    }
}
