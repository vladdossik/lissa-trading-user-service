package lissa.trading.user.service.mapper;

import lissa.trading.user.service.page.CustomPage;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PageMapper {

    static <T, U> CustomPage<U> toCustomPage(Page<T> page, Function<T, U> mapper) {
        List<U> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        String sort = page.getSort().stream()
                .map(order -> order.getProperty() + " " + order.getDirection())
                .collect(Collectors.joining(", "));

        return new CustomPage<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                sort
        );
    }
}