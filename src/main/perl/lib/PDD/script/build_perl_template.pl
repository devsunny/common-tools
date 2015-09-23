#!/usr/bin/env perl 
############################################################################################################################################
#
#           ODD Transform Script Compiler
#
############################################################################################################################################

package main;
use strict;
use FindBin;
use lib "$FindBin::Bin";

use List::MoreUtils qw( any none );
use Term::ANSIColor;
use Getopt::Long;
use Data::Dumper;
#use Switch;
use Text::Template;
use IO::File;
use Scalar::Util qw(looks_like_number);
use Cwd;
our $apphome = $FindBin::Bin;
our $program = 'build_transform';

my ($rules_file, $icommand_file, $no_execute, $help, $input_file, $output_file, $tlang, $filter_file);
GetOptions(
   'rules_file|r=s'     => \$rules_file,
   'icommand_file|f=s'  => \$icommand_file,
   'input_file|t=s'     => \$input_file,
   'output_file|l=s'    => \$output_file,
   'filter_file|x=s'    => \$filter_file,
   'tlang=s'     		=> \$tlang,
   'no_execute=i'       => \$no_execute,   
   'help|usage|?'       => \$help,
);

usage() if ! $rules_file || $help;
terminate("Rules File: [$rules_file] NOT FOUND") if ! -f $rules_file;
terminate("Rules File: [$rules_file] is EMPTY" ) if ! -s $rules_file;

my $PID            = $$;
my $pwd = cwd();


my $grammar = q {
{
	my $tallyCount = 0;
	my $formularCount = 0;	
	my $afterBody = 0;
	my $column_prefix = "\$dataArray";
	my $column_index_offset = 1;
	my $sequence_value = "\$ORECNUM";
	my $numberColumns = "\$numCols";
}

IgnoreableWhiteSpace: m/[ \t\r]*/ { 1; }

NewLine: <skip: q{}> IgnoreableWhiteSpace /\n/ { 1; }

LeftParen: '(' IgnoreableWhiteSpace { 1; }

RightParen: IgnoreableWhiteSpace ')' { 1; }

Param_Separator: IgnoreableWhiteSpace ',' IgnoreableWhiteSpace {1;}

Star: '*' { $return='0'; }

Integer: /\d+/m { $return=$item[1];}

Decimal: /(\+|-)?\d+(\.\d+)?/m { $return=$item[1];}	
			
SingleQuotedString: <skip: q{}> /[']/ m/[^\']*/ /[']/ 
						{ 
							$return=$item[3];
						}
DoubleQuotedString: <skip: q{}> /["]/ m/[^\"]*/ /["]/ 
						{ 
							#print "HELLO1:$item[3]\n";
							$return=$item[3];
						}

CommentLine: <skip: q{}> IgnoreableWhiteSpace '#' m/.*/ NewLine { 1; }
			{				
				$return=1;
			}

AtDatetime: <skip: q{}> IgnoreableWhiteSpace '@' m/(?:[ymd]{3,}|dmony)/i m/[ ]+/ '@' m/(12|24)(hr)?/  
			{ 			
					main::emitDateFunction($thisline, $thiscolumn, $text, '@', $item[4], $item[7]);							
					$return=$item[0];			
			}
AtDate: <skip: q{}> IgnoreableWhiteSpace '@' m/(?:[ymd]{3,}|dmony|((12|24)(hr)?))/i	
	 	   { 			
					
					main::emitDateFunction($thisline, $thiscolumn, $text, '@', $item[4]);		
					$return=$item[0];	 			
			}
AtStaticLine: <skip: q{}> IgnoreableWhiteSpace '@' m/.*/ 
			{
				main::emitStaticToken($thisline, $thiscolumn, $text, $item[4]);
				$return=$item[0];	
			}
			
String_Parameter: SingleQuotedString { $return=$item[1];}	
				  | DoubleQuotedString { $return=$item[1];}	


Select_By_Number: <skip: q{}> IgnoreableWhiteSpace  Integer   	 
				{
					
					my $colidx = $item[3] - $column_index_offset;
					if ( $item[3] == 0 ){
						$return="join(\"\$OFS\", \@dataArray)";
					}else{
						$return="$column_prefix\[$colidx\]";
					}
				}
				| <skip: q{}> IgnoreableWhiteSpace 	Star   
				{
					
					$return="join(\"\$OFS\", \@dataArray)";
				}

Select_By_Range:  <skip: q{}> IgnoreableWhiteSpace '[' IgnoreableWhiteSpace '$' Integer IgnoreableWhiteSpace '-' IgnoreableWhiteSpace '$' Integer IgnoreableWhiteSpace ']' 	
				{					
					for(my $pos=$item[6]; $pos<=$item[11]; $pos++)
					{
						 my $colidx = $pos - $column_index_offset;
						 main::emitPlainSelection($thisline, $thiscolumn, $text, "$column_prefix\[$colidx\]");
						 $return = $return . "$column_prefix\[$colidx\];\n"
					}	
					$return = $return;											
				}
				|
				<skip: q{}> IgnoreableWhiteSpace '[' IgnoreableWhiteSpace Integer IgnoreableWhiteSpace '-' IgnoreableWhiteSpace Integer IgnoreableWhiteSpace ']' 	
				{
					for(my $pos=$item[5]; $pos<=$item[9]; $pos++)
					{
						 my $colidx = $pos - $column_index_offset;
						 main::emitPlainSelection($thisline, $thiscolumn, $text, "$column_prefix\[$colidx\]");
						 $return = $return . "$column_prefix\[$colidx\];\n"
					}	
					$return = $return;				
				}
				
Int_Or_Func_Param:  m/\$\d+/ {   my $ret=main::formatParameter($item[1]); $return=$ret;}	
					| Decimal { $return="$item[1]";}	
					|  Function_Invocation { $return=$item[1];}	

					
All_Type_Func_Param: String_Parameter 
					{ 
						my $strVal = main::escapeString($item[1]) ;   
						$return=$strVal ; 

					}	
					| Int_Or_Func_Param { $return=$item[1];}


		
Static_Param: 	String_Parameter 
				{ 						
					my $strparam =  $item[1];
					$strparam =~ s/"/\\\\"/g;
					$return='"' . $strparam . '"';
				}	
				| Decimal { $return=$item[1];}	
									
Sequence_Function: <skip: q{}> IgnoreableWhiteSpace 'sequence' IgnoreableWhiteSpace '(' RightParen   
				   {						
						 my $valResult = main::functionValidation($thisline, $thiscolumn, $text, 'sequence');
						$return=$valResult || "$sequence_value"; 
				   }
				   | <skip: q{}> IgnoreableWhiteSpace 'sequence' 
					{												
						 my $valResult = main::functionValidation($thisline, $thiscolumn, $text, 'sequence');
						$return=$valResult || "$sequence_value"; 
					}
Lastcol_Function: <skip: q{}> IgnoreableWhiteSpace 'lastcol' IgnoreableWhiteSpace '(' RightParen  
				   {												
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, 'lastcol');
						$return=$valResult ||"$column_prefix\[ $numberColumns-1 \]";
				   }
				   | <skip: q{}> IgnoreableWhiteSpace 'lastcol' 
				   {												
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, 'lastcol');
						$return=$valResult || "$column_prefix\[\$numberColumns-1 \]"; 
				   }
				   
