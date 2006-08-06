#! /usr/bin/perl

# modules for parser
use Bio::SeqIO;
use Bio::SeqIO::genbank;
use Bio::PrimarySeq;
# module for database connection


# connecting to the oracle database
#$dbh=DBI->connect("dbi:Oracle:ps2288","CHIP_ADMIN","admin4db") or die "Failure!\n";

print "\n\nBEGIN\n";

# Read arguments from config file
$input_file="input.txt";
$basedir="D:/Washu_CVS/GeneConnect/GeneConnectServer/";
$output_file=$basedir."input.txt.op";

#Prepare output file with parsed data file names
open(WRITEOUTPUT,">>$output_file");
print WRITEOUTPUT "ensemblprotein_uniprot.",$input_file,"\n";
print WRITEOUTPUT "ensemblgene_entrezgene.",$input_file,"\n";
print WRITEOUTPUT "ensemblgene_unigene.",$input_file,"\n";
print WRITEOUTPUT "ensembltranscript_refseqmrna.",$input_file,"\n";
print WRITEOUTPUT "ensembltranscript_ensemblprotein.",$input_file,"\n";
print WRITEOUTPUT "ensemblprotein_refseqprotein.",$input_file,"\n";
print WRITEOUTPUT "ensemblgene_ensembltranscript.",$input_file,"\n";
print WRITEOUTPUT "refseqmrna_ensembltranscript.",$input_file,"\n";
print WRITEOUTPUT "refseqprotein_ensemblprotein.",$input_file,"\n";
print WRITEOUTPUT "uniprot_ensemblprotein.",$input_file,"\n";
close(WRITEOUTPUT);

#open(WRITE,">>output.txt");



$ensemblprotein_uniprot=$basedir."ensemblprotein_uniprot.".$input_file;
$ensemblgene_entrezgene=$basedir."ensemblgene_entrezgene.".$input_file;
$ensemblgene_unigene=$basedir."ensemblgene_unigene.".$input_file;
$ensembltranscript_refseqmrna=$basedir."ensembltranscript_refseqmrna.".$input_file;
$ensembltranscript_ensemblprotein=$basedir."ensembltranscript_ensemblprotein.".$input_file;
$ensemblprotein_refseqprotein=$basedir."ensemblprotein_refseqprotein.".$input_file;
$ensemblgene_ensembltranscript=$basedir."ensemblgene_ensembltranscript.".$input_file;
$refseqmrna_ensembltranscript=$basedir."refseqmrna_ensembltranscript.".$input_file;
$refseqprotein_ensemblprotein=$basedir."refseqprotein_ensemblprotein.".$input_file;
$uniprot_ensemblprotein=$basedir."uniprot_ensemblprotein.".$input_file;


# system(date);
#$way= "input.txt";
#$way = "Homo_sapiens.2000.dat";
#$way = shift;
$way = $basedir . $input_file;

$seqin = Bio::SeqIO->new( '-format' => 'Genbank' , -file => $way);

open(WRITE1,">>$ensemblprotein_uniprot");
open(WRITE2,">>$ensemblgene_entrezgene");
open(WRITE4,">>$ensemblgene_unigene");
open(WRITE5,">>$ensembltranscript_refseqmrna");
open(WRITE6,">>$ensembltranscript_ensemblprotein");
open(WRITE7,">>$ensemblprotein_refseqprotein");
open(WRITE8,">>$ensemblgene_ensembltranscript");
open(WRITE9,">>$refseqprotein_ensemblprotein");
open(WRITE10,">>$refseqmrna_ensembltranscript");
open(WRITE11,">>$uniprot_ensemblprotein");

print WRITE1 "LOAD DATA INFILE * APPEND INTO TABLE ensemblprotein_uniprot FIELDS TERMINATED BY '###' (ensemblprotein_id,uniprot_id)","\n","BEGINDATA","\n";
print WRITE2 "LOAD DATA INFILE * APPEND INTO TABLE ensemblgene_entrezgene FIELDS TERMINATED BY '###' (ensemblgene_id,entrezgene_id)" ,"\n","BEGINDATA","\n";
print WRITE4 "LOAD DATA INFILE * APPEND INTO TABLE ensemblgene_unigene FIELDS TERMINATED BY '###' (ensemblgene_id,unigene_id)" ,"\n","BEGINDATA","\n";
print WRITE5 "LOAD DATA INFILE * APPEND INTO TABLE ensembltranscript_refseqmrna FIELDS TERMINATED BY '###' (ensembltranscript_id,refseqmrna_id)" ,"\n","BEGINDATA","\n";
print WRITE6 "LOAD DATA INFILE * APPEND INTO TABLE ensembltranscript_ensemblprotein FIELDS TERMINATED BY '###' (ensembltranscript_id,ensemblprotein_id)","\n","BEGINDATA","\n";
print WRITE7 "LOAD DATA INFILE * APPEND INTO TABLE ensemblprotein_refseqprotein FIELDS TERMINATED BY '###' (ensemblprotein_id,refseqprotein_id)","\n","BEGINDATA","\n";
print WRITE8 "LOAD DATA INFILE * APPEND INTO TABLE ensemblgene_ensembltranscript FIELDS TERMINATED BY '###' (ensemblgene_id,ensembltranscript_id)","\n","BEGINDATA","\n";
print WRITE9 "LOAD DATA INFILE * APPEND INTO TABLE refseqprotein_ensemblprotein FIELDS TERMINATED BY '###' (refseqprotein_id,ensemblprotein_id)","\n","BEGINDATA","\n";
print WRITE10 "LOAD DATA INFILE * APPEND INTO TABLE refseqmrna_ensembltranscript FIELDS TERMINATED BY '###' (refseqmrna_id,ensembltranscript_id)","\n","BEGINDATA","\n";
print WRITE11 "LOAD DATA INFILE * APPEND INTO TABLE uniprot_ensemblprotein FIELDS TERMINATED BY '###' (uniprot_id,ensemblprotein_id)","\n","BEGINDATA","\n";



