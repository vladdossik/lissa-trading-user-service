package lissa.trading.user.service.service.publisher;

public interface StatsPublisher<T> {

    void publishAllUsersData();

    void publishUserData(T data);
}