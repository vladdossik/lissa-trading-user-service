package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.UserPatchDto;
import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

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
    User toUser(UserPostDto userPostDto);

    @Mapping(target = "externalId")
    UserResponseDto toUserResponseDto(User user);

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


