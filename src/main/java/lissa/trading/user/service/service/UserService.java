package lissa.trading.user.service.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(String externalId, User userUpdates) {
        Optional<User> userOpt = userRepository.findByExternalId(externalId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userUpdates.getFirstName() != null) user.setFirstName(userUpdates.getFirstName());
            if (userUpdates.getLastName() != null) user.setLastName(userUpdates.getLastName());
            if (userUpdates.getTelegramChatId() != null) user.setTelegramChatId(userUpdates.getTelegramChatId());
            if (userUpdates.getTelegramNickname() != null) user.setTelegramNickname(userUpdates.getTelegramNickname());
            if (userUpdates.getTinkoffToken() != null) user.setTinkoffToken(userUpdates.getTinkoffToken());
            if (userUpdates.getCurrentBalance() != null) user.setCurrentBalance(userUpdates.getCurrentBalance());
            if (userUpdates.getPercentageChangeSinceYesterday() != null)
                user.setPercentageChangeSinceYesterday(userUpdates.getPercentageChangeSinceYesterday());
            if (userUpdates.getMonetaryChangeSinceYesterday() != null)
                user.setMonetaryChangeSinceYesterday(userUpdates.getMonetaryChangeSinceYesterday());
            if (userUpdates.getAccountCount() != null) user.setAccountCount(userUpdates.getAccountCount());
            if (userUpdates.getIsMarginTradingEnabled() != null)
                user.setIsMarginTradingEnabled(userUpdates.getIsMarginTradingEnabled());
            if (userUpdates.getMarginTradingMetrics() != null)
                user.setMarginTradingMetrics(userUpdates.getMarginTradingMetrics());
            if (userUpdates.getTinkoffInvestmentTariff() != null)
                user.setTinkoffInvestmentTariff(userUpdates.getTinkoffInvestmentTariff());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    @Transactional
    public void blockUserByTelegramNickname(String telegramNickname) {
        Optional<User> userOpt = userRepository.findByTelegramNickname(telegramNickname);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsMarginTradingEnabled(false);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void deleteUserByExternalId(String externalId) {
        Optional<User> userOpt = userRepository.findByExternalId(externalId);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByExternalId(String externalId) {
        return userRepository.findByExternalId(externalId);
    }

    @Transactional(readOnly = true)
    public Page<User> getUsersWithPaginationAndFilters(Pageable pageable, String firstName, String lastName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);
        Predicate predicate = cb.conjunction();

        if (firstName != null && !firstName.isEmpty()) {
            predicate = cb.and(predicate, cb.like(user.get("firstName"), "%" + firstName + "%"));
        }

        if (lastName != null && !lastName.isEmpty()) {
            predicate = cb.and(predicate, cb.like(user.get("lastName"), "%" + lastName + "%"));
        }

        query.where(predicate);

        TypedQuery<User> typedQuery = entityManager.createQuery(query);

        List<User> result = typedQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(User.class)));
        countQuery.where(predicate);

        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);
        long totalRows = countTypedQuery.getSingleResult();

        return new PageImpl<>(result, pageable, totalRows);
    }
}