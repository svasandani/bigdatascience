#!/bin/bash

module load jdk/1.8.0_271

echo "Compiling"
srun --mem=8GB --time=00:20:00 --cpus-per-task=1 javac DataExploration.java
echo "Running"
srun --mem=8GB --time=02:00:00 --cpus-per-task=1 java DataExploration