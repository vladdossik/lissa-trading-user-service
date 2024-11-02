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
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatsPublisher implements StatsPublisher<User> {

    @Value("${integration.rabbit.statistics-service.user-queue}")
    private String userStatsQueue;

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void publishAllUsersData() {
        int batchSize = 200;
        List<UserStatsReportDto> users = userRepository.findAll().stream()
                .map(userMapper::toUserStatsReportDto)
                .toList();

        if (CollectionUtils.isEmpty(users)) {
            log.error("Users list is empty");
            return;
        }

        for (int i = 0; i < users.size(); i += batchSize) {
            int batchEnd = Math.min(users.size() - 1, i + batchSize);
            List<UserStatsReportDto> batchUsers = users.subList(i, batchEnd);
            rabbitTemplate.convertAndSend(userStatsQueue, batchUsers);
            log.info("Successfully published {} batch", i);
        }
        log.info("Users successfully published");
    }

    public void publishUserData(User user) {
        UserStatsReportDto userStatsReportDto = userMapper.toUserStatsReportDto(user);
        rabbitTemplate.convertAndSend(userStatsQueue, List.of(userStatsReportDto));
        log.info("User successfully published after update");
    }
}