package com.threehundredpercentbears.quiktrak.utils.monthlytransactions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyTransactionsHelper {

    private SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

    public SimpleDateFormat getFormat() {
        return format;
    }

    public void updateMonthFilter(MonthlyTransactionsViewModel viewModel, Calendar cal) {
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.setTime(cal.getTime());
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        firstDayOfMonth.set(Calendar.MINUTE, 0);
        firstDayOfMonth.set(Calendar.SECOND, 0);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.setTime(cal.getTime());
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        lastDayOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        lastDayOfMonth.set(Calendar.MINUTE, 59);
        lastDayOfMonth.set(Calendar.SECOND, 59);

        viewModel.updateMonthFilter(firstDayOfMonth.getTime(), lastDayOfMonth.getTime());
    }
}
