FROM tilos/java
RUN mkdir -p /host/
RUN mkdir -p /etc/service/streamer
RUN mkdir -p /etc/service/backend
ADD docker/streamer /etc/service/streamer/run
ADD docker/backend /etc/service/backend/run
ADD streamer/target/streamer.jar /host/
ADD service/target/service-jar-with-dependencies.jar /host/
ADD icecast-stat/target/icecast-stat.jar /host/
ADD docker/icecast-stat /etc/cron.d/icecast-stat
ADD prerender/target/prerender.jar /host/