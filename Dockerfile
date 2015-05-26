FROM tilos/java
RUN mkdir -p /host/
RUN mkdir -p /etc/service/streamer
RUN mkdir -p /etc/service/backend
ADD docker/streamer /etc/service/streamer/run
ADD docker/backend /etc/service/backend/run
ADD streamer/target/streamer.jar /host/
ADD service/target/target/service-jar-with-dependencies.jar /host/
