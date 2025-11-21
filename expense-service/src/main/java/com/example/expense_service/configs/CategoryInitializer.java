package com.example.expense_service.configs;

import com.example.expense_service.models.Category;
import com.example.expense_service.models.CategoryGroup;
import com.example.expense_service.models.Icon;
import com.example.expense_service.repositories.CategoryGroupRepository;
import com.example.expense_service.repositories.CategoryRepository;
import com.example.expense_service.repositories.IconRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CategoryInitializer {

        private final CategoryRepository categoryRepository;
        private final CategoryGroupRepository groupRepository;
        private final IconRepository iconRepository;

        public CategoryInitializer(CategoryRepository categoryRepository,
                        CategoryGroupRepository groupRepository,
                        IconRepository iconRepository) {
                this.categoryRepository = categoryRepository;
                this.groupRepository = groupRepository;
                this.iconRepository = iconRepository;
        }

        @PostConstruct
        public void initCategories() {
                if (categoryRepository.count() > 0)
                        return;

                Map<String, List<ChildCategory>> categoryMap = new LinkedHashMap<>();

                categoryMap.put("Ăn uống", List.of(
                                new ChildCategory("Ăn sáng", "ic_breakfast"),
                                new ChildCategory("Ăn trưa", "ic_lunch"),
                                new ChildCategory("Ăn tối", "ic_diner"),
                                new ChildCategory("Cà phê", "ic_cafe"),
                                new ChildCategory("Ăn tiệm", "ic_restaurant")));

                categoryMap.put("Dịch vụ sinh hoạt", List.of(
                                new ChildCategory("Điện", "ic_electric"),
                                new ChildCategory("Nước", "ic_water"),
                                new ChildCategory("Internet", "ic_internet"),
                                new ChildCategory("Điện thoại", "ic_smartphone"),
                                new ChildCategory("Điện thoại bàn", "ic_telephone"),
                                new ChildCategory("Gas", "ic_gas"),
                                new ChildCategory("Thuê người giúp việc", "ic_cleaning"),
                                new ChildCategory("Truyền hình", "ic_tv")));

                categoryMap.put("Đi lại", List.of(
                                new ChildCategory("Xăng xe", "ic_gasoline"),
                                new ChildCategory("Taxi/Grab", "ic_taxi"),
                                new ChildCategory("Bảo dưỡng xe", "ic_fixx"),
                                new ChildCategory("Rửa xe", "ic_carwash"),
                                new ChildCategory("Gửi xe", "ic_parking"),
                                new ChildCategory("Bảo hiểm", "ic_insurance")));

                categoryMap.put("Nhà cửa", List.of(
                                new ChildCategory("Thuê nhà", "ic_rent"),
                                new ChildCategory("Sửa chữa nhà cửa", "ic_houserepair"),
                                new ChildCategory("Mua sắm đồ đạc", "ic_interior")));

                categoryMap.put("Trang phục", List.of(
                                new ChildCategory("Giày dép", "ic_shoes"),
                                new ChildCategory("Quần áo", "ic_clother"),
                                new ChildCategory("Phụ kiện", "ic_accessory")));

                categoryMap.put("Hưởng thụ", List.of(
                                new ChildCategory("Du lịch", "ic_tourism"),
                                new ChildCategory("Phim ảnh ca nhạc", "ic_fim"),
                                new ChildCategory("Vui chơi giải trí", "ic_entertainment"),
                                new ChildCategory("Làm đẹp", "ic_skincare"),
                                new ChildCategory("Mỹ phẩm", "ic_cosmetics")));

                categoryMap.put("Con cái", List.of(
                                new ChildCategory("Học phí", "ic_tuition"),
                                new ChildCategory("Sách vở", "ic_book"),
                                new ChildCategory("Sữa", "ic_milk"),
                                new ChildCategory("Tiền tiêu vặt", "ic_pocketmoney"),
                                new ChildCategory("Đồ chơi", "ic_toy")));

                categoryMap.put("Hiếu hỉ", List.of(
                                new ChildCategory("Biếu tặng", "ic_gift"),
                                new ChildCategory("Cưới xin", "ic_marrrt"),
                                new ChildCategory("Ma chay", "ic_funeral"),
                                new ChildCategory("Thăm bệnh", "ic_visiatpatient")));

                categoryMap.put("Sức khỏe", List.of(
                                new ChildCategory("Khám chữa bệnh", "ic_stethoscope"),
                                new ChildCategory("Thuốc men", "ic_medicin"),
                                new ChildCategory("Thể thao", "ic_sport")));

                categoryMap.put("Phát triển bản thân", List.of(
                                new ChildCategory("Giao lưu, quan hệ", "ic_handshake"),
                                new ChildCategory("Học hành", "ic_study")));

                categoryMap.put("Thu tiền", List.of(
                                new ChildCategory("Đi vay", "ic_borrow"),
                                new ChildCategory("Được cho/tặng", "ic_gift_2"),
                                new ChildCategory("Khác", "ic_difference"),
                                new ChildCategory("Lương", "ic_salary_2"),
                                new ChildCategory("Thu nợ", "ic_debt"),
                                new ChildCategory("Thưởng", "ic_reward"),
                                new ChildCategory("Tiền lãi", "ic_interest"),
                                new ChildCategory("Tiền vào", "ic_income")));

                for (Map.Entry<String, List<ChildCategory>> entry : categoryMap.entrySet()) {
                        String groupName = entry.getKey();
                        Optional<CategoryGroup> groupOpt = groupRepository.findByName(groupName);

                        if (groupOpt.isEmpty()) {
                                System.err.println("Không tìm thấy nhóm cha: " + groupName);
                                continue;
                        }

                        CategoryGroup group = groupOpt.get();

                        for (ChildCategory c : entry.getValue()) {
                                Optional<Icon> iconOpt = iconRepository.findByFileName(c.iconFile());
                                if (iconOpt.isEmpty()) {
                                        System.err.println("Không tìm thấy icon: " + c.iconFile());
                                        continue;
                                }

                                Category category = new Category();
                                category.setId(UUID.randomUUID());
                                category.setName(c.name());
                                category.setGroup(group);
                                category.setIcon(iconOpt.get());

                                categoryRepository.save(category);
                        }
                }

                System.out.println("Đã khởi tạo " + categoryRepository.count() + " danh mục con.");
        }

        private record ChildCategory(String name, String iconFile) {
        }
}
