Ludii contextual RL
==============================

We apply contextual RL to different games picked from the Ludii plateform.

Project Organization
------------

    ├── LICENSE
    ├── Makefile           <- Makefile with commands like `make data` or `make train`
    ├── README.md          <- The top-level README for developers using this project.
    ├── data
    │   ├── external       <- Data from third party sources.
    │   ├── interim        <- Intermediate data that has been transformed.
    │   ├── processed      <- The final, canonical data sets for modeling.
    │   └── raw            <- The original, immutable data dump.
    |
    ├── requirements.txt   <- The requirements file for reproducing the analysis environment, e.g.
    │                         generated with `pip freeze > requirements.txt`
    │
    ├── setup.py           <- makes project pip installable (pip install -e .) so src can be imported
    ├── src                <- Source code for use in this project.
    │   └── main
    │       ├── java       <- Java code for Ludii
    |       |   ├── data
    |       |   |   └── MakeDataset.java <- Java code to make dataset from Ludii games
    |       |   |
    |       |   ├── mcts   <- Java code for MCTS agent in Ludii
    |       |   |   ├── ExampleUCT.java
    |       |   |   └── ExampleDUCT.java
    |       |   |
    |       |   ├── random  <- Java code for random agent in Ludii
    |       |   |   └── RandomAI.java
    |       |   |
    |       |   └── utils   <- Java code for utils : Tuples, Gson uses, etc. V1 = State, V2 = Features
    |       |       ├── Tuple3.java 
    |       |       ├── TupleAI.java 
    |       |       ├── TupleIntermediary.java 
    |       |       ├── TupleIntermediaryV2.java
    |       |       ├── utilsGsonV1.java
    |       |       └── utilsGsonV2.java
    |       |
    │       └── resouces   <- Local dependency for Ludii (Ludii.jar)
    |
    ├── tox.ini            <- tox file with settings for running tox; see tox.readthedocs.io
    │
    └── pom.xml            <- pom file with settings for running maven; see maven.apache.org

--------

<p><small>Project based on the <a target="_blank" href="https://drivendata.github.io/cookiecutter-data-science/">cookiecutter data science project template</a>.</small></p>

## How to launch it
First run 
```bash 
make install
``` 
to install Ludii-1.3.11.jar in your .m2 repository. 

(I don't know if necessary but) you can compile the java code with 
```bash
make compile
```

Then just run 
```bash
make
``` 
everytime you want, for it to create data on all the games possible in Ludii. It will play NUM_RUN runs per game.

Finally you can join each singular dataset into one big dataset with 
```bash
make combine
```
It will take all data from data/raw and combine them into one big dataset named combined.

/!\ It will not erase the previous combined dataset if it exists.