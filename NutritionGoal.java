package com.dailyfuel.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class NutritionGoal {
    private String name;
    private double calorieGoal;
    private double proteinGoal;
    private double carbGoal;
    private double fatGoal;
    private Category category;
    private List<Meal> meals;
    private LocalDate startDate; 
    private LocalDate endDate;   

    public Goals(String name, double calorieGoal, double proteinGoal, double carbGoal, double fatGoal, Category category, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.calorieGoal = calorieGoal;
        this.proteinGoal = proteinGoal;
        this.carbGoal = carbGoal;
        this.fatGoal = fatGoal;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate; 
        this.meals = new ArrayList<>();
    }
    
    public String getName() {
        return this.name;
    }

    public void addMeal(Meal m) {   
    	boolean withinTime = !m.getDate().isBefore(startDate) && !m.getDate().isAfter(endDate);
		if (m.getMealType() == this.category && withinTime) {
			meals.add(m);
        }
    }

    public double getTotalCalories() {
        return meals.stream().mapToDouble(Meal::getCalories).sum();
    }
    public double getTotalProtein() {
        return meals.stream().mapToDouble(Meal::getProtein).sum();
    }
    public double getTotalCarbs() {
        return meals.stream().mapToDouble(Meal::getCarbs).sum();
    }
    public double getTotalFat() {
        return meals.stream().mapToDouble(Meal::getFat).sum();
    }

    public boolean isCalorieGoalMet() {
        return getTotalCalories() >= calorieGoal;
    }
    public boolean isProteinGoalMet() {
        return getTotalProtein() >= proteinGoal;
    }
    public boolean isCarbGoalMet() {
        return getTotalCarbs() >= carbGoal;
    }
    public boolean isFatGoalMet() {
        return getTotalFat() >= fatGoal;
    }
    
    public boolean isCalorieGoalClose() {
        return getTotalCalories() >= (this.calorieGoal * 0.8);
    }
    public boolean isProteinGoalClose() {
        return getTotalProtein() >= (this.proteinGoal * 0.8);
    }
    public boolean isCarbGoalClose() {
        return getTotalCarbs() >= (this.carbGoal * 0.8);
    }
    public boolean isFatGoalClose() {
        return getTotalFat() >= (this.fatGoal * 0.8);
    }
    
    public double getRemainingCalories() {
        return calorieGoal - getTotalCalories();
    }
    public double getRemainingProtein() {
        return proteinGoal - getTotalProtein();
    }
    public double getRemainingCarbs() {
        return carbGoal - getTotalCarbs();
    }
    public double getRemainingFat() {
        return fatGoal - getTotalFat();
    }
	
    public void removeMeal(Meal m) {
        this.meals.remove(m);
    }
	
	public double getCalorieGoal() {
		return this.calorieGoal;
	}
	public double getProteinGoal() {
	    return proteinGoal;
	}
	public double getCarbGoal() {
	    return carbGoal;
	}
	public double getFatGoal() {
	    return fatGoal;
	}

	public LocalDate getStartDate() {
		return this.startDate;
	}

	public LocalDate getEndDate() {
		return this.endDate;
	}
	
	public Category getCategory() { 
		return this.category; 
	}
    
	@Override
    public String toString() {
		public String toString() {
		    StringBuilder sb = new StringBuilder();
		    sb.append(name).append(" [").append(category).append("]\n");
		    sb.append("Calories: ")
		      .append(String.format("%.1f", getTotalCalories()))
		      .append(" / ")
		      .append(String.format("%.1f", calorieGoal));
		    if (isOverGoal()) {
		        sb.append(" (Reached)");
		    }
		    else if (isCloseLimit()) {
		        sb.append(" (>80% Reached)");
		    }
		    sb.append("\n");
		    sb.append("Protein: ")
		      .append(String.format("%.1f", getTotalProtein()))
		      .append("g / ")
		      .append(String.format("%.1f", proteinGoal))
		      .append("g");
		    if (isProteinOverGoal()) {
		        sb.append(" (Reached)");
		    }
		    else if (isProteinCloseLimit()) {
		        sb.append(" (>80% Reached)");
		    }
		    sb.append("\n");
		    sb.append("Carbs: ")
		      .append(String.format("%.1f", getTotalCarbs()))
		      .append("g / ")
		      .append(String.format("%.1f", carbGoal))
		      .append("g");
		    if (isCarbOverGoal()) {
		        sb.append(" (Reached)");
		    }
		    else if (isCarbCloseLimit()) {
		        sb.append(" (>80% Reached)");
		    }
		    sb.append("\n");
		    sb.append("Fat: ")
		      .append(String.format("%.1f", getTotalFat()))
		      .append("g / ")
		      .append(String.format("%.1f", fatGoal))
		      .append("g");
		    if (isFatOverGoal()) {
		        sb.append(" (Reached)");
		    }
		    else if (isFatCloseLimit()) {
		        sb.append(" (>80% Reached)");
		    }
		    return sb.toString();
		}
}
