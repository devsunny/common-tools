package PDD::script::PDDTrnsfrmPerlFuncts;

use List::MoreUtils qw(firstidx);
use Scalar::Util qw(looks_like_number);
use Exporter 'import'; 
@EXPORT_OK = qw(odd_split ext_split lookup lastcol convert_date current_date scol prefix postfix ltrunc rtrunc 
				ifnull dateString srow unix2dos dos2unix trim0 ltrim0 rtrim0 trim 
				ltrim rtrim upper lower lc_first uc_first swapcase substring char string_char clean_unicode_str 
				concat odd_join ends_with is_blank reg_get_match reg_get_matches 
				reg_get_length_matches re_index random
				ceil abs floor max min pow sqrt  tally
				decimal_truncate decimal_round sample_col sample_email 
				sample_ssn sample_ccn odd_random_sample sequence group_last group_first 
				group_min group_max group_avg group_count group_reset 
				group_sum force_error force_abort search_replace is_null); 


use strict;

my @MONTH_ABBREVIATIONS = qw(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec);
my @MONTH_NAMES = qw(January February March April May June July August September October November December);
my $GLOBAL_SEQUNCE_START = undef;
my $GLOBAL_SEQUNCE_STEP = undef;
my @EMAIL_ADDRSS_CHARS = ('A', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '!', '#', '$', '%', '&', "'", '*', '+', '-',  '?',  '^', '_',  '~');
my @DOMAIN_ADDRSS_CHARS = ('A', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',  '-',   '_');
my $EMAIL_ADDRSS_CHARS_NUM = @EMAIL_ADDRSS_CHARS;
my $DOMAIN_ADDRSS_CHARS_NUM = @DOMAIN_ADDRSS_CHARS;


$::PDD_BLANK_AS_NULL=1;
$::PDD_NULLVAL='NULL';

sub is_null
{
	my ($val) = @_;
	if(! defined($val) ){
		return 1;
	}elsif( $::PDD_BLANK_AS_NULL && $val =~ /^$/){
		return 1;
	}elsif( defined ($::PDD_NULLVAL) && lc($::PDD_NULLVAL) eq lc($val)){
		return 1;
	}else{
		return 0;
	}
}



sub group_min
{
	my ($grp_key, $val) = @_;	
	
	if(!defined($::PDD_TRANSFORM_GROUP_MIN_HASH_REF))
	{
		my %transformGroupHash = {};
		$::PDD_TRANSFORM_GROUP_MIN_HASH_REF = \%transformGroupHash;			
	}	
	if(!defined($::PDD_TRANSFORM_GROUP_MIN_HASH_REF->{$grp_key}) && is_null($val)){		
		return $val;
	}elsif(!defined($::PDD_TRANSFORM_GROUP_MIN_HASH_REF->{$grp_key})){
		$::PDD_TRANSFORM_GROUP_MIN_HASH_REF->{$grp_key} = $val;
		return $val;
	}elsif($val<$::PDD_TRANSFORM_GROUP_MIN_HASH_REF->{$grp_key}){
		$::PDD_TRANSFORM_GROUP_MIN_HASH_REF->{$grp_key} = $val;	
		return $val;
	}else{
		return $::PDD_TRANSFORM_GROUP_MIN_HASH_REF->{$grp_key};
	}
}



sub group_max
{
	my ($grp_key, $val) = @_;	
	
	if(!defined($::PDD_TRANSFORM_GROUP_MAX_HASH_REF))
	{
		my %transformGroupHash = {};
		$::PDD_TRANSFORM_GROUP_MAX_HASH_REF = \%transformGroupHash;			
	}	
	if(!defined($::PDD_TRANSFORM_GROUP_MAX_HASH_REF->{$grp_key}) && is_null($val)){		
		return $val;
	}elsif(!defined($::PDD_TRANSFORM_GROUP_MAX_HASH_REF->{$grp_key})){
		$::PDD_TRANSFORM_GROUP_MAX_HASH_REF->{$grp_key} = $val;
		return $val;
	}elsif($val>$::PDD_TRANSFORM_GROUP_MAX_HASH_REF->{$grp_key}){
		$::PDD_TRANSFORM_GROUP_MAX_HASH_REF->{$grp_key} = $val;	
		return $val;
	}else{
		return $::PDD_TRANSFORM_GROUP_MAX_HASH_REF->{$grp_key};
	}
}



sub group_sum
{
	my ($grp_key, $val) = @_;
	if(!defined($::PDD_TRANSFORM_GROUP_SUM_HASH_REF))
	{
		my %transformGroupHash = {};
		$::PDD_TRANSFORM_GROUP_SUM_HASH_REF = \%transformGroupHash;			
	}
	if(!defined($::PDD_TRANSFORM_GROUP_SUM_HASH_REF->{$grp_key})){
		$::PDD_TRANSFORM_GROUP_SUM_HASH_REF->{$grp_key} = [0];
	}
	my $trueVal = is_null($val)?0:$val;	
	my $sum = $::PDD_TRANSFORM_GROUP_SUM_HASH_REF->{$grp_key}[0] + $trueVal;
	$::PDD_TRANSFORM_GROUP_SUM_HASH_REF->{$grp_key}[0] = $sum;
	return $sum;
}




sub group_avg
{
	my ($grp_key, $val) = @_;
	if(!defined($::PDD_TRANSFORM_GROUP_AVG_HASH_REF))
	{
		my %transformGroupHash = {};
		$::PDD_TRANSFORM_GROUP_AVG_HASH_REF = \%transformGroupHash;			
	}
	if(!defined($::PDD_TRANSFORM_GROUP_AVG_HASH_REF->{$grp_key})){
		$::PDD_TRANSFORM_GROUP_AVG_HASH_REF->{$grp_key} = [0, 0];
	}
	my $trueVal = is_null($val)?0:$val;
	my $gc = $::PDD_TRANSFORM_GROUP_AVG_HASH_REF->{$grp_key}[1] + 1;
	my $sum = $::PDD_TRANSFORM_GROUP_AVG_HASH_REF->{$grp_key}[0] + $trueVal;
	$::PDD_TRANSFORM_GROUP_AVG_HASH_REF->{$grp_key}[1] = $gc;
	$::PDD_TRANSFORM_GROUP_AVG_HASH_REF->{$grp_key}[0] = $sum;
	return $sum/$gc;
}

sub group_first
{
	my ($grp_key, $val) = @_;
	if(!defined($::PDD_TRANSFORM_GROUP_FIRST_HASH_REF))
	{
		my %transformGroupHash = {};
		$::PDD_TRANSFORM_GROUP_FIRST_HASH_REF = \%transformGroupHash;			
	}
	my $ret;
	if( !is_null($::PDD_TRANSFORM_GROUP_FIRST_HASH_REF->{$grp_key}) )
	{
		$ret = $::PDD_TRANSFORM_GROUP_FIRST_HASH_REF->{$grp_key};
	}else{		
		if(!is_null($val))
		{
			$::PDD_TRANSFORM_GROUP_FIRST_HASH_REF->{$grp_key} = $val;
		}
		$ret = $val;
	}
	return $ret;	
}



sub group_last
{
	my ($grp_key, $val) = @_;
	if(!defined($::PDD_TRANSFORM_GROUP_LAST_HASH_REF))
	{
		my %transformGroupHash = {};
		$::PDD_TRANSFORM_GROUP_LAST_HASH_REF = \%transformGroupHash;			
	}
	
	my $ret;	
	if(is_null($val))
	{		
		$ret = $::PDD_TRANSFORM_GROUP_LAST_HASH_REF->{$grp_key} || $val;
	}else{
		$::PDD_TRANSFORM_GROUP_LAST_HASH_REF->{$grp_key} = $val;
		$ret = $val;
	}	
	return $ret;	
}


sub group_count
{
	my ($grp_key, $val) = @_;
	if(!defined($::PDD_TRANSFORM_GROUP_COUNT_HASH_REF))
	{
		my %transformGroupHash = {};
		$::PDD_TRANSFORM_GROUP_COUNT_HASH_REF = \%transformGroupHash;			
	}
	if(!defined($::PDD_TRANSFORM_GROUP_COUNT_HASH_REF->{$grp_key})){
		$::PDD_TRANSFORM_GROUP_COUNT_HASH_REF->{$grp_key} = 0;
	}
	my $ret;	
	if(!is_null($val))
	{		
		$ret = $::PDD_TRANSFORM_GROUP_COUNT_HASH_REF->{$grp_key} +1;
		$::PDD_TRANSFORM_GROUP_COUNT_HASH_REF->{$grp_key} = $ret;
	}else{
		$ret = $::PDD_TRANSFORM_GROUP_COUNT_HASH_REF->{$grp_key};
	}	
	return $ret;	
}





sub random
{
	
	my ($low, $high, $max_size, $min_size) = @_;
	my $randVal;
	if( $low == 0 ){
		$randVal = int(rand($high));
	}else{
		$randVal = int(rand($high-$low)) + $low;
	}
	if(defined($min_size) && $min_size>0 && length($randVal)<$min_size){		
		my $str = '0'x($min_size - length($randVal));
		$randVal = $str . $randVal;	
	}
	if(!defined($min_size) && defined($max_size) && $max_size>0){
		my $str = '0'x($max_size - length($randVal));
		$randVal = $str . $randVal;	
	}	
	return $randVal;	
}


sub tally
{
	my ($colExpr, $value)= @_;	
	if(!defined($::PDD_TALLY_HASH_REF))
	{
		my %tallyTable = {};
		$::PDD_TALLY_HASH_REF = \%tallyTable;		
	}
	$::PDD_TALLY_HASH_REF->{$colExpr} = 0 unless defined $::PDD_TALLY_HASH_REF->{$colExpr};
	$::PDD_TALLY_HASH_REF->{$colExpr} = $::PDD_TALLY_HASH_REF->{$colExpr} + $value;
	return $::PDD_TALLY_HASH_REF->{$colExpr};
}


sub decimal_truncate
{
	my ($input, $len)  = @_;	
    #$input = abs($input);
	my $string = $input;	
	if( is_number($string) && $string =~ m/\./){		
		my @nums = split(/\./, $string, -1);
		if(length($nums[1])>$len){
			return ($nums[0] . "." . substr($nums[1], 0, $len)) + 0;	
		}else{
				return $input;
			}
	}else{
		return $::PDD_NULLVAL; #$input;
	}
}

sub decimal_round
{
	my ($input, $len)  = @_;	
	my $string = $input;	
	if( is_number($string) && $string =~ m/\./){		
		my @nums = split(/\./, $string, -1);
		if(length($nums[1])>$len){
			my $base =  ($nums[0] . "." . substr($nums[1], 0, $len)) + 0;	
			my $remain = substr($nums[1], $len, 1);
			if( $remain >= 5){
				if($input >=0 ){
					$base = $base + (1/(10**$len));
				}else{
					$base = $base - (1/(10**$len));
				}
			}	
			return $base;
		}else{
		   return $input;
		}		
	}else{
		return $::PDD_NULLVAL; #$input;
	}
}



sub sample_col
{
	my $col_len = shift @_;
	$col_len = 20 if(!defined($col_len ));
	my $ret = "";	
	my $idx= 0;
	for($idx=0; $idx<$col_len; $idx++)
	{
		$ret .= $EMAIL_ADDRSS_CHARS[int(rand($EMAIL_ADDRSS_CHARS_NUM))];
	}
	return $ret;
}


sub sample_email
{
	my $ret = "";
	my $lplen = int(rand(64));
	my $idx= 0;
	for($idx=0; $idx<$lplen; $idx++)
	{
		$ret .= $EMAIL_ADDRSS_CHARS[int(rand($EMAIL_ADDRSS_CHARS_NUM))];
	}
	$ret .= "@";
	$lplen = int(rand(64));
	for($idx=0; $idx<$lplen; $idx++)
	{
		$ret .= $DOMAIN_ADDRSS_CHARS[int(rand($DOMAIN_ADDRSS_CHARS_NUM))];
	}
	$ret .= ".com";
	
	return $ret;
}

sub sample_ssn
{
	my $seedSsn  = shift @_;
	my $ret ;
	if(defined($seedSsn)){
		$seedSsn =~ s/[ \-]//g;		
		if(length($seedSsn)>1){
			my $s1 = substr($seedSsn, 0, 3);
			$ret = odd_random_sample($s1, 3, 3, 999);
		}else{
			$ret = odd_random_sample(999, 3, 3, 999);
		}
		if(length($seedSsn)>3){
			$ret .= odd_random_sample(substr($seedSsn, 4, 2), 2, 2, 99);
		}else{
			$ret .= odd_random_sample(99, 2, 2, 99);
		}
		if(length($seedSsn)>5){
			$ret .= odd_random_sample(substr($seedSsn, 5, 4), 4, 4, 9999);
		}else{
			$ret .= odd_random_sample(9999, 4, 4, 9999);
		}		
	}else{
		$ret = odd_random_sample(999, 3, 3, 999);
		$ret .= odd_random_sample(99, 2, 2, 99);
		$ret .= odd_random_sample(9999, 4, 4, 9999);
	}	
	return $ret;
}

sub sample_ccn
{
	my $seedSsn  = shift @_;
	my $ret ;
	if(defined($seedSsn)){
		$seedSsn =~ s/[ \-]//g;		
		if(length($seedSsn)>1){
			my $s1 = substr($seedSsn, 0, 4);
			$ret = odd_random_sample($s1, 4, 4, 9999);
		}else{
			$ret = odd_random_sample(9999, 4, 4, 9999);
		}
		if(length($seedSsn)>4){
			$ret .= odd_random_sample(substr($seedSsn, 4, 4), 4, 4, 9999);
		}else{
			$ret .= odd_random_sample(9999, 4, 4, 9999);
		}
		if(length($seedSsn)>8){
			$ret .= odd_random_sample(substr($seedSsn, 8, 4), 4, 4, 9999);
		}else{
			$ret .=odd_random_sample(9999, 4, 4, 9999);
		}
		if(length($seedSsn)>12){
			$ret .= odd_random_sample(substr($seedSsn, 12, 4), 4, 4, 9999);
		}else{
			$ret .=odd_random_sample(9999, 4, 4, 9999);
		}			
	}else{
		$ret = odd_random_sample(9999, 4, 4, 9999);
		$ret .= odd_random_sample(9999, 4, 4, 9999);
		$ret .= odd_random_sample(9999, 4, 4, 9999);
		$ret .= odd_random_sample(9999, 4, 4, 9999);
	}	
	return $ret;
}



sub odd_random_sample
{
	my($seed, $minlen, $maxlen, $maxval) = @_;
	my $rnd = int(rand($seed));
	if(defined($maxval) && $maxval > 0){
		while($rnd>$maxval){
			$rnd = int(rand($seed));
	    }
	}
	my $strval = "$rnd";
	if(length($strval)<$minlen){
		my $idx = 0;
		my $n = $minlen - length($strval);
		for($idx = 0; $idx<$n; $idx++)
		{
		 $strval .= "0";
		}
	}
	if(length($strval)>$maxlen){		
		$strval = substr($strval, 0, $maxlen);
	}
	return $strval;
}


sub sequence
{
	my ($start, $step) = @_;	
	if( !defined($GLOBAL_SEQUNCE_START)){
		 if(is_number($start)){
			$GLOBAL_SEQUNCE_START = $start;
		}else{
			$GLOBAL_SEQUNCE_START = 0;
		}
		if(is_number($step)){
			$GLOBAL_SEQUNCE_STEP = $step;
		}else{
			$GLOBAL_SEQUNCE_STEP = 1;
		}		
	}
	my $ret = $GLOBAL_SEQUNCE_START;
	$GLOBAL_SEQUNCE_START += $GLOBAL_SEQUNCE_STEP;	
	return $ret;	
}


sub lc_first
{
	my $param = shift;
	return lcfirst($param);
}

sub uc_first
{
	my $param = shift;
	return ucfirst($param);
}



sub force_error
{
  my ($msg)  = @_;
  die($msg); 
}

sub force_abort
{
  my ($msg)  = @_;
  die($msg);
}



sub reg_get_match
{
	my ($string, $regex, $offset, $funcRef)  = @_;	
	my $teststr = ($offset)?substr($string, $offset, length($string)-$offset):$string;	
	my @matchs = ($teststr =~ m/$regex/);	
	return $funcRef->(@matchs);
}

sub reg_get_matches
{
	my ($string, $regex, $offset)  = @_;
	my $teststr = ($offset)?substr($string, $offset, length($string)-$offset):$string;		
	my @matchs = ($teststr =~ m/$regex/);
	return @matchs;
}


sub reg_get_length_matches
{
	my ($string, $regex, $offset)  = @_;
	my $teststr = ($offset)?substr($string, $offset, length($string)-$offset):$string;	
	my @ret = ();
	while($teststr =~ m/$regex/g){	
		my $ml = length($&);
		my $p = pos($teststr)-$ml;
		my @mes = ();
		$mes[0] =  $p + 1;
		$mes[1] =  $ml;
		push @ret, [@mes];		
	}	
	return @ret;
}

sub re_index
{
	my ($string, $regex, $offset)  = @_;
	my $ret;
	if(!defined($string) ||  !defined($regex)){
		$ret = $::PDD_NULLVAL;
	}elsif(lc($string) eq lc($::PDD_NULLVAL)){
		$ret = $string;
	}elsif(lc($regex) eq lc($::PDD_NULLVAL)){
		$ret = $regex;
	}else{
		my $teststr = ($offset)?substr($string, $offset, length($string)-$offset):$string;	
		 $ret = ($teststr =~ m/$regex/g)?(pos($teststr)-length($&)+1):0;
	}	
	return $ret;
}




sub is_number {
    my ($msg) = @_;
    return ($msg !~ m/[a-zA-Z]/ && looks_like_number($msg)) ? 1 : 0;
}

sub ceil
{
	my ($input)  = @_;	
    if (is_number($input)) {
        require POSIX;
        return POSIX::ceil($input);
    }
    else {
        return 0;
    }
}

sub abs
{
	my ($input)  = @_;	
    if (is_number($input)) {
	   require POSIX;
	   return POSIX::abs($input);
    }
    else {
        return 0;
    }
}


sub floor
{
	my ($input)  = @_;	

    if (is_number($input)) {
         require POSIX;
        return POSIX::floor($input);
    }
    else {
        return 0;
    }
}

sub max
{
	my ($ret, @rest)  = @_;	
	for my $val (@rest)
	{
        next if ! is_number($val);
		$ret = $val if($val > $ret);
	}	
    $ret = '' if ! defined $ret || ! is_number($ret);
	return $ret;
}

sub min
{
	my ($ret, @rest)  = @_;	
	for my $val (@rest)
	{
        next if ! is_number($val);
		$ret = $val if($val < $ret);
	}
	$ret = '' if ! defined $ret || ! is_number($ret);
	return $ret;
}


sub pow
{
	my ($num, $pnum)  = @_;			
	return $num**$pnum;
}

sub sqrt
{
	my ($num)  = @_;
    my $val = $::PDD_NULLVAL;
    if (is_number($num) && $num >= 0) {
        $val = $num**(1/2);
    }
	return $val;
}



sub odd_split
{
	my ($delimiter, $expr)  = @_;
	return split($delimiter, $expr, -1);
}


sub odd_join
{
	my ($delimiter, @elems)  = @_;		
	return  join($delimiter, @elems);
}


sub ends_with
{
	my ($string, $expr)  = @_;
    my $status = 0;
    $status = 1 if $string =~ m/$expr$/;
    return $status;
}


sub is_blank
{
	my ($string)  = @_;	
    return 1 unless defined $string;
	return $string  if( defined( $::PDD_NULLVAL ) &&  lc($::PDD_NULLVAL) eq lc($string));	
	my $status = 0;
    $status = 1 if $string =~ m/^\s*$/mg;
	return $status;
}






sub convert_unicode
{
	my $value = shift;	
	my $ret = chr($value);	
	return chr($value);
}


sub clean_unicode_str
{
	my $string = shift @_;		
	$string =~ s/(\\u(\d{4}))/convert_unicode($2)/eg;
	return $string;
}



sub concat
{
	my @params = @_;
	my $ret = "";	
	for my $val (@params)
	{		
		$ret = $ret . clean_unicode_str($val);
	}	
	return $ret;	 
}




sub string_char
{
	my ($input, $pos) = @_;
	if( $pos > length($input) ) {	
		return $::PDD_NULLVAL;
	}else{
		my $nth =  substr($input, $pos-1, 1);
		return ord($nth);
	}
}


sub upper
{
	my ($input) = @_;	
	return uc($input);
}


sub lower
{
	my ($input) = @_;	
	return lc($input);
}

sub swapcase
{
	my ($input) = @_;	
    $input =~ tr/A-Za-z/a-zA-Z/;
	return $input;
}

sub substring
{
	my ($expr, $strat, $length)  = @_;
	return substr($expr, $strat, $length);
}

sub char
{
	my ($input) = @_;	
	return chr($input);	 
}

sub unicode_char
{
	my ($input) = @_;	
	return chr($input);	 
}

sub trim
{
	my ($in) = @_;
	$in =~ s/^\s+|\s+$//mg;
	return $in;
}

sub ltrim
{
	my ($in) = @_;
	$in =~ s/^\s+//mg;
	return $in;
}

sub rtrim
{
	my ($in) = @_;
	$in =~ s/\s+$//mg;
	return $in;
}


sub trim0
{
	my ($in) = @_;
	$in =~ s/^[0]+//;
	$in =~ s/[0]+$//;
	return $in;
}

sub ltrim0
{
	my ($in) = @_;
	$in =~ s/^[0]+//;
	return $in;
}

sub rtrim0
{
	my ($in) = @_;
	$in =~ s/[0]+$//;
	return $in;
}


sub srow
{
	my ($data, $regexPattern, $replacement, $boolCaseInSensitive) = @_;		
	return search_replace($data, $regexPattern, $replacement, $boolCaseInSensitive);
}

#Search replace function
sub scol
{
	my ($data, $regexPattern, $replacement, $boolCaseInSensitive) = @_;		
	return search_replace($data, $regexPattern, $replacement, $boolCaseInSensitive);
}

sub search_replace
{
	my ($data, $regexPattern, $replacement, $boolCaseInSensitive) = @_;	
	if (defined($boolCaseInSensitive) && $boolCaseInSensitive){		
		$data =~ s/$regexPattern/$replacement/ig
	}else{		
		$data =~ s/$regexPattern/$replacement/g
	}	
	return $data;
}

sub unix2dos
{
	my ($in) = @_;
	$in =~ s/\r\n|\n/\r\n/g;	
	return $in;
}

sub dos2unix
{
	my ($in) = @_;
	$in =~ s/\r\n/\n/g;	
	return $in;
}

sub ifnull
{
	my ($nullablecol, $replacement) =  @_;
	
	if(defined($::PDD_NULLVAL) && defined($nullablecol) && lc($nullablecol) eq lc($::PDD_NULLVAL)){
		return $replacement;
	}elsif($nullablecol eq "" && defined($::PDD_BLANK_AS_NULL) ){
		return $replacement;
	}else{		
		return $nullablecol;
	}	
}


sub prefix
{
	my ($data, $pre) =  @_;	
	$pre = '' unless defined $pre;
	return $pre . $data;
}
sub postfix
{
	my ($data, $post) =  @_;	
	$post = '' unless defined $post;
	return  $data . $post;
}

sub ltrunc
{
	my ($data, $num) =  @_;   
	$data = substr $data, $num;
    $data = '' unless defined $data;
	return $data;
}

sub rtrunc
{
	my ($data, $num) =  @_;
    
    if ($num =~ m/^s*[-]/) {
        $num =~ s/^s*[-]//;
    }
    else {
        $num = '-'.$num;
    }
	$data = substr $data, 0, $num;
    $data = '' unless defined $data;
	return $data;
}



sub current_date
{
	my $format = shift;
	$format = "yyyyMMdd" unless defined $format;
	my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) =  localtime;	
	my %dh = {};
	$dh{'YEAR'} = $year+1900;
	$dh{'MONTH'} = $mon;
	$dh{'DAY'} = $mday;
	$dh{'HOUR'} = $hour;
	$dh{'MINUTE'} = $min;
	$dh{'SECOND'} = $sec;
	$dh{'MILLISECOND'} = 0;
	$dh{'DAY_OF_WEEK'} = $wday;
	$dh{'DAY_OF_YEAR'} = $yday;
	$dh{'IS_DST'} = $isdst;
	my $dvalref = \%dh;
	my $dout = $format;	
	$dout =~ s/(MON|Y{1,4}|M{1,4}|H{1,2}|D{1,2}|S{1,3}|a)/format_date_part($1, $dvalref)/ieg;	
	return $dout;		
}


