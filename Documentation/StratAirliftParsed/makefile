CC=g++
CFLAGS=-std=c++17

INCLUDECADMIUM=-I ../../cadmium/include
INCLUDEDESTIMES=-I ../../DESTimes/include

#TARGET TO COMPILE ALL THE TESTS TOGETHER (NOT SIMULATOR)
# === COMMON ===
common: StratAirLiftSimMessage.o StratAirliftUtils.o message.o
StratAirLiftSimMessage.o: messages/StratAirLiftSimMessage.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) messages/StratAirLiftSimMessage.cpp -o includes/StratAirLiftSimMessage.o

StratAirliftUtils.o: usrlib/StratAirliftUtils.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) usrlib/StratAirliftUtils.cpp -o includes/StratAirliftUtils.o

message.o: messages/message.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) messages/message.cpp -o includes/message.o

# === SIMS ===
ABP:
	cd experiments; cd ABP; make sim

StratAirliftTest:
	cd experiments; cd StratAirliftTest; make sim

Base:
	cd experiments; cd Base; make sim

ColdLakev1:
	cd experiments; cd ColdLakev1; make sim

ColdLakev2:
	cd experiments; cd ColdLakev2; make sim

ColdLakev3:
	cd experiments; cd ColdLakev3; make sim

Yellowknife:
	cd experiments; cd Yellowknife; make sim

CambridgeBay:
	cd experiments; cd CambridgeBay; make sim

sims: ABP StratAirliftTest Base ColdLakev1 ColdLakev2 ColdLakev3 Yellowknife CambridgeBay

# === SIMS ===
ABPTest:
	cd experiments; cd ABP; ./abp.exe

StratAirliftTestTest:
	cd experiments; cd StratAirliftTest; ./stratairlifttest.exe

BaseTest:
	cd experiments; cd Base; ./base.exe

ColdLakev1Test:
	cd experiments; cd ColdLakev1; ./coldlakev1.exe

ColdLakev2Test:
	cd experiments; cd ColdLakev2; ./coldlakev2.exe

ColdLakev3Test:
	cd experiments; cd ColdLakev3; ./coldlakev3.exe

YellowknifeTest:
	cd experiments; cd Yellowknife; ./yellowknife.exe

CambridgeBayTest:
	cd experiments; cd CambridgeBay; ./cambridgebay.exe

run: ABPTest StratAirliftTestTest BaseTest ColdLakev1Test ColdLakev2Test ColdLakev3Test YellowknifeTest CambridgeBayTest

# === MAIN ===
compile: common sims
all: compile run

#CLEAN COMMANDS
clean: 
	rm -f includes/*
	rm -f experiments/*/*.exe
	rm -f experiments/*/*.o
	rm -f experiments/*/*messages.txt
	rm -f experiments/*/*states.txt
	