package lissa.trading.user.service.service.publisher;

import lissa.trading.user.service.dto.response.UserStatsReportDto;
import lissa.trading.user.service.exception.UserNotFoundException;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatsPublisher implements StatsPublisher<User> {

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void publishAllStats() {
        List<UserStatsReportDto> users = userRepository.findAll().stream()
                .map(userMapper::toUserStatsReportDto).toList();

        if (users.isEmpty() || users == null) {
            log.error("Error getting all users");
            throw new UserNotFoundException("Error getting all users");
        }

        rabbitTemplate.convertAndSend("all-users-stats-queue", users);
        log.info("Users successfully published");
    }

    public void publishUserDataAfterUpdate(User user) {
        UserStatsReportDto userStatsReportDto = userMapper.toUserStatsReportDto(user);
        rabbitTemplate.convertAndSend("updated-user-stats-queue", userStatsReportDto);
        log.info("User successfully published after update");
    }
}
