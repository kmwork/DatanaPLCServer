/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.datana.steel.camel.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultMessage;
import ru.datana.steel.camel.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.camel.util.JsonParserClientUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class PlcProcessor implements Processor {

    private final static String PREFIX_LOG = "[Kafka:DatanaKafkaProducer] ";

    @PostConstruct
    protected void postConstruct() {
        log.info(PREFIX_LOG + "Запуск Kafka-сервиса.");
    }

    @PreDestroy
    protected void preDestroy() {
        log.info(PREFIX_LOG + "Остановка Клиент-Kafka-сервиса.");
    }

    private AtomicInteger counter = new AtomicInteger(0);

    private JsonParserClientUtil clientUtil = JsonParserClientUtil.getInstance();

    @Override
    public void process(Exchange exchange) throws Exception {
        int indexMsg = counter.incrementAndGet();
        String prefix = PREFIX_LOG + "[onMessage, index = " + indexMsg + "] ";
        log.debug(prefix + "Генерация команды");
        JsonRootSensorRequest result = clientUtil.loadJsonRequest();
        Message msg = new DefaultMessage(exchange);
        msg.setBody(result);
        exchange.setMessage(msg);
    }

}
