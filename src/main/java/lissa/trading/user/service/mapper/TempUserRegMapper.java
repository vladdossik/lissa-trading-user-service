package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.post.TempUserRegPostDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;
import lissa.trading.user.service.model.TempUserReg;
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
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TempUserReg toTempUserReg(TempUserRegPostDto tempUserRegPostDto);

    @Mapping(target = "externalId")
    TempUserRegResponseDto toTempUserRegResponseDto(TempUserReg tempUserReg);

    @AfterMapping
    default void setExternalId(@MappingTarget TempUserReg tempUserReg) {
        if (tempUserReg.getExternalId() == null) {
            tempUserReg.setExternalId(UUID.randomUUID());
        }
    }
}