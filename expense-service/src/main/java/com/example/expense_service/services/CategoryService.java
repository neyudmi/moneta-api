package com.example.expense_service.services;

import com.example.expense_service.dtos.CategoryResponseDTO;
import com.example.expense_service.entities.Category;
import com.example.expense_service.entities.Icon;
import com.example.expense_service.repositories.CategoryRepository;
import com.example.expense_service.repositories.IconRepository;
import com.example.expense_service.exceptions.BadRequestException;
import com.example.expense_service.exceptions.ResourceNotFoundException;
import com.example.expense_service.exceptions.UnauthorizedException;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final IconRepository iconRepository;

    public CategoryService(CategoryRepository categoryRepository, IconRepository iconRepository) {
        this.categoryRepository = categoryRepository;
        this.iconRepository = iconRepository;
    }

    @Transactional
    public List<CategoryResponseDTO> getAllCategories(UUID userId) {
        requireForUser(userId);

        // Create default categories for the user if they don't exist
        if (!categoryRepository.existsByUserId(userId)) {
            createDefaultCategories(userId);
        }

        List<Category> parents = categoryRepository.findByUserIdIsNull();

        List<Category> children = categoryRepository.findByUserId(userId);

        return Stream.concat(
                parents.stream(),
                children.stream())
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponseDTO getCategoryById(UUID categoryId, UUID userId) {
        Category category = categoryRepository.findById(categoryId)
                .filter(
                        cate -> cate.getUserId() == null || cate.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found: " + categoryId));
        return toResponse(category);
    }

    @Transactional
    public CategoryResponseDTO createCategory(UUID userId, String name, UUID iconId, UUID parentId) {
        requireForCategory(name, iconId, parentId);
        requireForUser(userId);

        Icon icon = iconRepository
                .findById(iconId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Icon not found: " + iconId));

        Category parent = categoryRepository
                .findById(parentId)
                .filter(cate -> cate.getUserId() == null)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Parent category not found: " + parentId));

        Category category = new Category(
                userId,
                name,
                icon,
                parent);

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDTO updateCategory(UUID categoryId, UUID userId, String name, UUID iconId, UUID parentId) {
        requireForUser(userId);
        requireForCategory(name, iconId, parentId);

        Category category = categoryRepository
                .findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found: " + categoryId));

        Icon icon = iconRepository
                .findById(iconId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Icon not found: " + iconId));

        Category parent = categoryRepository
                .findById(parentId)
                .filter(cate -> cate.getUserId() == null)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Parent category not found: " + parentId));

        category.setName(name);
        category.setIcon(icon);
        category.setParent(parent);

        return toResponse(category);
    }

    @Transactional
    public void deleteCategory(UUID categoryId, UUID userId) {
        requireForUser(userId);
        Category category = categoryRepository
                .findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));

        categoryRepository.delete(category);
    }

    private void requireForUser(UUID userId) {
        if (userId == null) {
            throw new UnauthorizedException(
                    "Authenticated user is required");
        }
    }

    private void requireForCategory(String name, UUID iconId, UUID parentId) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Category name is required");
        }
        if (iconId == null) {
            throw new BadRequestException("Icon is required");
        }
        if (parentId == null) {
            throw new BadRequestException("Parent category is required");
        }
    }

    private void createDefaultCategories(UUID userId) {

        Category food = getParent("Ăn uống");
        Category children = getParent("Con cái");
        Category service = getParent("Dịch vụ sinh hoạt");
        Category transportation = getParent("Đi lại");
        Category ceremony = getParent("Hiếu hỉ");
        Category entertainment = getParent("Hưởng thụ");
        Category house = getParent("Nhà cửa");
        Category health = getParent("Sức khỏe");
        Category selfDevelopment = getParent("Phát triển bản thân");
        Category clothing = getParent("Trang phục");
        Category income = getParent("Thu tiền");

        List<Category> categories = List.of(

                // Ăn uống
                createChild(userId, "Ăn sáng", "ic_breakfast", food),
                createChild(userId, "Ăn trưa", "ic_lunch", food),
                createChild(userId, "Ăn tối", "ic_dinner", food),
                createChild(userId, "Cà phê", "ic_cafe", food),
                createChild(userId, "Ăn tiệm", "ic_restaurant", food),

                // Con cái
                createChild(userId, "Học phí", "ic_tuition", children),
                createChild(userId, "Sách vở", "ic_book", children),
                createChild(userId, "Sữa", "ic_milk", children),
                createChild(userId, "Tiền tiêu vặt", "ic_pocketmoney", children),
                createChild(userId, "Đồ chơi", "ic_toy", children),

                // Dịch vụ sinh hoạt
                createChild(userId, "Điện", "ic_electric", service),
                createChild(userId, "Nước", "ic_water", service),
                createChild(userId, "Internet", "ic_internet", service),
                createChild(userId, "Điện thoại", "ic_smartphone", service),
                createChild(userId, "Điện thoại bàn", "ic_telephone", service),
                createChild(userId, "Gas", "ic_gas", service),
                createChild(userId, "Thuê người giúp việc", "ic_cleaning", service),
                createChild(userId, "Truyền hình", "ic_tv", service),

                // Đi lại
                createChild(userId, "Xăng xe", "ic_gasoline", transportation),
                createChild(userId, "Taxi/Grab", "ic_taxi", transportation),
                createChild(userId, "Bảo dưỡng xe", "ic_fix", transportation),
                createChild(userId, "Rửa xe", "ic_carwash", transportation),
                createChild(userId, "Gửi xe", "ic_parking", transportation),
                createChild(userId, "Bảo hiểm", "ic_insurance", transportation),

                // Hiếu hỉ
                createChild(userId, "Biếu tặng", "ic_gift", ceremony),
                createChild(userId, "Cưới xin", "ic_marriage", ceremony),
                createChild(userId, "Ma chay", "ic_funeral", ceremony),
                createChild(userId, "Thăm bệnh", "ic_visit_patient", ceremony),

                // Hưởng thụ
                createChild(userId, "Du lịch", "ic_tourism", entertainment),
                createChild(userId, "Phim ảnh ca nhạc", "ic_film", entertainment),
                createChild(userId, "Vui chơi giải trí", "ic_entertainment", entertainment),
                createChild(userId, "Làm đẹp", "ic_skincare", entertainment),
                createChild(userId, "Mỹ phẩm", "ic_cosmetics", entertainment),

                // Nhà cửa
                createChild(userId, "Thuê nhà", "ic_rent", house),
                createChild(userId, "Sửa chữa nhà cửa", "ic_houserepair", house),
                createChild(userId, "Mua sắm đồ đạc", "ic_interior", house),

                // Sức khỏe
                createChild(userId, "Khám chữa bệnh", "ic_stethoscope", health),
                createChild(userId, "Thuốc men", "ic_medicine", health),
                createChild(userId, "Thể thao", "ic_sport", health),

                // Phát triển bản thân
                createChild(userId, "Giao lưu, quan hệ", "ic_handshake", selfDevelopment),
                createChild(userId, "Học hành", "ic_study", selfDevelopment),

                // Trang phục
                createChild(userId, "Giày dép", "ic_shoes", clothing),
                createChild(userId, "Quần áo", "ic_clothes", clothing),
                createChild(userId, "Phụ kiện", "ic_accessory", clothing),

                // Thu tiền
                createChild(userId, "Đi vay", "ic_borrow", income),
                createChild(userId, "Được cho/tặng", "ic_gift_2", income),
                createChild(userId, "Khác", "ic_difference", income),
                createChild(userId, "Lương", "ic_salary_2", income),
                createChild(userId, "Thu nợ", "ic_debt", income),
                createChild(userId, "Thưởng", "ic_reward", income),
                createChild(userId, "Tiền lãi", "ic_interest", income),
                createChild(userId, "Tiền vào", "ic_income", income));

        categoryRepository.saveAll(categories);
    }

    private Category getParent(String name) {
        return categoryRepository
                .findByName(name)
                .orElseThrow(() -> new IllegalStateException(
                        "System category not found: " + name));
    }

    private Category createChild(
            UUID userId,
            String name,
            String iconFileName,
            Category parent) {

        Icon icon = iconRepository
                .findByFileName(iconFileName)
                .orElseThrow(() -> new IllegalStateException(
                        "Icon not found: " + iconFileName));

        return new Category(
                userId,
                name,
                icon,
                parent);
    }

    private CategoryResponseDTO toResponse(Category category) {

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getIcon().getFileName(),
                category.getParent() != null
                        ? category.getParent().getId()
                        : null);
    }

}
