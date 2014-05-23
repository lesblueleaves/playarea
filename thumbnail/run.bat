@echo off
cls
title "SpringRoll Thumbnail Server(Development)"
set MAVEN_OPTS=-Xmx2048m
mvn -o -q exec:java -Dexec.mainClass=com.cisco.d3a.filemon.MonitorMain -Denvironment=development %*