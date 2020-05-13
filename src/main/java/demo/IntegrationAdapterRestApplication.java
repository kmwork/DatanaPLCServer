package demo;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Configuration
public class IntegrationAdapterRestApplication {

    String body;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(IntegrationAdapterRestApplication.class, args);

//****************Get JSON FROM REST********************************************

        CamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        ProducerTemplate template = camelContext.createProducerTemplate();
        Map<String, Object> requestHeaders = new HashMap<>();
        requestHeaders.put(Exchange.HTTP_METHOD, "POST");
        String requestEndpointUri = "http://172.29.40.42:8080/rest/getData";
        String reqestBody = "{ request_id:" + "12}";
        String body = template.requestBodyAndHeaders(requestEndpointUri, reqestBody, requestHeaders, String.class);
        System.out.println(body);

//************** Add ROUTE and send JMS*******************************************
        camelContext.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
//                from("activemq:foo")
//                        .to("log:sample");
                from("timer://bar?fixedRate=true&delay=1&period=10000")
                        .setBody(constant(body))
//                        .convertBodyTo(String.class)
//                        .process(exchange -> exchange.getOut().setBody(exchange.getIn().getBody()))
                        .to("kafka:dima?brokers=172.29.40.42:9092");
            }

        });

    }
}


