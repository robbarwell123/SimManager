# Sim Manager v1.0

## Table of Contents

- [Overview](#Overview)
- [Quick Start Guide](#Quick_Install)
- [Important Files](#Important_Files)
- [File Structure](#File_Struture)

## Overview

This repository contains the code for Rob Barwell's submission to Carlton's SYSC 5104 final term project.  Details about the project can be found in the final paper in the documentation directory.  Also in the documentation directory are the Quick Start and Installation Guide for using this software.

## Quick_Install

The quick start guide contains useful information about how to use the software.  It also provides a quick path to download docker containers with the software already setup.  The quick start guide can be found in the documentation folder here: [Quick Start Guide](Documentation/QuickStartGuide.docx)

Also located in the guide is the location of all the example and experiment files and how to run them.  I would recommend starting here.

## Important_Files

There are many files in this repository.  Below are some of the important files you may be searching for:

- [Installation Guide](Documentation/InstallationGuide.docx)
- [Final Paper](Documentation/StratAirliftFinalPaper.pdf)

## File_Struture

This respository contains a number of components.  Below is a quick description of the various folders and what you will find in them:

- DockerFiles, contains the configuration files to make docker containers
- Documentation, contains all documentation for the project including example and experiment XMLs
- JavaSrc, contains the source code for all J2EE microservices
- Scripts, contain scripts to run and install Sim Manager
- Settings, contains the settings for microservices and webapps when using containers
- Shared, contains the location of all supporting material required to execute an experiment
- Simulators, is where you place the simulator you want to use
- WebRoot, contains all the webapp source code