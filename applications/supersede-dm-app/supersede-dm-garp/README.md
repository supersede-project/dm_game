<link rel="shortcut icon" type="image/png" href="images/favicon.png">
# [![SUPERSEDE](images/SUPERSEDE-logo.png)](https://www.supersede.eu/) project: Multi-decision-maker Requirements Prioritization
This repository contains code, artefacts, and configuration, developed in SUPERSEDE T3.3, that provide techniques and tools supporting the multi-decision-maker requirements process.

Repository Structure:
- lib: local dependencies required by the project
- resources/input: various input files required by the tool
- src: java source and test files
- pom.xml: maven build file

To build:
mvn package [-DskipTests]

To execute:
java -jar target/iga-0.0.1-SNAPSHOT-jar-with-dependencies.jar INPUT_DIR OUTPUT_DIR

The contents of this project are licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Main contact: Fitsum Kifetew <kifetew@fbk.eu>

<center>![Project funded by the European Union](images/european.union.logo.png)</center>