Random_Function:  <skip: q{}> IgnoreableWhiteSpace 'random' IgnoreableWhiteSpace '(' IgnoreableWhiteSpace	Integer(?)   RightParen
				{									
					my $rndParam= ($item[7][0])?"$item[7][0]":"1000000"; 
					my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $item[7][0]);
					$return=$valResult || "rand($rndParam)";
				}
				|  <skip: q{}> IgnoreableWhiteSpace 'random'
				{					
					my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3]);
					$return=$valResult || "rand(1000000)";
				}
 
UpperLower_Function: <skip: q{}> IgnoreableWhiteSpace m/upper|lower/ IgnoreableWhiteSpace '(' IgnoreableWhiteSpace	All_Type_Func_Param   RightParen 
				{
					my $colParam = $item[7];
					my $colstr = main::formatParameter($colParam );	
					my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $colstr);
					$return=$valResult || "$item[3]($colstr)";					
				}
				
Tally_Function: <skip: q{}> IgnoreableWhiteSpace m/tally/ IgnoreableWhiteSpace LeftParen Int_Or_Func_Param   RightParen 
				{
					$tallyCount++;	
					my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $item[6]);
					$return=$valResult || "tally($item[6])"; 
				}
				
Static_Function: <skip: q{}> IgnoreableWhiteSpace m/static/ IgnoreableWhiteSpace LeftParen	Static_Param   RightParen
				{
					$return= $item[6];
				}
				
Substring_Function: <skip: q{}> IgnoreableWhiteSpace m/substring|substr/ IgnoreableWhiteSpace LeftParen All_Type_Func_Param  Param_Separator  Int_Or_Func_Param  Param_Separator  Int_Or_Func_Param  RightParen 
					{
						my $colParam = $item[6];
						my $colstr = main::formatParameter($colParam );						
						my $offset = $item[8];
						$offset = $offset -1 if ( $offset =~ /\d+/);
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $colstr, $offset, $item[10]);
					    $return=$valResult || "$item[3]($colstr, $offset, $item[10])";						
					}

Convert_Date_Function: <skip: q{}> IgnoreableWhiteSpace m/convert_date/ IgnoreableWhiteSpace LeftParen Int_Or_Func_Param  Param_Separator  All_Type_Func_Param  Param_Separator  All_Type_Func_Param  RightParen 
					{
						
						my $colParam = $item[6];
						my $colstr = main::formatParameter($colParam );	
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $colstr, $item[8], $item[10]);
					    $return=$valResult || "$item[3]($colstr, $item[8], $item[10])";		
					}
					
Srow_Function:  <skip: q{}> IgnoreableWhiteSpace m/srow/ IgnoreableWhiteSpace LeftParen  All_Type_Func_Param  Param_Separator  All_Type_Func_Param  RightParen 
					{						
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $item[6], $item[8]);
					    $return=$valResult || "";		
					}					
Scol_Function:  <skip: q{}> IgnoreableWhiteSpace m/scol/ IgnoreableWhiteSpace LeftParen  Int_Or_Func_Param  Param_Separator  All_Type_Func_Param  Param_Separator  All_Type_Func_Param  RightParen 
					{
						my $colParam = $item[6];
						my $colstr = main::formatParameter($colParam );	
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $colstr, $item[8], $item[10]);
					    $return=$valResult ||  "$item[3]($colstr , $item[8], $item[10])";		
					}
SingleParam_Function: <skip: q{}> IgnoreableWhiteSpace m/abs|int|rtrim0|ltrim0|trim0|rtrim|ltrim|trim|ceil|random|srand|rand|ucfirst|lcfirst|swapcase|char|unicode_char|is_blank|sqrt|force_error|force_abort/ IgnoreableWhiteSpace LeftParen  Int_Or_Func_Param  RightParen 
					{
						my $colParam = $item[6];
						my $colstr = main::formatParameter($colParam );							
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $colstr);
					    $return=$valResult ||  "$item[3]($colstr)";		
					}
DoubleParam_Function:	 <skip: q{}> IgnoreableWhiteSpace m/ifnull|ltrunc|rtrunc|prefix|postfix|string_char|split|join|ends_with|pow|decimal_truncate|decimal_round|sequence|group_avg|group_count|group_first|group_last|group_min|group_max|group_sum/ IgnoreableWhiteSpace LeftParen  All_Type_Func_Param  Param_Separator  All_Type_Func_Param RightParen 
					{
						my $colParam = $item[6];
						my $colstr = main::formatParameter($colParam );	
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $colstr, $item[8]);
					    $return=$valResult ||  "$item[3]($colstr, $item[8])";		
					}
					
TripleParam_Function:	 <skip: q{}> IgnoreableWhiteSpace m/substring|re_index|reg_get_match|reg_get_matches|reg_get_length_matches/ IgnoreableWhiteSpace LeftParen  All_Type_Func_Param  Param_Separator  All_Type_Func_Param    Param_Separator  All_Type_Func_Param   RightParen 
					{
						my $colParam = $item[6];
						my $colstr = main::formatParameter($colParam );	
						my $valResult = main::functionValidation($thisline, $thiscolumn, $text, $item[3], $colstr, $item[8], $item[10]);
					    $return=$valResult ||  "$item[3]($colstr, $item[8], $item[10])";		
					}
					
