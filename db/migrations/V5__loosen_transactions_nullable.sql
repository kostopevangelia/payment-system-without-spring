-- Χαλαρώνουμε τα πεδία ώστε να μην απαιτούνται στο αρχικό insert του κώδικα
ALTER TABLE `transactions`
  MODIFY `payment_id` BIGINT NULL,
  MODIFY `provider`   VARCHAR(32) NULL,
  MODIFY `result`     VARCHAR(32) NULL;