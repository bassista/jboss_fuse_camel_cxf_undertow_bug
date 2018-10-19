package test.fuse.cxf.jaxrs.undertow;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

@ApplicationScoped
@ContextName("server3-context")
public class OrderRoute extends RouteBuilder {

    @Produces
    @Named("jsonProvider")
    public JacksonJsonProvider jsonProvider() {
        return new JacksonJsonProvider();
    }

    @Override
    public void configure() throws Exception {
        // use CXF-RS to setup the REST web service using the resource class
        // and use the simple binding style which is recommended to use
        from("cxfrs:http://localhost:8080/ctx3?resourceClasses=test.fuse.cxf.jaxrs.undertow.RestOrderService&bindingStyle=SimpleConsumer&providers=#jsonProvider")
            // call the route based on the operation invoked on the REST web service
            .toD("direct:${header.operationName}");

        // routes that implement the REST services
        from("direct:createOrder")
            .bean("orderService", "createOrder");

        from("direct:getOrder")
            .bean("orderService", "getOrder(${header.id})");

        from("direct:updateOrder")
            .bean("orderService", "updateOrder");

        from("direct:cancelOrder")
            .bean("orderService", "cancelOrder(${header.id})");
    }
}