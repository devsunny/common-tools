#include <botan/botan.h>
#include <botan/fpe_fe1.h>
#include <botan/sha160.h>

using namespace Botan;

#include <iostream>
#include <stdexcept>
#include <string>

#include "fpe.h"



void botan_fpe_init()
{
	LibraryInitializer init;
}


u64bit cc_rank(u64bit cc_number)
{  
	return cc_number;
}

u64bit cc_derank(u64bit cc_number)
{  
	return cc_number;
}

/*
* Use the SHA-1 hash of the account name or ID as a tweak
*/
SecureVector<byte> sha1(const std::string& acct_name)
{
	SHA_160 hash;
	hash.update(acct_name);
	return hash.final();
}

u64bit encrypt_number(u64bit cc_number, int length,
                         const std::string& key_str,
                         const std::string& tweak){
	BigInt n = 1;
	for(int j=0; j<length; ++j){
		n = n * 10;
	}	
	u64bit cc_ranked = cc_rank(cc_number);
	SymmetricKey key = sha1(key_str);
	BigInt c = FPE::fe1_encrypt(n, cc_ranked, key, sha1(tweak));
	if(c.bits() > 50)
	  throw std::runtime_error("FPE produced a number too large");
	u64bit enc_cc = 0;
	for(u32bit i = 0; i != 7; ++i)
	  enc_cc = (enc_cc << 8) | c.byte_at(6-i);
	return cc_derank(enc_cc);
}

u64bit decrypt_number(u64bit enc_cc, int length,
                        const std::string& key_str,
                         const std::string& tweak)
   {
    BigInt n = 1;
	for(int j=0; j<length; ++j){
		n = n * 10;
	}  
   u64bit cc_ranked = cc_rank(enc_cc);
   SymmetricKey key = sha1(key_str);
   BigInt c = FPE::fe1_decrypt(n, cc_ranked, key, sha1(tweak));   
   if(c.bits() > 50)
      throw std::runtime_error("FPE produced a number too large");
   u64bit dec_cc = 0;
   for(u32bit i = 0; i != 7; ++i)
      dec_cc = (dec_cc << 8) | c.byte_at(6-i);
   return cc_derank(dec_cc);
   }
   
const char* botan_fpe_encrypt(const char* num_str, int length, const char* key_chars, const char* tweak_chars)  
{
	u64bit number = atoll(num_str);
	std::string key_str = key_chars;
	std::string tweak_str = tweak_chars;
	u64bit enc_num = encrypt_number(number, length, key_str, tweak_chars);
	return to_string(enc_num).c_str();
}

const char* botan_fpe_decrypt(const char* num_str, int length, const char* key_chars, const char* tweak_chars)  
{
	u64bit number = atoll(num_str);	
	std::string key_str = key_chars;
	std::string tweak_str = tweak_chars;
	u64bit enc_num = decrypt_number(number, length, key_str, tweak_chars);	
	return to_string(enc_num).c_str();
}





