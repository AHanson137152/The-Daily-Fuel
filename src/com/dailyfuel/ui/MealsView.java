package com.dailyfuel.ui;

import com.dailyfuel.model.Category;
import com.dailyfuel.model.Meal;
import com.dailyfuel.model.NutritionProfile;
import com.dailyfuel.service.NutritionService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.function.Consumer;

public final class MealsView {
    private MealsView() { }

    public static VBox create(NutritionProfile profile, NutritionService service,
                              Consumer<String> navigate, Runnable refresh) {
        VBox root = UiComponents.page("Meal Log", "Add meals and review your nutrition history.", "meals", navigate);

        TextField name = field("Meal name");
        ComboBox<Category> category = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        category.setPromptText("Meal type");
        TextField calories = field("Calories");
        TextField protein = field("Protein (g)");
        TextField carbs = field("Carbs (g)");
        TextField fat = field("Fat (g)");
        DatePicker date = new DatePicker(LocalDate.now());
        TextField notes = field("Optional notes");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        addRow(form, 0, "Meal Name", name, "Meal Type", category);
        addRow(form, 1, "Calories", calories, "Protein", protein);
        addRow(form, 2, "Carbs", carbs, "Fat", fat);
        addRow(form, 3, "Date", date, "Notes", notes);

        Label feedback = new Label();
        Button add = new Button("Add Meal");
        add.getStyleClass().add("primary-button");
        add.setOnAction(event -> {
            try {
                if (name.getText().isBlank() || category.getValue() == null || date.getValue() == null) {
                    throw new IllegalArgumentException("Enter a meal name, type, and date.");
                }
                Meal meal = new Meal(category.getValue(), name.getText().trim(),
                        positive(calories, "calories"), positive(protein, "protein"),
                        positive(carbs, "carbs"), positive(fat, "fat"),
                        date.getValue(), notes.getText().trim());
                service.addMeal(meal);
                refresh.run();
            } catch (IllegalArgumentException ex) {
                feedback.setText(ex.getMessage());
                feedback.getStyleClass().setAll("error-label");
            }
        });
        HBox actions = new HBox(12, add, feedback);
        actions.setAlignment(Pos.CENTER_LEFT);

        TableView<Meal> table = UiComponents.mealTable();
        table.setItems(FXCollections.observableArrayList(profile.getMeals()));
        table.setPlaceholder(new Label("No meals logged yet."));
        VBox.setVgrow(table, Priority.ALWAYS);

        Button delete = new Button("Delete Selected Meal");
        delete.getStyleClass().add("danger-button");
        delete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        delete.setOnAction(event -> {
            service.removeMeal(table.getSelectionModel().getSelectedItem());
            refresh.run();
        });

        root.getChildren().addAll(UiComponents.card("Log a Meal", form, actions),
                UiComponents.card("All Meals", table, delete));
        VBox.setVgrow(root.getChildren().get(root.getChildren().size() - 1), Priority.ALWAYS);
        return root;
    }

    private static TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    private static void addRow(GridPane pane, int row, String leftLabel, Control left,
                               String rightLabel, Control right) {
        pane.add(new Label(leftLabel), 0, row);
        pane.add(left, 1, row);
        pane.add(new Label(rightLabel), 2, row);
        pane.add(right, 3, row);
        left.setMaxWidth(Double.MAX_VALUE);
        right.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(left, Priority.ALWAYS);
        GridPane.setHgrow(right, Priority.ALWAYS);
        pane.setPadding(new Insets(4));
    }

    private static double positive(TextField field, String label) {
        double value;
        try {
            value = Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Enter a valid number for " + label + ".");
        }
        if (value < 0) throw new IllegalArgumentException(label + " cannot be negative.");
        return value;
    }
}
