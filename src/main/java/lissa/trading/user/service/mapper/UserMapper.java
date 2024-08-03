package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.UserPatchDto;
import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.UserReg;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "telegramChatId", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "percentageChangeSinceYesterday", ignore = true)
    @Mapping(target = "monetaryChangeSinceYesterday", ignore = true)
    @Mapping(target = "accountCount", ignore = true)
    @Mapping(target = "isMarginTradingEnabled", ignore = true)
    @Mapping(target = "marginTradingMetrics", ignore = true)
    @Mapping(target = "tinkoffInvestmentTariff", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "positions", ignore = true)
    @Mapping(target = "operations", ignore = true)
    @Mapping(target = "favoriteStocks", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "posts", ignore = true)
    User toUser(UserPostDto userPostDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "telegramChatId", ignore = true)
    @Mapping(target = "tinkoffToken", ignore = true)
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
    User toUser(UserReg userReg);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserReg toUserReg(UserPostDto userPostDto);

    @Mapping(target = "externalId")
    @Mapping(target = "telegramChatId", ignore = true)
    @Mapping(target = "tinkoffToken", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "percentageChangeSinceYesterday", ignore = true)
    @Mapping(target = "monetaryChangeSinceYesterday", ignore = true)
    @Mapping(target = "accountCount", ignore = true)
    @Mapping(target = "isMarginTradingEnabled", ignore = true)
    @Mapping(target = "marginTradingMetrics", ignore = true)
    @Mapping(target = "tinkoffInvestmentTariff", ignore = true)
    UserResponseDto toUserResponseDto(UserReg userReg);

    @Mapping(target = "externalId")
    UserResponseDto toUserResponseDto(User user);

    @AfterMapping
    default void setExternalId(@MappingTarget UserReg userReg) {
        if (userReg.getExternalId() == null) {
            userReg.setExternalId(UUID.randomUUID());
        }
    }

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
}


