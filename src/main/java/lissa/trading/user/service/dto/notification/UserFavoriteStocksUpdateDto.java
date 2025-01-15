package lissa.trading.user.service.dto.notification;

import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFavoriteStocksUpdateDto {
    private UUID externalId;
    List<FavoriteStocksEntity> favoriteStocksEntity;
}
