CC=g++
CFLAGS=-std=c++17

INCLUDECADMIUM=-I ../../../../cadmium/include
INCLUDEDESTIMES=-I ../../../../DESTimes/include
INCLUDEATOMICS=-I ../../common/atomics
INCLUDES=../../includes/

# === COUPLED ===
sim.o: coldlakev2.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) $(INCLUDEATOMICS) coldlakev2.cpp -o coldlakev2.o

# === DEPLOY ===
sim: sim.o
	$(CC) -g -o coldlakev2 coldlakev2.o $(INCLUDES)StratAirLiftSimMessage.o $(INCLUDES)StratAirliftUtils.o 