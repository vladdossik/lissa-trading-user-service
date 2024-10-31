package lissa.trading.user.service.mapper;

import javax.annotation.processing.Generated;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.dto.response.UserStatsReportDto;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-31T17:45:09+0300",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.4 (Azul Systems, Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto toUserResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto.UserResponseDtoBuilder userResponseDto = UserResponseDto.builder();

        userResponseDto.externalId( user.getExternalId() );
        userResponseDto.firstName( user.getFirstName() );
        userResponseDto.lastName( user.getLastName() );
        userResponseDto.telegramNickname( user.getTelegramNickname() );
        userResponseDto.tinkoffToken( user.getTinkoffToken() );
        userResponseDto.telegramChatId( user.getTelegramChatId() );
        userResponseDto.percentageChangeSinceYesterday( user.getPercentageChangeSinceYesterday() );
        userResponseDto.monetaryChangeSinceYesterday( user.getMonetaryChangeSinceYesterday() );
        userResponseDto.accountCount( user.getAccountCount() );
        userResponseDto.isMarginTradingEnabled( user.getIsMarginTradingEnabled() );

        userResponseDto.totalCurrentBalance( calculateTotalBalance(user.getBalances()) );

        return userResponseDto.build();
    }

    @Override
    public UserStatsReportDto toUserStatsReportDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserStatsReportDto.UserStatsReportDtoBuilder userStatsReportDto = UserStatsReportDto.builder();

        userStatsReportDto.externalId( user.getExternalId() );
        userStatsReportDto.firstName( user.getFirstName() );
        userStatsReportDto.lastName( user.getLastName() );
        userStatsReportDto.telegramNickname( user.getTelegramNickname() );
        userStatsReportDto.percentageChangeSinceYesterday( user.getPercentageChangeSinceYesterday() );
        userStatsReportDto.monetaryChangeSinceYesterday( user.getMonetaryChangeSinceYesterday() );
        userStatsReportDto.accountCount( user.getAccountCount() );
        userStatsReportDto.createdAt( user.getCreatedAt() );
        userStatsReportDto.updatedAt( user.getUpdatedAt() );

        userStatsReportDto.totalBalance( calculateTotalBalance(user.getBalances()) );

        return userStatsReportDto.build();
    }

    @Override
    public User toUserFromTempUserReg(TempUserReg tempUserReg) {
        if ( tempUserReg == null ) {
            return null;
        }

        User user = new User();

        user.setId( tempUserReg.getId() );
        user.setExternalId( tempUserReg.getExternalId() );
        user.setFirstName( tempUserReg.getFirstName() );
        user.setLastName( tempUserReg.getLastName() );
        user.setTelegramNickname( tempUserReg.getTelegramNickname() );
        user.setTinkoffToken( tempUserReg.getTinkoffToken() );
        user.setCreatedAt( tempUserReg.getCreatedAt() );
        user.setUpdatedAt( tempUserReg.getUpdatedAt() );

        setExternalId( user );

        return user;
    }
}
