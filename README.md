# Keyword Extraction

## Usage
Make sure to run the following commands on a Bash shell on NYU HPC's Greene cluster.

### Data exploration
    $ ./DataExploration.sh

### *n*-gram analysis
    $ ./NgramAnalysis.sh

### Keyword extraction
Before running these `.sh` files, replace the string labelled:

    /path/to/stanford-corenlp-4.2.0/

with the appropriate path. Then, run the file as usual.

#### POS tagging
    $ ./POSTagger.sh

#### Dependency parser
    $ ./DependencyParser.sh

#### Named entity recognition
##### Entire dataset
    $ ./NamedEntities.sh

##### Positive and negative
    $ ./NamedEntitiesPosNeg.sh