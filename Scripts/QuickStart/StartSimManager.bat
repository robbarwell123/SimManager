@ECHO OFF
ECHO Starting Database
docker start mySQLDB
ECHO Starting Sim Manager
docker start SimManager
ECHO Starting Sim Visualizer
docker start SimVisualizer
ECHO Waiting for Database to Start
TIMEOUT 30
ECHO Starting Manage Experiment
docker start ManageExperiment
ECHO Starting Run Experiment
docker start RunExperiment
ECHO Starting View Experiment
docker start ViewExperiment
ECHO Completed Sim Manager Startup