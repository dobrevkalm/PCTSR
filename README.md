## Description
The Prize Collecting Traveling Sales Representative (PCTSR) is an NP-hard problem in theoretical computer science. Given a list of marketplaces, travel costs (e.g. distances between them) and a non-negative prize to collect at each market, the goal is to collect a given quota while minimizing the length of the tour.

## Implementation
The implementation is made using Java 8 and JavaFX. In order to run it, you need to make sure your JDK version supports JavaFX as some of them have it removed. More information about JavaFX and how to run it can be found on https://openjfx.io/.

The algorithm runs on the list of the most profitable companies in Denmark for 2019, based on the data from http://www.largestcompanies.com/toplists/denmark/largest-companies-by-earnings/.

The companies location was gathered using Pyhon 3 and GeoPy (https://geopy.readthedocs.io/en/stable/), and the distances between the companies was calculated based on the real road network thanks to https://github.com/Project-OSRM/.

## The GUI
The simple grapgic user interface allows the user to enter starting vertex (the company they wish to start from), desired profit (the profit to be collected), number of agents (each agent will have an unique route) and choose between two possible solutions (one or two, representing two different algorithms).

## How to run
 - javac *.java - to compile all the classes
 - java Main - run the GUI
 - java Main -t - run tests
 - java Main -rp - compute a tour and output it on the console (without the GUI)
 - java Main -e - run all the experiments (results are saved to .csv files)
 - java Main -e {1, 2, 3, 4, 5} - run a specific experiment
    = 1 - HeuristicOne coefficient
    = 2 - kmax parameter for HeuristicTwo
    = 3 - route modification percent for HeuristicTwo
    = 4 - mutations ratio for HeuristicTwo
    = 5 - comparisson between all heuristic methods

## Authors
Adrianna Wiacek (https://github.com/losica) & Kalin Dobrev (https://github.com/dobrevkalm)
