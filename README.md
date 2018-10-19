# jboss_fuse_camel_cxf_undertow_bug
poc of bug on undeploing a war on wildfly/boss with camel subsystem - wrong context is undeployed

1)compile project
2)deploy the 3 wars into jboss eap fuse (or use wildfly with camel extention patch)
3)reboot jboss
4)you can verify that all services are up and running executing the following request in get:
  * http://localhost:8080/ctx1/restapi/orders1/1
  * http://localhost:8080/ctx2/restapi/orders2/1
  * http://localhost:8080/ctx3/restapi/orders3/1
4)re-deploy server3.war
5)re-deploy server2.war
6)try to call http://localhost:8080/ctx3/restapi/orders3/1 you'll get a 404 response
5)looking at logs you'll see that during undeploy of server2.war wrong context has been shoutdown on undertow side:

org.jboss.as.repository - WFLYDR0001: Content added at location C:\jboss-eap-7.1\standalone\data\content\c9\1b1e31cf8a7a8f3900bc9216af0cf127238a73\content
org.wildfly.extension.camel - Remove Camel endpoint: http://127.0.0.1:8080/app2
org.wildfly.extension.undertow - WFLYUT0022: Unregistered web context: '/app2' from server 'default-server'
org.apache.camel.cdi.CamelContextProducer - Camel CDI is stopping Camel context [server2-context]
org.apache.camel.impl.DefaultCamelContext - Apache Camel 2.21.0.fuse-000112-redhat-3 (CamelContext: server2-context) is shutting down
org.apache.camel.impl.DefaultShutdownStrategy - Starting to graceful shutdown 2 routes (timeout 300 seconds)
org.wildfly.extension.camel - Remove Camel endpoint: http://127.0.0.1:8080/ctx3/restapi
org.wildfly.extension.undertow - WFLYUT0022: Unregistered web context: '/ctx3/restapi' from server 'default-server'
org.apache.camel.impl.DefaultShutdownStrategy - Route: route36 shutdown complete, was consuming from: http://0.0.0.0:80/ctx2/restapi
org.apache.camel.impl.DefaultShutdownStrategy - Route: route37 shutdown complete, was consuming from: direct://getOrder
org.apache.camel.impl.DefaultShutdownStrategy - Graceful shutdown of 2 routes completed in 0 seconds
org.wildfly.extension.camel - Camel context stopped: server2-context
org.wildfly.extension.camel - Unbind camel naming object: java:jboss/camel/context/server2-context
org.apache.camel.impl.DefaultCamelContext - Apache Camel 2.21.0.fuse-000112-redhat-3 (CamelContext: server2-context) uptime 2 minutes
org.apache.camel.impl.DefaultCamelContext - Apache Camel 2.21.0.fuse-000112-redhat-3 (CamelContext: server2-context) is shutdown in 0.010 seconds
org.jboss.as.server.deployment - WFLYSRV0028: Stopped deployment server2.war (runtime-name: server2.war) in 104ms
org.jboss.as.server.deployment - WFLYSRV0027: Starting deployment of "server2.war" (runtime-name: "server2.war")

Here is the issue:

org.wildfly.extension.camel - Remove Camel endpoint: http://127.0.0.1:8080/ctx3/restapi
org.wildfly.extension.undertow - WFLYUT0022: Unregistered web context: '/ctx3/restapi' from server 'default-server'
