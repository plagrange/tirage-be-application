package com.dictao.dtp.web.rest;

import com.dictao.dtp.persistence.TransactionService;
import com.dictao.dtp.persistence.entity.Transaction;
import java.util.List;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;

public class MockHelper {

    public static Transaction getCreatedTransaction(TransactionService mock) {
        ArgumentCaptor<Transaction> act = ArgumentCaptor.forClass(Transaction.class);
        verify(mock, atLeastOnce()).create(act.capture());
        List<Transaction> values = act.getAllValues();
        return values.get(values.size() - 1);
    }
}
