package lissa.trading.user.service.mapper;

import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.lissa.auth.lib.security.EncryptionService;
import lissa.trading.user.service.dto.notification.UserUpdateNotificationDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TempUserRegMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "broker", ignore = true)
    TempUserReg toTempUserReg(UserInfoDto userInfoDto);

    @Mapping(target = "roles", ignore = true)
    UserInfoDto toUserInfoDto(UserUpdateNotificationDto userUpdateNotificationDto);

    @Mapping(target = "externalId")
    TempUserRegResponseDto toTempUserRegResponseDto(TempUserReg tempUserReg);

    @AfterMapping
    default void setExternalId(@MappingTarget TempUserReg tempUserReg) {
        if (tempUserReg.getExternalId() == null || tempUserReg.getExternalId().toString().isEmpty()) {
            tempUserReg.setExternalId(UUID.randomUUID());
        }
    }

    @AfterMapping
    default void decryptTinkoffToken(@MappingTarget TempUserReg tempUserReg) {
        if (tempUserReg.getTinkoffToken() != null) {
            tempUserReg.setTinkoffToken(EncryptionService.decrypt(tempUserReg.getTinkoffToken()));
        }
    }

    @AfterMapping
    default void setBroker(@MappingTarget TempUserReg tempUserReg) {
        String tinkoffToken = tempUserReg.getTinkoffToken();
        if (tinkoffToken == null || tinkoffToken.isEmpty()) {
            tempUserReg.setBroker(SupportedBrokersEnum.MOEX);
        } else if (tinkoffToken.startsWith("t.") && tinkoffToken.length() == 88) {
            tempUserReg.setBroker(SupportedBrokersEnum.TINKOFF);
        }
    }
}