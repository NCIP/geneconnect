use strict;
use warnings;

#open (READ,"parameter.txt")|| die "cannot open file!";
use constant DIRECT => '1';
use constant INFERRED => '2';
use constant DELIMITER => '###';
use FetchParams;
use Config::Simple;

# GeneConnect server (Java) sends a properties file
# Read this properties file and store it in a hash
# first arguement is a -f; collect it and ignore it for now

my $optionalArgument = shift;
my $configFile = shift;

# read contents of config file into a hash by using module
my %configParams = %{FetchParams::getParams($configFile)};

my $basedir = $configParams{BASEDIR};

#The name of file to be parsed is obtained from the hash.
my $inputFile = ($configParams{INPUTFILE});
my @arr_files = @$inputFile;

# Open the file into which to write all 'output file names' created by
# this parser.
my $outputFile = $configParams{OUTPUTFILE};

#To get all the parameters like mysql path ,root,password,organism name ,release name from properties file.
my $propertiesfile ="ens_properties.txt";
my %properties = %{FetchParams::getParams($propertiesfile)};

my $localfolder = $configParams{BASEDIR};

#The local folder has all the mysql dump zip files which are unzipped using gunzip command
my @files_unzipped;

foreach my $file(@arr_files)
{
system("gunzip $localfolder/$file");
	
	my @unzip_file = split (/\.gz/,$file);
	
	push(@files_unzipped,$unzip_file[0]);	
}

#Make a file in which mysql commands are written 
open(fh_createdb, ">$localfolder/createdb.sql");
print fh_createdb "create database " . $properties{organism} . "_core_" . $properties{ensembl_release} . "_" . $properties{genome_release} . "\n";
close(fh_createdb);

#write user permissions to database in script
#make a '.sh ' file which will execute the above file to create a database in mysql

open(OUTFILE1, ">$localfolder/load.sh");
print OUTFILE1 $properties{path_to_mysql} . "/mysql -u " . $properties{mysqluser} . " -h " . $properties{hostname} ." --password=" . $properties{mysqlrootpasswd}  . " < " . $localfolder . "/createdb.sql\n";
close(OUTFILE1);

system ("chmod u+x $localfolder/load.sh");
#Executing the 'load.sh' file to create database.  
system ("bash $localfolder/load.sh");

#Similarly making a 'loaddb.sh' file to populate the database 
open(fh_loaddb, ">$localfolder/loaddb.sh");
print fh_loaddb $properties{path_to_mysql} . "/mysql -u " . $properties{mysqluser} . " -h " . $properties{hostname} . " --password=" . $properties{mysqlrootpasswd} . " " . $properties{organism} . "_core_" . $properties{ensembl_release} .
	"_" . $properties{genome_release} . " < " . $localfolder . "/" . $properties{organism} . "_core_" . $properties{ensembl_release} .
	"_" . $properties{genome_release} . ".sql\n";
print fh_loaddb $properties{path_to_mysql} . "/mysqlimport -u " . $properties{mysqluser} . " -h " . $properties{hostname} ." --password=" . $properties{mysqlrootpasswd} ." " . $properties{organism} . "_core_" . $properties{ensembl_release} .
	"_" . $properties{genome_release} . " -L " . $localfolder . "/*.txt.table\n"; 


#deleting the files 
foreach my $file1(@files_unzipped)
{
print fh_loaddb "rm " . $localfolder . '\/' .$file1 . "\n";
}


close(fh_loaddb);

system ("chmod u+x $localfolder/loaddb.sh");

#Executing the 'loaddb.sh' file to populate database. 
system ("bash $localfolder/loaddb.sh");


use lib './ensembl_api/modules';
use Bio::EnsEMBL::DBSQL::DBAdaptor;


my @chromosomes = ("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12","13", "14", "15", "16", "17", "18", "19", "20", "21", "X", "Y");


#mapping the file handlers with the link files

