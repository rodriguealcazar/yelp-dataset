# Yelp Dataset Challenge

This project contains:

 * a docker config to build a container that runs Spark and Zeppelin
 * a Zeppelin/Spark/Scala notebook
 * a Spark/Java application
 * scripts to build, start and stop the container and to run the application
 
I only had time to answer the following questions:

* Median and p95 opening time during the week, by neighbourhood, city, and state triplet.
* Median and p95 closing time during the week, by neighbourhood, city, and state triplet.

## Implementation

I wrote a Zeppelin notebook where I first investigated the dataset and figured
out how to answer the first question. Then, I took the approach and implemented
it into a fully fledged Spark application.

When faced with a new dataset and some questions about it, I find Zeppelin very
useful for quick and interactive investigation. I used Scala as it gives better
access to the Spark APIs. While not a Scala expert (yet!), notebook are usually
simple enough to not be problematic yet often a chance to learn a few new Scala 
tricks.

You will see that I first loaded the data and then ran different queries to get
the data in the form required to answer the questions. I have tried to leave
useful comments to explain what each paragraph is about.

I tend to prefer using Spark SQL whenever possible rather than the 
Dataframe/Dataset API as it is pretty much standard SQL with some useful extensions.
I find that many operations are easier to write and read in SQL than with the 
Spark API and also easier to share with non-programmers.

### Logic

The logic used to compute the percentiles is fairly simple and contained in 4
SQL queries which:

1. extract the opening hours for only the week days; lower case state, city and 
neighborhood.
1. explode the opening hours to get a separate row for each business for each 
day of the week.
1. split the opening hours string and convert opening and closing time to 
minutes since midnight.
1. group by state, city, neighborhood (only keep if at least 5), calculate the 
percentiles and convert the minutes back to time.

Given that the dataset and logic are the same for opening and closing time, all 
the computation is done for both at the same time and only the output is separate.

### Design

I chose Spark as it is really appropriate for this sort of data processing and
transformation task. Spark has the advantage of hiding the distribution which
means that you can use the same program and run it locally (integration tests, 
coding challenge, ...) or on a large cluster. The only difference will be the
configuration parameters used to start/submit the app.

The entry point is the `org.rodriguealcazar.yelp.DatasetChallenge` class which
contains the Java main. The main simply creates and starts the Spark session and
delegates to other classes the work. This is done to better separate 
responsibilities and also to allow `DatasetChallenge` to be instantiated
programmatically for integration tests (see `DatasetChallengeIT` for an example)
and the classes that perform the logic instantiated (with mocks where necessary)
to write unit tests.

In a real application, the input and output path that are hard-coded here would
be passed as input parameters to the spark-submit command and loaded into a 
config object using a CLI library, e.g. JCommander. That would allow running the
same job in different environments.

The SQL queries are kept in separate file in the`src/main/resources/queries` 
folder. This makes it easier to understand and maintain. It also means that each
query could be unit tested individually by starting a Spark session and setting
up the required tables into it. There was no time to do that here though.

Finally, there are 2 UDFs that allow us to write in code data manipulation that
are a bit cumbersome to write in SQL. These are for splitting the hours string
and converting opening and closing times to minutes and then to convert minutes
to time. Another advantage of UDFs is that they are unit testable.

### Data quality

Not much work was done on cleaning up the data safe for lower casing the state,
city and neighborhood to allow more matches. A quick look at the data shows a
certain inconsistency in naming format for those three fields though which would
require further investigation and a cleaning/preparing step before running the
job.

Note also that the code and queries assume that the data "works" and there is
not much done to handle exception, e.g. an opening time string would is assumed
to be in the format "h:m-h:m" and if it isn't the app will blow up.

## Running the application
 
### Docker image

To be able to view and use the Zeppelin notebook and submit the Spark 
application a Dockerfile is available to build a Docker image that has:

* Java 8
* Spark 2.1.3
* Zeppelin 0.8.0

The image is based on ubuntu:18.04 which is a bit overkill for our
needs. There exist much smaller images for running Java 8 but no time was spent
investigating those.

While the latest version of Spark at the time of writing is 2.3.2, here we use 
Spark 2.1.3. This is because it is the version supported out-of-the-box by 
Zeppelin 0.8.0. Trying to run a newer version of Spark would have proved too
time consuming for this exercise.

#### Prerequisites

* You will need a working installation of [Docker](https://docs.docker.com/install/).
* You will need [Bash](https://www.gnu.org/software/bash/) if you want to run
the scripts provided.

#### Building and running

To start a container you can call:

`./start-container.sh`

This will build the image first which can take a few minutes the first time.

#### Stopping

To stop and remove the container you can call:

`./stop-container.sh`

This will stop the container, automatically remove it, and then delete the 
mounted volume.

### Zeppelin notebook

Once the Docker container is running, you should be able to access from a browser:

`http://localhost:8080/#/notebook/2DTRGQ3RB`

Note: try `0.0.0.0:8080` in case localhost does not work.

### Spark application

To answer the questions, I wrote a Spark/Java application that can be submitted
to any Spark 2.1.x cluster (possibly to some other versions too but this was not
tested).

#### Building and submitting

The application can be built locally using [Gradle](https://gradle.org/) which
is included with the app. You can build, run the (very few) tests and create the
jar file with:

`./gradlew clean assemble`

If you want to build and submit the application to the Spark installation running
inside the container you can call:

`./build-and-run.sh`

This will build, copy the jar and config to the container and submit the 
Spark application as:

```bash
${SPARK_ROOT}/bin/spark-submit \
    --class org.rodriguealcazar.yelp.DatasetChallenge \
    --master local[2] \
    /tmp/yelp-dataset.jar
```

This means that Spark will run on a single machine (the container) with 2
executors in 2 separate threads.

Once the app has finished, the path to the result file will be displayed.
I had no time to look into ownership of the mounted volume so you will need
`sudo` to access the result file.

## Future work

In a real life situation, there are a few things that would have to be done
differently:

 * dataset would live on shared storage like HDFS or S3 (or similar)
 * dataset would be larger and Spark config would have to be tested and tuned
 to make sure the job works and performs (number of executors, memory, 
 partitions, ...)
 * dataset would have to be analyzed and cleaned up to optimize its usefulness
 * sample dataset would be created to use in the integration test to make sure
 the application gives the expected results
 * unit tests would be written for every SQL query
 
As for answering the other questions, new classes simialar to `BusinessDataset`
would be created to run the required queries. In terms of logic:

 * businesses opened after 21:00 could be done by looking at each day's closing
 hours and keeping the max and then counting the number of businesses where the
 max is after 21:00. This would again require exploding the hours into rows.
 * business with highest number of cool reviews would require loading the review
 dataset and joining it with the businesses on the business id. Once done it is
 simply a question of summing the cools grouping by business, state and city and
 getting the max.