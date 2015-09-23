#!/usr/bin/env perl
###################################################################
#  This is generated skeleton Test::More::Behaviour test spec file
#  Please replace context and it text with meaningful text that can 
#  describe your module or scripts functions.
#  
#  @author Sunny Liu
###################################################################


use strict;
use warnings;

use Test::More::Behaviour;
use PDD::script::PDDTrnsfrmPerlFuncts qw(lookup odd_split ext_split lastcol convert_date 
				current_date scol prefix postfix ltrunc rtrunc 
				ifnull dateString srow unix2dos dos2unix trim0 ltrim0 rtrim0 trim 
				ltrim rtrim upper lower lc_first uc_first swapcase 
				substring char string_char clean_unicode_str 
				concat odd_join ends_with is_blank 
				reg_get_match reg_get_matches reg_get_length_matches re_index 
				ceil abs floor max min pow tally random
				sqrt decimal_truncate decimal_round 
				sample_col sample_email 
				sample_ssn sample_ccn odd_random_sample sequence group_last group_first 
				group_min group_max group_avg group_count group_reset 
				group_sum force_error force_abort is_null); 

BEGIN {
	use_ok('PDD::script::PDDTrnsfrmPerlFuncts');
};

sub before_all {
        #before_all: once before everything
};

sub after_all {
        #after_all: once after everything
};

sub before_each {
		#before_each: once before every test case
};

sub after_each {
		#after_each: once after every test case
};

describe 'PDDTrnsfrmPerlFuncts' => sub {
		
		context 'function is_null ' => sub {	
			it 'should return 1 if null' => sub {				 
				 my $expect = 1;
				 my $calculatedResult = is_null('NULL');	
				 is($calculatedResult, $expect, 'return 1 if null');
			};	
			it 'should return 1 if ""' => sub {				 
				 my $expect = 1;
				 my $calculatedResult = is_null('');	
				 is($calculatedResult, $expect, 'return 1 if null');
			};	

			it 'should return 0 if "VALUE"' => sub {				 
				 my $expect = 0;
				 my $calculatedResult = is_null('VALUE');	
				 is($calculatedResult, $expect, 'return 0 if null');
			};		
		};
		
		
		
		context 'function group_first ' => sub {	
			it 'should be able to return the first non-NULL input value' => sub {				 
				 my $expect = 'Sunny';
				 my $calculatedResult = group_first('$1', 'NULL');		
					$calculatedResult = group_first('$1', 'Sunny');	
					$calculatedResult = group_first('$1', 'Liu');
					$calculatedResult = group_first('$1', 'John');	
				 is($calculatedResult, $expect, 'return the first non-NULL input value Sunny');
			};				
		};
		context 'function group_last ' => sub {	
			it 'should be able to return the last non-NULL input value' => sub {				 
				 my $expect = 'John';
				 my $calculatedResult = group_last('$1', 'NULL');		
					$calculatedResult = group_last('$1', 'Sunny');	
					$calculatedResult = group_last('$1', 'Liu');
					$calculatedResult = group_last('$1', 'John');	
				 is($calculatedResult, $expect, 'return the first non-NULL input value Sunny');
			};				
		};
		
		context 'function group_count ' => sub {	
			it 'should be able to return the count of all non-NULL input values' => sub {				 
				 my $expect = 3;
				 my $calculatedResult = group_count('$1', 'NULL');		
					$calculatedResult = group_count('$1', 'Sunny');	
					$calculatedResult = group_count('$1', 'Liu');
					$calculatedResult = group_count('$1', 'John');	
				 is($calculatedResult, $expect, 'return the count of all non-NULL input values');
			};				
		};
		
		context 'function group_avg ' => sub {	
			it 'should be able to return the average of all non-NULL input values' => sub {				 
				 my $expect = 200;
				 my $calculatedResult = group_avg('$1', 'NULL');		
					$calculatedResult = group_avg('$1', '100');	
					$calculatedResult = group_avg('$1', '200');
					$calculatedResult = group_avg('$1', '500');	
				 is($calculatedResult, $expect, 'return the average of all non-NULL input values');
			};				
		};
		
		context 'function group_sum ' => sub {	
			it 'should be able to return the sum of all non-NULL input values' => sub {				 
				 my $expect = 800;
				 my $calculatedResult = group_sum('$1', 'NULL');		
					$calculatedResult = group_sum('$1', '100');	
					$calculatedResult = group_sum('$1', '200');
					$calculatedResult = group_sum('$1', '500');	
				 is($calculatedResult, $expect, 'return the sum of all non-NULL input values');
			};				
		};
		
		context 'function group_max ' => sub {	
			it 'should be able to return the maximum non-NULL input value' => sub {				 
				 my $expect = 100;
				 my $calculatedResult = group_max('$1', 'NULL');		
					$calculatedResult = group_max('$1', '50');	
					$calculatedResult = group_max('$1', '100');
					$calculatedResult = group_max('$1', '60');	
				 is($calculatedResult, $expect, 'return the maximum non-NULL input value');
			};				
		};
		
		context 'function group_min ' => sub {	
			it 'should be able to return the minimum non-NULL input value' => sub {				 
				 my $expect = 50;
				 my $calculatedResult = group_min('$1', 'NULL');		
					$calculatedResult = group_min('$1', '50');	
					$calculatedResult = group_min('$1', '100');
					$calculatedResult = group_min('$1', '60');	
				 is($calculatedResult, $expect, 'return the minimum non-NULL input value');
			};				
		};
		
};



done_testing();

1;

