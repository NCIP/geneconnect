/*L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L*/

CREATE TABLESPACE "REFDATA" LOGGING 
DATAFILE '$ORACLE_HOME/CHIPDB/REFDATA01.ora' SIZE 16384M 
EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO 

CREATE TABLESPACE "POSTDATA" LOGGING 
DATAFILE '$ORACLE_HOME/CHIPDB/POSTDATA01.ora' SIZE 16384M 
EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO 

CREATE TABLESPACE "PUBMEDDATA" LOGGING 
DATAFILE '$ORACLE_HOME/CHIPDB/PUBMED_DATA01.ora' SIZE 8192M 
EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO

CREATE TABLESPACE "CACOREDATA" LOGGING 
DATAFILE '$ORACLE_HOME/CHIPDB/CACORE_DATA01.ora' SIZE 16384M 
EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO

CREATE USER cafeuser PROFILE DEFAULT IDENTIFIED BY cafeuser DEFAULT TABLESPACE REFDATA TEMPORARY TABLESPACE TEMP ACCOUNT UNLOCK;

GRANT CREATE ANY INDEX TO cafeuser;
GRANT CREATE ANY TABLE TO cafeuser;
GRANT UNLIMITED TABLESPACE TO cafeuser;
GRANT RESOURCE TO cafeuser;
GRANT DBA TO cafeuser;
GRANT CONNECT TO cafeuser;

