# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM openjdk:8

MAINTAINER Apache Kafka dev@kafka.apache.org
VOLUME ["/opt/kafka-dev"]

# Set the timezone.
ENV TZ="/usr/share/zoneinfo/America/Los_Angeles"

# Do not ask for confirmations when running apt-get, etc.
ENV DEBIAN_FRONTEND noninteractive

# Set the ducker.creator label so that we know that this is a ducker image.  This will make it
# visible to 'ducker purge'.  The ducker.creator label also lets us know what UNIX user built this
# image.
ARG ducker_creator=default
LABEL ducker.creator=$ducker_creator

# Update Linux and install necessary utilities.
RUN apt update && apt install -y sudo netcat iptables rsync unzip wget curl jq coreutils openssh-server net-tools vim python-pip python-dev libffi-dev libssl-dev cmake pkg-config libfuse-dev bsdtar && apt-get -y clean
RUN pip install -U pip==9.0.3 setuptools && pip install --upgrade cffi virtualenv pyasn1 boto3 pycrypto pywinrm ipaddress enum34 && pip install --upgrade ducktape==0.7.1

# Set up ssh
COPY ./ssh-config /root/.ssh/config
RUN ssh-keygen -q -t rsa -N '' -f /root/.ssh/id_rsa && cp -f /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

# Install Cloudera specific binary test dependencies
# following a pattern when naming directories and versions from the jar names in parcel:
# kafka_2.11-0.10.0-kafka-2.1.0.jar -> directory: kafka-0.10.0-kafka-2.1.0, version: 0.10.0-kafka-2.1.0
# kafka_2.11-1.0.1-cdh6.0.0.jar -> directory: kafka-1.0.1-cdh6.0.0, version: 1.0.1-cdh6.0.0
ENV CLOUDERA_REPO="https://archive.cloudera.com/"
ENV CLOUDERA_KAFKA_REPO="${CLOUDERA_REPO}kafka/parcels/"
RUN mkdir -p "/opt/cloudera/kafka-0.9.0-kafka-2.0.0" && \
  curl -s "${CLOUDERA_KAFKA_REPO}2.0.0/KAFKA-2.0.0-1.kafka2.0.0.p0.12-wheezy.parcel" \
    | tar xz --strip-components=1 -C "/opt/cloudera/kafka-0.9.0-kafka-2.0.0" && \
  ln -s "/opt/cloudera/kafka-0.9.0-kafka-2.0.0/lib/kafka/" "/opt/kafka-0.9.0-kafka-2.0.0"
RUN mkdir -p "/opt/cloudera/kafka-0.9.0-kafka-2.0.1" && \
  curl -s "${CLOUDERA_KAFKA_REPO}2.0.1/KAFKA-2.0.1-1.2.0.1.p0.5-wheezy.parcel" \
    | tar xz --strip-components=1 -C "/opt/cloudera/kafka-0.9.0-kafka-2.0.1" && \
  ln -s "/opt/cloudera/kafka-0.9.0-kafka-2.0.1/lib/kafka/" "/opt/kafka-0.9.0-kafka-2.0.1"
RUN mkdir -p "/opt/cloudera/kafka-0.9.0-kafka-2.0.2" && \
  curl -s "${CLOUDERA_KAFKA_REPO}2.0.2/KAFKA-2.0.2-1.2.0.2.p0.5-wheezy.parcel" \
    | tar xz --strip-components=1 -C "/opt/cloudera/kafka-0.9.0-kafka-2.0.2" && \
  ln -s "/opt/cloudera/kafka-0.9.0-kafka-2.0.2/lib/kafka/" "/opt/kafka-0.9.0-kafka-2.0.2"
RUN mkdir -p "/opt/cloudera/kafka-0.10.0-kafka-2.1.0" && \
  curl -s "${CLOUDERA_KAFKA_REPO}2.1.0/KAFKA-2.1.0-1.2.1.0.p0.115-wheezy.parcel" \
    | tar xz --strip-components=1 -C "/opt/cloudera/kafka-0.10.0-kafka-2.1.0" && \
  ln -s "/opt/cloudera/kafka-0.10.0-kafka-2.1.0/lib/kafka/" "/opt/kafka-0.10.0-kafka-2.1.0"
