package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "externalId")
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "telegramChatId", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "percentageChangeSinceYesterday", ignore = true)
    @Mapping(target = "monetaryChangeSinceYesterday", ignore = true)
    @Mapping(target = "accountCount", ignore = true)
    @Mapping(target = "isMarginTradingEnabled", ignore = true)
    @Mapping(target = "marginTradingMetrics", ignore = true)
    @Mapping(target = "tinkoffInvestmentTariff", ignore = true)
    @Mapping(target = "positions", ignore = true)
    @Mapping(target = "operations", ignore = true)
    @Mapping(target = "favoriteStocks", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
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
                value instanceof String && ((String) value).isEmpty() ? null : value
        ));
    }

    @AfterMapping
    default void setExternalId(@MappingTarget User user) {
        if (user.getExternalId() == null) {
            user.setExternalId(UUID.randomUUID());
        }
    }
}