Function_Invocation: Sequence_Function   {  $return=$item[1] ; }
					 | Random_Function    {  $return=$item[1] ; }
					 | Lastcol_Function    {  $return=$item[1] ; }
					 | UpperLower_Function   {  $return=$item[1] ; }
					 | Tally_Function    {  $return=$item[1] ; }
					 | Static_Function    {   $return=$item[1]; }
					 | Substring_Function   {  $return=$item[1] ; }
					 | Convert_Date_Function   {  $return=$item[1] ; }
					 | Srow_Function   {  $return=$item[1] ; }
					 | Scol_Function   {  $return=$item[1] ; }
					 | SingleParam_Function   {  $return=$item[1] ; }
					 | DoubleParam_Function   {  $return=$item[1] ; }
					 | TripleParam_Function   {  $return=$item[1] ; }
					 
Column_Expr: 	m/\$\d+/ { $return=main::formatParameter($item[1]) ; }
				| Decimal { $return=$item[1] ; }
				| String_Parameter { $return=$item[1] . '__String_Parameter__' ; }
				| Function_Invocation { $return=$item[1] ; }
				
				

Between_Expr:   <skip: q{}> Column_Expr IgnoreableWhiteSpace (m/not/i)(?) IgnoreableWhiteSpace m/between/i IgnoreableWhiteSpace LeftParen  Column_Expr Param_Separator Column_Expr RightParen
				{
					my $neg = $item[4][0];
					my $lop = $item[2];
					$lop =~ s/__String_Parameter__$//;
					my $rop1 = $item[9];
					$rop1 =~ s/__String_Parameter__$//;
					my $rop2 = $item[11];
					$rop2  =~ s/__String_Parameter__$//;					
					if( $neg ){
						$return="($lop < $rop1 || $lop > $rop2  )"
					}else{
						$return="($lop >= $rop1 && $lop <= $rop2  )"
					}					
				}
In_Expr_Addition:	Param_Separator Column_Expr
					{
						my $colExpr = $item[2];
						if( $colExpr =~ m/__String_Parameter__$/){
							$colExpr =~ s/__String_Parameter__$//;
							$colExpr = '"' . $colExpr . '"';
						}
						$return=$colExpr;
					}

In_Expr: <skip: q{}> Column_Expr IgnoreableWhiteSpace (m/not/i)(?) IgnoreableWhiteSpace m/in/i IgnoreableWhiteSpace LeftParen Column_Expr In_Expr_Addition(s?) RightParen
				{
					my $result = "";			
					my $neg = $item[4][0];					
					my $lop = $item[2];
					my $rop1 = $item[9];
					my $eq = "==";
					my $neq = "!=";
					
					if ( $lop =~ /__String_Parameter__$/ || $rop1 =~ /__String_Parameter__$/){
						if ($lop =~ /__String_Parameter__$/){
							$lop =~ s/__String_Parameter__$//;
								$lop = '"'. $lop . '"';						
						}
						if ($rop1 =~ /__String_Parameter__$/){
							$rop1 =~ s/__String_Parameter__$//;
							$rop1 = '"'. $rop1 . '"' ;
						}
						$eq = "eq";
						$neq = "ne";
					}
					
					if( $neg ){
						$result= $result . "( $lop $neq $rop1 ";
						foreach(@{$item[10]}){
							$result= $result . " && $lop $neq $_ ";
						}	
					}else{
						$result= $result . "( $lop $eq $rop1 ";
						foreach(@{$item[10]}){
							$result= $result . " || $lop $eq $_ ";
						}
					}	
					$result= $result . ")";			
					$return=$result;					
				}
Like_Expr: <skip: q{}> Column_Expr IgnoreableWhiteSpace (m/not/i)(?) IgnoreableWhiteSpace m/like/i IgnoreableWhiteSpace  String_Parameter  
				{
					my $result = "";			
					my $neg = $item[4][0];					
					my $likeClause = $item[9];
					my $lop = $item[2];
					if ($lop =~ /__String_Parameter__$/){
						$lop =~ s/__String_Parameter__$//;
						$lop = '"'. $lop . '"' ; 
					}
					$likeClause  =~ s/\?/\./g;
					$likeClause  =~ s/%/\.\*/g;
					if( $neg ){
						$result= $result . "$lop !~ /$likeClause/ ";	
					}else{
						$result= $result . "$lop =~ /$likeClause/ ";						
					}	
				    $return=$result;					
				}
				
Compare_Expr: <skip: q{}> Column_Expr IgnoreableWhiteSpace  m/!~|~|<>|!=|>=|>|==|=|<=|</ IgnoreableWhiteSpace Column_Expr
				{
					my $op = $item[4];
					my $lop = $item[2];
					my $rop1 = $item[6];
					if($op eq '<>'){
						$op='!=';
					}elsif ($op eq '='){
						$op='==';
					}
					if ( $lop =~ /__String_Parameter__$/ || $rop1 =~ /__String_Parameter__$/){
						if ($lop =~ /__String_Parameter__$/){
							$lop =~ s/__String_Parameter__$//;
								$lop = '"'. main::escape_perl_string($lop) . '"';						
						}
						if ($rop1 =~ /__String_Parameter__$/){
							$rop1 =~ s/__String_Parameter__$//;
							$rop1 = '"'. main::escape_perl_string($rop1) . '"' ;
						}
						if( $op eq '==' || $op eq '=' ){
							$op = 'eq';
						}elsif ( $op eq '!=' ){
							$op = 'ne';
						}
					}					
					$return="$lop $op $rop1";		
				}	
				
				
Condition_Expr: Between_Expr { $return="$item[1]" ; }
				| In_Expr { $return="$item[1]" ; }
				| Like_Expr { $return="$item[1]" ; }
				| Compare_Expr { $return="$item[1]" ; }				
				| LeftParen Condition_Expr_Or RightParen { $return="($item[2])" ; }

Condition_Expr_And_Addition: <skip: q{}> IgnoreableWhiteSpace m/and|&&/ IgnoreableWhiteSpace Condition_Expr
							{
								 $return=$item[5];
							}
				
