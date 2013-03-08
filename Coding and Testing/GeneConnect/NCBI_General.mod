<!-- ============================================ -->
<!-- This section mapped from ASN.1 module NCBI-General -->
 
<!-- Elements used by other modules:
          Date ,
          Person-id ,
          Object-id ,
          Dbtag ,
          Int-fuzz ,
          User-object -->
 
 
<!-- ============================================ -->
<!-- Definition of Date -->
 
 
<!--
$Revision: 1.1 $
**********************************************************************
 
  NCBI General Data elements
  by James Ostell, 1990
  Version 3.0 - June 1994
 
**********************************************************************
 StringStore is really a VisibleString.  It is used to define very
   long strings which may need to be stored by the receiving program
   in special structures, such as a ByteStore, but it's just a hint.
   AsnTool stores StringStores in ByteStore structures.
 OCTET STRINGs are also stored in ByteStores by AsnTool
 
 typedef struct bsunit {             /* for building multiline strings */
 Nlm_Handle str;            /* the string piece */
 Nlm_Int2 len_avail,
 len;
 struct bsunit PNTR next; }       /* the next one */
 Nlm_BSUnit, PNTR Nlm_BSUnitPtr;
 
 typedef struct bytestore {
 Nlm_Int4 seekptr,       /* current position */
 totlen,             /* total stored data length in bytes */
 chain_offset;       /* offset in ByteStore of first byte in curchain */
 Nlm_BSUnitPtr chain,       /* chain of elements */
 curchain;           /* the BSUnit containing seekptr */
 } Nlm_ByteStore, PNTR Nlm_ByteStorePtr;
 
 AsnTool incorporates this as a primitive type, so the definition
   is here just for completeness
 
  StringStore ::= [APPLICATION 1] IMPLICIT OCTET STRING
 
 BigInt is really an INTEGER. It is used to warn the receiving code to expect
    a value bigger than Int4 (actually Int8). It will be stored in DataVal.bigintvalue
 
    Like StringStore, AsnTool incorporates it as a primitive. The definition would be:
   BigInt ::= [APPLICATION 2] IMPLICIT INTEGER
 
 Date is used to replace the (overly complex) UTCTtime, GeneralizedTime
  of ASN.1
  It stores only a date
 
 -->
<!ELEMENT Date ( 
               Date_str |
               Date_std )>
 
 
<!-- 
 for those unparsed dates
 -->
