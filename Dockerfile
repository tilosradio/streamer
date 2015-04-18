FROM tilos/wildfly
ADD resteasy-atom/target/atom-provider.jar /opt/jboss/wildfly/standalone/deployments/
ADD service/target/service.war /opt/jboss/wildfly/standalone/deployments/
