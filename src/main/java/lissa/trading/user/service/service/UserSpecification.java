package lissa.trading.user.service.service;

import lissa.trading.user.service.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class UserSpecification {

    private UserSpecification() {
        throw new IllegalStateException("Instantiation of 'UserSpecification' is not allowed.");
    }

    public static Specification<User> firstNameContains(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null) {
                log.debug("firstName is null");
                return null;
            }
            log.debug("Filtering users by firstName containing: {}", firstName);
            return cb.like(root.get("firstName"), "%" + firstName + "%");
        };
    }

    public static Specification<User> lastNameContains(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null) {
                log.debug("lastName is null");
                return null;
            }
            log.debug("Filtering users by lastName containing: {}", lastName);
            return cb.like(root.get("lastName"), "%" + lastName + "%");
        };
    }

    public static Specification<User> withFilters(String firstName, String lastName) {
        log.debug("Applying filters: firstName = {}, lastName = {}", firstName, lastName);
        return Specification.where(firstNameContains(firstName)).and(lastNameContains(lastName));
    }
}