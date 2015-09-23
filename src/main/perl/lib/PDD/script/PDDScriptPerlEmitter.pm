package PDD::script::PDDScriptPerlEmitter;

use List::MoreUtils qw( any );

my %PDD_INTERNAL_FUNCTION_NAME_MAP = (split=>'odd_split', 
										int =>'floor', 
										lcfirst=>'lc_first', 
										ucfirst=>'uc_first', 
										join=>'odd_join',										
										);

my $PDD_FUNCTION_VALIDATORS = {	tally=>sub {
									my @params = @_;									
									my $valid = scalar(@params)==1 && $params[0] =~ /^\$\d+$/;			
									die 'function tally only allow column expression parameter.' unless $valid;
									return 'tally(\'' .$params[0] . '\',' . $params[0] . ')'; 
								},									
								 								
								group_min=>sub {
									my @params = @_;
									my $valid = scalar(@params)==2 && $params[0] =~ /^\$\d+$/;			
									die 'function group_min only allow column expression parameter.' unless $valid;
									return 'group_min(' .$params[0] . ','.  $params[1]  . ')'; 
								},	
								group_max=>sub {
									my @params = @_;
									my $valid = scalar(@params)==2 && $params[0] =~ /^\$\d+$/;			
									die 'function group_max only allow column expression parameter.' unless $valid;
									return 'group_max(' . $params[0] . ','.  $params[1]  . ')'; 
								},	
								group_last=>sub {
									my @params = @_;
									my $valid = scalar(@params)==2 && $params[0] =~ /^\$\d+$/;			
									die 'function group_last only allow column expression parameter.' unless $valid;
									return 'group_last(' .$params[0] . ','.  $params[1]  . ')'; 
								},	
								
								group_first=>sub {
									my @params = @_;
									my $valid = scalar(@params)==2 && $params[0] =~ /^\$\d+$/;			
									die 'function group_first only allow column expression parameter.' unless $valid;
									return 'group_first(' .$params[0] . ','.  $params[1]  . ')'; 
								},	
								
								group_avg=>sub {
									my @params = @_;
									my $valid = scalar(@params)==2 && $params[0] =~ /^\$\d+$/;			
									die 'function group_avg only allow column expression parameter.' unless $valid;
									return 'group_avg(' .$params[0] . ','.  $params[1]  . ')'; 
								},
								
								group_sum=>sub {
									my @params = @_;
									my $valid = scalar(@params)==2 && $params[0] =~ /^\$\d+$/;			
									die 'function group_sum only allow column expression parameter.' unless $valid;
									return 'group_sum(' .$params[0] . ','.  $params[1]  . ')'; 
								},
								
								group_count=>sub {
									my @params = @_;
									my $valid = scalar(@params)==1 && $params[0] =~ /^\$\d+$/;			
									die 'function group_count only allow column expression parameter.' unless $valid;
									return 'group_count(' .$params[0] . ')'; 
								},	
								
							 };
my @PDD_FUNCTIONS = qw(odd_split ext_split lookup lastcol convert_date current_date scol prefix postfix ltrunc rtrunc 
				ifnull srow unix2dos dos2unix trim0 ltrim0 rtrim0 trim 
				ltrim rtrim upper lower lc_first uc_first swapcase substring char string_char clean_unicode_str 
				concat odd_join ends_with is_blank 				
				reg_get_match reg_get_matches reg_get_length_matches re_index 
				ceil abs floor max min pow sqrt  decimal_truncate decimal_round  tally
				random odd_random_sample sample_col sample_email sample_ssn sample_ccn  
				sequence 
				group_last group_first group_min group_max group_avg group_count group_reset group_sum 
				force_error force_abort search_replace is_null);

my %PDD_TRANSFORM_SETTINGS = (
		indelim=>'$FS',
		outdelim=>'$OFS',
		limit=>'$limit',
		recindelim=>'$RS',
		recoutdelim=>'$ORS',
		ignorecase=>'$IGNORECASE',
		skip=>'$skip',
		limit=>'$limit',
	);				
				
