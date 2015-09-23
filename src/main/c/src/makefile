all: aes.o
	gcc -o aes128  aes.o -L/usr/local/ssl/lib/ -lcrypto -lssl
aes.o: aes.c
	gcc -c -Wall aes.c -I/usr/local/ssl/include/ -o aes.o
