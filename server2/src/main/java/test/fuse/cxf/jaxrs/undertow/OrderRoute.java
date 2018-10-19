package test.fuse.cxf.jaxrs.undertow;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.cxf.jaxrs.BindingStyle;
import org.apache.camel.component.cxf.jaxrs.CxfRsComponent;
import org.apache.camel.component.cxf.jaxrs.CxfRsEndpoint;

@ApplicationScoped
@ContextName("server2-context")
public class OrderRoute extends RouteBuilder {
   
  public CxfRsEndpoint restEndpoint(String endpointUri, Class<?> clazz, CamelContext context) {
    final CxfRsEndpoint serviceEndpoint =
        new CxfRsEndpoint(endpointUri, new CxfRsComponent(context));
    serviceEndpoint.addResourceClass(clazz);

    serviceEndpoint.setBindingStyle(BindingStyle.SimpleConsumer);
    List providers = new ArrayList<>();
    providers.add(new JacksonJsonProvider());
    serviceEndpoint.setProviders(providers);

    serviceEndpoint.setBridgeErrorHandler(true);
    serviceEndpoint.setLoggingFeatureEnabled(true);
    serviceEndpoint.setSkipFaultLogging(false);
    serviceEndpoint.setSynchronous(true);

    return serviceEndpoint;
  }

  @Override
  public void configure() throws Exception {
    // use CXF-RS to setup the REST web service using the resource class
    // and use the simple binding style which is recommended to use
	from(restEndpoint("http://0.0.0.0:80/ctx2/restapi",RestOrderService.class,getContext()))
        // call the route based on the operation invoked on the REST web service
        .toD("direct:${header.operationName}");

    // routes that implement the REST services
    from("direct:getOrder")
        .bean("orderService", "getOrder(${header.id})");
  }
    
}
