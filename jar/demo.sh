#!/bin/bash
 
time java -Xmx8g -jar CHIME_release.jar demo.csv > res.log
grep -i Motif res.log | cut -d: -f2 > res.txt
grep -i dim res.log > getDim.m
