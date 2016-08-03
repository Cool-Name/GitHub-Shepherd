#!/bin/bash
# Grabs the process containing shepherd
# and kills it

pid = 'ps aux | grep Shepherd | awk '{print $2}'
kill -9 $pid
