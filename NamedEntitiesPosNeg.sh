#!/bin/bash

module load jdk/1.8.0_271

for file in `find /path/to/stanford-corenlp-4.2.0/ -name "*.jar"`; do export CLASSPATH="$CLASSPATH:`realpath $file`"; done

echo "Compiling"
srun --mem=8GB --time=00:20:00 --cpus-per-task=1 javac NamedEntitiesPosNeg.java
echo "Running"
srun --mem=8GB --time=03:00:00 --cpus-per-task=1 java NamedEntitiesPosNeg