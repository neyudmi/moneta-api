package com.example.expense_service.configs;

import com.example.expense_service.entities.Icon;
import com.example.expense_service.repositories.IconRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("iconInitializer")
public class IconInitializer {

    private final IconRepository iconRepository;

    public IconInitializer(IconRepository iconRepository) {
        this.iconRepository = iconRepository;
    }

    @PostConstruct
    public void init() {
        if (iconRepository.count() > 0) {
            return;
        }

        List<String> fileNames = List.of(
                "ic_accessory", "ic_book", "ic_breakfast", "ic_cafe", "ic_carwash",
                "ic_cleaning", "ic_clothes", "ic_cosmetics", "ic_dinner", "ic_electric",
                "ic_entertainment", "ic_film", "ic_fix", "ic_funeral", "ic_gas",
                "ic_gasoline", "ic_gift", "ic_handshake", "ic_houserepair", "ic_insurance",
                "ic_interior", "ic_internet", "ic_lunch", "ic_marriage", "ic_medicine",
                "ic_milk", "ic_parking", "ic_pocketmoney", "ic_rent", "ic_restaurant",
                "ic_shoes", "ic_skincare", "ic_smartphone", "ic_sport", "ic_stethoscope",
                "ic_study", "ic_taxi", "ic_telephone", "ic_tourism", "ic_toy",
                "ic_truck", "ic_tuition", "ic_tv", "ic_visit_patient", "ic_water",
                "ic_food", "ic_children", "ic_house", "ic_house_2", "ic_destination",
                "ic_hieuhi", "ic_travel", "ic_improve", "ic_health", "ic_suit",
                "ic_borrow", "ic_gift_2", "ic_savemoney", "ic_salary_2", "ic_reward",
                "ic_interest", "ic_income", "ic_debt", "ic_difference");

        List<Icon> icons = fileNames.stream()
                .map(this::createIcon)
                .toList();

        iconRepository.saveAll(icons);
    }

    private Icon createIcon(String fileName) {
        String name = fileName
                .replace("ic_", "")
                .replace("_", " ");

        return new Icon(
                capitalize(name),
                fileName,
                "Default icon: " + name);
    }

    private String capitalize(String text) {
        return Character.toUpperCase(text.charAt(0))
                + text.substring(1);
    }
}