my $ensemblgene_ensembltranscript_Hs=">$basedir/ENSEMBLGENE_ENSEMBLTRANS.txt";
my $ensembltranscript_ensemblpeptide_Hs=">$basedir/ENSEMBLTRANS_ENSEMBLPROT.txt";
my $ensemblgene_entrezgene_Hs=">$basedir/ENSEMBLGENE_ENTREZGENE.txt";

my $entrezgene_ensemblgene_Hs=">$basedir/ENTREZGENE_ENSEMBLGENE.txt";
my $ensemblgene_unigene_Hs=">$basedir/ENSEMBLGENE_UNIGENE.txt";

my $unigene_ensemblgene_Hs=">$basedir/UNIGENE_ENSEMBLGENE.txt";
my $ensembltranscript_refseqmrna_Hs=">$basedir/ENSEMBLTRANS_REFSEQMRNA.txt";
my $ensemblpeptide_refseqprotein_Hs=">$basedir/ENSEMBLPROT_REFSEQPROTEIN.txt";
my $ensemblpeptide_uniprotKB_Hs=">$basedir/ENSEMBLPROT_UNIPROT.txt";

my $refseqmrna_ensembltranscript_Hs=">$basedir/REFSEQMRNA_ENSEMBLTRANS.txt";
my $refseqprotein_ensemblpeptide_Hs=">$basedir/REFSEQPROTEIN_ENSEMBLPROT.txt";
my $uniprotKB_ensemblpeptide_Hs=">$basedir/UNIPROT_ENSEMBLPROT.txt";

#open all the output files to create link files
open(ensg_enstr,$ensemblgene_ensembltranscript_Hs);
open(enstr_enspep,$ensembltranscript_ensemblpeptide_Hs);
open(ensg_entg,$ensemblgene_entrezgene_Hs);
open(ensg_ug,$ensemblgene_unigene_Hs);

open(entg_ensg,$entrezgene_ensemblgene_Hs);
open(ug_ensg,$unigene_ensemblgene_Hs);

open(enstr_rsmrna,$ensembltranscript_refseqmrna_Hs);
open(enspep_rsprot,$ensemblpeptide_refseqprotein_Hs);
open(enspep_uniprot,$ensemblpeptide_uniprotKB_Hs);

open(rsmrna_enstr,$refseqmrna_ensembltranscript_Hs);
open(rsprot_enspep,$refseqprotein_ensemblpeptide_Hs);
open(uniprot_enspep,$uniprotKB_ensemblpeptide_Hs);

open(WRITE,">$basedir/$outputFile");

#writing the names of all output files in the output.txt
print WRITE "ENSEMBLGENE_ENSEMBLTRANS.txt\nENSEMBLTRANS_ENSEMBLPROT.txt\nENSEMBLGENE_ENTREZGENE.txt\nENTREZGENE_ENSEMBLGENE.txt\nENSEMBLGENE_UNIGENE.txt\nUNIGENE_ENSEMBLGENE.txt\nENSEMBLTRANS_REFSEQMRNA.txt\nENSEMBLPROT_REFSEQPROTEIN.txt\nENSEMBLPROT_UNIPROT.txt\nREFSEQMRNA_ENSEMBLTRANS.txt\nREFSEQPROTEIN_ENSEMBLPRO.txt\nUNIPROT_ENSEMBLPROT.txt\n";

