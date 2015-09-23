package PDD::script::PDDScriptCompiler;

use Parse::RecDescent;
use PDD::commons::ClassUtil qw(has_func_defined);

my $pddScriptGrammar = q{

number_literal: /([-+])?((\d+(\.\d+)?([Ee][-+]?\d+)?)|(\.\d+)([Ee][-+]?\d+)?)/

string_literal: "'" /([^']|'')*/ "'" {  my $str = $item[2]; $str =~ s/''/'/g;  $return="'" . PDD::script::PDDScriptCompiler::quoteString($str) . "'";}
				| '"' /[^"]*/ '"' {  my $str = $item[2]; $str =~ s/''/'/g;  $return="'" . PDD::script::PDDScriptCompiler::quoteString($str) . "'";}


single_field_selection: /\$\d+/

range_field_selection: '[' /\$\d+/ <commit> '-' /\$\d+/ ']'
					  {
						 #PDD::script::PDDScriptCompiler::debug_rule(@item);
						 my $start_idx = substr($item[2], 1);
						 my $end_idx = substr($item[5], 1);
						 my @elems;						
						 for(my $pos=$start_idx; $pos<=$end_idx; $pos++)
						 {
							push(@elems, '$' . $pos);													
						 }						
						 $return=\@elems;
					  }

identifier_expr: /[A-Za-z][A-Za-z0-9_\.]*/

global_setting_expr: identifier_expr '=' /.*/
					{
						PDD::script::PDDScriptCompiler::emitGlobalSetting($thisline, $thiscolumn, $text, $item[1], $item[3]);
						$return=1;
					}
				
substraction_expr: addition_expr(s /-/)
				{
					#PDD::script::PDDScriptCompiler::debug_rule(@{$item[1]});
					my $r = join('+', @{$item[1]});					
					#print "substraction_expr: $r\n";
					$return=$r;
				}
				
addition_expr: division_expr(s /\+/)
				{
					#PDD::script::PDDScriptCompiler::debug_rule(@{$item[1]});
					my $r = join('/', @{$item[1]});					
					$return=$r;
				}

division_expr: multiply_expr(s /\//)
			   {
					#PDD::script::PDDScriptCompiler::debug_rule(@{$item[1]});
					my $r = join('/', @{$item[1]});					
					$return=$r;
				}
multiply_expr: atomic_field(s /\*/)
						{
							#PDD::script::PDDScriptCompiler::debug_rule(@{$item[1]});
							my $r = join('*', @{$item[1]});							
							$return=$r;
						}


filter_expr: /if/i <commit> '(' logical_expr ')' 
			{				
				PDD::script::PDDScriptCompiler::emitSelectionFilter($thisline, $thiscolumn, $text, $item[4]);
				$return=$item[4];				
			}

logical_expr: logical_and_expr (/or|\|\|/i logical_and_expr )(s?)
				{
					 my @logicExpr = ();
					 push(@logicExpr, $item[1]);
					 foreach(@{$item[2]}){
						push(@logicExpr, $_);
					 }					 
					 $return=join(' || ', @logicExpr);
					 if( $return !~ /^[(].+[)]$/){
						$return= '(' . $return . ')';
					 }
				}


logical_and_expr: compare_expr (/and|&&/i compare_expr)(s?)
				{
					 my @logicExpr = ();
					 push(@logicExpr, $item[1]);
					 foreach(@{$item[2]}){
						push(@logicExpr, $_);
					 }					 
					 $return=join(' && ', @logicExpr);
					 if( $return !~ /^[(].+[)]$/){
						$return= '(' . $return . ')';
					 }
				}

compare_expr: logical_atomic_expr between_expr	
				{ 				
					$return = '(' . $item[1] . '>=' . $item[2][0] . ' && ' .  $item[1] . '<=' . $item[2][1] . ')'; 
				}			
			 | logical_atomic_expr logical_cmp_trail(?)
			 {
				#PDD::script::PDDScriptCompiler::debug_rule(@{$item[2]});				
				$return= $item[1];
				if(defined($item[2]) && defined($item[2][0])){					
					$return=  $item[1] . $item[2][0][0] . $item[2][0][1];					
				}	
			 }

logical_cmp_trail: comp_operator logical_atomic_expr
				  {
					 my @cmpParams = ();
					 push(@cmpParams, $item[1]);
					 push(@cmpParams, $item[2]);
					 $return = \@cmpParams; 
				  }
			 
logical_atomic_expr: math_expr {$return=$item[1];}
					| '(' logical_expr ')' {$return=$item[2];}


like_operator: (/not/i)(?) /like/i
				{
					$return = '~';
				}

between_expr: /between/i <commit> math_expr /and/i math_expr
			  {
					my @betweenParams = ();
					push(@betweenParams, $item[3]);
					push(@betweenParams, $item[5]);
					$return = \@betweenParams;
			  }
					
comp_operator: m/!~|~|<>|!=|>=|>|==|=|<=|</
			  | like_operator

atomic_field: number_literal {$return=$item[1];}
			  | string_literal {$return=$item[1];}
			  | single_field_selection {$return=$item[1];}
			  | function_expr {$return=$item[1];}
			  | '(' math_expr ')' {$return=$item[2];}
			  			  
math_expr: substraction_expr {$return=$item[1];}
		   


function_signature: '(' ')' 
						{
							my @params = ();
							$return = \@params;
						}
					| '(' math_expr (',' math_expr)(s?) ')'
						{
							my @params = ();
							my $fp = $item[2];							
							push(@params, $fp);
							foreach(@{$item[3]}){
								push(@params, $_);								
							}
							$return = \@params;
						}
		  
function_expr: identifier_expr function_signature
			   {
					my $validatedFunction = PDD::script::PDDScriptCompiler::transformFunction($thisline, $thiscolumn, $text, $item[1], $item[2]);
					$return = ($validatedFunction)?$validatedFunction:($item[1] . "(" . join(',', @{$item[2]})  .")" );					
			   }
			   | /@(\w+|'[^']+')/
			   {				
				 my $param = $1;
				 $param =~ s/^'//;
				 $param =~ s/'$//;
				 $return="current_date('$param')";
			   }


case_expr: /case/i <commit> case_when_expr(s) case_else_expr(?) /end/i
		  {
			my $ret = PDD::script::PDDScriptCompiler::transformCaseSatement($thisline, $thiscolumn, $text, $item[3], $item[4][0]);
			if($ret){
				$return=$ret;
			}else{
				$return = 'case ';
				foreach(@{$item[3]}){
					my @whenClause = @{$_};
					$return .= "when $whenClause[0] then  $whenClause[1] "
				}
				$return .= 'end';				
			}
		  }

case_when_expr: /when/i logical_expr /then/i math_expr
				{
					#PDD::script::PDDScriptCompiler::debug_rule(@item);
					my @caseWhen = ();
					push(@caseWhen, $item[2]);
					push(@caseWhen, $item[4]);
					$return=\@caseWhen;
				}
case_else_expr: /else/i math_expr
				{
					#PDD::script::PDDScriptCompiler::debug_rule(@item);
					$return=$item[2];
				}
				
field_expression: case_expr	{  PDD::script::PDDScriptCompiler::emitCaseSelection($thisline, $thiscolumn, $text, $item[1]); $return=$item[1];}			  
				  | range_field_selection {PDD::script::PDDScriptCompiler::emitRangeSelection($thisline, $thiscolumn, $text, $item[1]); $return=$item[1];}
				  | math_expr {PDD::script::PDDScriptCompiler::emitNormalSelection($thisline, $thiscolumn, $text, $item[1]); $return=$item[1];}


selection_start: '[extract]'

selection_end: '[/extract]'

field_selection_list: field_expression(s /,/)
					{
						$return=1;
					}

field_all_selection: '*'

transform_selection: field_all_selection					
					| field_selection_list
					

transform_body:  selection_start <commit> filter_expr(?) transform_selection selection_end


compileUnit:  global_setting_expr(s?)  transform_body  global_setting_expr(s?) /\Z/
				{					
					1;
				}
				| <error>
	
};

my $parserHandlerRef = undef;

#Leave room for dynamic compiling
sub new
{
	 my ($class, $grammar) = @_;		
	 $grammar = $pddScriptGrammar unless defined $grammar;
	 my $self = {
		_debug => 0,
		_dev_dry_run => 0,	
		_pdd_script_grammar => $grammar,
	 };	 
	 bless $self, $class;		 
	 
	 return $self;	 
}


sub compile
{	 
	 my ($self, $pddscript, $handler) = @_;		
	 $parserHandlerRef = $handler;
	 if($self->debug()){
		$::RD_TRACE = 1;
		$::RD_HINT = 1;
	 }else{
		$::RD_TRACE = undef;
		$::RD_HINT = undef;
	 }
	 my $parser = Parse::RecDescent->new($self->{_pdd_script_grammar});	 
	 defined $parser->compileUnit($pddscript) or die "invalid rules input!\n";	 
}



sub emitGlobalSetting
{
	my ($line, $column, $remainText, $key, $value) = @_;
	if(has_func_defined($parserHandlerRef, 'emitGlobalSetting')){
		$parserHandlerRef->emitGlobalSetting($line, $column, $remainText, $key, $value);
	}	
}



sub emitSelectionFilter
{
	my ($line, $column, $remainText, $selectText) = @_;
	if(has_func_defined($parserHandlerRef, 'emitSelectionFilter')){
		$parserHandlerRef->emitSelectionFilter($line, $column, $remainText, $selectText);
	}
}


sub emitRangeSelection
{
	my ($line, $column, $remainText, $rangeRef) = @_;	
	if(has_func_defined($parserHandlerRef, 'emitRangeSelection')){
		$parserHandlerRef->emitRangeSelection($line, $column, $remainText, $rangeRef);
	}
}

sub emitCaseSelection
{
	my ($line, $column, $remainText, $selectText) = @_;	
	if(has_func_defined($parserHandlerRef, 'emitCaseSelection')){
		$parserHandlerRef->emitCaseSelection($line, $column, $remainText, $selectText);
	}
}

sub emitNormalSelection
{
	my ($line, $column, $remainText, $selectText) = @_;	
	if(has_func_defined($parserHandlerRef, 'emitNormalSelection')){
		$parserHandlerRef->emitNormalSelection($line, $column, $remainText, $selectText);
	}
}



sub transformFunction
{
	my ($line, $column, $remainText, $functionName, $parameRef) = @_;			
	if(has_func_defined($parserHandlerRef, 'transformFunction')){
		return $parserHandlerRef->transformFunction($line, $column, $remainText, $functionName, $parameRef);
	}
	return 0;
}


sub transformCaseSatement
{
	my ($line, $column, $remainText, $whenStmts, $elseStmt) = @_;
	if(has_func_defined($parserHandlerRef, 'transformCaseSatement')){
		return $parserHandlerRef->transformCaseSatement($line, $column, $remainText, $whenStmts, $elseStmt);
	}	
	return 0;
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


sub quoteString
{
	my ($str) = @_;
	$str =~ s/'/\\'/g;
	
	return $str;
}


sub debug_rule
{	
	my @matchItems = @_;
	my $matchCnt = scalar @matchItems;
	my $pos = 1;
	while($pos < $matchCnt)
	{
		last unless $matchItems[$pos];
		print "$matchItems[0]:$pos:" . $matchItems[$pos] . "\n";						
		$pos += 1;
	}
	return 1;
}

1;