package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumMenuCategory {
    BEVERAGES("Beverages"),             // Drinks, including soft drinks, juices, tea, coffee, and alcoholic beverages
    MAIN_COURSE("Main Course"),         // Main dishes, typically the centerpiece of a meal
    DESSERTS("Desserts"),               // Sweet dishes served after the main course, including cakes, ice cream, and pastries
    SNACKS("Snacks"),                   // Light, quick-to-eat dishes like chips, nuts, or small sandwiches
    FRIED_FOOD("Fried Food"),           // Dishes that are deep-fried or pan-fried, such as fries, tempura, or fried chicken
    GRILLED_FOOD("Grilled Food"),       // Grilled dishes, including barbecue, kebabs, and steaks
    SPECIALTIES("Specialties"),         // Specialty dishes unique to the restaurant or cuisine
    APPETIZERS("Appetizers"),           // Starters or small dishes served before the main course, such as soups or salads
    VEGETARIAN("Vegetarian"),           // Dishes made without meat, often plant-based
    VEGAN("Vegan"),                     // Dishes made entirely without animal products
    SEAFOOD("Seafood"),                 // Dishes featuring fish, shrimp, crab, and other seafood
    SIDE_DISHES("Side Dishes"),         // Dishes served alongside the main course, such as mashed potatoes or steamed vegetables;
    OTHERS("");
    private final String displayName;

    // Constructor
    EnumMenuCategory(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    // Getter for the display name
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    // Method to convert display name to enum
    public static EnumMenuCategory fromDisplayName(String displayName) {
        for (EnumMenuCategory category : EnumMenuCategory.values()) {
            if (category.getDisplayName().equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    // Method to return all display names as a String array
    public static String[] getDisplayNames() {
        String[] displayNames = new String[EnumMenuCategory.values().length];
        for (int i = 0; i < EnumMenuCategory.values().length; i++) {
            displayNames[i] = EnumMenuCategory.values()[i].getDisplayName();
        }
        return displayNames;
    }
}
