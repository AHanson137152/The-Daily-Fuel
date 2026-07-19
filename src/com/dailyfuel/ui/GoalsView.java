package com.dailyfuel.ui;

import com.dailyfuel.data.DataManager;
import com.dailyfuel.model.Category;
import com.dailyfuel.model.NutritionGoal;
import com.dailyfuel.model.NutritionProfile;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.function.Consumer;

public final class GoalsView {
    private static final String ALL_MEAL_TYPES = "All meal types";

    private GoalsView() { }

    public static VBox create(NutritionProfile profile, Consumer<String> navigate, Runnable refresh) {
        VBox root = UiComponents.page("Nutrition Goals", "Set calorie and macro targets for all meal types or one category within a date range.", "goals", navigate);
        TextField name = field("Goal name");
        TextField calorie = field("Calories");
        TextField protein = field("Protein (g)");
        TextField carbs = field("Carbs (g)");
        TextField fat = field("Fat (g)");
        ComboBox<String> category = new ComboBox<>();
        category.getItems().add(ALL_MEAL_TYPES);
        for (Category value : Category.values()) {
            category.getItems().add(value.name());
        }
        category.setValue(ALL_MEAL_TYPES);
        DatePicker start = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker end = new DatePicker(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        add(form, 0, "Name", name, "Category", category);
        add(form, 1, "Calorie Goal", calorie, "Protein Goal", protein);
        add(form, 2, "Carb Goal", carbs, "Fat Goal", fat);
        add(form, 3, "Start Date", start, "End Date", end);

        Label feedback = new Label();
        Button create = new Button("Create Goal");
        create.getStyleClass().add("primary-button");
        create.setOnAction(event -> {
            try {
                if (name.getText().isBlank() || start.getValue() == null || end.getValue() == null)
                    throw new IllegalArgumentException("Complete the name and dates.");
                if (start.getValue().isAfter(end.getValue())) throw new IllegalArgumentException("Start date must be before end date.");
                Category selectedCategory = ALL_MEAL_TYPES.equals(category.getValue())
                        ? null
                        : Category.valueOf(category.getValue());
                NutritionGoal goal = new NutritionGoal(name.getText().trim(), number(calorie), number(protein),
                        number(carbs), number(fat), selectedCategory, start.getValue(), end.getValue());
                profile.addGoal(goal);
                for (com.dailyfuel.model.Meal meal : profile.getMeals()) goal.addMeal(meal);
                DataManager.saveGoals(profile.getGoals());
                refresh.run();
            } catch (IllegalArgumentException ex) {
                feedback.setText(ex.getMessage());
                feedback.getStyleClass().setAll("error-label");
            }
        });

        VBox list = new VBox(10);
        if (profile.getGoals().isEmpty()) list.getChildren().add(new Label("No goals created yet."));
        for (NutritionGoal goal : profile.getGoals()) {
            Label details = new Label(goal.toString() + "\n" + goal.getStartDate() + " to " + goal.getEndDate());
            details.setWrapText(true);
            Button delete = new Button("Delete");
            delete.getStyleClass().add("danger-button");
            delete.setOnAction(event -> {
                profile.removeGoal(goal);
                DataManager.saveGoals(profile.getGoals());
                refresh.run();
            });
            HBox row = new HBox(16, details, delete);
            row.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(details, Priority.ALWAYS);
            list.getChildren().add(row);
        }

        root.getChildren().addAll(UiComponents.card("New Goal", form, new HBox(12, create, feedback)),
                UiComponents.card("Current Goals", list));
        return root;
    }

    private static TextField field(String prompt) { TextField f = new TextField(); f.setPromptText(prompt); return f; }
    private static double number(TextField field) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (value < 0) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException ex) { throw new IllegalArgumentException("All targets must be valid nonnegative numbers."); }
    }
    private static void add(GridPane pane, int row, String a, Control one, String b, Control two) {
        pane.add(new Label(a), 0, row); pane.add(one, 1, row);
        pane.add(new Label(b), 2, row); pane.add(two, 3, row);
        GridPane.setHgrow(one, Priority.ALWAYS); GridPane.setHgrow(two, Priority.ALWAYS);
        one.setMaxWidth(Double.MAX_VALUE); two.setMaxWidth(Double.MAX_VALUE);
    }
}
