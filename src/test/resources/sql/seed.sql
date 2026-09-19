INSERT INTO transactions (transaction_id, `status`, transaction_type, amount, refunded_amount, currency)
VALUES
  ('tx-demo-1', 'PENDING', 'CARD', 42.50, 0.00, 'EUR'),
  ('tx-demo-2', 'APPROVED','CARD', 10.00, 0.00, 'EUR')
ON DUPLICATE KEY UPDATE transaction_id = VALUES(transaction_id);
