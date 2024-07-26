package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    User toUser(UserPostDto userPostDto);

    @Mapping(target = "externalId", source = "user.externalId")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "telegramChatId", source = "user.telegramChatId")
    @Mapping(target = "telegramNickname", source = "user.telegramNickname")
    @Mapping(target = "tinkoffToken", source = "user.tinkoffToken")
    @Mapping(target = "currentBalance", source = "user.currentBalance")
    @Mapping(target = "percentageChangeSinceYesterday", source = "user.percentageChangeSinceYesterday")
    @Mapping(target = "monetaryChangeSinceYesterday", source = "user.monetaryChangeSinceYesterday")
    @Mapping(target = "accountCount", source = "user.accountCount")
    @Mapping(target = "isMarginTradingEnabled", source = "user.isMarginTradingEnabled")
    @Mapping(target = "marginTradingMetrics", source = "user.marginTradingMetrics")
    @Mapping(target = "tinkoffInvestmentTariff", source = "user.tinkoffInvestmentTariff")
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "firstName", expression = "java(isEmpty(userPostDto.getFirstName()) ? null : userPostDto.getFirstName())")
    @Mapping(target = "lastName", expression = "java(isEmpty(userPostDto.getLastName()) ? null : userPostDto.getLastName())")
    @Mapping(target = "telegramChatId", expression = "java(userPostDto.getTelegramChatId() == null ? user.getTelegramChatId() : userPostDto.getTelegramChatId())")
    @Mapping(target = "telegramNickname", expression = "java(isEmpty(userPostDto.getTelegramNickname()) ? null : userPostDto.getTelegramNickname())")
    @Mapping(target = "tinkoffToken", expression = "java(isEmpty(userPostDto.getTinkoffToken()) ? null : userPostDto.getTinkoffToken())")
    @Mapping(target = "currentBalance", expression = "java(userPostDto.getCurrentBalance() == null ? user.getCurrentBalance() : userPostDto.getCurrentBalance())")
    @Mapping(target = "percentageChangeSinceYesterday", expression = "java(userPostDto.getPercentageChangeSinceYesterday() == null ? user.getPercentageChangeSinceYesterday() : userPostDto.getPercentageChangeSinceYesterday())")
    @Mapping(target = "monetaryChangeSinceYesterday", expression = "java(userPostDto.getMonetaryChangeSinceYesterday() == null ? user.getMonetaryChangeSinceYesterday() : userPostDto.getMonetaryChangeSinceYesterday())")
    @Mapping(target = "accountCount", expression = "java(userPostDto.getAccountCount() == null ? user.getAccountCount() : userPostDto.getAccountCount())")
    @Mapping(target = "isMarginTradingEnabled", expression = "java(userPostDto.getIsMarginTradingEnabled() == null ? user.getIsMarginTradingEnabled() : userPostDto.getIsMarginTradingEnabled())")
    @Mapping(target = "marginTradingMetrics", expression = "java(isEmpty(userPostDto.getMarginTradingMetrics()) ? null : userPostDto.getMarginTradingMetrics())")
    @Mapping(target = "tinkoffInvestmentTariff", expression = "java(isEmpty(userPostDto.getTinkoffInvestmentTariff()) ? null : userPostDto.getTinkoffInvestmentTariff())")
    void updateUserFromDto(UserPostDto userPostDto, @MappingTarget User user);

    @AfterMapping
    default void setExternalId(@MappingTarget User user) {
        if (user.getExternalId() == null) {
            user.setExternalId(UUID.randomUUID());
        }
    }

    default boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}