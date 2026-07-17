package com.dailyfuel.main;

import com.dailyfuel.data.DataManager;
import com.dailyfuel.logic.RecurringMealSync;
import com.dailyfuel.model.NutritionProfile;
import com.dailyfuel.model.RecurringMeal;
import com.dailyfuel.service.NutritionService;
import com.dailyfuel.ui.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private final NutritionProfile profile = new NutritionProfile(DataManager.loadMeals(), DataManager.loadGoals());
    private final NutritionService service = new NutritionService(profile);
    private final List<RecurringMeal> recurringMeals = new ArrayList<>(DataManager.loadRecurringMeals());
    private Stage stage;
    private String currentPage = "dashboard";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        RecurringMealSync.syncRecurringMeals(profile, recurringMeals);
        DataManager.saveMeals(profile.getMeals());
        stage.setTitle("Daily Fuel");
        stage.setMinWidth(980);
        stage.setMinHeight(700);
        show("dashboard");
        stage.show();
    }

    private void show(String page) {
        currentPage = page;
        Parent root = switch (page) {
            case "meals" -> MealsView.create(profile, service, this::show, this::refresh);
            case "goals" -> GoalsView.create(profile, this::show, this::refresh);
            case "recurring" -> RecurringMealsView.create(profile, recurringMeals, this::show, this::refresh);
            case "search" -> SearchView.create(profile, this::show);
            case "reports" -> ReportsView.create(profile, this::show);
            default -> DashboardView.create(profile, this::show);
        };

        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root, 1280, 820);
            String css = Main.class.getResource("/com/dailyfuel/ui/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        stage.setTitle("Daily Fuel - " + Character.toUpperCase(page.charAt(0)) + page.substring(1));
    }

    private void refresh() {
        show(currentPage);
    }

    @Override
    public void stop() {
        DataManager.saveMeals(profile.getMeals());
        DataManager.saveGoals(profile.getGoals());
        DataManager.saveRecurringMeals(recurringMeals);
    }
}
