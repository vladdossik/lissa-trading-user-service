package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
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

    void updateUserFromDto(UserPostDto userPostDto, @MappingTarget User user);
}