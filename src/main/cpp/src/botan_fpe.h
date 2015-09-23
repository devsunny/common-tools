#ifdef __cplusplus
extern "C" {
#endif

BOTAN_DLL void botan_fpe_init();
BOTAN_DLL u64bit encrypt_number(u64bit plain_num, const char* key_chars, const char* tweak_chars);
BOTAN_DLL u64bit decrypt_number(u64bit enc_num, const char* key_chars, const char* tweak_chars);
BOTAN_DLL void clean_up(char* enc);
#ifdef __cplusplus
} // extern "C"
#endif