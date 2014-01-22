#!/bin/sh
ARGC=$#
if [ $ARGC -eq 0 ]; then
echo "Perl Test::More::Behavior Spec Runnner and Skeleton Generator"
echo "                   Version 1.0                               "
echo "                 Author: Sunny Liu                           "
echo ""
echo ""
echo "This test runner can run all test specs recursively in the specified file"
echo "directory; however spec file name has to end with 'Spec' with or without"
echo "file extension, it is cse insensitive, for example, mytestSpec.pl, "
echo "Testspec.t MyModuleNameSpec.t and AotherModuleNameSpec etc."
echo ""
echo "Usage:"	
echo ""													
echo "Generate Skeleton spec:"
echo "    $0 gen  path_to_spec_file"
echo ""
echo "Run Test"
echo "    $0 path_to_specs_directory"
echo "    $0 path_to_spec_file"
exit 1
fi

if [ "$1" = "gen" ]; then
SKELTONFILE="$2"
echo "#!/usr/bin/env perl" > $SKELTONFILE
echo "" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "use strict;" >> $SKELTONFILE
echo "use warnings;" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "use Test::More::Behaviour;" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "BEGIN {" >> $SKELTONFILE
echo "		#use_ok('Your::Module::Here');" >> $SKELTONFILE
echo "};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "sub before_all {" >> $SKELTONFILE
echo "        #before_all: once before everything" >> $SKELTONFILE
echo "};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "sub after_all {" >> $SKELTONFILE
echo "        #after_all: once after everything" >> $SKELTONFILE
echo "};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "sub before_each {" >> $SKELTONFILE
echo "		#before_each: once before every test case" >> $SKELTONFILE
echo "};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "sub after_each {" >> $SKELTONFILE
echo "		#after_each: once after every test case" >> $SKELTONFILE
echo "};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "describe 'My Best Perl Module Ever' => sub {" >> $SKELTONFILE
echo "		context 'when something happens' => sub {" >> $SKELTONFILE
echo "			it 'has result of something or some value ' => sub {" >> $SKELTONFILE
echo "				 my \$expect = 0.0;" >> $SKELTONFILE
echo "				 my \$calculatedResult = 0;" >> $SKELTONFILE
echo "				is(\$calculatedResult, \$expect)" >> $SKELTONFILE
echo "			};" >> $SKELTONFILE
echo "		};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "		context 'when another action happens, what result should be?' => sub {" >> $SKELTONFILE
echo "			it 'has an result value of ????' => sub {" >> $SKELTONFILE
echo "				 my \$expect = 'HORRAY';" >> $SKELTONFILE
echo "				 my \$calculatedResult = 'HORRAY';" >> $SKELTONFILE
echo "				is(\$calculatedResult, \$expect)" >> $SKELTONFILE
echo "			};" >> $SKELTONFILE
echo "		};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "		 context 'when there are multiple actions, you need to cross check results' => sub {" >> $SKELTONFILE
echo "				 my \$source = (100);" >> $SKELTONFILE
echo "				 my \$target = (0);" >> $SKELTONFILE
echo "				\$target += 50;" >> $SKELTONFILE
echo "				\$source -= \$target;" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "				it 'source should has reduced amount' => sub {" >> $SKELTONFILE
echo "					is(\$source , 50);" >> $SKELTONFILE
echo "				};" >> $SKELTONFILE
echo "				it 'Destination should have new amount' => sub {" >> $SKELTONFILE
echo "					 is(\$target, 50);" >> $SKELTONFILE
echo "				};" >> $SKELTONFILE
echo "		};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "};" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "#Lets notify the test framework that test is completed for this spec" >> $SKELTONFILE
echo "done_testing();" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "" >> $SKELTONFILE
echo "$SKELTONFILE has been generated."
exit 0
fi

SPEC_DIR=$1

if [ "x$SPEC_DIR" = "x" ]; then
echo "Please specify the test specs directory."
echo "Usage: $0 path_specs_directory"
exit 1
fi

if [ -f "$SPEC_DIR" ]; then
    echo "Running Spec: $SPEC_DIR"
	perl "$SPEC_DIR"	
fi

if [ -d "$SPEC_DIR" ]; then
echo "SPEC DIR: $SPEC_DIR"
SPECS=` find "$SPEC_DIR" -type f -regextype posix-egrep -iregex "^.+spec(\..+)?$"`

for spec in $SPECS; do  
  if [ -f "$spec" ]; then
    echo "Running Spec: $spec"
	perl "$spec"	
  fi  
done

fi





