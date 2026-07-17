package com.dailyfuel.ui;

import com.dailyfuel.model.Meal;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public final class UiComponents {
    private UiComponents() { }

    public static VBox page(String title, String subtitle, String currentPage,
                            Consumer<String> navigate) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("page-title");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("page-subtitle");
        subtitleLabel.setWrapText(true);

        VBox root = new VBox(18, navigation(currentPage, navigate), titleLabel, subtitleLabel);
        root.getStyleClass().add("page");
        root.setPadding(new Insets(26));
        VBox.setVgrow(root, Priority.ALWAYS);
        return root;
    }

    public static HBox navigation(String currentPage, Consumer<String> navigate) {
        HBox bar = new HBox(8);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("nav-bar");
        addNavButton(bar, "Dashboard", "dashboard", currentPage, navigate);
        addNavButton(bar, "Meals", "meals", currentPage, navigate);
        addNavButton(bar, "Goals", "goals", currentPage, navigate);
        addNavButton(bar, "Recurring", "recurring", currentPage, navigate);
        addNavButton(bar, "Search", "search", currentPage, navigate);
        addNavButton(bar, "Reports", "reports", currentPage, navigate);
        return bar;
    }

    private static void addNavButton(HBox bar, String text, String page,
                                     String currentPage, Consumer<String> navigate) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        if (page.equals(currentPage)) button.getStyleClass().add("nav-button-active");
        button.setDisable(page.equals(currentPage));
        button.setOnAction(event -> navigate.accept(page));
        bar.getChildren().add(button);
    }

    public static VBox card(String title, javafx.scene.Node... children) {
        Label label = new Label(title);
        label.getStyleClass().add("card-title");
        VBox card = new VBox(10, label);
        card.getChildren().addAll(children);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));
        return card;
    }

    public static TableView<Meal> mealTable() {
        TableView<Meal> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Meal, String> name = column("Meal", meal -> meal.getMealName());
        TableColumn<Meal, Object> type = column("Type", Meal::getMealType);
        TableColumn<Meal, Object> calories = column("Calories", meal -> format(meal.getCalories()));
        TableColumn<Meal, Object> protein = column("Protein", meal -> format(meal.getProtein()) + " g");
        TableColumn<Meal, Object> carbs = column("Carbs", meal -> format(meal.getCarbs()) + " g");
        TableColumn<Meal, Object> fat = column("Fat", meal -> format(meal.getFat()) + " g");
        TableColumn<Meal, Object> date = column("Date", Meal::getDate);
        TableColumn<Meal, String> notes = column("Notes", Meal::getNotes);
        table.getColumns().addAll(name, type, calories, protein, carbs, fat, date, notes);
        return table;
    }

    private static <T> TableColumn<Meal, T> column(String title,
                                                    java.util.function.Function<Meal, T> getter) {
        TableColumn<Meal, T> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(getter.apply(cell.getValue())));
        return column;
    }

    public static String format(double value) {
        return String.format("%.1f", value);
    }
}
