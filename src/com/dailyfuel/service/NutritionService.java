package com.dailyfuel.service;

import com.dailyfuel.data.DataManager;
import com.dailyfuel.logic.NutritionCalculator;
import com.dailyfuel.model.Meal;
import com.dailyfuel.model.NutritionGoal;
import com.dailyfuel.model.NutritionProfile;

import java.time.LocalDate;
import java.util.List;

public class NutritionService {
	
	private final NutritionProfile profile;

    public NutritionService(NutritionProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Nutrition profile cannot be null.");
        }

        this.profile = profile;
    }

    public void addMeal(Meal meal) {
        profile.addMeal(meal);
        DataManager.saveMeals(profile.getMeals());
    }

    public boolean removeMeal(Meal meal) {
        boolean removed = profile.removeMeal(meal);

        if (removed) {
            DataManager.saveMeals(profile.getMeals());
        }

        return removed;
    }

    public List<Meal> getMeals() {
        return profile.getMeals();
    }

    public List<Meal> getMealsForDate(LocalDate date) {
        return profile.getMeals()
                .stream()
                .filter(meal -> meal.getDate().equals(date))
                .toList();
    }

    public double getCaloriesForDate(LocalDate date) {
        return NutritionCalculator.calculateCalories(
                profile.getMeals(),
                date);
    }

    public double getProteinForDate(LocalDate date) {
        return NutritionCalculator.calculateProtein(
                profile.getMeals(),
                date);
    }

    public double getCarbsForDate(LocalDate date) {
        return NutritionCalculator.calculateCarbs(
                profile.getMeals(),
                date);
    }

    public double getFatForDate(LocalDate date) {
        return NutritionCalculator.calculateFat(
                profile.getMeals(),
                date);
    }

    public NutritionGoal getNutritionGoal() {
        return profile.getNutritionGoal();
    }

    public void updateNutritionGoal(NutritionGoal goal) {
        profile.setNutritionGoal(goal);
        DataManager.saveGoals(profile.getGoals());
    }
}
