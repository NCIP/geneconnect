
use Bio::Seq;
use Bio::SeqIO;
use Bio::SeqIO::fasta;
use FetchParams;

# GeneConnect server (Java) sends a properties file
# Read this properties file and store it in a hash
# first arguement is a -f; collect it and ignore it for now


my $optionalArgument = shift;

my $configFile = shift;

# read contents of config file into a hash by using module FetchParams

my %configParams = %{FetchParams::getParams($configFile)};

#The name of file to be parsed is obtained from the hash.

my $inputFile = ($configParams{INPUTFILE});

my @arrayofinputfile = @$inputFile;

#reading parameters specific for this parser from the parser specific properties file

my $propertiesfile ="properties_blastsourcegenbankmrna.txt";

my %properties = %{FetchParams::getParams($propertiesfile)};

#reading the names of all organisms
my $organism=($properties{Organism});
my @arrayfororganism = @$organism;

#open organism specific fasta files in append mode
foreach my $org (@arrayfororganism)
{
     open($org,">>genbank_mRNA" .$org . ".fasta");
}

foreach my $file (@arrayofinputfile)
{
	my $inputfilepath = ($configParams{BASEDIR}) . "\/" . $file;

	my $Seq_in = Bio::SeqIO ->new(-format => 'Genbank', -file => $inputfilepath);
	while(my $query = $Seq_in->next_seq())
	{
 
	 #extracting the accession number of the sequence
	 my $accession=$query->display_id;

	 #extracting the sequence itself
	 my $sequence=$query->seq;

	 #picking up the organism from the feature section of the sequence
	 foreach my $feature($query->all_SeqFeatures())
	   {
		#if the primary tag is source

		if($feature->primary_tag eq 'source')
		{	       		 
                        
			foreach my $tag ($feature->all_tags())

			{
				#if the subtag is organism
				if($tag eq 'organism')
				{

					
					foreach my $value ($feature->each_tag_value('organism'))
					{
                                                #writing the accession and sequence in the organism specific fasta files
						print $value ">$accession\n$sequence\n";

					}#value of organism




				}#if subtag organism

			} #for each sub tag under source

		  }# if tag source


	}#all features of the seq


	 }#each seq

 }#each org