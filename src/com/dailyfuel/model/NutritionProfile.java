package com.dailyfuel.model;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class NutritionProfile {
    private List<Meal> meals;
    private List<NutritionGoal> goals;


    public NutritionProfile() {
        meals = new ArrayList<>();
        goals = new ArrayList<>();
    }

    public NutritionProfile(List<Meal> meals, List<NutritionGoal> goals) {
        this.meals = meals == null ? new ArrayList<>() : new ArrayList<>(meals);
        this.goals = goals == null ? new ArrayList<>() : new ArrayList<>(goals);
        rebuildGoalProgress();
    }

    public void addGoal(NutritionGoal goal) {
        this.goals.add(goal);
    }
    
    public List<String> addMeal(Meal m) {
        this.meals.add(m);
		List<String> notifications = new ArrayList<>(); 
        for (NutritionGoal goal : this.goals) {
        	// Check status BEFORE adding the meal
            double caloriesBefore = goal.getTotalCalories(); // Check amount before adding to goal
			goal.addMeal(m); 
			double caloriesAfter = goal.getTotalCalories(); // Check amount after adding to goal
            
            // Check status AFTER adding the meal
            if (caloriesAfter > caloriesBefore) {
	            boolean wasOver = caloriesBefore > goal.getCalorieGoal();
	            boolean isOver = caloriesAfter > goal.getCalorieGoal();
	
	            boolean wasClose = caloriesBefore >= (goal.getCalorieGoal() * 0.8);
	            boolean isClose = caloriesAfter >= (goal.getCalorieGoal() * 0.8);
	        	
	        	//Notify
	        	if (isOver && !wasOver)
	        		notifications.add("You have exceeded your " + goal.getName());
	        	else if (isClose && !wasClose)
	        		notifications.add("You have reached 80% of your " + goal.getName());    
			}
		}
		return notifications;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public boolean removeMeal(Meal meal) {
        if (!meals.remove(meal)) {
            return false;
        }
        rebuildGoalProgress();
        return true;
    }

    public double getCaloriesConsumed() {
        double total = 0;
        for (Meal m : meals) {
        	total += m.getCalories();
        }
        return total;
    }
    
    
    //Filter by Category
    public double FilterCategory(Category category) {
        double total = 0;
        for (Meal m : meals) {
            if (category == null || m.getMealType() == category) // added null for UI chart
                total += m.getCalories();      
        }
        return total;
    }

    //Get total calories for a specific period
    public double getTotalCalories(LocalDate startDate, LocalDate endDate) {
        return meals.stream()
                .filter(m -> !m.getDate().isBefore(startDate) && !m.getDate().isAfter(endDate))
                .mapToDouble(Meal::getCalories)
                .sum();
    }
	
    
    public void deleteMeal(Meal m) {
        // Remove from the profile's main list
        this.meals.remove(m);

        // Also remove from any associated goals
        for (NutritionGoal goal : this.goals) {
            goal.removeMeal(m);
        }
    }

    //Make println(NutritionProfile) work
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("All Meals:\n");
        for (Meal m : meals) {
            sb.append(m).append("\n");
        }
        sb.append("Today's Calories: ").append(String.format("%.1f", getCaloriesConsumed()));
        return sb.toString();
    }

    public double getTotalCalories() {
        return FilterCategory(null);
    }
	
	public List<NutritionGoal> getGoals() {
		return this.goals;
	}

    public NutritionGoal getNutritionGoal() {
        return goals.isEmpty() ? null : goals.get(0);
    }

    public void setNutritionGoal(NutritionGoal goal) {
        goals.clear();
        if (goal != null) {
            goals.add(goal);
        }
        rebuildGoalProgress();
    }

    public boolean removeGoal(NutritionGoal goal) {
        return goals.remove(goal);
    }

    private void rebuildGoalProgress() {
        for (NutritionGoal goal : goals) {
            goal.clearMeals();
            for (Meal meal : meals) {
                goal.addMeal(meal);
            }
        }
    }
	
}
