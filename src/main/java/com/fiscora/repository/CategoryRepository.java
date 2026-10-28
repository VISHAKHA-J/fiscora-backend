package com.fiscora.repository;

import com.fiscora.entity.Category;
import com.fiscora.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser_UserIdOrderByTypeAscNameAsc(Long userId);

    List<Category> findByUser_UserIdAndTypeOrderByNameAsc(Long userId, CategoryType type);

    Optional<Category> findByCategoryIdAndUser_UserId(Long categoryId, Long userId);

    boolean existsByUser_UserIdAndNameIgnoreCase(Long userId, String name);
}