sub convert_date
{
	my ($date_in, $in_format, $out_format) = @_;
	my $dfRef = [];	
	my $rin_format = $in_format;
	$rin_format =~ s/(MON|Y{1,4}|M{1,4}|H{1,2}|D{1,2}|S{1,3}|a)/reformat_date_pattern($1, $dfRef)/ieg;		
	my @mv = $date_in =~ /$rin_format/;
	my %dh = {};
	if(scalar @mv){
		my $pos = 0;
		foreach(@{$dfRef})
		{
			my $AB = $_;
			my $val = $mv[$pos++];
			if( $AB =~ /Y4/){				
				$dh{'YEAR'} = $val;
			}elsif( $AB =~ /Y2/){							
				$dh{'YEAR'} = $val>50?(1900+$val):(2000+$val);
			}elsif( $AB =~ /M2|M1/){						
				$dh{'MONTH'} = $val-1;
			}elsif( $AB =~ /M3/){								
				my $m = firstidx {$_ =~ /$val/i } @MONTH_ABBREVIATIONS;
				$dh{'MONTH'} = $m;				
			}elsif( $AB =~ /d2|d1/){					
				$dh{'DAY'} = $val;				
			}elsif( $AB =~ /H2|H1/i){						
				$dh{'HOUR'} = $val;				
			}elsif( $AB =~ /m2|m1/){						
				$dh{'MINUTE'} = $val;				
			}elsif( $AB =~ /s2|s1/){						
				$dh{'SECOND'} = $val;				
			}elsif( $AB =~ /S3|S1/){						
				$dh{'MILLISECOND'} = $val;				
			}elsif( $AB =~ /A/i){						
				$dh{'A'} = $val;				
			}
		}			
	}else{
		print "$date_in format not match $rin_format\n";
	}
	my $dvalref = \%dh;
	my $dout = $out_format;	
	$dout =~ s/(MON|Y{1,4}|M{1,4}|H{1,2}|D{1,2}|S{1,3}|a)/format_date_part($1, $dvalref)/ieg;	
	return $dout;
}

