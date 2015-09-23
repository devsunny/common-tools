package PDD::script::SourceIterator;
use warnings;
use strict;
use utf8;
use Encode;
use PerlIO::gzip;

sub new
{
	my ($class, @param) = @_;
	my $self = {
		SourceFileArrayRef => \@param,
		current_file_handle => undef,
		current_file_type => 0,
		_utf8 => 0,
		_in_compress => 0,		
	};
	bless $self, $class;
	return $self;
}

sub utf8
{
	 my ( $self, $utf8 ) = @_;
	 if( defined $utf8){
		 if($utf8 == 1 || $utf8 eq "yes" || $utf8 eq "true"){
			$self->{_utf8} = 1;
		 }else{
			$self->{_utf8} = 0;
		 }
	 }
	 return  $self->{_utf8};
}


sub compressed
{
	 my ( $self, $compress ) = @_;
	 if( defined $compress){
		 if($compress == 1 || $compress eq "yes" || $compress eq "true"){
			$self->{_in_compress} = 1;
		 }else{
			$self->{_in_compress} = 0;
		 }
	 }
	 return  $self->{_in_compress};
}



sub openSource
{
	my ($self, $utf8, $in_compress) = @_;
	$self->utf8($utf8) if( defined $utf8);
	$self->utf8($in_compress) if( defined $in_compress);	
	if( scalar @{$self->{SourceFileArrayRef}} ){
			;
	}else{
		$self->{current_file_type} = 1;
	}
}


sub readLine
{
	my ($self) = @_;
	my $ret = undef;
	if(	$self->{current_file_type} == 1){
		if($self->utf8())
		{
			use open qw(:std :utf8);
			$ret = <>;
		}else{
			$ret = <STDIN>;	
		}		
	}else{
		if ( defined($self->{current_file_handle}))
		{
			my $fh = $self->{current_file_handle};
			$ret = <$fh>;
		}
		if(!$ret)
		{
			$ret = $self->readToLine();
		}		
	}	
	$ret;
}

sub readToLine
{
	my ($self) = @_;	
	my $ret = undef;
	while(1)
	{
		my $file = shift(@{$self->{SourceFileArrayRef}});
		last unless $file;
		close($self->{current_file_handle}) if defined($self->{current_file_handle});
		my $fh;
		if( $self->utf8() && $self->compressed() )
		{	
			open($fh, '<:gzip', $file);
			binmode $fh, '::encoding(utf8)';
		}elsif( $self->compressed() )
		{	
			open($fh, '<:gzip', $file);			
		}elsif( $self->utf8() )
		{	
			open($fh, '<:encoding(utf8)', $file);			
		}else{
			open($fh, "<", $file);
		}
		while (<$fh>) 
		{
			$ret = $_;
			$self->{current_file_handle} = $fh;
			last;
		}
		last if $ret;
	}	
	return $ret;
}


sub closeSource
{
	my ($self) = @_;	
	close($self->{current_file_handle}) if defined($self->{current_file_handle});
}


1;
