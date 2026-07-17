package com.dailyfuel.logic;

import com.dailyfuel.model.Category;
import com.dailyfuel.model.Meal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
public class MealSearch {
	private final List<Meal> meals;

    // Constructor
    public MealSearch(List<Meal> meals) {
    	 if (meals == null) {
             this.meals = new ArrayList<>();
         } else {
             this.meals = meals;
         }
     }
    

    // Search meals by name, ignoring capitalization
    // If the keyword is blank or null, return all meals
    public List<Meal> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllMeals();
        }

        String lowerKeyword = keyword.trim().toLowerCase(Locale.ROOT);

        return meals.stream()
        		.filter(meal -> meal != null)
                .filter(meal -> meal.getMealName() != null)
                .filter(meal ->
                        meal.getMealName()
                                .toLowerCase(Locale.ROOT)
                                .contains(lowerKeyword))
                .collect(Collectors.toList());
    }
    // Filter meals by calorie range
    public List<Meal> filterByCalories(double minCalories, double maxCalories) {
    	 if (minCalories > maxCalories) {
             return Collections.emptyList();
         }
    	 
    	 return meals.stream()
                 .filter(meal -> meal != null)
                 .filter(meal ->
                         meal.getCalories() >= minCalories
                         && meal.getCalories() <= maxCalories)
                 .collect(Collectors.toList());
     }
       
    // Filter meals by date range
    public List<Meal> filterByDateRange( LocalDate startDate, LocalDate endDate) {
    	  if (startDate == null
                  || endDate == null
                  || startDate.isAfter(endDate)) {

              return Collections.emptyList();
          }


        return meals.stream()
                .filter(meal ->
                        !meal.getDate().isBefore(startDate)
                        && !meal.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    // Filter meals by meal type
    public List<Meal> filterByMealType(Category mealType) {
    	  if (mealType == null) {
              return Collections.emptyList();
          }
    	
    	return meals.stream()
                .filter(meal ->
                        meal.getMealType() == mealType)
                .collect(Collectors.toList());
    }

    // Filter meals by protein range
    public List<Meal> filterByProtein(  double minProtein, double maxProtein) {
    	  if (minProtein > maxProtein) {
              return Collections.emptyList();
          }
        return meals.stream()
                .filter(meal ->
                        meal.getProtein() >= minProtein
                        && meal.getProtein() <= maxProtein)
                .collect(Collectors.toList());
    }

    // Filter meals by carbohydrate range
    public List<Meal> filterByCarbs(double minCarbs, double maxCarbs) {
    	 if (minCarbs > maxCarbs) {
             return Collections.emptyList();
         }

        return meals.stream()
                .filter(meal ->
                        meal.getCarbs() >= minCarbs
                        && meal.getCarbs() <= maxCarbs)
                .collect(Collectors.toList());
    }

    // Filter meals by fat range
    public List<Meal> filterByFat(double minFat, double maxFat) {
    	 if (minFat > maxFat) {
             return Collections.emptyList();
         }


        return meals.stream()
                .filter(meal ->
                        meal.getFat() >= minFat
                        && meal.getFat() <= maxFat)
                .collect(Collectors.toList());
    }

    // Return a copy of all meals
    public List<Meal> getAllMeals() {
        return new ArrayList<>(meals);
    }
}
