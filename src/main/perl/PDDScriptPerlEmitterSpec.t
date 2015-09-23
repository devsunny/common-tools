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
	use_ok('PDD::script::PDDScriptPerlEmitter');
};



describe 'PDD::script::PDDScriptPerlEmitter' => sub {
		
		context 'emitGlobalSetting ' => sub {	
			my $perl = new PDD::script::PDDScriptPerlEmitter();
			
			it 'should return "$line = dos2unix($line);" if dos2unix=1' => sub {				 
				 my $expect = '$line = dos2unix($line);' . "\n";
				 my $calculatedResult = $perl->emitGlobalSetting(123, 123, "test all", "dos2unix", "1");	
				 is($calculatedResult, $expect, 'return "$line = dos2unix($line);" if dos2unix=1');
			};	

			it 'should return "$line = unix2dos($line)" if unix2dos=1' => sub {				 
				 my $expect = '$line = unix2dos($line);' . "\n";
				 my $calculatedResult = $perl->emitGlobalSetting(123, 123, "test all", "unix2dos", "1");	
				 is($calculatedResult, $expect, 'return "$line = unix2dos($line)"; if unix2dos=1');
			};	
			
			it 'should return "$line = unix2dos($line)" if indelim=","' => sub {				 
				 my $expect = '$FS=",";' . "\n";
				 my $calculatedResult = $perl->emitGlobalSetting(123, 123, "test all", "indelim", ",");	
				 is($calculatedResult, $expect, 'return "$line = unix2dos($line)"; if unix2dos=1');
			};	
			
			
		};
		
		context 'transformCaseSatement ' => sub {	
			my $perl = new PDD::script::PDDScriptPerlEmitter();			
			it 'should return transform if-elsif-else perl statement' => sub {				 
				 my @when1 = ('$1==2 && $13>1.5 && $6=="ABC"', '123');
				 my @when2 = ('$1==4 && $6=="ABC"', '23');
				 my @when3 = ('$1==5', '126');
				 my $else = '122';
				 my @when = (\@when1, \@when2, \@when3);
				 my $expect = "\n". 'if($1==2 && $13>1.5 && $6=="ABC"){ $case_var_01=123;}elsif($1==4 && $6=="ABC"){ $case_var_01=23;}elsif($1==5){ $case_var_01=126;}else { $case_var_01=122;}' . "\n";
				 my $calculatedResult = $perl->transformCaseSatement(123, 123, "test all", \@when, $else);	
				 is($calculatedResult, $expect, 'return transform if-elsif-else perl statement');
			};
			
		};
		
		context 'transformFunction ' => sub {	
			my $perl = new PDD::script::PDDScriptPerlEmitter();			
			it 'should return tally(\'$3\',$3) if \'tally\', \'$3\'' => sub {	
				  my @params = ('$3');
				 my $expect = 'tally(\'$3\',$3)';
				 my $calculatedResult = $perl->transformFunction(123, 123, "test all", 'tally', \@params);	
				 is($calculatedResult, $expect, 'return tally(\'$3\',$3) if \'tally\', \'$3\'');
			};
			
			it 'should return tally(\'$3\',$3) if \'tally\', \'$3\'' => sub {	
				  my @params = ('$3');
				 my $expect = 'tally(\'$3\',$3)';
				 my $calculatedResult = $perl->transformFunction(123, 123, "test all", 'tally', \@params);	
				 is($calculatedResult, $expect, 'return tally(\'$3\',$3) if \'tally\', \'$3\'');
			};
			
			
			
		};
		
		
				
};



done_testing();

1;

