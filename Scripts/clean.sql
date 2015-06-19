DROP DATABASE IF EXISTS  prof_config;
create database prof_config;
DROP DATABASE IF EXISTS  prof_governance;
create database prof_governance;
DROP DATABASE IF EXISTS  prof_userstore;
create database prof_userstore;

use prof_config;
source mysql.sql
use prof_governance;
source mysql.sql
use prof_userstore;
source mysql.sql