Condition_Expr_And: Condition_Expr Condition_Expr_And_Addition(s?)
					{
						my $result="$item[1]";
						foreach(@{$item[2]}){
							$result= $result . " && $_";
						}
						
						$return=$result;
					}
Condition_Expr_Or_addition: <skip: q{}>  IgnoreableWhiteSpace m/or|\|\|/ IgnoreableWhiteSpace Condition_Expr_And
							{
								 $return=$item[5];
							}


Condition_Expr_Or: Condition_Expr_And Condition_Expr_Or_addition(s?)
					{
						my $result= "$item[1]";
						foreach(@{$item[2]}){
							$result= $result . " || $_";
						}						
						$return=$result;
					}					
					
ColumnSelection: Select_By_Number NewLine  { main::emitPlainSelection($thisline, $thiscolumn, $text, $item[1]);  $return=$item[1]; }
				 | Select_By_Range NewLine {  $return=$item[1]; }
				 | AtDatetime  NewLine {  $return=$item[1]; }
				 | AtDate NewLine { $return=$item[1]; }
				 | AtStaticLine  {  $return=$item[1]; }
				 | Function_Invocation NewLine {  main::emitFunctionLine($thisline, $thiscolumn, $text, $item[1]) unless ($item[1] =~ /^\s*$/) ; $return=$item[1]; }

				 
BlankLine: 	NewLine { 1; }		

expr_atom: 	All_Type_Func_Param { $return=$item[1]; }
			| m/\$\d+/ { if($item[1]==0) { $return="\$$item[1]"; } else{ $return=$item[1]; }  }
			| LeftParen  expr_substraction  RightParen { $return="($item[2])"; }
					
multiply: 	<skip: q{}> IgnoreableWhiteSpace '*' IgnoreableWhiteSpace  expr_atom 
				{
						 $return=$item[5];
				}
				
		
expr_multiply: <skip: q{}> IgnoreableWhiteSpace expr_atom multiply(s?)
				{
					
						my $result="" . "$item[3]";						
						foreach(@{$item[4]}){
							$result= $result . "*$_";
						}
						$result= $result . "" ;					
						$return=$result;
				}
				
divide: 	<skip: q{}> IgnoreableWhiteSpace '/' IgnoreableWhiteSpace  expr_multiply 
				{
						$return=$item[5];
				}	

expr_divide: <skip: q{}> IgnoreableWhiteSpace  expr_multiply divide(s?)
				{
						my $result="" . "$item[3]";						
						foreach(@{$item[4]}){
							$result= $result . "/$_";
						}
						$result= $result . "" ;
						$return=$result;
				}
addition:  <skip: q{}> IgnoreableWhiteSpace '+' IgnoreableWhiteSpace expr_divide  {$return=$item[5]; }

expr_addition: 	<skip: q{}> IgnoreableWhiteSpace expr_divide addition(s?) 
				{
						my $result="" . "$item[3]";						
						foreach(@{$item[4]}){
							$result= $result . "+$_";
						}
						$result= $result . "" ;
						$return=$result;
				}
substraction: <skip: q{}> IgnoreableWhiteSpace '-' IgnoreableWhiteSpace expr_addition {$return=$item[5]; }

expr_substraction:  <skip: q{}> IgnoreableWhiteSpace expr_addition substraction(s?) 
				{
						my $result="" . "$item[3]";						
						foreach(@{$item[4]}){							
							$result= $result . "-$_";
						}
						$result= $result . "" ;
						$return=$result;
				}

formula_line: <skip: q{}> IgnoreableWhiteSpace 'formula' IgnoreableWhiteSpace '=' expr_substraction  NewLine  
				{
					$formularCount++;
					main::emitPlainSelection($thisline, $thiscolumn, $text, $item[6]);					
					$return=$item[6];
					
				}
			  | <skip: q{}> IgnoreableWhiteSpace  expr_substraction NewLine 
			  {
					$formularCount++;
					main::emitPlainSelection($thisline, $thiscolumn, $text, $item[3]);						
					$return=$item[3];	  
			  }
			  
when_expr: <skip: q{}> IgnoreableWhiteSpace 'when'  IgnoreableWhiteSpace Condition_Expr_Or IgnoreableWhiteSpace 'then' IgnoreableWhiteSpace expr_substraction
				{
					$return = "(" . $item[5] . ")\{ \$\{VARNAME\}=" . $item[9] . "; \}";
				}
case_else_expr: <skip: q{}> IgnoreableWhiteSpace 'else'  IgnoreableWhiteSpace expr_substraction
				{
					$return = "\{  \$\{VARNAME\}=" . $item[5] . "; \}";
				}
case_line: <skip: q{}> IgnoreableWhiteSpace  'case'  when_expr(s)  case_else_expr(?) IgnoreableWhiteSpace 'end' NewLine
				{
					main::emitCaseStatement($thisline, $thiscolumn, $text, $item[5][0], @{$item[4]});					
					$return=1;
				}
				
Condition_Line: <skip: q{}> IgnoreableWhiteSpace 'if' IgnoreableWhiteSpace LeftParen  Condition_Expr_Or  RightParen NewLine
				{
					main::emitFilterStatement($thisline, $thiscolumn, $text, "if ($item[6])");
					$return = "if ($item[6])";					
				}
				
Extract_Start_Tag: <skip: q{}> IgnoreableWhiteSpace '[extract]' NewLine

Extract_End_Tag: <skip: q{}> IgnoreableWhiteSpace '[/extract]' NewLine
				{
					$afterBody = 1;
					$return=$item[0];
				}

Transform_Rules: Condition_Line {  1;}
		| ColumnSelection { 1;}
		| formula_line { 1;}
		| case_line { 1;}
		| CommentLine { 1;}
		| BlankLine	{ 1;}		
		| <error: Invalid commmand at line $thisline "$text" > 

Transform_Body: Extract_Start_Tag Transform_Rules(s) Extract_End_Tag

