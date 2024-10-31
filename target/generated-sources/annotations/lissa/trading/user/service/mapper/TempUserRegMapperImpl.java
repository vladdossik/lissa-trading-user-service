package lissa.trading.user.service.mapper;

import javax.annotation.processing.Generated;
import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;
import lissa.trading.user.service.model.TempUserReg;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-30T17:29:54+0300",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 21.0.4 (Azul Systems, Inc.)"
)
@Component
public class TempUserRegMapperImpl implements TempUserRegMapper {

    @Override
    public TempUserReg toTempUserReg(UserInfoDto userInfoDto) {
        if ( userInfoDto == null ) {
            return null;
        }

        TempUserReg tempUserReg = new TempUserReg();

        tempUserReg.setFirstName( userInfoDto.getFirstName() );
        tempUserReg.setLastName( userInfoDto.getLastName() );
        tempUserReg.setTelegramNickname( userInfoDto.getTelegramNickname() );
        tempUserReg.setTinkoffToken( userInfoDto.getTinkoffToken() );

        setExternalId( tempUserReg );

        return tempUserReg;
    }

    @Override
    public TempUserRegResponseDto toTempUserRegResponseDto(TempUserReg tempUserReg) {
        if ( tempUserReg == null ) {
            return null;
        }

        TempUserRegResponseDto tempUserRegResponseDto = new TempUserRegResponseDto();

        tempUserRegResponseDto.setExternalId( tempUserReg.getExternalId() );
        tempUserRegResponseDto.setFirstName( tempUserReg.getFirstName() );
        tempUserRegResponseDto.setLastName( tempUserReg.getLastName() );
        tempUserRegResponseDto.setTelegramNickname( tempUserReg.getTelegramNickname() );
        tempUserRegResponseDto.setTinkoffToken( tempUserReg.getTinkoffToken() );
        tempUserRegResponseDto.setCreatedAt( tempUserReg.getCreatedAt() );
        tempUserRegResponseDto.setUpdatedAt( tempUserReg.getUpdatedAt() );

        return tempUserRegResponseDto;
    }
}