sub new
{
	my ($class, $grammar) = @_;	
	my $self = {
		_debug => 0,
		_dev_dry_run => 0,	
		_case_variable_seq => 0,				
	};	 
	bless $self, $class;	
	my @beginBlock = ();
	my @declareBlock = ();	
	my @globalDynaFuncts = ();
	my @preProcessBlock = ();
	my $filter = undef;
	my @fieldSelections = ();
	my @postProcessBlock = ();
	my @endBlock = ();
	my %ioSpecs = ();
	
	my $langStruct = {
		ioSpecs=>\%ioSpecs,
		beginBlock=>\@beginBlock,
		declareBlock=>\@declareBlock,
		globalDynaFuncts=>\@globalDynaFuncts,
		preProcessBlock=>\@preProcessBlock,
		filter=>$filter,
		fieldSelections=>\@fieldSelections,
		postProcessBlock=>\@postProcessBlock,
		endBlock=>\@endBlock,		
	};
	$self->{langStruct} = $langStruct;
	return $self;	 
}


sub devDryRun
{
	 my ( $self, $devDryRun ) = @_;
	 if( defined $devDryRun ){
		 if($devDryRun == 1 || $devDryRun eq "yes" || $devDryRun eq "true"){
			$self->{_devDryRun} = 1;
		 }else{
			$self->{_devDryRun} = 0;
		 }
	 }
	 return  $self->{_devDryRun};
}

sub debug
{
	 my ( $self, $debug ) = @_;
	 if( defined $debug){
		 if($debug == 1 || $debug eq "yes" || $debug eq "true"){
			$self->{_debug} = 1;
		 }else{
			$self->{_debug} = 0;
		 }
	 }
	 return  $self->{_debug};
}


sub emitGlobalSetting
{
	my ($self, $line, $column, $remainText, $name, $value) = @_;			
	my $output = "";
	$name =~ s/^\s+//;
	$name =~ s/\s+$//;
	$value =~ s/^\s+//;
	$value =~ s/\s+$//;	
	my $lc_key = lc $name;	
	my $mappedSetting = $PDD_TRANSFORM_SETTINGS{$lc_key};
	
	if( defined($mappedSetting) ){		
		if ($value =~ /^(-)?\d+(\.\d+)?((e|E)(-)?\d+)?$/){
			$output = $mappedSetting . "=" . $value . ";\n"; 
		}else{
			$output = $mappedSetting . "=" . translateNamedDelimter($value) . ";\n"; 
		}	
		push(@{$self->{langStruct}->{declareBlock}}, $output);
	}elsif( any { $_ eq $lc_key } qw(in_compress, out_compress, charset, command_file datafile outfile xform_log) )
	{
		$self->{langStruct}->{$ioSpecs}->{ $lc_key } = $value; 
	}elsif( $lc_key eq 'dos2unix' )
	{
		if ($value == 1 || (any {lc $value eq $_} qw(yes y) )) {
			$output = '$line = dos2unix($line);' . "\n";
			push (@{$self->{langStruct}->{preProcessBlock}}, $output); 	
	
		}
	}elsif( $lc_key eq 'unix2dos' )
	{
		if ($value == 1 || (any {lc $value eq $_} qw(yes y) )) {
			$output = '$line = unix2dos($line);'  . "\n";
			push (@{$self->{langStruct}->{preProcessBlock}}, $output); 				
		}
	}elsif ($lc_key eq 'lookup') { 
		#handleLookupSpec($value);
	}		
	
	if(!$output)
	{
		if ($value =~ /^(-)?\d+(\.\d+)?((e|E)(-)?\d+)?$/){
			$output = $name . "=" . $value . ";\n"; 
		}else{
			$output = $name . "=\"" . $value . "\";\n"; 
		}
	}	
	return $output;
}



sub emitSelectionFilter
{
	my ($self, $line, $column, $remainText, $selectText) = @_;			
	$self->{langStruct}->{filter} = $selectText;
    return $selectText;	
}


sub emitRangeSelection
{
	my ($self, $line, $column, $remainText, $rangeRef) = @_;			
	foreach(@{$rangeRef}){		
		push(@{$self->{langStruct}->{fieldSelections}}, $_);		
	}
	
}

sub emitCaseSelection
{
	my ($self, $line, $column, $remainText, $selectText) = @_;			
	print ">>>emitCaseSelection: $selectText\n";
	
}

