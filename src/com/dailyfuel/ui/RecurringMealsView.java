package com.dailyfuel.ui;

import com.dailyfuel.data.DataManager;
import com.dailyfuel.logic.RecurringMealSync;
import com.dailyfuel.model.Category;
import com.dailyfuel.model.NutritionProfile;
import com.dailyfuel.model.RecurringMeal;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public final class RecurringMealsView {
    private RecurringMealsView() { }

    public static VBox create(NutritionProfile profile, List<RecurringMeal> rules,
                              Consumer<String> navigate, Runnable refresh) {
        VBox root = UiComponents.page("Recurring Meals", "Automatically log meals on a daily, weekly, or monthly schedule.", "recurring", navigate);
        TextField name = field("Meal name");
        ComboBox<Category> category = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        TextField calories = field("Calories"); TextField protein = field("Protein");
        TextField carbs = field("Carbs"); TextField fat = field("Fat");
        DatePicker start = new DatePicker(LocalDate.now());
        ComboBox<RecurringMeal.Frequency> frequency = new ComboBox<>(FXCollections.observableArrayList(RecurringMeal.Frequency.values()));
        frequency.setValue(RecurringMeal.Frequency.DAILY);
        TextField occurrences = field("-1 for unlimited"); occurrences.setText("-1");
        TextField notes = field("Optional notes");

        GridPane form = new GridPane(); form.setHgap(12); form.setVgap(12);
        add(form, 0, "Meal", name, "Type", category);
        add(form, 1, "Calories", calories, "Protein", protein);
        add(form, 2, "Carbs", carbs, "Fat", fat);
        add(form, 3, "Start", start, "Frequency", frequency);
        add(form, 4, "Occurrences", occurrences, "Notes", notes);

        Label feedback = new Label();
        Button create = new Button("Add Recurring Meal"); create.getStyleClass().add("primary-button");
        create.setOnAction(event -> {
            try {
                if (name.getText().isBlank() || category.getValue() == null || start.getValue() == null)
                    throw new IllegalArgumentException("Enter a name, category, and start date.");
                int maximum = Integer.parseInt(occurrences.getText().trim());
                if (maximum == 0 || maximum < -1) throw new IllegalArgumentException("Occurrences must be -1 or a positive whole number.");
                rules.add(new RecurringMeal(name.getText().trim(), category.getValue(), number(calories), number(protein),
                        number(carbs), number(fat), start.getValue(), frequency.getValue(), maximum, notes.getText().trim()));
                RecurringMealSync.syncRecurringMeals(profile, rules);
                DataManager.saveMeals(profile.getMeals());
                DataManager.saveRecurringMeals(rules);
                refresh.run();
            } catch (NumberFormatException ex) {
                feedback.setText("Occurrences must be -1 or a positive whole number."); feedback.getStyleClass().setAll("error-label");
            } catch (IllegalArgumentException ex) {
                feedback.setText(ex.getMessage()); feedback.getStyleClass().setAll("error-label");
            }
        });

        VBox ruleList = new VBox(9);
        if (rules.isEmpty()) ruleList.getChildren().add(new Label("No recurring meals created yet."));
        for (RecurringMeal rule : rules) {
            String limit = rule.getMaxOccurrences() == -1 ? "unlimited" : String.valueOf(rule.getMaxOccurrences());
            Label label = new Label(rule.getMealName() + " | " + rule.getMealType() + " | " +
                    rule.getFrequency() + " from " + rule.getStartDate() + " | " + limit + " occurrence(s)");
            Button delete = new Button("Delete"); delete.getStyleClass().add("danger-button");
            delete.setOnAction(event -> { rules.remove(rule); DataManager.saveRecurringMeals(rules); refresh.run(); });
            HBox row = new HBox(12, label, delete); row.setAlignment(Pos.CENTER_LEFT); HBox.setHgrow(label, Priority.ALWAYS);
            ruleList.getChildren().add(row);
        }
        root.getChildren().addAll(UiComponents.card("New Recurring Meal", form, new HBox(12, create, feedback)),
                UiComponents.card("Schedules", ruleList));
        return root;
    }

    private static TextField field(String prompt) { TextField f = new TextField(); f.setPromptText(prompt); return f; }
    private static double number(TextField field) {
        try { double value = Double.parseDouble(field.getText().trim()); if (value < 0) throw new NumberFormatException(); return value; }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Nutrition amounts must be valid nonnegative numbers."); }
    }
    private static void add(GridPane pane, int row, String a, Control one, String b, Control two) {
        pane.add(new Label(a), 0, row); pane.add(one, 1, row); pane.add(new Label(b), 2, row); pane.add(two, 3, row);
        one.setMaxWidth(Double.MAX_VALUE); two.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(one, Priority.ALWAYS); GridPane.setHgrow(two, Priority.ALWAYS);
    }
}
