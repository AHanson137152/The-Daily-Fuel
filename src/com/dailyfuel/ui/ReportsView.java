package com.dailyfuel.ui;

import com.dailyfuel.model.Category;
import com.dailyfuel.model.Meal;
import com.dailyfuel.model.NutritionProfile;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public final class ReportsView {
    private ReportsView() { }

    public static VBox create(NutritionProfile profile, Consumer<String> navigate) {
        VBox root = UiComponents.page("Nutrition Reports", "Choose a period to summarize calories and macronutrients.", "reports", navigate);
        DatePicker start = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker end = new DatePicker(LocalDate.now());
        Button generate = new Button("Generate Report"); generate.getStyleClass().add("primary-button");
        HBox controls = new HBox(10, new Label("From"), start, new Label("To"), end, generate);
        controls.setAlignment(Pos.CENTER_LEFT);
        VBox output = new VBox(14);

        generate.setOnAction(event -> {
            output.getChildren().clear();
            if (start.getValue() == null || end.getValue() == null || start.getValue().isAfter(end.getValue())) {
                Label error = new Label("Select a valid date range."); error.getStyleClass().add("error-label");
                output.getChildren().add(error); return;
            }
            List<Meal> meals = profile.getMeals().stream()
                    .filter(m -> !m.getDate().isBefore(start.getValue()) && !m.getDate().isAfter(end.getValue())).toList();
            double calories = meals.stream().mapToDouble(Meal::getCalories).sum();
            double protein = meals.stream().mapToDouble(Meal::getProtein).sum();
            double carbs = meals.stream().mapToDouble(Meal::getCarbs).sum();
            double fat = meals.stream().mapToDouble(Meal::getFat).sum();
            Label summary = new Label(String.format("%d meals  |  %.1f calories  |  %.1f g protein  |  %.1f g carbs  |  %.1f g fat",
                    meals.size(), calories, protein, carbs, fat));
            summary.getStyleClass().add("report-summary");

            BarChart<String, Number> macroChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
            macroChart.setTitle("Macronutrient Totals (grams)");
            macroChart.setLegendVisible(false);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>("Protein", protein));
            series.getData().add(new XYChart.Data<>("Carbs", carbs));
            series.getData().add(new XYChart.Data<>("Fat", fat));
            macroChart.getData().add(series);
            macroChart.setPrefHeight(360);

            PieChart categoryChart = new PieChart();
            categoryChart.setTitle("Calories by Meal Type");
            for (Category category : Category.values()) {
                double value = meals.stream().filter(m -> m.getMealType() == category).mapToDouble(Meal::getCalories).sum();
                if (value > 0) categoryChart.getData().add(new PieChart.Data(category.toString(), value));
            }
            if (categoryChart.getData().isEmpty()) categoryChart.getData().add(new PieChart.Data("No data", 1));
            categoryChart.setPrefHeight(360);
            HBox charts = new HBox(16, macroChart, categoryChart);
            HBox.setHgrow(macroChart, Priority.ALWAYS); HBox.setHgrow(categoryChart, Priority.ALWAYS);
            macroChart.setMaxWidth(Double.MAX_VALUE); categoryChart.setMaxWidth(Double.MAX_VALUE);
            output.getChildren().addAll(summary, charts);
        });
        root.getChildren().addAll(UiComponents.card("Report Period", controls), output);
        generate.fire();
        return root;
    }
}
