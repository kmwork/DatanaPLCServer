package demo;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;


public class DemoKosyaCamelApp {


    public static void main(String[] args) throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        registry.bind("s7Bean", S7Bean.class);

        CamelContext camelContext = new DefaultCamelContext(registry);
        camelContext.addRoutes(new S7SocketForCamelRoute());

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start", "Packt");

        camelContext.stop();
    }
}