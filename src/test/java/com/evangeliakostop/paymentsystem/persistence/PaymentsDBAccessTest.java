package com.evangeliakostop.paymentsystem.persistence;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentsDBAccessTest {

    @Mock
    private JdbcTemplate paymentsDbTemplate;
    @InjectMocks
    private PaymentsDBAccess paymentsDBAccess;

    private static final String INSERT_TRANSACTION =
            "INSERT INTO transactions (transaction_id, status, transaction_type, amount, refunded_amount, currency) VALUES(?, ?, ?, ?, ?, ?)";


    @Test
    void insertInitTransactionSuccess_payment() {

        String transactionId = "";
        Long amount = (long) 100.0;
        String currency = "";

        when(paymentsDbTemplate.update(anyString(), anyString(), anyString(), anyString(), anyDouble(), anyDouble(), anyString())).thenReturn(1);
        paymentsDBAccess.insertInitTransaction(transactionId, TransactionType.PAYMENT, amount, currency);

        verify(paymentsDbTemplate, times(1)).update(anyString(), anyString(), anyString(), anyString(), anyLong(), anyDouble(), anyString());
    }

    @Test
    void insertInitTransactionSuccess_refund() {

        String transactionId = "";
        Long amount = (long) 100.0;
        String currency = "";

        when(paymentsDbTemplate.update(anyString(), anyString(), anyString(), anyString(), anyDouble(), anyDouble(), anyString())).thenReturn(1);
        paymentsDBAccess.insertInitTransaction(transactionId, TransactionType.REFUND, amount, currency);

        verify(paymentsDbTemplate, times(1)).update(anyString(), anyString(), anyString(), anyString(), anyLong(), anyDouble(), anyString());
    }

    @Test
    void insertInitTransactionException_payment() {

        String transactionId = "";
        Long amount = 100L;
        String currency = "";

        when(paymentsDbTemplate.update(
                eq(INSERT_TRANSACTION),
                eq(transactionId),
                eq(PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription()),
                eq(TransactionType.PAYMENT.getDescription()),
                eq(amount),
                eq(0.0),  // This is for non-refund case
                eq(currency)))
                .thenThrow(new DataAccessException("error") {
                });

        assertThrows(RuntimeException.class, () ->
                paymentsDBAccess.insertInitTransaction(transactionId, TransactionType.PAYMENT, amount, currency)
        );

        // Verify the update was called once
        verify(paymentsDbTemplate, times(1)).update(
                eq(INSERT_TRANSACTION),
                eq(transactionId),
                eq(PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription()),
                eq(TransactionType.PAYMENT.getDescription()),
                eq(amount),
                eq(0.0),
                eq(currency)
        );

    }
}