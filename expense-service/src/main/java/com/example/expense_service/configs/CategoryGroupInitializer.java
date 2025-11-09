package com.example.expense_service.configs;

import com.example.expense_service.models.CategoryGroup;
import com.example.expense_service.models.Icon;
import com.example.expense_service.repositories.CategoryGroupRepository;
import com.example.expense_service.repositories.IconRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryGroupInitializer {

    private final CategoryGroupRepository groupRepository;
    private final IconRepository iconRepository;

    public CategoryGroupInitializer(CategoryGroupRepository groupRepository, IconRepository iconRepository) {
        this.groupRepository = groupRepository;
        this.iconRepository = iconRepository;
    }

    @PostConstruct
    public void initCategoryGroups() {
        if (groupRepository.count() > 0)
            return;

        List<GroupData> groups = List.of(
                new GroupData("Ăn uống", "ic_food"),
                new GroupData("Con cái", "ic_children"),
                new GroupData("Dịch vụ sinh hoạt", "ic_house"),
                new GroupData("Đi lại", "ic_destination"),
                new GroupData("Hiếu hỉ", "ic_hieuhi"),
                new GroupData("Hưởng thụ", "ic_travele"),
                new GroupData("Nhà cửa", "ic_house_2"),
                new GroupData("Sức khỏe", "ic_health"),
                new GroupData("Phát triển bản thân", "ic_improve"),
                new GroupData("Trang phục", "ic_suit"));

        for (GroupData g : groups) {
            Optional<Icon> iconOpt = iconRepository.findByFileName(g.iconFile());
            if (iconOpt.isPresent()) {
                CategoryGroup group = new CategoryGroup(
                        UUID.randomUUID(),
                        g.name(),
                        iconOpt.get());
                groupRepository.save(group);
            } else {
                System.err.println("Không tìm thấy icon cho nhóm: " + g.name() + " (" + g.iconFile() + ")");
            }
        }

        System.out.println("Đã khởi tạo " + groupRepository.count() + " nhóm danh mục cha mặc định.");
    }

    private record GroupData(String name, String iconFile) {
    }
}