Name_Value_Pair_line: <skip: q{}> IgnoreableWhiteSpace m/\w+/ IgnoreableWhiteSpace '=' m/.*/ NewLine
					{
						if($afterBody == 1){
							main::emitEndNameValuePair($thisline, $thiscolumn, $text, $item[3], $item[6]);
						}else{
							main::emitBeginNameValuePair($thisline, $thiscolumn, $text, $item[3], $item[6]);
						}						
						$return=$item[0];
					}
					| <error>

Global_Line: Name_Value_Pair_line { $return="$item[1]" ; }			
			| CommentLine { $return="$item[1]" ; }
			| BlankLine		 { 1; }
			| <error> 
			
ODDCompileUnit: Global_Line(s?) Transform_Body Global_Line(s?) /\Z/
				{					
					1;
				}
				| <error>


		
};



my $lib_path       = "$FindBin::Bin/xform_func";
my $xform_template = "$lib_path/perl.template";
my @beginBlock = ();
my @selections = ();
my @usedCustomFunctions = ();
my %customFunctions={};
my @endBlock = ();
my $filterStatement = "";
my %userDefinedFunctions = ();
my $command_file = "";
my $tallyCount = 0;
my $caseSelectCount = 0;
my @caseStatements = ();
my $oddscript = read_fullfile($rules_file);
#print "$oddscript\n";


my @pre_functs = ();

my %xform_begin = ();
my @xform_begin_code = ();
my %xform_end = ();
my @xform_col = ();
my @line_function = ();
my %Xtract_Rule      = ();
my $lookupfunction = "return '';";


my %xform = (
	presplit_function  => \@pre_functs,
	begin_section => \%xform_begin,
	begin_code    => \@xform_begin_code,
	skip_row      => undef,
	utf8          => undef,
	end_section   => \%xform_end,
	row_filter    => undef,
	filter_file    => undef,	
	in_compress    => undef,
	out_compress    => undef,
	data_columns  => \@selections,
	output_limit  => undef,
	line_function => \@line_function,
	xform_log     => '/dev/null',	
	case_stmts   => \@caseStatements,
	lookupfunction => \$lookupfunction,
);


#$::RD_CHECK = 0;
#$::RD_ERRORS = 0;
#$::RD_WARN = 1;
#$::RD_HINT =1;
$::RD_ERROR =1;
#$RD_TRACE=1;
#$RD_HINT=1;

my $parser = Parse::RecDescent->new($grammar);

#print STDERR "Compiler Grammar Generated" ."\n";

defined $parser->ODDCompileUnit($oddscript) or die "invalid rules input!\n";

#print STDERR  "Odd script compiled" ."\n";

if(	$filterStatement ){
	$xform{'row_filter'} = $filterStatement;
}

if(	$filter_file ){
	$xform{'filter_file'} = $filter_file;
}

my $selColCnt = scalar @selections;
if( $selColCnt == 0 ) {
	terminate("No field selection has been specified.") 
}

my $temp = Text::Template->new(SOURCE => $xform_template) or die "$Text::Template::ERROR\n";

#print STDERR  "Perl Template loaded" ."\n";

my $code = $temp->fill_in(HASH => \%xform) or die "$Text::Template::ERROR\n";

#print STDERR  "Perl Template rendered with odd script rules" ."\n";


if (! defined $command_file ) {
   $command_file = "/tmp/odw.$PID.xform";
   $no_execute   = 0 if !defined $no_execute;
}
else {
    $no_execute = 1 if ! defined $no_execute;
}

$no_execute = 0 if ! defined $no_execute;

$command_file = $icommand_file if( $icommand_file );

#Another work around to fix fullpath requirement
if ( $command_file =~ m/^\// || $command_file =~ m/^[A-Z]:/i){
	$command_file = $command_file ;
}else{
	if( $command_file =~ m/\\/ ){
		$command_file = $pwd . "\\" . $command_file;
	}else{
		$command_file = $pwd . "/" . $command_file;
	}
}

my $fh = IO::File->new( "> $command_file" )
   or terminate("Couldn't open command file '$command_file' for writing: [$!]");

print {$fh} $code;
close  $fh;

chmod 0700, $command_file;
#print "Command File: $command_file\n" if $no_execute;

my $cmdToBeExecuted = $command_file;
if( $input_file ){	
	$cmdToBeExecuted .= " $input_file" ;
}
if( $output_file ){
	$cmdToBeExecuted .= " >$output_file" ;
}
if (!$no_execute) {   
	system("$cmdToBeExecuted");
    if ($? == -1) {
        terminate( "Failed to execute: [$!]\n");
    }
    else {
        warn "Transformed successfully\n";
        #unlink $command_file;
    }
}

######################################################################################################################
#
#  Transform Logic
######################################################################################################################

sub formatParameter
{
	my $colParam = shift @_;
	my $colstr = "" . $colParam;
	if($colParam =~ m/^\$?(\d+)$/ ){							
		my $colNum  =  $1;
		if (  $colNum ==0 ){
			$colstr = "join(\"\$OFS\", \@dataArray)";
		}else{
			$colstr = "\$dataArray\[" . ($1 - 1) . "\]";
		}
	}
	return $colstr;
}

sub load_user_defined_funcLists
{
	opendir(LIBDIR, $lib_path) or die $!;
	while (my $file = readdir(LIBDIR)) {        
        next if ($file =~ m/^\./);		
		if ($file =~ m/\.awk$/){
			my ($fname, $ext) = ($file =~ m/([A-Za-z]\w*)\.(awk)$/);
			#print "$fname = $file\n";
			$userDefinedFunctions{$fname} = $file;
		}
    }
    closedir(LIBDIR);	
}


sub read_fullfile
{
	my ($filename) = @_;
	my $whole_file;
	open my $fh, $filename or terminate("Error: Couldn't read file [$filename]: [$!]");
	{
		undef $/;
		$whole_file = <$fh>;
	}      
	return $whole_file;
}



sub emitFilterStatement
{
	my ($line, $column, $remainText, $ifstmt) = @_;	
	$filterStatement = $ifstmt;
}

sub emitBeginNameValuePair
{
	my ($line, $column, $remainText, $name, $value) = @_;		
	push(@beginBlock, emitNameValuePair($line, $column, $remainText, $name, $value));	
}

sub emitEndNameValuePair
{
	my ($line, $column, $remainText, $name, $value) = @_;
	push(@endBlock, emitNameValuePair($line, $column, $remainText, $name, $value));	
}

