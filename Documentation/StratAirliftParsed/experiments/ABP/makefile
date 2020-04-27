CC=g++
CFLAGS=-std=c++17

INCLUDECADMIUM=-I ../../../../cadmium/include
INCLUDEDESTIMES=-I ../../../../DESTimes/include
INCLUDEATOMICS=-I ../../common/atomics
INCLUDES=../../includes/

# === COUPLED ===
sim.o: abp.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) $(INCLUDEATOMICS) abp.cpp -o abp.o

# === DEPLOY ===
sim: sim.o
	$(CC) -g -o abp abp.o $(INCLUDES)message.o 