sub emitNormalSelection
{
	my ($self, $line, $column, $remainText, $selectText) = @_;	
	push(@{$self->{langStruct}->{fieldSelections}}, $selectText);	
}



sub transformFunction
{
	my ($self, $line, $column, $remainText, $functionName, $parameRef) = @_;			
	my $functName = $self->validate_function_name($functionName);
	die "Invalid function $functionName, please refer user gruide for support function list." unless $functName;
	my $functDef = undef;
	my $paramCnt = scalar(@{$parameRef});
	my $functValidatorPtr = $PDD_FUNCTION_VALIDATORS->{$functName};
	if( defined($functValidatorPtr) )
	{
		$functDef = $functValidatorPtr->(@{$parameRef});		
	}else{
		$functDef = $functName . '(' . join(',', @{$parameRef}). ')';
	}
	return $functDef;
}

sub validate_function_name
{
	my ($self, $fname) = @_;
	my $mappedName = $PDD_INTERNAL_FUNCTION_NAME_MAP{$fname}||$fname;
	my $ret = undef;
	if(scalar (grep(/^$mappedName$/, @PDD_FUNCTIONS)) == 1){
		$ret = $mappedName;
	}
	return $ret;
}


sub transformCaseSatement
{
	my ($self, $line, $column, $remainText, $whenStmts, $elseStmt) = @_;
	#_case_variable_seq
	$self->{_case_variable_seq}++;	
	my $varName = "case_var_" .sprintf("%02d", $self->{_case_variable_seq});
	push (@{$self->{langStruct}->{declareBlock}}, 'my $' . $varName . ";\n"); 	
	
	$script = "\n";
	my $pos = 0;
	foreach(@{$whenStmts})
	{
		my @whenClause = @{$_};
	    my $ifstmt = ($pos==0)?"if":"elsif";
	   $script .= $ifstmt . '(' . $whenClause[0] . '){ $' . $varName . '=' .  $whenClause[1] . ';}';
	   $pos++;
	}
	 $script .= 'else { $'  . $varName . '=' .  $elseStmt . ';}';
	 $script .= "\n";
	push (@{$self->{langStruct}->{preProcessBlock}}, $script); 	
	push(@{$self->{langStruct}->{fieldSelections}}, '$'. $varName );		
	return $script;
}

#######################################################
#
# Internal used function defined here;
#
#######################################################

sub handleLookupSpec
{
	my $lookupspec = shift @_;
	my @lkups = split(/;/, $lookupspec);
	my @lookupNames = ();
	foreach(@lkups)
	{
		my $lk = $_;
		my @lkparts = split(':', $lk);	
		push(@lookupNames, $lkparts[0]);
		push (@xform_begin_code , "my \$$lkparts[0] = ();\n");
		#push (@xform_begin_code , generateLookupArray(@lkparts));
	}
	#$lookupfunction = generateLookupFunction(@lookupNames);
}



sub translateNamedDelimter
{
my ($value) = @_;
my %namedDelimiters = (
	 pipe        => '|',
	 space       => ' ',
	 comma       => ',',
	 colon       => ':',
	 semicolon   => ';',
	 tab         => '\\t',
	 caret       => '^',
	 backslash   => '\\\\',
	 soh         => 'chr(1)',
	 newline     => '\\n',
	 dos_newline => '\\r\\n',
 );
my $delimit=$namedDelimiters{$value} || $value;
$delimit =~ s{"}{\\"}g;

if ( $delimit =~ m/0x([0-9A-F]{2})/i ){
	my $outdelim = '';	
	while ($delimit=~ /0x([0-9A-F]{2})/isg )
	{
			my $letter = chr(hex($1));
			if( $letter eq "\r" ){
				$letter = "\\r";
			}elsif ( $letter eq "\n" ){
				$letter = "\\n";
			}elsif ( $letter eq "\t" ){
				$letter = "\\t";
			}elsif ( $letter eq "\\" ){
				$letter = "\\\\";
			}			
			$outdelim .= $letter;
	}
	$delimit = $outdelim;
}
$delimit = '"' . $delimit . '"';
return $delimit;
}








1;