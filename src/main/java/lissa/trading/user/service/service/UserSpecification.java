package lissa.trading.user.service.service;

import lissa.trading.user.service.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> firstNameContains(String firstName) {
        return (root, query, cb) -> firstName == null ? null : cb.like(root.get("firstName"), "%" + firstName + "%");
    }

    public static Specification<User> lastNameContains(String lastName) {
        return (root, query, cb) -> lastName == null ? null : cb.like(root.get("lastName"), "%" + lastName + "%");
    }

    public static Specification<User> withFilters(String firstName, String lastName) {
        return Specification.where(firstNameContains(firstName)).and(lastNameContains(lastName));
    }
}