RUN mkdir -p "/opt/cloudera/kafka-0.10.0-kafka-2.1.1" && \
  curl -s "${CLOUDERA_KAFKA_REPO}2.1.1/KAFKA-2.1.1-1.2.1.1.p0.18-wheezy.parcel" \
    | tar xz --strip-components=1 -C "/opt/cloudera/kafka-0.10.0-kafka-2.1.1" && \
  ln -s "/opt/cloudera/kafka-0.10.0-kafka-2.1.1/lib/kafka/" "/opt/kafka-0.10.0-kafka-2.1.1"
RUN mkdir -p "/opt/cloudera/kafka-0.10.0-kafka-2.1.2" && \
  curl -s "${CLOUDERA_KAFKA_REPO}2.1.2/KAFKA-2.1.2-1.2.1.2.p0.6-wheezy.parcel" \
    | tar xz --strip-components=1 -C "/opt/cloudera/kafka-0.10.0-kafka-2.1.2" && \
  ln -s "/opt/cloudera/kafka-0.10.0-kafka-2.1.2/lib/kafka/" "/opt/kafka-0.10.0-kafka-2.1.2"
RUN mkdir -p "/opt/cloudera/kafka-0.10.2-kafka-2.2.0" && \
  curl -s "${CLOUDERA_KAFKA_REPO}2.2.0/KAFKA-2.2.0-1.2.2.0.p0.68-wheezy.parcel" \
    | tar xz --strip-components=1 -C "/opt/cloudera/kafka-0.10.2-kafka-2.2.0" && \
  ln -s "/opt/cloudera/kafka-0.10.2-kafka-2.2.0/lib/kafka/" "/opt/kafka-0.10.2-kafka-2.2.0"
RUN mkdir -p "/opt/cloudera/kafka-0.11.0-kafka-3.0.0" && \
    curl -s "${CLOUDERA_KAFKA_REPO}3.0.0/KAFKA-3.0.0-1.3.0.0.p0.40-jessie.parcel" \
        | tar xz --strip-components=1 -C "/opt/cloudera/kafka-0.11.0-kafka-3.0.0" && \
    ln -s "/opt/cloudera/kafka-0.11.0-kafka-3.0.0/lib/kafka/" "/opt/kafka-0.11.0-kafka-3.0.0"
RUN mkdir -p "/opt/cloudera/kafka-1.0.1-kafka-3.1.0" && \
    curl -s "${CLOUDERA_KAFKA_REPO}3.1.0/KAFKA-3.1.0-1.3.1.0.p0.35-jessie.parcel" \
        | tar xz --strip-components=1 -C "/opt/cloudera/kafka-1.0.1-kafka-3.1.0" && \
    ln -s "/opt/cloudera/kafka-1.0.1-kafka-3.1.0/lib/kafka/" "/opt/kafka-1.0.1-kafka-3.1.0"
# cdh kafka
ENV CLOUDERA_CDH6_REPO="${CLOUDERA_REPO}cdh6/"
RUN mkdir -p "/opt/cloudera/kafka-1.0.1-cdh6.0.0" && \
    curl -s "${CLOUDERA_CDH6_REPO}6.0.0/parcels/CDH-6.0.0-1.cdh6.0.0.p0.537114-xenial.parcel" \
        | bsdtar xzf - --strip-components=1 -C "/opt/cloudera/kafka-1.0.1-cdh6.0.0" && \
    ln -s "/opt/cloudera/kafka-1.0.1-cdh6.0.0/lib/kafka/" "/opt/kafka-1.0.1-cdh6.0.0"

# some downstream version contains a line, which ruins some checks:
# echo "Final CLASSPATH is $CLASSPATH"
RUN sed -i.bak 's/echo "Final CLASSPATH is \$CLASSPATH"//g' "/opt/kafka-1.0.1-cdh6.0.0/bin/kafka-run-class.sh"
RUN sed -i.bak 's/echo "Final CLASSPATH is \$CLASSPATH"//g' "/opt/kafka-1.0.1-kafka-3.1.0/bin/kafka-run-class.sh"

