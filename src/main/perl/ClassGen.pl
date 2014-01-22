#!/bin/env perl
############################################################################
#
#  This tool is used to Generate Perl Database Model Skelton Class with getters
#  and setters.
#
############################################################################

use strict;

use File::Path qw(make_path);

my $numberOfArgs = scalar @ARGV;
if($numberOfArgs < 2 )
{	
	print "Usage:\t\t" . $0 . " package className [...[[attribute1], attribute2]...  attributeN]\n";
	exit 1;
}
my $package = shift @ARGV ;
my $className = shift @ARGV ;

my $useDoubleColon = 0;
$useDoubleColon = 1 if ( $package && $package =~ m/::/);
my $packageDir = $package;
$packageDir =~ s/::/\//g if( $package && $useDoubleColon ) ;
make_path($packageDir) if( $package );
my $classFile = "$className.pm";
$classFile = "$packageDir/$className.pm" if( $package );

open(my $cfh, ">", $classFile) || die "Unable to open file $classFile";

my $classHeader = "$className;";
$classHeader = "$packageDir/$className;" if( $package );

$classHeader =~ s/\//::/g;

print $cfh "package $classHeader\n\n";

print $cfh "use PDD::commons::ObjectBinder;\n";
print $cfh "use strict;\n\n";


print $cfh "sub new {\n";
print $cfh "\t my \$class = shift;\n";
print $cfh "\t my \$self = {\n";
print $cfh  "\t\t_debug"  . " => 0,\n" ;
print $cfh  "\t\t_dev_dry_run"  . " => 0,\n" ;
foreach (@ARGV)
{
	my @ret = parseAttr($_);
	print STDOUT "\t\t" . join("----", @ret) . "\n";
	print $cfh  "\t\t_" . $ret[0] . " => shift,\n";
}

print $cfh  "\t\t_sql_insert"  . " => \"" . genInsert()  . "\",\n" ;
print $cfh  "\t\t_sql_select_all"  . " => \"" . genSelectAll()  . "\",\n" ;
print $cfh  "\t\t_sql_select_by_key"  . " => \"" . genSelectByKey()  . "\",\n" ;
print $cfh  "\t\t_sql_update_by_key"  . " => \"" . genUpdateByKey()  . "\",\n" ;
print $cfh  "\t\t_sql_validate_exist"  . " => \"" . genSelectByKey()  . "\",\n" ;
print $cfh  "\t\t_sql_delete_by_key"  . " => \"" . genDeleteByKey()  . "\",\n" ;
	
print $cfh "\t };\n";
print $cfh "\t bless \$self, \$class;\n";
print $cfh "\t return \$self;\n";
print $cfh "}\n\n";

foreach (@ARGV)
{
	my @ret = parseAttr($_);
	my $name = $ret[0];
	print $cfh "sub ". $name . " {\n";
	print $cfh "\t my ( \$self, \$$name ) = \@_;\n";
	print $cfh "\t \$self->{_$name} = \$$name if defined(\$$name);\n";
	print $cfh "\t return \$self->{_$name};\n";		
	print $cfh "}\n\n";
	
	#print $cfh "sub set". ucfirst($name) . " {\n";
	#print $cfh "\t my ( \$self, \$$name ) = \@_;\n";
	#print $cfh "\t \$self->{_$name} = \$$name if defined(\$$name);\n";
	#print $cfh "\t return \$self->{_$name};\n";		
	#print $cfh "}\n\n";

	#print $cfh "sub get". ucfirst($name) . " {\n";
	#print $cfh "\t my( \$self ) = \@_;\n";
    #print $cfh "\t return \$self->{_$name};\n";
	#print $cfh "}\n\n";
}


print $cfh "sub debug {\n";
print $cfh "\t my( \$self, \$val ) = \@_;\n";
print $cfh "\t \$self->{_debug} = \$val if defined \$val;\n";
print $cfh "\t return \$self->{_debug};\n";
print $cfh "}\n\n";

print $cfh "sub devDryRun {\n";
print $cfh "\t my( \$self, \$val ) = \@_;\n";
print $cfh "\t \$self->{_dev_dry_run} = \$val if defined \$val;\n";
print $cfh "\t return \$self->{_dev_dry_run};\n";
print $cfh "}\n\n";

print $cfh "sub getInsertSql {\n";
print $cfh "\t my( \$self ) = \@_;\n";
print $cfh "\t return \$self->{_sql_insert};\n";
print $cfh "}\n\n";


print $cfh "sub getSelectAllSql {\n";
print $cfh "\t my( \$self ) = \@_;\n";
print $cfh "\t return \$self->{_sql_select_all};\n";
print $cfh "}\n\n";


