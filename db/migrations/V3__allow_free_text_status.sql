-- 0) Χρησιμοποιούμε το τρέχον schema
SET @db := DATABASE();

-- 1) Αν υπάρχει FK που ακουμπά το transactions.status, ρίξ' το
SET @fk_name := (
  SELECT kcu.CONSTRAINT_NAME
  FROM information_schema.KEY_COLUMN_USAGE kcu
  WHERE kcu.CONSTRAINT_SCHEMA = @db
    AND kcu.TABLE_NAME = 'transactions'
    AND kcu.COLUMN_NAME = 'status'
    AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
  LIMIT 1
);
SET @sql := IF(@fk_name IS NOT NULL,
               CONCAT('ALTER TABLE `transactions` DROP FOREIGN KEY `', @fk_name, '`'),
               'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) Αν υπάρχει index πάνω στο transactions.status, ρίξ' το
SET @idx_name := (
  SELECT s.INDEX_NAME
  FROM information_schema.STATISTICS s
  WHERE s.TABLE_SCHEMA = @db
    AND s.TABLE_NAME = 'transactions'
    AND s.COLUMN_NAME = 'status'
  ORDER BY (CASE WHEN s.NON_UNIQUE = 0 THEN 0 ELSE 1 END), s.SEQ_IN_INDEX
  LIMIT 1
);
SET @sql := IF(@idx_name IS NOT NULL,
               CONCAT('DROP INDEX `', @idx_name, '` ON `transactions`'),
               'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) Αν ΔΕΝ υπάρχει στήλη status στον transactions, φτιάξ' την
SET @has_status := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'transactions'
    AND COLUMN_NAME = 'status'
);

SET @sql := IF(@has_status = 0,
               'ALTER TABLE `transactions` ADD COLUMN `status` VARCHAR(64) NULL',
               'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) Κάνε τη στήλη status σε VARCHAR(64) NOT NULL (μόνο αν υπάρχει ήδη)
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA=@db AND TABLE_NAME='transactions' AND COLUMN_NAME='status') = 1,
  'ALTER TABLE `transactions` MODIFY COLUMN `status` VARCHAR(64) NOT NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5) Ρίξε τον πίνακα status αν υπάρχει
DROP TABLE IF EXISTS `status`;
