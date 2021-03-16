#!/bin/bash

module load jdk/1.8.0_271

echo "Running JAR file"
srun --mem=8GB --time=00:20:00 --cpus-per-task=1 java -jar output/DataExploration.jar