# Install binary test dependencies.
# we use the same versions as in vagrant/base.sh
ARG KAFKA_MIRROR="https://s3-us-west-2.amazonaws.com/kafka-packages"
RUN mkdir -p "/opt/kafka-0.8.2.2" && chmod a+rw /opt/kafka-0.8.2.2 && curl -s "$KAFKA_MIRROR/kafka_2.11-0.8.2.2.tgz" | tar xz --strip-components=1 -C "/opt/kafka-0.8.2.2"
RUN mkdir -p "/opt/kafka-0.9.0.1" && chmod a+rw /opt/kafka-0.9.0.1 && curl -s "$KAFKA_MIRROR/kafka_2.11-0.9.0.1.tgz" | tar xz --strip-components=1 -C "/opt/kafka-0.9.0.1"
RUN mkdir -p "/opt/kafka-0.10.0.1" && chmod a+rw /opt/kafka-0.10.0.1 && curl -s "$KAFKA_MIRROR/kafka_2.11-0.10.0.1.tgz" | tar xz --strip-components=1 -C "/opt/kafka-0.10.0.1"
RUN mkdir -p "/opt/kafka-0.10.1.1" && chmod a+rw /opt/kafka-0.10.1.1 && curl -s "$KAFKA_MIRROR/kafka_2.11-0.10.1.1.tgz" | tar xz --strip-components=1 -C "/opt/kafka-0.10.1.1"
RUN mkdir -p "/opt/kafka-0.10.2.2" && chmod a+rw /opt/kafka-0.10.2.2 && curl -s "$KAFKA_MIRROR/kafka_2.11-0.10.2.2.tgz" | tar xz --strip-components=1 -C "/opt/kafka-0.10.2.2"
RUN mkdir -p "/opt/kafka-0.11.0.3" && chmod a+rw /opt/kafka-0.11.0.3 && curl -s "$KAFKA_MIRROR/kafka_2.11-0.11.0.3.tgz" | tar xz --strip-components=1 -C "/opt/kafka-0.11.0.3"
RUN mkdir -p "/opt/kafka-1.0.2" && chmod a+rw /opt/kafka-1.0.2 && curl -s "$KAFKA_MIRROR/kafka_2.11-1.0.2.tgz" | tar xz --strip-components=1 -C "/opt/kafka-1.0.2"
RUN mkdir -p "/opt/kafka-1.1.1" && chmod a+rw /opt/kafka-1.1.1 && curl -s "$KAFKA_MIRROR/kafka_2.11-1.1.1.tgz" | tar xz --strip-components=1 -C "/opt/kafka-1.1.1"
RUN mkdir -p "/opt/kafka-2.0.0" && chmod a+rw /opt/kafka-2.0.0 && curl -s "$KAFKA_MIRROR/kafka_2.12-2.0.0.tgz" | tar xz --strip-components=1 -C "/opt/kafka-2.0.0"

# Streams test dependencies
RUN curl -s "$KAFKA_MIRROR/kafka-streams-0.10.0.1-test.jar" -o /opt/kafka-0.10.0.1/libs/kafka-streams-0.10.0.1-test.jar
RUN curl -s "$KAFKA_MIRROR/kafka-streams-0.10.1.1-test.jar" -o /opt/kafka-0.10.1.1/libs/kafka-streams-0.10.1.1-test.jar
RUN curl -s "$KAFKA_MIRROR/kafka-streams-0.10.2.2-test.jar" -o /opt/kafka-0.10.2.2/libs/kafka-streams-0.10.2.2-test.jar
RUN curl -s "$KAFKA_MIRROR/kafka-streams-0.11.0.3-test.jar" -o /opt/kafka-0.11.0.3/libs/kafka-streams-0.11.0.3-test.jar
RUN curl -s "$KAFKA_MIRROR/kafka-streams-1.0.2-test.jar" -o /opt/kafka-1.0.2/libs/kafka-streams-1.0.2-test.jar
RUN curl -s "$KAFKA_MIRROR/kafka-streams-1.1.1-test.jar" -o /opt/kafka-1.1.1/libs/kafka-streams-1.1.1-test.jar
RUN curl -s "$KAFKA_MIRROR/kafka-streams-2.0.0-test.jar" -o /opt/kafka-2.0.0/libs/kafka-streams-2.0.0-test.jar

# The version of Kibosh to use for testing.
# If you update this, also update vagrant/base.sy
ARG KIBOSH_VERSION="d85ac3ec44be0700efe605c16289fd901cfdaa13"

# Install Kibosh
RUN apt-get install fuse
RUN cd /opt && git clone -q  https://github.com/confluentinc/kibosh.git && cd "/opt/kibosh" && git reset --hard $KIBOSH_VERSION && mkdir "/opt/kibosh/build" && cd "/opt/kibosh/build" && ../configure && make -j 2

# Set up the ducker user.
RUN useradd -ms /bin/bash ducker && mkdir -p /home/ducker/ && rsync -aiq /root/.ssh/ /home/ducker/.ssh && chown -R ducker /home/ducker/ /mnt/ && echo 'ducker ALL=(ALL) NOPASSWD: ALL' >> /etc/sudoers
USER ducker

CMD sudo service ssh start && tail -f /dev/null