#open(WRITE1,">>ensemblprotein_uniprot.txt");
#open(WRITE2,">>ensemblgene_entrezgene.txt");
#open(WRITE4,">>ensemblgene_unigene.txt");
#open(WRITE5,">>ensembltranscript_refseqmrna.txt");
#open(WRITE6,">>ensembltranscript_ensemblprotein.txt");
#open(WRITE7,">>ensemblprotein_refseqprotein.txt");
#open(WRITE8,">>ensemblgene_ensembltranscript.txt");
#open(WRITE9, ">>refseqmrna_ensembltranscript.txt");
#open(WRITE10, ">>refseqprotein_ensemblprotein.txt");
#open(WRITE11, ">>uniprot_ensemblprotein.txt");

#open(WRITE3,">>outputtemp.txt");

sub unique
{

  my @array=@_;

  %seen = ();
  @uniq = ();
  foreach $item (@array) 
  {
    unless ($seen{$item}) 
    {
      # if we get here we have not seen it before
      $seen{$item} = 1;
      push (@uniq, $item);
    }
  }
  return(\@uniq);
}

$count=0;
$count1=0;
$count2=0;
my @forentrezgene=();
my @forunigene=();
my @forrefseqn=();
my @foruniprot=();
my @refseqprt=();

# loop over all entries in the mRNA RefSeq file

