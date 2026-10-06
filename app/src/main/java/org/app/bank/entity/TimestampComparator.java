package org.app.bank.entity;

import java.util.*;

public class TimestampComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction t1, Transaction t2) {
        if (t1.getId() == t2.getId()) {
            return 0;
        }

        return t1.getTimestamp().compareTo(t2.getTimestamp());
    }
}