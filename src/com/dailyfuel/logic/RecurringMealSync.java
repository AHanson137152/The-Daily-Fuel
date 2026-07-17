package com.dailyfuel.logic;
	import java.time.LocalDate;
	import java.util.List;
	import com.dailyfuel.model.NutritionProfile;
	import com.dailyfuel.model.RecurringMeal;
	import com.dailyfuel.model.Meal;

	public class RecurringMealSync {
		
		//Add recurring transaction from Recurring list
	    public static void syncRecurringMeals(NutritionProfile profile, List<RecurringMeal> recurringMeals) {
	      
	    	LocalDate today = LocalDate.now();
	        for (RecurringMeal recurringMeal : recurringMeals) {
	            
	            recurringMeal.generateMealsUntil(today); 
	            
	            for (Meal newMeal : recurringMeal.getMealHistory()) { 
	            	recurringMeal.getMealHistory();
	                if (!CheckDuplicate(profile, newMeal)) {
	                    profile.addMeal(newMeal);              
	            }
	        }
	    }
	    }
	    
	    //Check duplicate transactions
	    private static boolean CheckDuplicate(NutritionProfile profile, Meal meal) {
	    	
	    	for (Meal existingMeal : profile.getMeals()) {

	            boolean sameDate =
	                    existingMeal.getDate()
	                            .equals(meal.getDate());

	            boolean sameName =
	                    existingMeal.getName()
	                            .equals(meal.getName());

	            boolean sameCalories =
	                    Double.compare(
	                            existingMeal.getCalories(),
	                            meal.getCalories()) == 0;

	            boolean sameMealType =
	                    existingMeal.getMealType()
	                            == meal.getMealType();

	            if (sameDate
	                    && sameName
	                    && sameCalories
	                    && sameMealType) {

	                return true;
	            }
	        }

	        return false;
	    }
	}
