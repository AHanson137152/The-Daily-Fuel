package com.dailyfuel.model;

	
	import java.time.LocalDate;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Objects;

	public class RecurringMeal {

		public enum Frequency {
		        DAILY,
		        WEEKLY,
		        MONTHLY
		    }

		    private String mealName;
		    private Category mealType;
		    private double calories;
		    private double protein;
		    private double carbs;
		    private double fat;
		    private LocalDate startDate;
		    private Frequency frequency;
		    private int maxOccurrences;
		    private String notes;

		    private final List<Meal> mealHistory;

		    public RecurringMeal(
		            String mealName,
		            Category mealType,
		            double calories,
		            double protein,
		            double carbs,
		            double fat,
		            LocalDate startDate,
		            Frequency frequency,
		            int maxOccurrences,
		            String notes) {

		        this.mealName = mealName;
		        this.mealType = mealType;
		        this.calories = calories;
		        this.protein = protein;
		        this.carbs = carbs;
		        this.fat = fat;
		        this.startDate = startDate;
		        this.frequency = frequency;
		        this.maxOccurrences = maxOccurrences;
		        this.notes = notes;
		        this.mealHistory = new ArrayList<>();
		    }

		    public void generateMealsUntil(LocalDate endDate) {
		        LocalDate currentDate;

		        if (mealHistory.isEmpty()) {
		            currentDate = startDate;
		        } else {
		            LocalDate lastDate =
		                    mealHistory.get(mealHistory.size() - 1).getDate();

		            currentDate = getNextDate(lastDate);
		        }

		        while (!currentDate.isAfter(endDate)) {
		            if (maxOccurrences != -1
		                    && mealHistory.size() >= maxOccurrences) {
		                break;
		            }

		            Meal meal = new Meal(
		                    mealType,
		                    mealName,
		                    calories,
		                    protein,
		                    carbs,
		                    fat,
		                    currentDate,
		                    notes
		            );

		            mealHistory.add(meal);
		            currentDate = getNextDate(currentDate);
		        }
		    }

		    private LocalDate getNextDate(LocalDate currentDate) {
		        switch (frequency) {
		            case DAILY:
		                return currentDate.plusDays(1);

		            case WEEKLY:
		                return currentDate.plusWeeks(1);

		            case MONTHLY:
		                return currentDate.plusMonths(1);

		            default:
		                throw new IllegalStateException(
		                        "Unknown recurring meal frequency");
		        }
		    }

		    public List<Meal> getMealHistory() {
		        return new ArrayList<>(mealHistory);
		    }

		    public String getMealName() {
		        return mealName;
		    }

		    public Category getMealType() {
		        return mealType;
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

		    public LocalDate getStartDate() {
		        return startDate;
		    }

		    public Frequency getFrequency() {
		        return frequency;
		    }

		    public int getMaxOccurrences() {
		        return maxOccurrences;
		    }

		    public String getNotes() {
		        return notes;
		    }

		    @Override
		    public boolean equals(Object object) {
		        if (this == object) {
		            return true;
		        }

		        if (object == null || getClass() != object.getClass()) {
		            return false;
		        }

		        RecurringMeal that = (RecurringMeal) object;

		        return Double.compare(that.calories, calories) == 0
		                && Double.compare(that.protein, protein) == 0
		                && Double.compare(that.carbs, carbs) == 0
		                && Double.compare(that.fat, fat) == 0
		                && maxOccurrences == that.maxOccurrences
		                && Objects.equals(mealName, that.mealName)
		                && mealType == that.mealType
		                && Objects.equals(startDate, that.startDate)
		                && frequency == that.frequency
		                && Objects.equals(notes, that.notes);
		    }

		    @Override
		    public int hashCode() {
		        return Objects.hash(
		                mealName,
		                mealType,
		                calories,
		                protein,
		                carbs,
		                fat,
		                startDate,
		                frequency,
		                maxOccurrences,
		                notes
		        );
		    }

}
