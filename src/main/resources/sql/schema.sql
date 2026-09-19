CREATE TABLE IF NOT EXISTS transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  transaction_id   VARCHAR(64)  NOT NULL UNIQUE,
  `status`         VARCHAR(32)  NOT NULL,          -- backticks για σιγουριά
  transaction_type VARCHAR(32)  NOT NULL,
  amount           DECIMAL(12,2) NOT NULL,
  refunded_amount  DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  currency         VARCHAR(3)   NOT NULL,
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