<!ELEMENT Date_str ( #PCDATA )>
 
<!-- 
 use this if you can
 -->
<!ELEMENT Date_std ( Date-std )>
 
 
 
<!-- Definition of Date-std -->
 
 
<!-- 
 NOTE: this is NOT a unix tm struct
 -->
<!ELEMENT Date-std ( 
               Date-std_year ,
               Date-std_month? ,
               Date-std_day? ,
               Date-std_season? ,
               Date-std_hour? ,
               Date-std_minute? ,
               Date-std_second? )>
 
 
<!-- 
 full year (including 1900)
 -->
<!ELEMENT Date-std_year ( %INTEGER; )>
 
<!-- 
 month (1-12)
 -->
<!ELEMENT Date-std_month ( %INTEGER; )>
 
<!-- 
 day of month (1-31)
 -->
<!ELEMENT Date-std_day ( %INTEGER; )>
 
<!-- 
 for "spring", "may-june", etc
 -->
<!ELEMENT Date-std_season ( #PCDATA )>
 
<!-- 
 hour of day (0-23)
 -->
<!ELEMENT Date-std_hour ( %INTEGER; )>
 
<!-- 
 minute of hour (0-59)
 -->
<!ELEMENT Date-std_minute ( %INTEGER; )>
 
<!-- 
 second of minute (0-59)
 -->
<!ELEMENT Date-std_second ( %INTEGER; )>
 
 
<!-- Definition of Dbtag -->
 
 
<!-- 
 Dbtag is generalized for tagging
 eg. { "Social Security", str "023-79-8841" }
 or  { "member", id 8882224 }
 -->
<!ELEMENT Dbtag ( 
               Dbtag_db ,
               Dbtag_tag )>
 
 
<!-- 
 name of database or system
 -->
<!ELEMENT Dbtag_db ( #PCDATA )>
 
<!-- 
 appropriate tag
 -->
<!ELEMENT Dbtag_tag ( Object-id )>
 
 
 
<!-- Definition of Object-id -->
 
 
<!-- 
 Object-id can tag or name anything
 
 -->
<!ELEMENT Object-id ( 
               Object-id_id |
               Object-id_str )>
 
<!ELEMENT Object-id_id ( %INTEGER; )>
<!ELEMENT Object-id_str ( #PCDATA )>
 
 
<!-- Definition of Person-id -->
 
 
<!-- 
 Person-id is to define a std element for people
 
 -->
<!ELEMENT Person-id ( 
               Person-id_dbtag |
               Person-id_name |
               Person-id_ml |
               Person-id_str )>
 
 
<!-- 
 any defined database tag
 -->
<!ELEMENT Person-id_dbtag ( Dbtag )>
 
<!-- 
 structured name
 -->
<!ELEMENT Person-id_name ( Name-std )>
 
<!-- 
 MEDLINE name (semi-structured)
 -->
<!ELEMENT Person-id_ml ( #PCDATA )>
 
<!-- 
    eg. "Jones RM"
 unstructured name
 -->
<!ELEMENT Person-id_str ( #PCDATA )>
 
 
 
 
<!-- Definition of Name-std -->
 
 
<!-- 
 Structured names
 -->
<!ELEMENT Name-std ( 
               Name-std_last ,
               Name-std_first? ,
               Name-std_middle? ,
               Name-std_full? ,
               Name-std_initials? ,
               Name-std_suffix? ,
               Name-std_title? )>
 
<!ELEMENT Name-std_last ( #PCDATA )>
<!ELEMENT Name-std_first ( #PCDATA )>
<!ELEMENT Name-std_middle ( #PCDATA )>
 
<!-- 
 full name eg. "J. John Smith, Esq"
 -->
<!ELEMENT Name-std_full ( #PCDATA )>
 
<!-- 
 first + middle initials
 -->
<!ELEMENT Name-std_initials ( #PCDATA )>
 
<!-- 
 Jr, Sr, III
 -->
<!ELEMENT Name-std_suffix ( #PCDATA )>
 
<!-- 
 Dr., Sister, etc
 -->
<!ELEMENT Name-std_title ( #PCDATA )>
 
 
<!-- Definition of Int-fuzz -->
 
 
<!-- 
**** Int-fuzz **********************************************
*
*   uncertainties in integer values
 -->
<!ELEMENT Int-fuzz ( 
               Int-fuzz_p-m |
               Int-fuzz_range |
               Int-fuzz_pct |
               Int-fuzz_lim |
               Int-fuzz_alt )>
 
 
<!-- 
 plus or minus fixed amount
 -->
<!ELEMENT Int-fuzz_p-m ( %INTEGER; )>
 
<!-- 
 max to min
 -->
<!ELEMENT Int-fuzz_range ( 
               Int-fuzz_range_max ,
               Int-fuzz_range_min )>
 
<!-- 
 % plus or minus (x10) 0-1000
 -->
<!ELEMENT Int-fuzz_pct ( %INTEGER; )>
 
<!-- 
 some limit value
 something else
 -->
<!ELEMENT Int-fuzz_lim %ENUM; >
<!ATTLIST Int-fuzz_lim value ( 
               unk |
               gt |
               lt |
               tr |
               tl |
               circle |
               other )  #REQUIRED >
 
<!-- 
 set of alternatives for the integer
 -->
<!ELEMENT Int-fuzz_alt ( Int-fuzz_alt_E+ )>
 
<!ELEMENT Int-fuzz_range_max ( %INTEGER; )>
<!ELEMENT Int-fuzz_range_min ( %INTEGER; )>
 
<!ELEMENT Int-fuzz_alt_E ( %INTEGER; )>
 
 
<!-- Definition of User-object -->
 
 
<!-- 
**** User-object **********************************************
*
*   a general object for a user defined structured data item
*    used by Seq-feat and Seq-descr
 -->
<!ELEMENT User-object ( 
               User-object_class? ,
               User-object_type ,
               User-object_data )>
 
 
<!-- 
 endeavor which designed this object
 -->
<!ELEMENT User-object_class ( #PCDATA )>
 
<!-- 
 type of object within class
 -->
<!ELEMENT User-object_type ( Object-id )>
 
<!-- 
 the object itself
 -->
<!ELEMENT User-object_data ( User-field+ )>
 
 
 
 
<!-- Definition of User-field -->
 
<!ELEMENT User-field ( 
               User-field_label ,
               User-field_num? ,
               User-field_data )>
 
 
<!-- 
 field label
 -->
<!ELEMENT User-field_label ( Object-id )>
 
<!-- 
 required for strs, ints, reals, oss
 -->
<!ELEMENT User-field_num ( %INTEGER; )>
 
<!-- 
 field contents
 -->
<!ELEMENT User-field_data ( 
               User-field_data_str |
               User-field_data_int |
               User-field_data_real |
               User-field_data_bool |
               User-field_data_os |
               User-field_data_object |
               User-field_data_strs |
               User-field_data_ints |
               User-field_data_reals |
               User-field_data_oss |
               User-field_data_fields |
               User-field_data_objects )>
 
 
<!ELEMENT User-field_data_str ( #PCDATA )>
<!ELEMENT User-field_data_int ( %INTEGER; )>
<!ELEMENT User-field_data_real ( %REAL; )>
<!ELEMENT User-field_data_bool %BOOLEAN; >
<!ATTLIST User-field_data_bool value ( true | false )  #REQUIRED >
<!ELEMENT User-field_data_os ( %OCTETS; )>
 
<!-- 
 for using other definitions
 -->
<!ELEMENT User-field_data_object ( User-object )>
<!ELEMENT User-field_data_strs ( User-field_data_strs_E+ )>
<!ELEMENT User-field_data_ints ( User-field_data_ints_E+ )>
<!ELEMENT User-field_data_reals ( User-field_data_reals_E+ )>
<!ELEMENT User-field_data_oss ( User-field_data_oss_E+ )>
<!ELEMENT User-field_data_fields ( User-field+ )>
<!ELEMENT User-field_data_objects ( User-object+ )>
 
 
<!ELEMENT User-field_data_strs_E ( #PCDATA )>
 
<!ELEMENT User-field_data_ints_E ( %INTEGER; )>
 
<!ELEMENT User-field_data_reals_E ( %REAL; )>
 
<!ELEMENT User-field_data_oss_E ( %OCTETS; )>
 
 
 
 
 
 
