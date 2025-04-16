//package com.artifact.configuration;
//
//import org.slf4j.MDC;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import reactor.core.publisher.Mono;
//
//@Configuration
//public class TraceConfig {
//
//    @Bean
//    public GlobalFilter traceIdFilter() {
//        return (exchange, chain) -> {
//            String traceId = MDC.get("traceId");
//            if (traceId != null) {
//                exchange.getRequest().mutate()
//                        .header("X-B3-TraceId", traceId)
//                        .build();
//            }
//            return chain.filter(exchange)
//                    .then(Mono.fromRunnable(() -> {
//                        MDC.put("traceId", exchange.getRequest().getHeaders().getFirst("X-B3-TraceId"));
//                    }));
//        };
//    }
//}
