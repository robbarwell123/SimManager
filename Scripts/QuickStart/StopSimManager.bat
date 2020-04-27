@ECHO OFF
ECHO Stopping View Experiment
docker stop ViewExperiment
ECHO Stopping Run Experiment
docker stop RunExperiment
ECHO Stopping Manage Experiment
docker stop ManageExperiment
ECHO Waiting for Microservices to Stopping
TIMEOUT 30
ECHO Stopping Sim Visualizer
docker stop SimVisualizer
ECHO Stopping Sim Manager
docker stop SimManager
ECHO Stopping Database
docker stop mySQLDB
ECHO Completed Sim Manager Shutdown