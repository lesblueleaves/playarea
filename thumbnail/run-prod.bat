@echo off
cls
title "SpringRoll Thumbnail Server(Production)"
set MAVEN_OPTS=-Xmx1024m
mvn -o -q exec:java -Dexec.mainClass=com.cisco.d3a.filemon.MonitorMain -Denvironment=production %*