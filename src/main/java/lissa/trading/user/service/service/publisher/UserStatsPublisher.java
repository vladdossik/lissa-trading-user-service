package lissa.trading.user.service.service.publisher;

import lissa.trading.user.service.dto.response.UserStatsReportDto;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatsPublisher implements StatsPublisher<User> {

    @Value("${integration.rabbit.statistics-service.user-queue}")
    private String userStatsQueue;
    private Integer defaultOffset = 0;

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void publishAllUsersData() {
        List<UserStatsReportDto> users;
        int batchSize = 200;
        log.info("batch and offset : {}, {}", batchSize, defaultOffset);

        while (true) {
            users = findAllWithLimitAndOffset(batchSize, defaultOffset);

            if (CollectionUtils.isEmpty(users)) {
                log.info("All users have been published");
                break;
            }

            rabbitTemplate.convertAndSend(userStatsQueue, users);
            defaultOffset += batchSize;
        }

        log.info("Users successfully published");
    }

    @Transactional(readOnly = true)
    protected List<UserStatsReportDto> findAllWithLimitAndOffset(int batchSize, int defaultOffset) {
        return userRepository.findAllWithLimitAndOffset(batchSize, defaultOffset).stream()
                .map(userMapper::toUserStatsReportDto)
                .toList();
    }

    public void publishUserData(User user) {
        UserStatsReportDto userStatsReportDto = userMapper.toUserStatsReportDto(user);
        rabbitTemplate.convertAndSend(userStatsQueue, List.of(userStatsReportDto));
        log.info("User successfully published after update");
    }
}