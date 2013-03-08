/*L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L*/

create database <Database Name>;

GRANT ALL PRIVILEGES ON *.* TO '<Username>'@'%' IDENTIFIED BY '<password>' WITH GRANT OPTION;

set global interactive_timeout=999999999;
set global wait_timeout=999999999;
