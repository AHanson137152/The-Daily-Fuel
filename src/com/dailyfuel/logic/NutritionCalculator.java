package com.dailyfuel.logic;

import com.dailyfuel.model.Meal;

import java.time.LocalDate;
import java.util.List;

public class NutritionCalculator {


    public static double calculateCalories(
            List<Meal> meals,
            LocalDate date) {

        return meals.stream()
                .filter(meal -> meal.getDate().equals(date))
                .mapToDouble(Meal::getCalories)
                .sum();
    }

    public static double calculateProtein(
            List<Meal> meals,
            LocalDate date) {

        return meals.stream()
                .filter(meal -> meal.getDate().equals(date))
                .mapToDouble(Meal::getProtein)
                .sum();
    }

    public static double calculateCarbs(
            List<Meal> meals,
            LocalDate date) {

        return meals.stream()
                .filter(meal -> meal.getDate().equals(date))
                .mapToDouble(Meal::getCarbs)
                .sum();
    }

    public static double calculateFat(
            List<Meal> meals,
            LocalDate date) {

        return meals.stream()
                .filter(meal -> meal.getDate().equals(date))
                .mapToDouble(Meal::getFat)
                .sum();
    }
}


