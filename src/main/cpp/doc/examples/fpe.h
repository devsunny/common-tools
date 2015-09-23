#ifdef __cplusplus
extern "C" {
#endif
void botan_fpe_init();
const char* botan_fpe_encrypt(const char* num_str, int length, const char* key_chars, const char* tweak_chars);
const char* botan_fpe_decrypt(const char* num_str, int length, const char* key_chars, const char* tweak_chars);

#ifdef __cplusplus
} // extern "C"
#endif