package com.dailyfuel.logic;

import com.dailyfuel.model.NutritionGoal;


public class GoalComparison {
	public static double caloriesRemaining(double caloriesConsumed, NutritionGoal goal) {

        return goal.getCalorieGoal() - caloriesConsumed;
    }

    public static boolean calorieGoalExceeded(double caloriesConsumed, NutritionGoal goal) {

        return caloriesConsumed > goal.getCalorieGoal();
    }

    public static boolean carbGoalExceeded(double carbsConsumed, NutritionGoal goal) {

        return carbsConsumed > goal.getCarbGoal();
    }

    public static double calorieProgressPercentage(double caloriesConsumed, NutritionGoal goal) {

        if (goal.getCalorieGoal() <= 0) {
            return 0;
        }

        return caloriesConsumed
                / goal.getCalorieGoal()
                * 100;
    }
}
