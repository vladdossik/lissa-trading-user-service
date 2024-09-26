package lissa.trading.user.service.repository.entity;

import lissa.trading.user.service.model.entity.PostsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface PostsEntityRepository extends JpaRepository<PostsEntity, Long> {

    List<PostsEntity> findByUserId(Long userId);

    // Найти посты пользователя, созданные после определенной даты
    List<PostsEntity> findByUserIdAndPostDateAfter(Long userId, OffsetDateTime postDate);

    // Найти все посты, отсортированные по дате создания (по возрастанию)
    List<PostsEntity> findAllByOrderByPostDateAsc();

    void deleteByUserId(Long userId);
}