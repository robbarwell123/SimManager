CC=g++
CFLAGS=-std=c++17

INCLUDECADMIUM=-I ../../../../cadmium/include
INCLUDEDESTIMES=-I ../../../../DESTimes/include
INCLUDEATOMICS=-I ../../common/atomics
INCLUDES=../../includes/

# === COUPLED ===
sim.o: stratairlifttest.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) $(INCLUDEATOMICS) stratairlifttest.cpp -o stratairlifttest.o

# === DEPLOY ===
sim: sim.o
	$(CC) -g -o stratairlifttest stratairlifttest.o $(INCLUDES)StratAirLiftSimMessage.o $(INCLUDES)StratAirliftUtils.o 