package lissa.trading.user.service.mapper;

import lissa.trading.user.service.page.CustomPage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Mapper
public interface PageMapper {

    PageMapper INSTANCE = Mappers.getMapper(PageMapper.class);

    default <T> Page<T> toPage(CustomPage<T> customPage, Pageable pageable) {
        return new PageImpl<>(
                customPage.getContent(),
                pageable,
                customPage.getTotalElements()
        );
    }
}