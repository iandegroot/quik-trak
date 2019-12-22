package com.threehundredpercentbears.quiktrak.utils.monthlytransactions;

import java.util.Date;

public interface MonthlyTransactionsViewModel {

    void updateMonthFilter(Date startDate, Date endDate);
}
