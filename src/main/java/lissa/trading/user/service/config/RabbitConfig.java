package lissa.trading.user.service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${integration.rabbit.statistics-service.user-queue}")
    private String userStatsQueue;

    @Value("${integration.rabbit.user-service.exchange.name}")
    private String exchange;

    @Value("${integration.rabbit.user-service.favourite-stocks-queue.name}")
    private String userFavoriteStocksQueue;

    @Value("${integration.rabbit.user-service.user-update-queue.name}")
    private String userUpdateQueue;

    @Bean
    public Queue usersStatsQueue() {
        return new Queue(userStatsQueue, true);
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue userFavoriteStocksQueue() {
        return new Queue(userFavoriteStocksQueue, true);
    }

    @Bean public Queue userUpdateQueue() {
        return new Queue(userUpdateQueue, true);
    }

    @Bean
    public Binding favoriteStocksBinding(Queue userFavoriteStocksQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userFavoriteStocksQueue)
                .to(userExchange)
                .with("*.favourite-stocks");
    }

    @Bean
    public Binding userUpdateBinding(Queue userUpdateQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userUpdateQueue)
                .to(userExchange)
                .with("*.user-update");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}