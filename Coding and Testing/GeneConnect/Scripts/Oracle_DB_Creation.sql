/*L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L*/

CREATE TABLESPACE "{TABLESPACE_NAME}" LOGGING DATAFILE 
'{TABLESPACE_PATH}' SIZE 100M EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO;

CREATE USER {USERNAME} PROFILE "DEFAULT" IDENTIFIED BY {PASSWORD} DEFAULT TABLESPACE "{TABLESPACE_NAME}" TEMPORARY TABLESPACE "TEMP" QUOTA UNLIMITED ON "{TABLESPACE_NAME}";

GRANT CONNECT, RESOURCE TO {USERNAME};

GRANT DBA TO {USERNAME};

ALTER USER {USERNAME} DEFAULT ROLE ALL;