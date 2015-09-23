#include <botan_fpe.h>
#include <stdio.h>
#include <stdlib.h>


//gcc -O2 -ansi  -Wall   -I../../build/include fpetest.c -L../.. -lbotan-1.10 -lbotan_fpe  -o fpetest

int main(int argc, char* argv[])
{
	botan_fpe_init();
	printf("%s \n ", argv[1]);
	const char* enc = botan_fpe_encrypt(argv[1], 5, argv[3], argv[2]);
	printf("%s \n ", enc);
	const char* dec = botan_fpe_decrypt(enc, 5, argv[3], argv[2]);
	printf("%s \n ", dec);
	return 0;
}



