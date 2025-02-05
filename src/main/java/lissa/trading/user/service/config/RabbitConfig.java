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

    @Value("${integration.rabbit.outbound.statistics-service.user.queue}")
    private String userStatsQueue;

    @Value("${integration.rabbit.outbound.user-service.favourite-stocks.queue}")
    private String userServiceFavoriteStocksQueue;

    @Value("${integration.rabbit.outbound.user-service.user-update.queue}")
    private String userServiceUpdateQueue;

    @Value("${integration.rabbit.inbound.tg-bot.favourite-stocks.queue}")
    private String tgBotFavoriteStocksQueue;

    @Value("${integration.rabbit.inbound.tg-bot.user-update.queue}")
    private String tgBotUserUpdateQueue;

    @Value("${integration.rabbit.outbound.user-notifications.exchange}")
    private String exchange;

    @Value("${integration.rabbit.outbound.user-service.favourite-stocks.routing-key}")
    private String userServiceFavouriteStocksQueueRoutingKey;

    @Value("${integration.rabbit.outbound.user-service.user-update.routing-key}")
    private String userServiceUpdateQueueRoutingKey;

    @Value("${integration.rabbit.inbound.tg-bot.favourite-stocks.routing-key}")
    private String tgBotFavouriteStocksQueueRoutingKey;

    @Value("${integration.rabbit.inbound.tg-bot.user-update.routing-key}")
    private String tgBotUpdateQueueRoutingKey;

    @Bean
    public Queue usersStatsQueue() {
        return new Queue(userStatsQueue, true);
    }

    @Bean
    public Queue userServiceFavoriteStocksQueue() {
        return new Queue(userServiceFavoriteStocksQueue, true);
    }

    @Bean
    public Queue userServiceUserUpdateQueue() {
        return new Queue(userServiceUpdateQueue, true);
    }

    @Bean
    public Queue tgBotFavoriteStocksQueue() {return new Queue(tgBotFavoriteStocksQueue, true);}

    @Bean
    public Queue tgBotUserUpdateQueue() {return new Queue(tgBotUserUpdateQueue, true);}

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding userServiceFavoriteStocksBinding(Queue userServiceFavoriteStocksQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userServiceFavoriteStocksQueue)
                .to(userExchange)
                .with(userServiceFavouriteStocksQueueRoutingKey);
    }

    @Bean
    public Binding userServiceUserUpdateBinding(Queue userServiceUserUpdateQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userServiceUserUpdateQueue)
                .to(userExchange)
                .with(userServiceUpdateQueueRoutingKey);
    }

    @Bean
    public Binding tgBotFavoriteStocksBinding(Queue tgBotFavoriteStocksQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(tgBotFavoriteStocksQueue)
                .to(userExchange)
                .with(tgBotFavouriteStocksQueueRoutingKey);
    }

    @Bean
    public Binding tgBotUserUpdateBinding(Queue tgBotUserUpdateQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(tgBotUserUpdateQueue)
                .to(userExchange)
                .with(tgBotUpdateQueueRoutingKey);
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