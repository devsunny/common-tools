use strict;
use PDD::script::SourceIterator;


my $srcIter = new PDD::script::SourceIterator(@ARGV);

$srcIter->openSource();
while(my $line=$srcIter->readLine())
{
	chomp($line);
	print $line . "\n" if($line);
}
$srcIter->closeSource();