sub emitNameValuePair
{
	my ($line, $column, $remainText, $name, $value) = @_;	
	my $output = "";
	$name =~ s/^\s+//;
	$name =~ s/\s+$//;
	$value =~ s/^\s+//;
	$value =~ s/\s+$//;	
	my $lc_key = lc $name;
	#print "$lc_key\n";
	if ($lc_key eq 'indelim' ) { 
		$xform_begin{ '$FS' } = translateNamedDelimter($value);
	}
	elsif ($lc_key eq 'outdelim') { 
		$xform_begin{ '$OFS' } = translateNamedDelimter($value);
	}
	elsif ($lc_key eq 'limit') { 
		$xform_begin{ '$limit' } = $value;
	}
	elsif ($lc_key eq 'recindelim') { 
		$xform_begin{ '$RS' } = translateNamedDelimter($value);
	}
	elsif ($lc_key eq 'recoutdelim') { 
		$xform_begin{ '$ORS' } = translateNamedDelimter($value);
	}
	elsif ($lc_key eq 'ignorecase') { 
		$xform_begin{ '$IGNORECASE' } = 1;
	}
	elsif ($lc_key eq 'skip') { 
		$xform_begin{ '$skip' } = $value; 
	}elsif ($lc_key eq 'in_compress' && $value eq '1') { 
		$xform{ 'in_compress' } = $value; 
	}elsif ($lc_key eq 'out_compress' && $value eq '1') { 
		$xform{ 'out_compress' } = $value; 
	}
	elsif ($lc_key eq 'charset' && $value eq 'utf8') { 
		$xform{ 'utf8' } = $value; 
	}elsif ($lc_key eq 'command_file') {  
		$Xtract_Rule{ 'command_file'} = $value; 
		$command_file = $value; 	
		unlink $value if -f $value; 
	}
	elsif ($lc_key eq 'datafile') { 
		$Xtract_Rule{ 'datafile'} = $value; 
	}
	elsif ($lc_key eq 'logfile') { 
		$xform{ 'xform_log' } = $value; 
	}
	elsif ($lc_key eq 'outfile') { 
		$Xtract_Rule{ 'outfile'} = $value; 
	}
	elsif ($lc_key eq 'dos2unix') { 
		if ($value == 1 || (any {lc $value eq $_} qw(yes y) )) {
			push (@pre_functs, '$line = dos2unix($line)'); 			
		}
	}
	elsif ($lc_key eq 'unix2dos') { 
		if ($value == 1 || (any {lc $value eq $_} qw(yes y) )) {
			push (@pre_functs, '$line = unix2dos($line)'); 				
		}
	}elsif ($lc_key eq 'lookup') { 
		handleLookupSpec($value);
	}		
	if ($value =~ /^(-)?\d+(\.\d+)?((e|E)(-)?\d+)?$/){
		$output = $name . "=" . $value . ";"; 
	}else{
		$output = $name . "=\"" . $value . "\";"; 
	}
	return $output;
}

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
		push (@xform_begin_code , "my \$$lkparts[0] = ()");
		push (@xform_begin_code , generateLookupArray(@lkparts));
	}
	$lookupfunction = generateLookupFunction(@lookupNames);
}

sub generateLookupFunction
{
	my @names = @_;
	my $functionText = "";	
	my $cnt = 0;
	foreach(@names){
		$cnt++;
		if($cnt==1){
			$functionText .= "        if \( \$name eq \"" . $_ . "\" \)\{ return \$". $_  ."\[\$key\];\}\n";
		}else{
			$functionText .= "        elsif \( \$name eq \"" . $_ . "\" \)\{ return \$". $_  ."\[\$key\];\}\n";
		}
	}	
	return $functionText;
}


sub generateLookupArray
{
	my ($name, $file, $coldel, $rowdel, $key, $val) = @_;	
	my $delimited = ($coldel eq "" )?undef:1;
	my @keys;
	my @vals;
	if(defined($delimited)){			
			my @ar = split(/f/, $key);
			foreach(@ar)
			{
				next unless ($_ =~ m/\d+/);				
				push(@keys, $_ -1);
			}
			my @vr = split(/f/, $val);
			foreach(@vr)
			{				
				next unless ($_ =~ m/\d+/);
				push(@vals, $_ - 1);
			}
	}else{		
		#print "DEBUG KEY $key\n";		
		my @far = split(/c/, $key);
		foreach(@far)
		{
			next unless ($_ =~ m/\d+/);
			#print "DEBUG  $_\n";
			if($_ =~ m/(\d+)[-](\d+)/ ){
				my @range = ();
				my $offset = $1-1;
				my $length = $2 - $1 + 1;
				$range[0] = $offset;
				$range[1] = $length;
				#print "DEBUG length $length\n";
				push(@keys, \@range);
			}else{
				my $offset = $_ - 1;
				my @range = ($offset, 1);
				push(@keys, \@range);
			}
		}
		#print "DEBUG VAL $val\n";
		my @fvr = split(/c/, $val);
		foreach(@fvr)
		{
			next unless ($_ =~ m/\d+/);
			if($_ =~  m/(\d+)[-](\d+)/ ){				
				my @range = ();
				my $offset = $1-1;
				my $length = $2 - $1 + 1;
				$range[0] = $offset;
				$range[1] = $length;
				#print "DEBUG length $length\n";
				push(@vals, \@range);
			}else{
				my $offset = $_ - 1;
				my @range = ($offset, 1);
				push(@vals, \@range);
			}
		}
	}
	#print "coldel>>>>>>>>>>>>>$coldel\n";
	$coldel = getLookupDelimiter( $coldel);	
	#print "coldel>>>>>>>>>>>>>$coldel\n";
	
	my $arrayText = "";
	open my $info, $file or die "Could not open $file: $!";	
	#print "rowdel>>>>>>>>>>>>>$rowdel\n";
	$rowdel = getLookupDelimiter( $rowdel);
	#print "rowdel>>>>>>>>>>>>>$rowdel\n";
	
	local $/ = $rowdel;		
	while( my $line = <$info>)  { 
		next if ($line =~ m/^\s*$/);
		my $pkey = "";
		my $pval = "";
		if(defined($delimited)){
			my @data = split(/$coldel/, $line);				
			foreach(@keys){				 
				  my $ck =  $data[$_];
				 $ck =~ s/^\s+//;
				 $ck =~ s/\s+$//;
				 $pkey .= $ck;
			}
			foreach(@vals){				 
				 my $cv = $data[$_];
				  $cv =~ s/^\s+//;
				  $cv =~ s/\s+$//;
				  $pval .= $cv;
			}			
		}else{
			foreach(@keys){
				 my $k = $_;
				 my $ck = substr($line, ${$k}[0], ${$k}[1]);
				 $ck =~ s/^\s+//;
				 $ck =~ s/\s+$//;
				 $pkey .= $ck;
			}
			foreach(@vals){
				  my $v = $_;
				  my $cv = substr($line, ${$v}[0], ${$v}[1]);
				  $cv =~ s/^\s+//;
				  $cv =~ s/\s+$//;
				  $pval .= $cv;
			}			
		}	
		$arrayText .= "\$$name\[\"$pkey\"\]=\"$pval\";\n";		
	}
	close $info;	
	return $arrayText;
}

