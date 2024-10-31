package lissa.trading.user.service.service.publisher;

public interface StatsPublisher<T> {

    void publishAllStats();

    void publishStatAfterUpdate(T stats);
}
