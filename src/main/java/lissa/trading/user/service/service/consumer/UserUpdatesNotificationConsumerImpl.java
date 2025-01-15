package lissa.trading.user.service.service.consumer;

import lissa.trading.user.service.dto.notification.UserUpdateNotificationDto;
import lissa.trading.user.service.service.publisher.NotificationContext;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.service.UserService;
import lissa.trading.user.service.service.creation.temp.TempUserCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserUpdatesNotificationConsumerImpl implements UserUpdatesNotificationConsumer {

    private final NotificationContext notificationContext;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TempUserCreationService tempUserCreationService;
    private final TempUserRegMapper tempUserRegMapper;
    private final UserRepository userRepository;

    @Value("${integration.rabbit.user-service.user-update-queue.routing-key}")
    private String userUpdateRoutingKey;

    @RabbitListener(queues = "${integration.rabbit.user-service.user-update-queue.name}")
    @Transactional
    @Override
    public void receiveUserUpdateNotification(UserUpdateNotificationDto userUpdateDto, @Header("amqp_receivedRoutingKey") String routingKey) {
        log.info("received user update notification: {}", userUpdateDto);
        if (routingKey.equals(userUpdateRoutingKey)) {
            log.info("not processing, returning");
            return;
        }
        notificationContext.setFromExternalSource(true);
        log.info("processing user update notification, setting event context: {}", notificationContext);
        processNotification(userUpdateDto);
    }

    private void processNotification(UserUpdateNotificationDto userUpdate) {
        log.info("processing user update notification: {}", userUpdate);
        switch (userUpdate.getOperation()) {
            case REGISTER:
                registerUser(userUpdate);
                return;
            case UPDATE:
                updateUser(userUpdate);
        }
    }

    private void registerUser(UserUpdateNotificationDto userUpdate) {
        if (userRepository.existsByTelegramNickname(userUpdate.getTelegramNickname())) {
            log.info("user already exists, updating, {}", userUpdate);
            updateUser(userUpdate);
            return;
        }
        log.info("registering user {}", userUpdate);
        tempUserCreationService.createTempUser(
                tempUserRegMapper.toUserInfoDto(userUpdate));
    }

    private void updateUser(UserUpdateNotificationDto userUpdate) {
        if (!userRepository.existsByTelegramNickname(userUpdate.getTelegramNickname())) {
            log.info("user does not exist, registering, {}", userUpdate);
            registerUser(userUpdate);
            return;
        }
        log.info("updating user {}", userUpdate);
        userService.updateUser(userUpdate.getExternalId(),
                           userMapper.toUserPatchDto(userUpdate));
    }
}
