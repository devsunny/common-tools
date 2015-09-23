#include <botan/botan.h>
#include <botan/fpe_fe1.h>
#include <botan/sha160.h>

using namespace Botan;

#include <iostream>
#include <stdexcept>
#include <string>

#include "botan_fpe.h"


//gcc -O2 -ansi  -Wall   -I../../build/include fpetest.c -L../.. -lbotan-1.10 -lbotan_fpe  -o fpetest

void botan_fpe_init()
{
	LibraryInitializer init;
}


SecureVector<byte> sha1(const std::string& acct_name)
   {
   SHA_160 hash;
   hash.update(acct_name);
   return hash.final();
   }

BigInt calFactor(u64bit number)
{
	BigInt n = 10;
	int a = 1;
   u64bit l = number;
   while(a)
   {
		l = l /10;
		if( l == 0){
			a = 0;
		}else{
			n *= 10;
		}
   }
   return n;
}
   
   
u64bit encrypt_number(u64bit plain_num,
                         const char* key_str,
                         const char* tweak)
{
BigInt n = calFactor(plain_num); 
u64bit cc_ranked = plain_num;

const SymmetricKey key = sha1(key_str);
const std::string& acct_name = tweak;

BigInt c = FPE::fe1_encrypt(n, cc_ranked, key, sha1(acct_name));
if(c.bits() > 50)
  throw std::runtime_error("FPE produced a number too large");
u64bit enc_cc = 0;
for(u32bit i = 0; i != 7; ++i)
  enc_cc = (enc_cc << 8) | c.byte_at(6-i);
return enc_cc;
}

u64bit decrypt_number(u64bit enc_num,
                         const char* key_str,
                         const char* tweak)
   {
   
    BigInt n = calFactor(enc_num); 
	u64bit cc_ranked = enc_num;
	const SymmetricKey key = sha1(key_str);
   const std::string& acct_name = tweak;
   
   BigInt c = FPE::fe1_decrypt(n, cc_ranked, key, sha1(acct_name));
   if(c.bits() > 50)
      throw std::runtime_error("FPE produced a number too large");
   u64bit dec_cc = 0;
   for(u32bit i = 0; i != 7; ++i)
      dec_cc = (dec_cc << 8) | c.byte_at(6-i);
   return dec_cc;
   }



void clean_up(char* enc)
{
	free(enc);
}



