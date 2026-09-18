package com.example.expense_service.configs;

import com.example.expense_service.entities.Category;
import com.example.expense_service.entities.Icon;
import com.example.expense_service.repositories.CategoryRepository;
import com.example.expense_service.repositories.IconRepository;
import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("iconInitializer")
public class CategoryInitializer {

        private final CategoryRepository categoryRepository;
        private final IconRepository iconRepository;

        public CategoryInitializer(
                        CategoryRepository categoryRepository,
                        IconRepository iconRepository) {
                this.categoryRepository = categoryRepository;
                this.iconRepository = iconRepository;
        }

        @PostConstruct
        public void init() {

                if (categoryRepository.countByUserIdIsNull() > 0) {
                        return;
                }

                createParent("Ăn uống", "ic_food");
                createParent("Con cái", "ic_children");
                createParent("Dịch vụ sinh hoạt", "ic_house");
                createParent("Đi lại", "ic_destination");
                createParent("Hiếu hỉ", "ic_hieuhi");
                createParent("Hưởng thụ", "ic_travel");
                createParent("Nhà cửa", "ic_house_2");
                createParent("Sức khỏe", "ic_health");
                createParent("Phát triển bản thân", "ic_improve");
                createParent("Trang phục", "ic_suit");
                createParent("Thu tiền", "ic_income");
        }

        private void createParent(String name, String iconFileName) {

                Icon icon = iconRepository
                                .findByFileName(iconFileName)
                                .orElseThrow(() -> new IllegalStateException(
                                                "Icon not found: " + iconFileName));

                Category category = new Category(name, icon);

                categoryRepository.save(category);
        }
}