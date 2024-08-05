package lissa.trading.user.service.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lissa.trading.user.service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSpecificationTest {

    private Root<User> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder cb;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);
        cb = mock(CriteriaBuilder.class);
    }

    @Test
    void testFirstNameContains() {
        String firstName = "John";
        Predicate predicate = mock(Predicate.class);
        when(cb.like(root.get("firstName"), "%" + firstName + "%")).thenReturn(predicate);

        Specification<User> specification = UserSpecification.firstNameContains(firstName);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb, times(1)).like(root.get("firstName"), "%" + firstName + "%");
    }

    @Test
    void testFirstNameContains_Null() {
        Specification<User> specification = UserSpecification.firstNameContains(null);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNull(result);
    }

    @Test
    void testLastNameContains() {
        String lastName = "Doe";
        Predicate predicate = mock(Predicate.class);
        when(cb.like(root.get("lastName"), "%" + lastName + "%")).thenReturn(predicate);

        Specification<User> specification = UserSpecification.lastNameContains(lastName);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb, times(1)).like(root.get("lastName"), "%" + lastName + "%");
    }

    @Test
    void testLastNameContains_Null() {
        Specification<User> specification = UserSpecification.lastNameContains(null);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNull(result);
    }

    @Test
    void testWithFilters() {
        String firstName = "John";
        String lastName = "Doe";
        Predicate firstNamePredicate = mock(Predicate.class);
        Predicate lastNamePredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(cb.like(root.get("firstName"), "%" + firstName + "%")).thenReturn(firstNamePredicate);
        when(cb.like(root.get("lastName"), "%" + lastName + "%")).thenReturn(lastNamePredicate);
        when(cb.and(firstNamePredicate, lastNamePredicate)).thenReturn(combinedPredicate);

        Specification<User> specification = UserSpecification.withFilters(firstName, lastName);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb, times(1)).like(root.get("firstName"), "%" + firstName + "%");
        verify(cb, times(1)).like(root.get("lastName"), "%" + lastName + "%");
        verify(cb, times(1)).and(firstNamePredicate, lastNamePredicate);
    }

    @Test
    void testWithFilters_OnlyFirstName() {
        String firstName = "John";
        Predicate firstNamePredicate = mock(Predicate.class);

        when(cb.like(root.get("firstName"), "%" + firstName + "%")).thenReturn(firstNamePredicate);

        Specification<User> specification = UserSpecification.withFilters(firstName, null);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb, times(1)).like(root.get("firstName"), "%" + firstName + "%");
        verify(cb, times(0)).like(root.get("lastName"), "%" + null + "%");
    }

    @Test
    void testWithFilters_OnlyLastName() {
        String lastName = "Doe";
        Predicate lastNamePredicate = mock(Predicate.class);

        when(cb.like(root.get("lastName"), "%" + lastName + "%")).thenReturn(lastNamePredicate);

        Specification<User> specification = UserSpecification.withFilters(null, lastName);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb, times(0)).like(root.get("firstName"), "%" + null + "%");
        verify(cb, times(1)).like(root.get("lastName"), "%" + lastName + "%");
    }

    @Test
    void testWithFilters_NoFilters() {
        Specification<User> specification = UserSpecification.withFilters(null, null);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNull(result);
    }
}

