CC=g++
CFLAGS=-std=c++17

INCLUDECADMIUM=-I ../../../../cadmium/include
INCLUDEDESTIMES=-I ../../../../DESTimes/include
INCLUDEATOMICS=-I ../../common/atomics
INCLUDES=../../includes/

# === COUPLED ===
sim.o: base.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) $(INCLUDEATOMICS) base.cpp -o base.o

# === DEPLOY ===
sim: sim.o
	$(CC) -g -o base base.o $(INCLUDES)StratAirLiftSimMessage.o $(INCLUDES)StratAirliftUtils.o 