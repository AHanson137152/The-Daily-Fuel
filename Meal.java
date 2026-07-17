package com.dailyfuel.model;
import java.time.LocalDate;

public class Meal {
    private Category mealType;
    private String mealName;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private LocalDate date;
    private String notes;

    public Meal(Category mealType, String mealName, double calories, double protein, double carbs, double fat, LocalDate date, String notes) {
		this.mealType = mealType;
		this.mealName = mealName;
		this.calories = calories;
		this.protein = protein;
		this.carbs = carbs;
		this.fat = fat;
		this.date = date;
		this.notes = notes;
	}

    public Category getMealType() {
        return mealType;
    }

    public String getMealName() {
        return mealName;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFat() {
        return fat;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

        //Make println(Meal) work
        @Override
        public String toString() {
        	return mealName + " | " + mealType + " | " + calories + " Calories | " + protein + "g Protein | " + carbs + "g Carbs | " + fat + "g Fat | " + date;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Meal that = (Meal) o;
            return Double.compare(that.calories, calories) == 0 &&
				   Double.compare(that.protein, protein) == 0 &&
				   Double.compare(that.carbs, carbs) == 0 &&
				   Double.compare(that.fat, fat) == 0 &&
				   mealType == that.mealType &&
				   mealName.equals(that.mealName) &&
				   date.equals(that.date) &&
				   notes.equals(that.notes);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(mealType, mealName, calories, protein, carbs, fat, date, notes);
        }
    }