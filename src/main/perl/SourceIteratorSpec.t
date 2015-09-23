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

BEGIN {
	use_ok('PDD::script::PDDTrnsfrmPerlFuncts');
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
			
			it 'should return 0 if "VALUE"' => sub {				 
				 my $expect = 0;
				 my $calculatedResult = is_null('VALUE');	
				 is($calculatedResult, $expect, 'return 0 if null');
			};		
		};
				
};



done_testing();

1;

