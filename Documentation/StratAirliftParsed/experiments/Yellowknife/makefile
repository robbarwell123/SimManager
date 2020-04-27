CC=g++
CFLAGS=-std=c++17

INCLUDECADMIUM=-I ../../../../cadmium/include
INCLUDEDESTIMES=-I ../../../../DESTimes/include
INCLUDEATOMICS=-I ../../common/atomics
INCLUDES=../../includes/

# === COUPLED ===
sim.o: yellowknife.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) $(INCLUDEATOMICS) yellowknife.cpp -o yellowknife.o

# === DEPLOY ===
sim: sim.o
	$(CC) -g -o yellowknife yellowknife.o $(INCLUDES)StratAirLiftSimMessage.o $(INCLUDES)StratAirliftUtils.o 