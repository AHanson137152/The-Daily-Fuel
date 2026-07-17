package com.dailyfuel.ui;

import com.dailyfuel.model.Category;
import com.dailyfuel.model.Meal;
import com.dailyfuel.model.NutritionGoal;
import com.dailyfuel.model.NutritionProfile;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public final class DashboardView {
    private DashboardView() { }

    public static VBox create(NutritionProfile profile, Consumer<String> navigate) {
        VBox root = UiComponents.page("Daily Fuel", "Your nutrition at a glance", "dashboard", navigate);
        LocalDate today = LocalDate.now();
        List<Meal> todayMeals = profile.getMeals().stream()
                .filter(meal -> today.equals(meal.getDate())).toList();

        double calories = todayMeals.stream().mapToDouble(Meal::getCalories).sum();
        double protein = todayMeals.stream().mapToDouble(Meal::getProtein).sum();
        double carbs = todayMeals.stream().mapToDouble(Meal::getCarbs).sum();
        double fat = todayMeals.stream().mapToDouble(Meal::getFat).sum();

        HBox summary = new HBox(14,
                metric("Calories", UiComponents.format(calories), "kcal"),
                metric("Protein", UiComponents.format(protein), "grams"),
                metric("Carbs", UiComponents.format(carbs), "grams"),
                metric("Fat", UiComponents.format(fat), "grams"));
        summary.setAlignment(Pos.CENTER);
        for (javafx.scene.Node node : summary.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        PieChart chart = new PieChart();
        chart.setTitle("Today's Calories by Meal Type");
        chart.setLegendVisible(true);
        for (Category category : Category.values()) {
            double total = todayMeals.stream().filter(m -> m.getMealType() == category)
                    .mapToDouble(Meal::getCalories).sum();
            if (total > 0) chart.getData().add(new PieChart.Data(category.toString(), total));
        }
        if (chart.getData().isEmpty()) chart.getData().add(new PieChart.Data("No meals yet", 1));
        chart.setPrefHeight(330);

        VBox goalCard = buildGoalCard(profile);
        ListView<String> recent = new ListView<>();
        recent.setItems(FXCollections.observableArrayList(profile.getMeals().stream()
                .sorted(Comparator.comparing(Meal::getDate).reversed()).limit(8)
                .map(Meal::toString).toList()));
        recent.setPlaceholder(new Label("No meals have been logged yet."));
        recent.setPrefHeight(250);

        HBox lower = new HBox(16, UiComponents.card("Nutrition Breakdown", chart),
                UiComponents.card("Recent Meals", recent));
        HBox.setHgrow(lower.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(lower.getChildren().get(1), Priority.ALWAYS);
        ((VBox) lower.getChildren().get(0)).setMaxWidth(Double.MAX_VALUE);
        ((VBox) lower.getChildren().get(1)).setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(summary, goalCard, lower);
        return root;
    }

    private static VBox metric(String label, String value, String unit) {
        Label name = new Label(label);
        name.getStyleClass().add("metric-label");
        Label number = new Label(value);
        number.getStyleClass().add("metric-value");
        Label suffix = new Label(unit);
        suffix.getStyleClass().add("muted-label");
        VBox box = UiComponents.card("", name, number, suffix);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private static VBox buildGoalCard(NutritionProfile profile) {
        NutritionGoal goal = profile.getGoals().stream().findFirst().orElse(null);
        if (goal == null) return UiComponents.card("Goal Progress", new Label("Add a nutrition goal to see progress here."));
        ProgressBar calories = progress(goal.getTotalCalories(), goal.getCalorieGoal());
        ProgressBar protein = progress(goal.getTotalProtein(), goal.getProteinGoal());
        ProgressBar carbs = progress(goal.getTotalCarbs(), goal.getCarbGoal());
        ProgressBar fat = progress(goal.getTotalFat(), goal.getFatGoal());
        return UiComponents.card("Goal Progress: " + goal.getName(),
                new Label("Calories: " + UiComponents.format(goal.getTotalCalories()) + " / " + UiComponents.format(goal.getCalorieGoal())), calories,
                new Label("Protein: " + UiComponents.format(goal.getTotalProtein()) + " / " + UiComponents.format(goal.getProteinGoal()) + " g"), protein,
                new Label("Carbs: " + UiComponents.format(goal.getTotalCarbs()) + " / " + UiComponents.format(goal.getCarbGoal()) + " g"), carbs,
                new Label("Fat: " + UiComponents.format(goal.getTotalFat()) + " / " + UiComponents.format(goal.getFatGoal()) + " g"), fat);
    }

    private static ProgressBar progress(double current, double target) {
        ProgressBar bar = new ProgressBar(target <= 0 ? 0 : Math.min(current / target, 1));
        bar.setMaxWidth(Double.MAX_VALUE);
        return bar;
    }
}
