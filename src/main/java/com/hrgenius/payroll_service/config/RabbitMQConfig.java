package com.hrgenius.payroll_service.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange:hrgenius.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key-payroll-processed:payroll.processed}")
    private String routingKeyPayrollProcessed;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue payrollProcessedQueue() {
        return new Queue("payroll.processed.queue", true);
    }

    @Bean
    public Binding payrollBinding(Queue payrollProcessedQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(payrollProcessedQueue)
                .to(exchange)
                .with(routingKeyPayrollProcessed);
    }

    // Is bean se startup par hi queue RabbitMQ par create ho jayegi
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}