sub format_date_part
{
	my ($dp, $daRef)  = @_;	
	my %dval = %{$daRef};
	my $ret = "";
	if( $dp =~ /^(yyyy|YYYY|y|Y)$/ ){
		$ret = (defined $dval{'YEAR'})? $dval{'YEAR'}:"0000";
	}elsif( $dp =~ /^(yy|YY)$/ ){
		my $y = (defined $dval{'YEAR'})? $dval{'YEAR'}:0;
		$ret = sprintf("%02d", $y % 100);
	}elsif( $dp =~ /^MM$/ ){
		my $m = (defined $dval{'MONTH'})? $dval{'MONTH'}:0;
		$ret = sprintf("%02d", $m +1);
	}elsif( $dp =~ /^M$/ ){
		my $m = (defined $dval{'MONTH'})? $dval{'MONTH'}:0;
		$ret = sprintf("%d", $m +1);
	}elsif( $dp =~ /^(MMM|MON)$/i ){
		my $m = (defined $dval{'MONTH'})? $dval{'MONTH'}:0;
		$ret = $MONTH_ABBREVIATIONS[$m];
	}elsif( $dp =~ /^(MMMM|MONTH)$/i ){
		my $m = (defined $dval{'MONTH'})? $dval{'MONTH'}:0;
		$ret = $MONTH_NAMES[$m];
	}elsif( $dp =~ /^(dd)$/i ){
		my $d = (defined $dval{'DAY'})? $dval{'DAY'}:0;
		$ret = sprintf("%02d", $d);
	}elsif( $dp =~ /^(d)$/i ){
		my $d = (defined $dval{'DAY'})? $dval{'DAY'}:0;
		$ret = sprintf("%d", $d );
	}elsif( $dp =~ /^(HH)$/ ){
		my $h = (defined $dval{'HOUR'})? $dval{'HOUR'}:0;
		$h += 12 if( defined $dval{'A'} && $dval{'A'} =~ /PM/i);
		$ret = sprintf("%02d", $h );		
	}elsif( $dp =~ /^(H)$/ ){
		my $h = (defined $dval{'HOUR'})? $dval{'HOUR'}:0;
		$h += 12 if( defined $dval{'A'} && $dval{'A'} =~ /PM/i);
		$ret = sprintf("%d", $h );
	}elsif( $dp =~ /^(hh)$/ ){
		my $h = (defined $dval{'HOUR'})? $dval{'HOUR'}:0;
		$h += 12 if( defined $dval{'A'} && $dval{'A'} =~ /PM/i);
		$ret = sprintf("%02d", $h%12 );
	}elsif( $dp =~ /^(h)$/ ){
		my $h = (defined $dval{'HOUR'})? $dval{'HOUR'}:0;
		$h += 12 if( defined $dval{'A'} && $dval{'A'} =~ /PM/i);
		$ret = sprintf("%d", $h );
	}elsif( $dp =~ /^(mm)$/i ){
		my $m = (defined $dval{'MINUTE'})? $dval{'MINUTE'}:0;		
		$ret = sprintf("%02d", $m);
	}elsif( $dp =~ /^(m)$/i ){
		my $m = (defined $dval{'MINUTE'})? $dval{'MINUTE'}:0;	
		$ret = sprintf("%d", $m);
	}elsif( $dp =~ /^(ss)$/ ){
		my $s = (defined $dval{'SECOND'})? $dval{'SECOND'}:0;	
		$ret = sprintf("%02d", $s);
	}elsif( $dp =~ /^(s)$/ ){
		my $s = (defined $dval{'SECOND'})? $dval{'SECOND'}:0;
		$ret = sprintf("%d", $s);
	}elsif( $dp =~ /^(SSS)$/i ){
		my $ms = (defined $dval{'MILLISECOND'})? $dval{'MILLISECOND'}:0;
		$ret = sprintf("%03d", $ms);
	}elsif( $dp =~ /^(S)$/i ){
		my $ms = (defined $dval{'MILLISECOND'})? $dval{'MILLISECOND'}:0;
		$ret = sprintf("%d", $ms);
	}elsif( $dp =~ /^(a)$/i ){
		if(defined $dval{'A'}){
			$ret = $dval{'A'}
		}elsif(defined $dval{'HOUR'} && $dval{'HOUR'}>=12){
			$ret = "PM";
		}else{
			$ret = "AM";
		}
	}else{
		$ret = $dp;
	}
	return $ret;
}


