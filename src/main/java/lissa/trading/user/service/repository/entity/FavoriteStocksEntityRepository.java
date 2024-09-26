package lissa.trading.user.service.repository.entity;

import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteStocksEntityRepository extends JpaRepository<FavoriteStocksEntity, Long> {

    List<FavoriteStocksEntity> findByUserId(Long userId);

    Optional<FavoriteStocksEntity> findByStockNameAndUserId(String stockName, Long userId);

    void deleteByUserId(Long userId);

    void deleteByStockNameAndUserId(String stockName, Long userId);
}