#check mysql installation
systemctl status mysql
systemctl enable mysql
systemctl start mysql

mysqladmin -u root password "<YOUR PASSWORD>"
or
mysqladmin -u root -p'oldpassword' password "<YOUR PASSWORD>"

mysql -u root -p"<YOUR PASSWORD>"

CREATE DATABASE enisoinfodb DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE USER 'enisoinfouser'@'localhost' IDENTIFIED BY "<YOUR PASSWORD>";
GRANT ALL PRIVILEGES ON enisoinfodb . * TO 'enisoinfouser'@'localhost';
FLUSH PRIVILEGES;


CREATE DATABASE pmpayrolldb DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE USER 'pmpayrolluser'@'localhost' IDENTIFIED BY 'pmpayroll77';
GRANT ALL PRIVILEGES ON pmpayrolldb . * TO 'pmpayrolluser'@'localhost';
FLUSH PRIVILEGES;


mysqldump -u MY_USERNAME -pMY_PASSWORD -databases MY_DATABASE_NAME | gzip > MY_DATABASE_NAME-`date +%Y%m%d`.sql.zip



SET FOREIGN_KEY_CHECKS = 0;
SET @tables = NULL;
SELECT GROUP_CONCAT(table_schema, '.', table_name) INTO @tables
  FROM information_schema.tables
  WHERE table_schema = 'enisoinfodb'; -- specify DB name here.

SET @tables = CONCAT('DROP TABLE ', @tables);
PREPARE stmt FROM @tables;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
SET FOREIGN_KEY_CHECKS = 1;
