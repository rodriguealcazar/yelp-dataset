FROM ubuntu:18.04

LABEL version="zeppelin-0.8.0"

WORKDIR /tmp

RUN apt-get update && \
    apt-get -y upgrade && \
    apt-get install -y software-properties-common && \
    apt-get -y install wget

# Java 8
ARG JAVA_MAJOR_VERSION=8
ENV JAVA_HOME /usr/lib/jvm/java-${JAVA_MAJOR_VERSION}-oracle
RUN \
  echo oracle-java${JAVA_MAJOR_VERSION}-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd${JAVA_MAJOR_VERSION}team/java && \
  apt-get install -y oracle-java${JAVA_MAJOR_VERSION}-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk${JAVA_MAJOR_VERSION}-installer

# SPARK
ENV SPARK_VERSION 2.1.3
ENV SPARK_PACKAGE spark-${SPARK_VERSION}-bin-hadoop2.7
ENV SPARK_HOME /usr/spark-${SPARK_VERSION}
ENV PATH $PATH:${SPARK_HOME}/bin
RUN wget -nv https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/${SPARK_PACKAGE}.tgz && \
    tar -zxvf ${SPARK_PACKAGE}.tgz && \
    rm -rf ${SPARK_PACKAGE}.tgz && \
    mv /tmp/$SPARK_PACKAGE $SPARK_HOME

# Zeppelin
ARG ZEPPELIN_VERSION="0.8.0"
ARG ZEPPELIN_HOME="/usr/zeppelin"
ENV ZEPPELIN_HOME ${ZEPPELIN_HOME}
ENV ZEPPELIN_LOG ${ZEPPELIN_HOME}/logs
ENV ZEPPELIN_NOTEBOOK_DIR ${ZEPPELIN_HOME}/notebook
RUN wget -nv http://archive.apache.org/dist/zeppelin/zeppelin-${ZEPPELIN_VERSION}/zeppelin-${ZEPPELIN_VERSION}-bin-netinst.tgz && \
    tar -zxvf zeppelin-${ZEPPELIN_VERSION}-bin-netinst.tgz && \
    rm -rf zeppelin-${ZEPPELIN_VERSION}-bin-netinst.tgz && \
    mv zeppelin-${ZEPPELIN_VERSION}-bin-netinst ${ZEPPELIN_HOME} && \
    echo "tail -F /${ZEPPELIN_HOME}/logs/*" >> ${ZEPPELIN_HOME}/bin/zeppelin-daemon.sh

COPY notebook ${ZEPPELIN_HOME}/notebook
COPY yelp_academic_dataset_business.json.gz /tmp/

EXPOSE 8080 4040 4041

CMD ["/usr/zeppelin/bin/zeppelin-daemon.sh", "start"]
