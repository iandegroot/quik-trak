package com.threepbears.quiktrak;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

        editText.removeTextChangedListener(this);
        String nonFormattedValue = s.replaceAll("[$,.]", "");
        String formattedValue = CurrencyFormatter.createCurrencyFormattedString(Integer.parseInt(nonFormattedValue));

        editText.setText(formattedValue);
        editText.setSelection(formattedValue.length());
        editText.addTextChangedListener(this);
    }
}