#for loading base tables
print ensg_enstr "LOAD DATA INFILE * APPEND INTO TABLE ENSEMBLGENE_ENSEMBLTRANS FIELDS TERMINATED BY '###' (ESN_ENSBLGENEID, ESN_ENSBLTRANSCRIPTID, ESN_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print enstr_enspep "LOAD DATA INFILE * APPEND INTO TABLE ENSEMBLTRANS_ENSEMBLPROT FIELDS TERMINATED BY '###' (ESP_ENSBLTRANSCRIPTID, ESP_ENSBLPROTEINID, ESP_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print ensg_entg "LOAD DATA INFILE * APPEND INTO TABLE ENSEMBLGENE_ENTREZGENE FIELDS TERMINATED BY '###' (ESE_ENSEMBLGENEID, ESE_GENEID, ESE_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print ensg_ug "LOAD DATA INFILE * APPEND INTO TABLE ENSEMBLGENE_UNIGENE FIELDS TERMINATED BY '###' (EBU_ENSEMBLGENEID, EBU_UNIGENEID, EBU_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print enstr_rsmrna "LOAD DATA INFILE * APPEND INTO TABLE ENSEMBLTRANS_REFSEQMRNA FIELDS TERMINATED BY '###' (ESR_ENSBLTRANSCRIPTID, ESR_REFSEQMRNAID, ESR_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print enspep_rsprot "LOAD DATA INFILE * APPEND INTO TABLE ENSEMBLPROT_REFSEQPROTEIN FIELDS TERMINATED BY '###' (EPR_ENSBLPROTEINID, EPR_REFSEQPROTEINID, EPR_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print enspep_uniprot "LOAD DATA INFILE * APPEND INTO TABLE ENSEMBLPROT_UNIPROT FIELDS TERMINATED BY '###' (ESU_ENSBLPROTEINID, ESU_UNIPROTID, ESU_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";

print entg_ensg "LOAD DATA INFILE * APPEND INTO TABLE ENTREZGENE_ENSEMBLGENE FIELDS TERMINATED BY '###' (EEG_GENEID, EEG_ENSEMBLGENEID, EEG_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print ug_ensg "LOAD DATA INFILE * APPEND INTO TABLE UNIGENE_ENSEMBLGENE FIELDS TERMINATED BY '###' (UEG_UNIGENEID, UEG_ENSEMBLGENEID, UEG_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";

print rsmrna_enstr "LOAD DATA INFILE * APPEND INTO TABLE REFSEQMRNA_ENSEMBLTRANS FIELDS TERMINATED BY '###' (RET_REFSEQMRNAID, RET_ENSBLTRANSCRIPTID, RET_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print rsprot_enspep "LOAD DATA INFILE * APPEND INTO TABLE REFSEQPROTEIN_ENSEMBLPROT FIELDS TERMINATED BY '###' (REP_REFSEQPROTEINID, REP_ENSBLPROTEINID, REP_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print uniprot_enspep "LOAD DATA INFILE * APPEND INTO TABLE UNIPROT_ENSEMBLPROT FIELDS TERMINATED BY '###' (UEP_UNIPROTKBID, UEP_ENSBLPROTEINID, UEP_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";

# define a helper subroutine to print DBEntries
sub print_DBEntries
{
 my $thisgene = shift;
 my $thistrans = shift;
 my $thistransl = shift;
 my $db_entries = shift;
 my $mykey;
 
 #checking for each database cross reference
 foreach my $dbe (@$db_entries)
 {
 #Searching for Refseqmrna
  if ($dbe->dbname() eq "RefSeq_dna" || $dbe->dbname() eq "RefSeq_dna_predicted")
  {
   
    print enstr_rsmrna $thistrans->stable_id().DELIMITER.$dbe->primary_id().DELIMITER.DIRECT."\n";
    print rsmrna_enstr $dbe->primary_id().DELIMITER.$thistrans->stable_id().DELIMITER.INFERRED."\n";
  }
  
  #Searching for Refseqprotein
  elsif ($dbe->dbname() eq "RefSeq_peptide" || $dbe->dbname() eq "RefSeq_peptide_predicted")
  {
    
   if($thistransl ne '')
   {
    print enspep_rsprot $thistransl->stable_id().DELIMITER.$dbe->primary_id().DELIMITER.DIRECT."\n";
    print rsprot_enspep $dbe->primary_id().DELIMITER.$thistransl->stable_id().DELIMITER.INFERRED."\n";
    }
    else
    {
    
    print "*$thistransl\n";
    }
   
  }
   #Searching for Uniprot/SWISSPROT
  elsif ($dbe->dbname() eq "Uniprot/SWISSPROT" || $dbe->dbname() eq "Uniprot/SPTREMBL")
  {
  
    print enspep_uniprot $thistransl->stable_id().DELIMITER.$dbe->primary_id().DELIMITER.DIRECT."\n";
    print uniprot_enspep $dbe->primary_id().DELIMITER.$thistransl->stable_id().DELIMITER.INFERRED."\n";
  }
 }
}

# define a helper subroutine to print DBLinks
sub print_DBLinks
{
 my $thisgene = shift;
 my $thistrans = shift;
 my $thistransl = shift;
 my $db_entries = shift;
 my $mykey;
 foreach my $dbe (@$db_entries)
 {
 #Searching for UniGene
  if ($dbe->dbname() eq "UniGene")
  {
  
    print ensg_ug $thisgene->stable_id().DELIMITER.$dbe->primary_id().DELIMITER.DIRECT."\n";
    print ug_ensg $dbe->primary_id().DELIMITER.$thisgene->stable_id().DELIMITER.INFERRED."\n";
  
  }
  #Searching for EntrezGene
  elsif ($dbe->dbname() eq "EntrezGene")
  {
  
    print ensg_entg $thisgene->stable_id().DELIMITER.$dbe->primary_id().DELIMITER.DIRECT."\n";
    print entg_ensg $dbe->primary_id().DELIMITER.$thisgene->stable_id().DELIMITER.INFERRED."\n";	
   
  }
 }
}

#connecting to the mysql ensembl database
my $db = new Bio::EnsEMBL::DBSQL::DBAdaptor(
    -host   => $properties{hostname},
    -user   => $properties{mysqluser},
    -dbname => $properties{dbname},
    -pass   => $properties{mysqlrootpasswd});

print "Getting slice adaptor...\n";
my $slice_adaptor = $db->get_SliceAdaptor();


print "@chromosomes\n";

#for each chromosome take the ensembl genes in that chromosome and other informations
foreach my $thischr (@chromosomes)
{
 print "Getting slice of chromosome $thischr...\n";
 my $slice = $slice_adaptor->fetch_by_region('chromosome', $thischr); 
 print "Getting all genes...\n";
 my @genes = @{$slice->get_all_Genes};
 my $count=0;
 #for each gene of that chromosome
 foreach my $gene (@genes)
 {
  $count++;
  print "Writing gene $count:  ", $gene->stable_id(), "\n";
  #variables for storing transcript info
  my $trans;
  my $transl;
  
  #calling the print_DBLinks subroutine 
  print_DBLinks($gene,$trans,$transl,$gene->get_all_DBLinks());
 
  #for each transcript of each gene of that chromosome
  foreach $trans(@{$gene->get_all_Transcripts()}){
  
   #getting ensembl gene id and
   #getting ensembl transcript id
   print ensg_enstr $gene->stable_id().DELIMITER.$trans->stable_id().DELIMITER.DIRECT."\n";
   
   

   print_DBEntries($gene,$trans,$transl,$trans->get_all_DBEntries());
   
  
   # watch out: pseudogenes have no translation
   if($trans->translation()) {
     $transl = $trans->translation();
    #getting ensembl protein id
     
     print enstr_enspep $trans->stable_id().DELIMITER.$transl->stable_id().DELIMITER.DIRECT."\n";
     print_DBEntries($gene,$trans,$transl,$transl->get_all_DBEntries());
   }
  }
 }
}

close(ensg_enstr);
close(enstr_enspep);
close(ensg_entg);
close(entg_ensg);

close(ensg_ug);
close(ug_ensg);
close(enstr_rsmrna);
close(enspep_rsprot);
close(enspep_uniprot);

close(rsmrna_enstr);
close(rsprot_enspep);
close(uniprot_enspep);


open(fh_dropdb, ">dropdb.sql");
print fh_dropdb "drop database ". $properties{dbname} . "\n";
close(fh_dropdb);
open(OUTFILE2, ">drop.sh");
print OUTFILE2 $properties{path_to_mysql} . "/mysql -u " . $properties{mysqluser} . " -h " . $properties{hostname} .  " < " . "dropdb.sql\n";
close(OUTFILE2);
system ("chmod u+x drop.sh");
system ("bash drop.sh");