print $cfh "sub getSelectByKeySql {\n";
print $cfh "\t my( \$self ) = \@_;\n";
print $cfh "\t return \$self->{_sql_select_by_key};\n";
print $cfh "}\n\n";

print $cfh "sub getValidateExistSql {\n";
print $cfh "\t my( \$self ) = \@_;\n";
print $cfh "\t return \$self->{_sql_validate_exist};\n";
print $cfh "}\n\n";

print $cfh "sub getUpdateByKeySql {\n";
print $cfh "\t my( \$self ) = \@_;\n";
print $cfh "\t return \$self->{_sql_update_by_key};\n";
print $cfh "}\n\n";


print $cfh "sub getDeleteByKeySql {\n";
print $cfh "\t my( \$self ) = \@_;\n";
print $cfh "\t return \$self->{_sql_delete_by_key};\n";
print $cfh "}\n\n";


print $cfh "sub initialize {\n";
print $cfh "\t my( \$self, \$valStr,  \$delim ) = \@_;\n";
my $str = "\t my \@metaHeader = qw(";
my $argc = scalar @ARGV;
my $pos = 0;
foreach (@ARGV)
{
	$pos++;
	my @ret = parseAttr($_);
	$str .= $ret[0];
	$str .= " " unless $pos==$argc;
}
$str .= ");\n";
print $cfh  $str;
print $cfh  "\t \$delim = '|' unless defined \$delim;\n";
print $cfh  "\t PDD::commons::ObjectBinder->bindWithValDelimitedList(\$self, \\\@metaHeader,  \$valStr, \$delim);\n";
print $cfh  "\t return \$self;\n";
print $cfh "}\n\n";



print $cfh "1;\n";

close($cfh) 
	|| warn "close failed: $!";	
	
print "\t\t$className has been generated at $classFile.\n"	;


sub parseAttr
{
	my ($attr) = @_;	
	my @ret = ();	
	if( $attr =~ /^([A-Za-z][A-Za-z0-9_]+):(VARCHAR|NUMBER)$/i){
		$ret[0] = $1;
		$ret[1] = uc($2);
	}else{
		$ret[0] = $attr;
		$ret[1] = "VARCHAR";
	}	
	return @ret;
}

sub genInsert
{
	my $sqlstmt = "INSERT INTO $className (";
	my $count = @ARGV;
	my $idx = 0;
	foreach(@ARGV)
	{		
		$idx++;
		my @ret = parseAttr($_);
		$sqlstmt .=  $ret[0]  ;
		$sqlstmt .= "," unless $idx==$count;
	}
	$sqlstmt .= ") values (";	
	$idx = 0;
	foreach(@ARGV)
	{
		$idx++;
		my @ret = parseAttr($_);
		$sqlstmt .= ":" . $ret[0] . ":" . $ret[1] ;		
		$sqlstmt .= "," unless $idx==$count;
	}
	$sqlstmt .= ")";
	return $sqlstmt;
}




sub genSelectAll
{
	my $sqlstmt = "SELECT ";
	my $count = @ARGV;
	my $idx = 0;
	foreach(@ARGV)
	{
		$idx++;
		my @ret = parseAttr($_);
		$sqlstmt .=  $ret[0] ;
		$sqlstmt .= "," unless $idx==$count;
	}
	$sqlstmt .= " FROM " . $className ;	
	return $sqlstmt;
}

sub genSelectByKey
{
	my $sqlstmt = genSelectAll();
	my @ret = parseAttr($ARGV[0]);
	$sqlstmt .= " WHERE " . $ret[0] . "= :" . $ret[0] . ":" . $ret[1];	
	return $sqlstmt;
}

sub genUpdateByKey
{
	my $sqlstmt = "UPDATE $className SET ";
	my $count = @ARGV;
	my $idx = 0;
	foreach(@ARGV)
	{
		$idx++;
		my @ret = parseAttr($_);
		$sqlstmt .=  $ret[0]  . '=:' . $ret[0] . ":" . $ret[1];	
		$sqlstmt .= "," unless $idx==$count;
	}
	my @ret = parseAttr($ARGV[0]);
	$sqlstmt .= " WHERE " . $ret[0] . "= :" . $ret[0] . ":" . $ret[1];	
	return $sqlstmt;	
}


sub genDeleteByKey
{
	my @ret = parseAttr($ARGV[0]);
	my $sqlstmt = "DELETE FROM $className ";	
	$sqlstmt .= " WHERE " . $ret[0] . "= :" . $ret[0] . ":" . $ret[1];	
	return $sqlstmt;
}

	
1;
	
	
	