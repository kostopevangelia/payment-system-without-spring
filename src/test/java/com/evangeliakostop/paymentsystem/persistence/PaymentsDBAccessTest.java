package com.evangeliakostop.paymentsystem.persistence;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.TransactionType;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentsDBAccessTest {

    @Mock
    private HikariDataSource dataSource;

    @Mock private Connection connection;

    @Mock private PreparedStatement preparedStatement;

    @InjectMocks
    private PaymentsDBAccess paymentsDBAccess;

    private static final String INSERT_TRANSACTION =
            "INSERT INTO transactions (transaction_id, status, transaction_type, amount, refunded_amount, currency) VALUES(?, ?, ?, ?, ?, ?)";


    @Test
    void insertInitTransactionSuccess_payment() throws SQLException {

        String transactionId = "";
        Long amount = (long) 100.0;
        String currency = "";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        paymentsDBAccess.insertInitTransaction(
                transactionId,
                TransactionType.PAYMENT,
                amount,
                currency);

        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1))
                .setString(1, transactionId);

        verify(preparedStatement, times(1))
                .setString(
                        2,
                        PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription()
                );

        verify(preparedStatement, times(1))
                .setString(
                        3,
                        TransactionType.PAYMENT.getDescription()
                );

        verify(preparedStatement, times(1))
                .setLong(4, amount);

        verify(preparedStatement, times(1))
                .setDouble(5, 0.0);

        verify(preparedStatement, times(1))
                .setString(6, currency);

        verify(preparedStatement, times(1))
                .executeUpdate();    }

    @Test
    void insertInitTransactionSuccess_refund() throws SQLException {

        String transactionId = "";
        Long amount = (long) 100.0;
        String currency = "";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        paymentsDBAccess.insertInitTransaction(
                transactionId,
                TransactionType.REFUND,
                amount,
                currency);

        verify(dataSource, times(1)).getConnection();

        verify(connection, times(1))
                .prepareStatement(anyString());

        verify(preparedStatement, times(1))
                .setString(1, transactionId);

        verify(preparedStatement, times(1))
                .setString(
                        2,
                        PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription()
                );

        verify(preparedStatement, times(1))
                .setString(
                        3,
                        TransactionType.REFUND.getDescription()
                );

        verify(preparedStatement, times(1))
                .setLong(4, amount);

        verify(preparedStatement, times(1))
                .setDouble(5, amount);

        verify(preparedStatement, times(1))
                .setString(6, currency);

        verify(preparedStatement, times(1))
                .executeUpdate();
    }

    @Test
    void insertInitTransactionException_payment() throws SQLException {

        String transactionId = "";
        Long amount = 100L;
        String currency = "";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(eq(INSERT_TRANSACTION)))
                .thenReturn(preparedStatement);

        when(preparedStatement.executeUpdate())
                .thenThrow(new SQLException("error"));


        assertThrows(RuntimeException.class, () ->
                paymentsDBAccess.insertInitTransaction(
                        transactionId,
                        TransactionType.PAYMENT,
                        amount,
                        currency)
        );

        verify(dataSource, times(1)).getConnection();

        verify(connection, times(1))
                .prepareStatement(eq(INSERT_TRANSACTION));

        verify(preparedStatement, times(1))
                .setString(1, transactionId);

        verify(preparedStatement, times(1))
                .setString(
                        2,
                        PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription()
                );

        verify(preparedStatement, times(1))
                .setString(
                        3,
                        TransactionType.PAYMENT.getDescription()
                );

        verify(preparedStatement, times(1))
                .setLong(4, amount);

        verify(preparedStatement, times(1))
                .setDouble(5, 0.0);

        verify(preparedStatement, times(1))
                .setString(6, currency);

        verify(preparedStatement, times(1))
                .executeUpdate();

    }
}