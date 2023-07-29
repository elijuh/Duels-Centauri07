CREATE TABLE IF NOT EXISTS `userdata` (
  `uuid` BINARY(16) PRIMARY KEY,
  `name` VARCHAR(16),
  `wins` INT,
  `losses` INT,
  `kills` INT,
  `deaths` INT,
  `streak` INT
);