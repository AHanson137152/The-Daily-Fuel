package com.dailyfuel.ui;

import com.dailyfuel.model.Category;
import com.dailyfuel.model.Meal;
import com.dailyfuel.model.NutritionProfile;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class SearchView {
    private SearchView() { }

    public static VBox create(NutritionProfile profile, Consumer<String> navigate) {
        VBox root = UiComponents.page("Search Meals", "Filter by name or notes, category, date, and calorie range.", "search", navigate);
        TextField query = new TextField();
        query.setPromptText("Meal name or notes");
        ComboBox<Category> category = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        category.setPromptText("Any category");
        DatePicker start = new DatePicker();
        start.setPromptText("Start date");
        DatePicker end = new DatePicker();
        end.setPromptText("End date");
        TextField minimum = new TextField(); minimum.setPromptText("Min calories");
        TextField maximum = new TextField(); maximum.setPromptText("Max calories");
        Button search = new Button("Apply Filters"); search.getStyleClass().add("primary-button");
        Button clear = new Button("Clear");

        HBox filters = new HBox(10, query, category, start, end, minimum, maximum, search, clear);
        filters.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(query, Priority.ALWAYS);

        Label resultCount = new Label();
        TableView<Meal> table = UiComponents.mealTable();
        table.setItems(FXCollections.observableArrayList(profile.getMeals()));
        table.setPlaceholder(new Label("No matching meals."));
        resultCount.setText(profile.getMeals().size() + " meal(s)");
        VBox.setVgrow(table, Priority.ALWAYS);

        Runnable apply = () -> {
            String text = query.getText().trim().toLowerCase(Locale.ROOT);
            double min = parseOr(minimum.getText(), 0);
            double max = parseOr(maximum.getText(), Double.MAX_VALUE);
            LocalDate from = start.getValue();
            LocalDate through = end.getValue();
            List<Meal> matches = profile.getMeals().stream()
                    .filter(meal -> text.isEmpty() || meal.getMealName().toLowerCase(Locale.ROOT).contains(text)
                            || meal.getNotes().toLowerCase(Locale.ROOT).contains(text))
                    .filter(meal -> category.getValue() == null || meal.getMealType() == category.getValue())
                    .filter(meal -> from == null || !meal.getDate().isBefore(from))
                    .filter(meal -> through == null || !meal.getDate().isAfter(through))
                    .filter(meal -> meal.getCalories() >= min && meal.getCalories() <= max)
                    .toList();
            table.setItems(FXCollections.observableArrayList(matches));
            resultCount.setText(matches.size() + " meal(s)");
        };
        search.setOnAction(event -> apply.run());
        query.setOnAction(event -> apply.run());
        clear.setOnAction(event -> {
            query.clear(); category.setValue(null); start.setValue(null); end.setValue(null);
            minimum.clear(); maximum.clear(); apply.run();
        });

        root.getChildren().addAll(UiComponents.card("Filters", filters), resultCount, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private static double parseOr(String text, double fallback) {
        try { return text == null || text.isBlank() ? fallback : Double.parseDouble(text.trim()); }
        catch (NumberFormatException ex) { return fallback; }
    }
}
