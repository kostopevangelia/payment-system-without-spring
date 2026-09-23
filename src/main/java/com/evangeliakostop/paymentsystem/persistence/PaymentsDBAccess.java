package com.evangeliakostop.paymentsystem.persistence;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.TransactionType;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
public class PaymentsDBAccess {

    private final HikariDataSource dataSource;

    public PaymentsDBAccess(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertInitTransaction(String transactionId, TransactionType transactionType, Long amount, String currency) {

        log.info("Method insertInitTransaction entered for transactionId: {}", transactionId);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SqlStatements.INSERT_TRANSACTION)) {

            statement.setString(1, transactionId);
            statement.setString(2, PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription());
            statement.setString(3, transactionType.getDescription());
            statement.setLong(4, amount);
            statement.setDouble(5, transactionType.equals(TransactionType.REFUND) ? amount : 0.0);
            statement.setString(6, currency);

            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Method insertInitTransaction - Exception: {}", e.getMessage());
            throw new CustomException(
                    "PaymentsDBAccess - error",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR);
        }
        log.info("Method insertInitTransaction exiting successfully for transactionId: {}", transactionId);
    }
}
