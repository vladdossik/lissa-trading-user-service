package lissa.trading.user.service.service.publisher.stats;

public interface StatsPublisher<T> {

    void publishAllUsersData();

    void publishUserData(T data);
}