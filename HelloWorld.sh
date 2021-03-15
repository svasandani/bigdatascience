#!/bin/bash

module load jdk/1.8.0_271

echo "Running JAR file"
srun --mem=1GB --time=00:01:00 --cpus-per-task=1 java -jar output/HelloWorld.jar