sub getLookupDelimiter
{
	my ($del) =  @_;	
	if ($del =~ m/\\(\d+)/){
		return chr($1);
	}elsif ($del =~ m/^\\\\n$/ || $del =~ m/^\\n$/ ){		
		return "\n";
	}elsif($del =~ m/^\\\\t$/ || $del =~ m/^\\t$/){
		return "\t";
	}elsif($del =~ m/^\\\\b$/ || $del =~ m/^\\b$/){
		return "\b";
	}elsif($del =~ m/^\\\\f$/ || $del =~ m/^\\f$/){
		return "\f";
	}elsif($del =~ m/^\\\\r$/ || $del =~ m/^\\r$/){
		return "\r";
	}else{
		return $del;
	}
}


sub isStringLiteral
{
	my $val = shift @_;
	my $output = 0;
	if ($val =~ /^(-)?\d+(\.\d+)?((e|E)(-)?\d+)?$/){
		$output = 0; 
	}elsif($val =~ /^\$\d+$/) {
		$output = 0; 
	}else{
		$output = 1; 
	}
	return $output;
}


sub emitDateFunction
{
	my ($line, $column, $remainText, $name, $dtf, $tf) = @_;
	$dtf = lc($dtf);
	$tf = lc($tf );
	my %date_formats = (
        dmony     => '%d-%b-%Y',
        mdy       => '%m-%d-%Y',
        ymd       => '%y-%m-%d',
        yyyymmdd  => '%Y%m%d',		
		'24hr'    => '%H:%M:%S',
        '12hr'    => '%I:%M:%S %p',
		'24'      => '%H:%M:%S',
        '12'      => '%I:%M:%S %p',
    );		
	
	my $dtString = $date_formats{$dtf} || "";	
	$dtString .= " " . $date_formats{$tf} if $date_formats{$tf};
	$dtString =~ s/^\s+//;
	$dtString =~ s/\s+$//;		
	#print "DEBBBBUUGG->$dtString\n";
	if($name eq "@"){		
		die "Invalid date time format at line [$line:$column] "  . ( split /\n/, $remainText )[0] unless $dtString;
		my $func = 'strftime("' . $dtString . '", localtime)';
		push(@selections, $func);
	}else{
		#so far, we d not have other date format yet;
	}	
}


sub parse_dtfmt
{
	my ($ifmt, $ofmt) = @_;	
	$ifmt =~ s/^"//;
	$ifmt =~ s/"$//;
	$ofmt =~ s/^"//;
	$ofmt =~ s/"$//;
	$ofmt = lc($ofmt);
	my $iystart =0;
	my $imstart = 0;
	my $idstart =0;
	my $yy = 44;
	my $mm = 22;
	my $dd = 22;
	
	if( $ifmt =~ m/yyyy/ ){
		$iystart = index($ifmt, 'yyyy');
		$yy = 40;
	}else{
		$iystart = index($ifmt, 'yy');
		$yy = 20;
	}	
	if( $ofmt =~ m/yyyy/ ){		
		$yy += 4;
	}else{		
		$yy += 2;
	}	
	if( $ifmt =~ m/mon/ ){
		$imstart = index($ifmt, 'mon');	
		$mm = 30;
	}else{		
		$imstart = index($ifmt, 'mm');	
		$mm = 20;
	}
	if( $ofmt =~ m/mon/ ){		
		$mm += 3;
	}else{		
		$mm += 2;
	}	
	if( $ifmt =~ m/dd/ ){
		$idstart = index($ifmt, 'dd');	
		$dd = 20;
	}else{		
		$idstart = index($ifmt, 'd');	
		$dd = 10;
	}
	if( $ofmt =~ m/dd/ ){		
		$dd += 2;
	}else{		
		$dd += 1;
	}
	my @dtfmt = ($iystart, $imstart, $idstart, $yy, $mm, $dd);	
	return @dtfmt;
}


sub handle_convert_date
{
	 my ($line, $column, $remainText, $col, $input_fmt, $output_fmt) = @_;
	 $input_fmt = lc($input_fmt);
	 $output_fmt = lc($output_fmt);
	 my $result = '';
	 my @pd = parse_dtfmt($input_fmt, $output_fmt);
	 my $partialparam = join(',', @pd);
	 $result .= 'convert_date(' . $col . ',' . $partialparam . ',' . $output_fmt . ')';	   
     return $result;

}


