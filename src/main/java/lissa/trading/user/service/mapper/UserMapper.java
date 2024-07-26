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
    User toUser(UserPostDto userPostDto);

    @Mapping(target = "externalId", source = "user.externalId")
    UserResponseDto toUserResponseDto(User user);

    @AfterMapping
    default void updateUserFromDto(UserPatchDto userPatchDto, @MappingTarget User user) {
        mapOptionalValue(userPatchDto.getFirstName(), user::setFirstName);
        mapOptionalValue(userPatchDto.getLastName(), user::setLastName);
        mapOptionalValue(userPatchDto.getTelegramChatId(), user::setTelegramChatId);
        mapOptionalValue(userPatchDto.getTelegramNickname(), user::setTelegramNickname);
        mapOptionalValue(userPatchDto.getTinkoffToken(), user::setTinkoffToken);
        mapOptionalValue(userPatchDto.getCurrentBalance(), user::setCurrentBalance);
        mapOptionalValue(userPatchDto.getPercentageChangeSinceYesterday(), user::setPercentageChangeSinceYesterday);
        mapOptionalValue(userPatchDto.getMonetaryChangeSinceYesterday(), user::setMonetaryChangeSinceYesterday);
        mapOptionalValue(userPatchDto.getAccountCount(), user::setAccountCount);
        mapOptionalValue(userPatchDto.getIsMarginTradingEnabled(), user::setIsMarginTradingEnabled);
        mapOptionalValue(userPatchDto.getMarginTradingMetrics(), user::setMarginTradingMetrics);
        mapOptionalValue(userPatchDto.getTinkoffInvestmentTariff(), user::setTinkoffInvestmentTariff);
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


