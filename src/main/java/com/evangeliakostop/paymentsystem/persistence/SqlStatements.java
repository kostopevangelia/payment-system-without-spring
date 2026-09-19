package com.evangeliakostop.paymentsystem.persistence;

public class SqlStatements {

    private SqlStatements() {
        // prevent instantiation
    }

    public static final String INSERT_TRANSACTION =
            "INSERT INTO transactions (transaction_id, status, transaction_type, amount, refunded_amount, currency) VALUES(?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_PAYMENT_INITIATION =
            "UPDATE transactions " +
                    "SET status = ?, " +
                    "transaction_type = ?, " +
                    "amount = ?, currency = ?, " +
                    "sender_account = ?, " +
                    "receiver_account = ? " +
                    "WHERE transaction_id = ?";


}