sub replace_date_val
{
	my $dp = shift;
	my $dhRef = shift;
	my %dh = %{$dhRef};
	my $ret = "";
	
	return $ret;
}


sub reformat_date_pattern
{
	my $dp = shift;
	my $dfRef = shift;
	my @df = @{$dfRef};
	my $ret = "";
	if( $dp =~ /^(yyyy|YYYY|y|Y)$/ ){
		$ret = "(\\d{4})";
		$$dfRef[scalar @df] = "Y4";
	}elsif( $dp =~ /^(yy|YY)$/ ){
		$ret = "(\\d{2})";
		$$dfRef[scalar @df] =  "Y2";
	}elsif( $dp =~ /^MM$/ ){
		$ret = "(\\d{2})";
		$$dfRef[scalar @df] = "M2";
	}elsif( $dp =~ /^M$/ ){
		$ret = "(\\d{1,2})";
		$$dfRef[scalar @df] =  "M1";
	}elsif( $dp =~ /^(MMM|MON)$/i ){
		$ret = "(\\w{3})";
		$$dfRef[scalar @df] =  "M3";
	}elsif( $dp =~ /^(dd)$/ ){
		$ret = "(\\d{2})";
		$$dfRef[scalar @df] =  "d2";
	}elsif( $dp =~ /^(d)$/ ){
		$ret = "(\\d{1,2})";
		$$dfRef[scalar @df] =  "d1";
	}elsif( $dp =~ /^(HH)$/ ){
		$ret = "(\\d{2})";
		$$dfRef[scalar @df] =  "H2";
	}elsif( $dp =~ /^(H)$/ ){
		$ret = "(\\d{1,2})";
		$$dfRef[scalar @df] =  "H1";
	}elsif( $dp =~ /^(hh)$/ ){
		$ret = "(\\d{2})";
		$$dfRef[scalar @df] =  "h2";
	}elsif( $dp =~ /^(h)$/ ){
		$ret = "(\\d{1,2})";
		$$dfRef[scalar @df] =  "h1";
	}elsif( $dp =~ /^(mm)$/i ){
		$ret = "(\\d{2})";
		$$dfRef[scalar @df] =  "m2";
	}elsif( $dp =~ /^(m)$/i ){
		$ret = "(\\d{1,2})";
		$$dfRef[scalar @df] =  "m1";
	}elsif( $dp =~ /^(ss)$/ ){
		$ret = "(\\d{2})";
		$$dfRef[scalar @df] =  "s2";
	}elsif( $dp =~ /^(s)$/ ){
		$ret = "(\\d{1,2})";
		$$dfRef[scalar @df] = "s1";
	}elsif( $dp =~ /^(SSS)$/ ){
		$ret = "(\\d{3})";
		$$dfRef[scalar @df] =  "S3";
	}elsif( $dp =~ /^(S)$/ ){
		$ret = "(\\d{1,3})";
		$$dfRef[scalar @df] = "S1";
	}elsif( $dp =~ /^(a)$/i ){
		$ret = "([AP]M)";
		$$dfRef[scalar @df] = "a";
	}else{
		$ret = $dp;
	}	
	return $ret;
}



