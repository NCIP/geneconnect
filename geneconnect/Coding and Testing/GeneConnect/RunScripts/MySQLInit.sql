create database <Database Name>;

GRANT ALL PRIVILEGES ON *.* TO '<Username>'@'%' IDENTIFIED BY '<password>' WITH GRANT OPTION;

set global interactive_timeout=999999999;
set global wait_timeout=999999999;
