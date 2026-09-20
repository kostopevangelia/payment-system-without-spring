ALTER TABLE `transactions`
  ADD COLUMN `transaction_id`    varchar(128) NOT NULL,
  ADD COLUMN `transaction_type`  varchar(32)  NOT NULL,
  ADD COLUMN `amount`            decimal(19,2) NOT NULL,
  ADD COLUMN `refunded_amount`   decimal(19,2) NOT NULL DEFAULT 0,
  ADD COLUMN `currency`          varchar(8)   NOT NULL;