#each chunk of input file starting with LOCUS can be called a sequence
while(my $seqobj = $seqin->next_seq())
{

  #all features of one sequence including gene, mrna and cds
  foreach $feat($seqobj->all_SeqFeatures())
  {
      #if the primary tag is gene
      if($feat->primary_tag eq 'gene')
      {	
        if((!$forentrezgene[0]) && ($count > 0))      		       
        {
          print WRITE2  $geneid,"###",@forentrezgene,"\n";   		       
        }
        elsif((@forentrezgene) && ($count > 0))
        {
          $temp= unique(@forentrezgene);	                        
          @uni=@{$temp};	                                                
          print WRITE2  $geneid,"###",@uni,"\n";
        }
        if((!$forunigene[0]) && ($count > 0))      		       
        {
           print WRITE4  $geneid,"###",@forunigene,"\n";   		       
        }

      elsif((@forunigene) && ($count > 0))
      {
        $temp= unique(@forunigene);	                        
        @uni=@{$temp};	                                                
        print WRITE4 $geneid,"###",@uni,"\n";


      }

      @forentrezgene=();
      @forunigene=();
      $count++;

      foreach $tag ($feat->all_tags())
      {
        if($tag eq gene)
        {
          foreach $value ($feat->each_tag_value(gene))
          {
            $geneid=$value;
            #print WRITE "gene id------->",$value,"\n";
          }
        }
      } 			


    }#tag gene


    if($feat->primary_tag eq 'mRNA')
    { 
      foreach $tag ($feat->all_tags())
      {
        if($tag eq note)
        {
          foreach $value ($feat->each_tag_value(note))
          {
            if((!$forrefseqn[0]) && ($count1 > 0))      		       
            {

              foreach $refid (@forrefseqn)
              {
                print WRITE5  $transcriptid,"###",$refid,"\n";   		       
                # Adding reflexive entry in
                # refseqmran_ensembletranscript.txt file
                print WRITE9 $refid, "###", $transcriptid,"\n";
              }
            }
            elsif((@forrefseqn) && ($count1 > 0))
            {
              $temp= unique(@forrefseqn);	                        
              @uni=@{$temp};	                                                
              foreach $refid (@uni)
              {
                print WRITE5 $transcriptid,"###",$refid,"\n";
                # Adding reflexive entry in
                # refseqmran_ensembletranscript.txt file
                print WRITE9 $refid, "###", $transcriptid,"\n";
              }

            }

            @forrefseqn=();
            $count1++;

            @fortrid=split(/=/,$value);
            $transcriptid=@fortrid[1];
            #print WRITE "transcript id-------->",$transcriptid,"\n";
            print WRITE8 $geneid,"###",$transcriptid,"\n";


          }
        }
      } 			


    }#tag mRNA



#Start of a CDS
    if($feat->primary_tag eq 'CDS')
    { 
#$i=0;
      foreach $tag ($feat->all_tags())
      {
        if($tag eq protein_id)
        {
          foreach $value ($feat->each_tag_value(protein_id))
          {

            if((!$foruniprot[0]) && ($count2 > 0))      		       
            {
              foreach $uniprotid (@foruniprot)
              {
                print WRITE1  $proteinid,"###",$uniprotid,"\n";   		       
                print WRITE11 $uniprotid, "###",$proteinid,"\n";
              }
            }

            elsif((@foruniprot) && ($count2 > 0))
            {
              $temp= unique(@foruniprot);	                        
              @uni=@{$temp};

              foreach $items (@uni)
              {
                print WRITE1  $proteinid,"###",$items,"\n";
                print WRITE11 $items, "###",$proteinid,"\n";
              }


            }

            if((!$refseqprt[0]) && ($count2 > 0))      		       
            {
              foreach $refseqprotid (@refseqprt)
              {
                print WRITE7  $proteinid,"###", $refseqprotid,"\n";   		       
                print WRITE10  $refseqprotid, "###",$proteinid,"\n";
              }
            }

            elsif((@refseqprt) && ($count2 > 0))
            {
              $temp= unique(@refseqprt);	                        
              @uni=@{$temp};
              foreach $refseqprotid (@uni)
              {
                print WRITE7  $proteinid,"###",$refseqprotid,"\n";
                print WRITE10 $refseqprotid,"###",$proteinid,"\n";
              }
            }

            @foruniprot=();
            @refseqprt=();
            $count2++;
            $proteinid=$value;
            #print WRITE "protein id-------->",$proteinid,"\n";
            print WRITE6 $transcriptid,"###",$proteinid,"\n";



          }
        }#protein id
      }




      foreach $tag ($feat->all_tags())
      {
        if($tag eq db_xref)
        {      
          #$entrezgeneid="";
          foreach $value ($feat->each_tag_value(db_xref))
          {

            if(($value =~ /EntrezGene:(.*)/) )
            {
              $entrezgeneid = $1;
              #print WRITE "Entrez gene id-------->",$entrezgeneid,"\n";
              push(@forentrezgene,$entrezgeneid);
              print scalar(@forentrezgene),"\n";					                                  


            }

          }



        }

      }





      foreach $tag ($feat->all_tags())
      {
        if($tag eq db_xref)
        {
          foreach $value ($feat->each_tag_value(db_xref))
          {			
            if(($value =~ /Uniprot\/SPTREMBL:(.*)/) ||($value =~ /Uniprot\/Varsplic:(.*)/)||($value =~ /Uniprot\/SWISSPROT:(.*)/))
            {
              $uniprotid = $1;
              #print WRITE "Uniprotid-------->",$uniprotid,"\n";
              #print WRITE1 $proteinid,"###",$uniprotid,"\n";
              push(@foruniprot,$uniprotid);



            }

          }
        }
      }

      foreach $tag ($feat->all_tags())
      {
        if($tag eq db_xref)
        {
          foreach $value ($feat->each_tag_value(db_xref))
          {

            if(($value =~ /RefSeq_peptide:(.*)/) )
            {
              $refseqpid= $1;
              #print WRITE "refseq protein id-------->",$refseqpid,"\n";
              @forrefseqp=split(/\./,$refseqpid);
              $refseqprtid=@forrefseqp[0];
              #print WRITE "refseq protein id-------->",$refseqprtid,"\n";
              #print WRITE7 $proteinid,"###",$refseqprtid,"\n";
              push(@refseqprt,$refseqprtid);


            }
          }
        }

      }

      foreach $tag ($feat->all_tags())
      {
        if($tag eq db_xref)
        {
          foreach $value ($feat->each_tag_value(db_xref))
          { 

            if(($value =~ /RefSeq_dna(.*)/) )
            {
              $refseqnid= $1;
              #print WRITE "refseq nucleotide id-------->",$refseqnid,"\n";
              @forrefseqnuc=split(/:/,$refseqnid);
              $tempo=@forrefseqnuc[1];
              @forrefseqn1=split(/\./,$tempo);
              $refseqnucid=@forrefseqn1[0];
              #print WRITE "refseq nucleotide id-------->",$refseqnucid,"\n";
              push(@forrefseqn,$refseqnucid);
              #print WRITE5 $transcriptid,"###",$refseqnucid,"\n";


            }
          }
        }
      }

      foreach $tag ($feat->all_tags())
      {
        if($tag eq db_xref)
        {
          foreach $value ($feat->each_tag_value(db_xref))
          {

            if(($value =~ /UniGene:(.*)/) )
            {
                $unigeneid= $1;
                #print WRITE "unigene id-------->",$unigeneid,"\n";
                push(@forunigene,$unigeneid);
            }
          }

        }

      }
      #print WRITE2 $geneid,"###",$entrezgeneid,"\n";
    }#CDS   

  }#all features of a seq          
}#each seq
