FROM tilos/wildfly
RUN mkdir -p /host/
RUN mkdir -p /etc/service/streamer
ADD docker/run /etc/service/streamer/
ADD resteasy-atom/target/atom-provider.jar /opt/jboss/wildfly/standalone/deployments/
ADD service/target/service.war /opt/jboss/wildfly/standalone/deployments/
ADD streamer/target/streamer.jar /host/
