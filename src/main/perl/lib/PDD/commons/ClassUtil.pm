package PDD::commons::ClassUtil;

use Exporter 'import'; 
@EXPORT_OK = qw(is_instance_of has_func_defined); 

sub is_instance_of
{
	my ($objRef, $type) = @_;
	return 0  unless (defined($objRef) && defined($type));
	my $ret =  eval { $objRef->isa($type);} ; 
	$ret = $ret || 0;
	return $ret;
}


sub has_func_defined
{
	my ($handleRef, $funcName) = @_;
	my $ret = 0;
	$ret = defined eval { $handleRef->can($funcName); } || 0;	
	return $ret;
}

