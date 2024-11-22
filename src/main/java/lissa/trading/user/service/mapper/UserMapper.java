package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.dto.response.UserStatsReportDto;
import lissa.trading.user.service.dto.response.UserIdsResponseDto;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.BalanceEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "externalId")
    @Mapping(target = "totalCurrentBalance", expression = "java(calculateTotalBalance(user.getBalances()))")
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "externalIds", expression = "java(java.util.Collections.singletonList(externalIds))")
    UserIdsResponseDto toUserIdsResponseDto(UUID externalIds);

    @Mapping(target = "externalId")
    @Mapping(target = "totalBalance", expression = "java(calculateTotalBalance(user.getBalances()))")
    UserStatsReportDto toUserStatsReportDto(User user);

    @Mapping(target = "telegramChatId", ignore = true)
    @Mapping(target = "balances", ignore = true)
    @Mapping(target = "percentageChangeSinceYesterday", ignore = true)
    @Mapping(target = "monetaryChangeSinceYesterday", ignore = true)
    @Mapping(target = "accountCount", ignore = true)
    @Mapping(target = "userAccounts", ignore = true)
    @Mapping(target = "isMarginTradingEnabled", ignore = true)
    @Mapping(target = "marginTradingMetrics", ignore = true)
    @Mapping(target = "favoriteStocks", ignore = true)
    @Mapping(target = "positions", ignore = true)
    @Mapping(target = "operations", ignore = true)
    @Mapping(target = "posts", ignore = true)
    User toUserFromTempUserReg(TempUserReg tempUserReg);

    @AfterMapping
    default User updateUserFromDto(UserPatchDto userPatchDto, @MappingTarget User user) {
        mapOptionalValue(userPatchDto.getFirstName(), user::setFirstName);
        mapOptionalValue(userPatchDto.getLastName(), user::setLastName);
        mapOptionalValue(userPatchDto.getTelegramNickname(), user::setTelegramNickname);
        mapOptionalValue(userPatchDto.getTinkoffToken(), user::setTinkoffToken);
        return user;
    }

    default <T> void mapOptionalValue(Optional<T> optional, Consumer<T> setter) {
        optional.ifPresent(value -> setter.accept(
                value instanceof String string && string.isEmpty() ? null : value
        ));
    }

    @AfterMapping
    default void setExternalId(@MappingTarget User user) {
        if (user.getExternalId() == null) {
            user.setExternalId(UUID.randomUUID());
        }
    }

    // Метод для подсчета общего баланса пользователя
    default BigDecimal calculateTotalBalance(List<BalanceEntity> balances) {
        return balances.stream()
                .map(BalanceEntity::getTotalAmountBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}