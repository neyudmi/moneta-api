package com.example.expense_service.configs;

import com.example.expense_service.models.Icon;
import com.example.expense_service.repositories.IconRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class IconInitializer {

    private final IconRepository iconRepository;

    public IconInitializer(IconRepository iconRepository) {
        this.iconRepository = iconRepository;
    }

    @PostConstruct
    public void initIcons() {
        if (iconRepository.count() > 0)
            return;

        List<String> fileNames = Arrays.asList(
                "ic_accessory", "ic_book", "ic_breakfast", "ic_cafe", "ic_carwash",
                "ic_cleaning", "ic_clother", "ic_cosmetics", "ic_diner", "ic_electric",
                "ic_entertainment", "ic_fim", "ic_fixx", "ic_funeral", "ic_gas",
                "ic_gasoline", "ic_gift", "ic_handshake", "ic_houserepair", "ic_insurance",
                "ic_interior", "ic_internet", "ic_lunch", "ic_marrrt", "ic_medicin",
                "ic_milk", "ic_parking", "ic_pocketmoney", "ic_rent", "ic_restaurant",
                "ic_shoes", "ic_skincare", "ic_smartphone", "ic_sport", "ic_stethoscope",
                "ic_study", "ic_taxi", "ic_telephone", "ic_tourism", "ic_toy",
                "ic_truck", "ic_tuition", "ic_tv", "ic_visiatpatient", "ic_water", "ic_food", "ic_children", "ic_house",
                "ic_house_2", "ic_destination", "ic_hieuhi", "ic_travele", "ic_improve", "ic_health", "ic_suit",
                "ic_borrow", "ic_gift_2", "ic_savemoney", "ic_salary_2", "ic_reward", "ic_interest", "ic_income",
                "ic_debt", "ic_defference");

        fileNames.forEach(name -> {
            String displayName = name.replace("ic_", "")
                    .replace("_", " ")
                    .trim();
            Icon icon = new Icon(
                    capitalize(displayName),
                    name,
                    "Default icon: " + displayName);
            iconRepository.save(icon);
        });

        System.out.println("Icon data initialized: " + iconRepository.count() + " icons added.");
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty())
            return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
