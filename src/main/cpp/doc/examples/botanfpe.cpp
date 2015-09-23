#include "botanfpe.h"


#include <botan/botan.h>
#include <botan/fpe_fe1.h>
#include <botan/sha160.h>

using namespace Botan;

#include <iostream>
#include <stdexcept>


/*
* Use the SHA-1 hash of the account name or ID as a tweak
*/
SecureVector<byte> sha1(const std::string& acct_name)
{
   SHA_160 hash;
   hash.update(acct_name);
   return hash.final();
}
   


const char* fpe_encrypt(const char* plainNumber,  const char* key const char* tweak)
{
	SymmetricKey key = sha1(key);
	SecureVector<byte> tweak = sha1(tweak);
	
	
}