sub lastcol
{
	my ($dataRef, $ridx) = @_;	
	my $len = scalar(@{$dataRef});
	$ridx = -1 unless defined $ridx;
	my $outidx = $len + $ridx;
	return $$dataRef[$outidx];	
}


sub lookup
{
	my ($key, $hashRef) = @_;
	return $hashRef->{$key} || "";
}

#This split function should be able to handle standard CSV file format
sub ext_split
{
	my @ret = ();
	my $idx = 0;
	my ($delim, $string) = @_;	
	my $quoteVal = ord("\"");
	my $delimVal = ord($delim);
	my $escapeVal = ord("\\");
	my @strchars = split (//, $string, -1);	
	my $len = scalar(@strchars);	
	my $buf = "";
	my $pos;
	for($pos=0; $pos<$len; $pos++)
	{		
		my $c = $strchars[$pos];		
		my $cval = ord($c);
		if($cval == $quoteVal && length($buf)==0){
					for($pos++; $pos<$len; $pos++){
						my $c2 = $strchars[$pos];						
						my $c2Val = ord($c2);
						if($c2Val == $quoteVal ){							
							last;
						}elsif($c2Val == $escapeVal){
							if($pos<$len-1){
								my $ncVal = ord($strchars[$pos+1]);
								if($ncVal==$quoteVal){
									$pos++;
									$buf .= "\"";
								}elsif($ncVal==$escapeVal){
									$pos++;
									$buf .= "\\";
								}elsif($ncVal==ord("n")){
									$pos++;
									$buf .= "\n";
								}elsif($ncVal==ord("t")){
									$pos++;
									$buf .= "\t";
								}elsif($ncVal==ord("r")){
									$pos++;
									$buf .= "\r";
								}elsif($ncVal==ord("b")){
									$pos++;
									$buf .= "\b";
								}elsif($ncVal==ord(",")){
									$pos++;
									$buf .= ",";
								}else{
									$buf .= $c2;
								}
							}else{
								$buf .= $c2;
							}							
						}else{
							$buf .= $c2;
						}
					}	
							
		}elsif($cval == $delimVal || $pos==($len-1)){
			$ret[$idx++] = $buf;			
			$buf = "";				
		}else{		
			$buf .= $c;
		}
	}
	return @ret;
}


1;
