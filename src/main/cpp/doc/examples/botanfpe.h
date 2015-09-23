#ifdef __cplusplus
extern "C" {
#endif

SecureVector<byte> sha1(const std::string& acct_name);

const char* fpe_encrypt(const char* plainNumber,  const char* key const char* tweak);

const char* fpe_decrypt(const char* encNumber,  const char* key const char* tweak);


#ifdef __cplusplus
} // extern "C"
#endif