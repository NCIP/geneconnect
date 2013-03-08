package FetchParams;


use strict;
use warnings;
use Config::Simple;

sub getParams
{
  my $fileName = shift;
  my %Config;
  my $objCfg = new Config::Simple($fileName) or die "Cant find $fileName parameter file";
  %Config = $objCfg->vars(-block => 'default');


  my ($tmp, $val);
  foreach (keys (%Config))
  {
    $tmp = $_;
    $val = $Config{$tmp};
    delete $Config{$tmp};
    $_ =~ s/^default\.(.*)/$1/g;
    $Config{$_} = $val;
  }
    
  return (\%Config);
}


1;
__END__;
