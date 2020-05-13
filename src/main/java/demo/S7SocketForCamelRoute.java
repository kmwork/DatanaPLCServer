package demo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class S7SocketForCamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer:kostya?period=5000")
                /*from("timer:bar")*/
                .bean(S7Bean.class);

        from("direct:start").to("bean:s7Bean").to("log:sample");
    }


//    @Override
//    public void configure() throws Exception {
//        final String cxfUri =
//                String.format("cxf:http://localhost:%d/paymentService?serviceClass=%s",
//                        port1, Payment.class.getCanonicalName());
//
//        from(cxfUri)
//                .id("wsRoute")
//                .transform(simple("${in.body[0]}"))
//                .log("request = ${body}")
//                .bean(PaymentServiceImpl.class)
//                .log("response = ${body}");
//    }
}
