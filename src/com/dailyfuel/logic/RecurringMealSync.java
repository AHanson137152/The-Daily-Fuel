package com.dailyfuel.logic;
	import java.time.LocalDate;
	import java.util.List;
	import com.dailyfuel.model.NutritionProfile;
	import com.dailyfuel.model.RecurringMeal;
	import com.dailyfuel.model.Meal;

	public class RecurringMealSync {
		
		//Add recurring meal from Recurring list
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
	    
	    //Check meal transactions
	    private static boolean CheckDuplicate(NutritionProfile profile, Meal meal) {
	    	
	    	for (Meal existingMeal : profile.getMeals()) {

	            boolean sameDate =
	                    existingMeal.getDate()
	                            .equals(meal.getDate());

	            boolean sameName =
                    existingMeal.getMealName()
                            .equals(meal.getMealName());

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

	        return false;
	    }
	}