sub functionValidation
{
	my ($line, $column, $remainText, $funcName, @arguments) = @_;
	if ( $funcName eq 'convert_date' ){
		my ($col, $input_fmt, $output_fmt) = @arguments;
		return handle_convert_date($line, $column, $remainText, $col, $input_fmt, $output_fmt);
	}elsif($funcName eq 'tally'){
	     $tallyCount++;
		 my ($tallyParam) = @arguments;
		 my $funcname = "\$tally_$tallyCount";	
		 my $fuctBg = 'my ' . $funcname;
		$xform_begin{ $fuctBg } = 0;
		$tallyParam = formatParameter($tallyParam);
		push @line_function, "$funcname += $tallyParam";			
		return $funcname;
	}elsif($funcName eq 'srow'){
	    my  ($line, $column, $remainText, $funcName, $search, $replacement) = @_;
		push @pre_functs, "\$line = srow(\$line, $search, $replacement)";			
		return undef;
	}elsif($funcName eq 'random' || $funcName eq 'rand'){	     
		 my ($seed) = @arguments;
		 push(@usedCustomFunctions, 'rand');
		 my $funcname = "rand";		 
		$seed = 1000000 unless ($seed);
		return $funcname . "($seed)";
	}elsif($funcName eq 'int'){
	    my  ($line, $column, $remainText, $funcName, $param) = @_;				
		return 'floor(' . $param . ')';
	}
	
	
	#print "invoking function $funcName with ";
	#foreach(@arguments){
	#	print "$_, ";
	#}
	#print  " at line \[$line:$column\], please do transformation at sub functionValidation\n";
			
	return 0;
}
#my $tallyCount = 0;
#my $caseSelectCount = 0;
sub emitCaseStatement
{
	my ($line, $column, $remainText, $elseStmt, $firstWhen, @whenStmts) = @_;
	++$caseSelectCount;
	my $caseVarName = "\$caseVar_$caseSelectCount";
	
	$firstWhen =~ s/\$\{VARNAME\}/$caseVarName/;
	
	my $output = "if $firstWhen ";
	foreach(@whenStmts)
	{
		my $fwp = $_;
		$fwp =~ s/\$\{VARNAME\}/$caseVarName/;
		$output .= " else if $fwp ";
	}
	if($elseStmt){
		$elseStmt =~ s/\$\{VARNAME\}/$caseVarName/;
		$output .= " else $elseStmt ";
	}
	
	
	$xform_begin{ 'my ' . $caseVarName } = undef;	
	push (@line_function, $output);
	my $selOut = "$caseVarName";
	push(@selections, $selOut);
}


sub emitPlainSelection
{
	
	my ($line, $column, $remainText, $selectText) = @_;			
	push(@selections, $selectText) if ($selectText);
	
}


sub emitStaticToken
{
	my ($line, $column, $remainText, $staticText) = @_;
    my $output = "\"";
	if (looks_like_number($staticText)){
		$output = $staticText;
	}else{
		for (my $key = 0; $key < length($staticText); $key++) {
			my $achar =  substr ($staticText, $key, 1);
			#print "ACHAR: $achar\n";
			if ( $achar eq "\"" ){
				$output .=  '\\' . '"';
			}elsif($achar eq "'" ){
				my $achar2 =  substr ($staticText, $key+1, 1);
				if($achar2 eq "'" ){
					#SQL escape, we fix it here;					
					$key++;
				}
				$output .= $achar;				
			}else{
				$output .= $achar;
			}
		}
		$output .= "\"";
	}	
	push(@selections, $output);
}

sub escapeString
{
	my ($stringtext) = @_;
	$stringtext =~ s/^"//;
	$stringtext =~ s/"$//;	
	my $strlength = length($stringtext);
	#print "INPUT : $stringtext\n";
	my $output = "\"";
	for (my $key = 0; $key < $strlength; $key++) {
			my $achar =  substr ($stringtext, $key, 1);
			#print "ACHAR: $achar\n";
			if ( $achar eq "\\" ){
				my $achar2 =  substr ($stringtext, $key+1, 1);
				if($achar2 eq "\"" ){									
					$key++;
					$output .= "\\\"";	
				}else{
					$output .= $achar;	
				}				
			}elsif ( $achar eq "\"" ){
				$output .=  '\\' . '"';
			}elsif ( $achar eq "\n" ){
				$output .=  '\\' . 'n';
			}elsif ( $achar eq "\r" ){
				$output .=  '\\' . '"';
			}elsif ( $achar eq "\"" ){
				$output .=  '\\' . 'r';
			}elsif ( $achar eq "\t" ){
				$output .=  '\\' . 't';
			}elsif ( $achar eq "\f" ){
				$output .=  '\\' . 'f';
			}elsif ( $achar eq "\b" ){
				$output .=  '\\' . 'b';
			}elsif($achar eq "'" ){
				my $achar2 =  substr ($stringtext, $key+1, 1);
				if($achar2 eq "'" ){
					#SQL escape, we fix it here;					
					$key++
				}
				$output .= $achar;				
			}else{
				$output .= $achar;
			}
	}
	$output .= "\"";
	#print "OUTPUT : $output\n";
	return $output;
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



sub emitFunctionLine
{
	my ($thisline, $thiscolumn, $text, $funcExpr) = @_;		
	push(@selections, $funcExpr) if ( $funcExpr ne 'void');
	
}


sub usage {

   my $USAGE = "
Required Parameters:
   -r|rules_file <value>  
               Where value is the rules file (ODD parsing syntax)

Optional Parameters:
   -f|command_file <value>  
               Where value is the path of output perl script file
               This will also trigger execution of transformation
               
   -i|no_execute <1|0>    
               Execute(0) generated xform command or not(1). If command file is 
               passed in rules file or passed as parameter, execution won't happen
               until this option passed.
			   
	-t|input_file <value>     
               Where value is the path of input file that will be executed 
			   by generated perl script.
	-l|output_file <value>     
               Where value is the path of output file that will be produced 
			   by generated perl script.
			   
";

   print STDERR "$USAGE\n";
   exit(0);
}

sub escape_perl_string {
   my ($string) = @_;
   my $ret = "";
   my @chars = split(//, $string);
   my $q = ord("\"");
   my $nl = ord("\n");
   my $tabv = ord("\t");
   
   foreach(@chars)
   {
		if(ord($_)==$q){
			$ret .= "\\\"";
		}elsif(ord($_)==$nl){
			$ret .= "\\n";
		}elsif(ord($_)==$tabv){
			$ret .= "\\t";
		}else{
			$ret .= $_;
		}
   }
   
   return $ret;
}


sub terminate {
   my ($msg) = @_;
   displayerror("$msg") if $msg;
   exitpgm(1);
}

sub displayerror {
   my ($msg) = @_;
   print "Incorrect Syntax: $msg\n";
}

sub exitpgm {
   my ($return_code) = @_;
   #close($fh) if $fh ne "STDOUT";
   exit($return_code);
}

