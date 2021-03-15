#!/bin/bash
echo "Compiling"
srun --mem=1GB --time=00:01:00 --cpus-per-task=1 javac HelloWorld.java
echo "Running"
srun --mem=1GB --time=00:15:00 --cpus-per-task=1 java HelloWorld