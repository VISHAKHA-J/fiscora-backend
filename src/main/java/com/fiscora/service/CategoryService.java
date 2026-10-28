package com.fiscora.service;

import com.fiscora.dto.category.CategoryResponse;
import com.fiscora.dto.category.CreateCategoryRequest;
import com.fiscora.dto.category.UpdateCategoryRequest;
import com.fiscora.entity.Category;
import com.fiscora.entity.User;
import com.fiscora.enums.CategoryType;
import com.fiscora.exception.CategoryNameAlreadyExistsException;
import com.fiscora.exception.CategoryNotFoundException;
import com.fiscora.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fiscora.exception.CategoryHasTransactionsException;
import com.fiscora.repository.TransactionRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public CategoryResponse create(User user, CreateCategoryRequest request) {
        String name = request.name().trim();
        ensureNameIsFree(user, name);

        Category category = Category.builder()
                .user(user)
                .name(name)
                .type(request.type())
                .build();

        return CategoryResponse.from(categoryRepository.save(category));
    }

    // type == null returns every category.
    @Transactional(readOnly = true)
    public List<CategoryResponse> list(User user, CategoryType type) {
        List<Category> categories = (type == null)
                ? categoryRepository.findByUser_UserIdOrderByTypeAscNameAsc(user.getUserId())
                : categoryRepository.findByUser_UserIdAndTypeOrderByNameAsc(user.getUserId(), type);

        return categories.stream().map(CategoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(User user, Long categoryId) {
        return CategoryResponse.from(findOwned(user, categoryId));
    }

    @Transactional
    public CategoryResponse update(User user, Long categoryId, UpdateCategoryRequest request) {
        Category category = findOwned(user, categoryId);
        String name = request.name().trim();

        if (!category.getName().equalsIgnoreCase(name)) {
            ensureNameIsFree(user, name);
        }

        category.setName(name);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void delete(User user, Long categoryId) {
        Category category = findOwned(user, categoryId);

        long count = transactionRepository.countByCategory_CategoryId(categoryId);
        if (count > 0) {
            throw new CategoryHasTransactionsException(
                    "This category has " + count + (count == 1 ? " transaction" : " transactions")
                            + ", so it can't be deleted. Delete or move them first.");
        }
        categoryRepository.delete(category);
    }

    // 404 for both "does not exist" and "belongs to someone else".
    private Category findOwned(User user, Long categoryId) {
        return categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }

    private void ensureNameIsFree(User user, String name) {
        if (categoryRepository.existsByUser_UserIdAndNameIgnoreCase(user.getUserId(), name)) {
            throw new CategoryNameAlreadyExistsException("You already have a category named \"" + name + "\"");
        }
    }
}