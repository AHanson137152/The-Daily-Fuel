package com.dailyfuel.data;

import com.dailyfuel.model.Category;
import com.dailyfuel.model.Meal;
import com.dailyfuel.model.NutritionGoal;
import com.dailyfuel.model.RecurringMeal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class DataManager {
	
	private static final String SEPARATOR = "|";
    private static final Path MEALS_FILE =
            Paths.get("meals.txt");
    private static final Path GOALS_FILE = Paths.get("goals.txt");
    private static final Path RECURRING_FILE = Paths.get("recurring-meals.txt");

    /*
     * Saves all Daily Fuel application data.
     *
     * Additional information, such as nutrition goals,
     * can be added here later.
     */
    public static void saveAllData(List<Meal> meals) {
        saveMeals(meals);
    }

    /*
     * Loads all Daily Fuel application data.
     *
     * This currently returns the saved meals.
     */
    public static List<Meal> loadAllData() {
        return loadMeals();
    }

    /*
     * Saves every meal in the list to meals.txt.
     *
     * File order:
     * meal type
     * meal name
     * calories
     * protein
     * carbs
     * fat
     * date
     * notes
     */
    public static void saveMeals(List<Meal> meals) {
        try (BufferedWriter writer =
                     Files.newBufferedWriter(
                             MEALS_FILE,
                             StandardCharsets.UTF_8)) {

            if (meals == null) {
                return;
            }

            for (Meal meal : meals) {
                if (meal == null) {
                    continue;
                }

                String line = String.join(
                        SEPARATOR,
                        meal.getMealType().name(),
                        encodeText(meal.getMealName()),
                        String.valueOf(meal.getCalories()),
                        String.valueOf(meal.getProtein()),
                        String.valueOf(meal.getCarbs()),
                        String.valueOf(meal.getFat()),
                        meal.getDate().toString(),
                        encodeText(meal.getNotes())
                );

                writer.write(line);
                writer.newLine();
            }

        } catch (IOException exception) {
            System.err.println(
                    "Error saving meals: "
                            + exception.getMessage());
        }
    }

    /*
     * Loads meals from meals.txt.
     *
     * If the file does not exist yet, an empty list
     * is returned instead of causing an error.
     */
    public static List<Meal> loadMeals() {
        List<Meal> meals = new ArrayList<>();

        if (!Files.exists(MEALS_FILE)) {
            return meals;
        }

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             MEALS_FILE,
                             StandardCharsets.UTF_8)) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                String[] parts =
                        line.split("\\|", -1);

                if (parts.length != 8) {
                    System.err.println(
                            "Skipping invalid meal on line "
                                    + lineNumber
                                    + ": expected 8 values.");
                    continue;
                }

                try {
                    Category mealType =
                            Category.valueOf(parts[0]);

                    String mealName =
                            decodeText(parts[1]);

                    double calories =
                            Double.parseDouble(parts[2]);

                    double protein =
                            Double.parseDouble(parts[3]);

                    double carbs =
                            Double.parseDouble(parts[4]);

                    double fat =
                            Double.parseDouble(parts[5]);

                    LocalDate date =
                            LocalDate.parse(parts[6]);

                    String notes =
                            decodeText(parts[7]);

                    Meal meal = new Meal(
                            mealType,
                            mealName,
                            calories,
                            protein,
                            carbs,
                            fat,
                            date,
                            notes
                    );

                    meals.add(meal);

                } catch (IllegalArgumentException
                         | DateTimeParseException exception) {

                    System.err.println(
                            "Skipping invalid meal on line "
                                    + lineNumber
                                    + ": "
                                    + exception.getMessage());
                }
            }

        } catch (IOException exception) {
            System.err.println(
                    "Error loading meals: "
                            + exception.getMessage());
        }

        return meals;
    }

    /*
     * Encodes meal names and notes so characters such
     * as "|" do not damage the saved file format.
     */
    private static String encodeText(String text) {
        if (text == null) {
            text = "";
        }

        return Base64.getEncoder().encodeToString(
                text.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * Converts saved meal names and notes back
     * into normal text.
     */
    private static String decodeText(String encodedText) {
        if (encodedText == null
                || encodedText.isEmpty()) {
            return "";
        }

        byte[] decodedBytes =
                Base64.getDecoder().decode(encodedText);

        return new String(
                decodedBytes,
                StandardCharsets.UTF_8);
    }

    public static void saveGoals(List<NutritionGoal> goals) {
        try (BufferedWriter writer = Files.newBufferedWriter(GOALS_FILE, StandardCharsets.UTF_8)) {
            if (goals == null) return;
            for (NutritionGoal goal : goals) {
                writer.write(String.join(SEPARATOR,
                        encodeText(goal.getName()),
                        String.valueOf(goal.getCalorieGoal()),
                        String.valueOf(goal.getProteinGoal()),
                        String.valueOf(goal.getCarbGoal()),
                        String.valueOf(goal.getFatGoal()),
                        goal.getCategory().name(),
                        goal.getStartDate().toString(),
                        goal.getEndDate().toString()));
                writer.newLine();
            }
        } catch (IOException exception) {
            System.err.println("Error saving goals: " + exception.getMessage());
        }
    }

    public static List<NutritionGoal> loadGoals() {
        List<NutritionGoal> goals = new ArrayList<>();
        if (!Files.exists(GOALS_FILE)) return goals;
        try (BufferedReader reader = Files.newBufferedReader(GOALS_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 8) continue;
                goals.add(new NutritionGoal(
                        decodeText(parts[0]),
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]),
                        Category.valueOf(parts[5]),
                        LocalDate.parse(parts[6]),
                        LocalDate.parse(parts[7])));
            }
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Error loading goals: " + exception.getMessage());
        }
        return goals;
    }

    public static void saveRecurringMeals(List<RecurringMeal> rules) {
        try (BufferedWriter writer = Files.newBufferedWriter(RECURRING_FILE, StandardCharsets.UTF_8)) {
            if (rules == null) return;
            for (RecurringMeal rule : rules) {
                writer.write(String.join(SEPARATOR,
                        encodeText(rule.getMealName()),
                        rule.getMealType().name(),
                        String.valueOf(rule.getCalories()),
                        String.valueOf(rule.getProtein()),
                        String.valueOf(rule.getCarbs()),
                        String.valueOf(rule.getFat()),
                        rule.getStartDate().toString(),
                        rule.getFrequency().name(),
                        String.valueOf(rule.getMaxOccurrences()),
                        encodeText(rule.getNotes())));
                writer.newLine();
            }
        } catch (IOException exception) {
            System.err.println("Error saving recurring meals: " + exception.getMessage());
        }
    }

    public static List<RecurringMeal> loadRecurringMeals() {
        List<RecurringMeal> rules = new ArrayList<>();
        if (!Files.exists(RECURRING_FILE)) return rules;
        try (BufferedReader reader = Files.newBufferedReader(RECURRING_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 10) continue;
                rules.add(new RecurringMeal(
                        decodeText(parts[0]), Category.valueOf(parts[1]),
                        Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]), Double.parseDouble(parts[5]),
                        LocalDate.parse(parts[6]), RecurringMeal.Frequency.valueOf(parts[7]),
                        Integer.parseInt(parts[8]), decodeText(parts[9])));
            }
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Error loading recurring meals: " + exception.getMessage());
        }
        return rules;
    }
