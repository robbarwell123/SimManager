CC=g++
CFLAGS=-std=c++17

INCLUDECADMIUM=-I ../../../../cadmium/include
INCLUDEDESTIMES=-I ../../../../DESTimes/include
INCLUDEATOMICS=-I ../../common/atomics
INCLUDES=../../includes/

# === COUPLED ===
sim.o: coldlakev1.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) $(INCLUDEATOMICS) coldlakev1.cpp -o coldlakev1.o

# === DEPLOY ===
sim: sim.o
	$(CC) -g -o coldlakev1 coldlakev1.o $(INCLUDES)StratAirLiftSimMessage.o $(INCLUDES)StratAirliftUtils.o 