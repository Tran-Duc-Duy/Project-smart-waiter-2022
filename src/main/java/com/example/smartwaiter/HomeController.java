package com.example.smartwaiter;

import com.example.smartwaiter.model.Dish;
import com.example.smartwaiter.model.Meal;
import com.example.smartwaiter.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.example.smartwaiter.IO.readFile;


public class HomeController implements Initializable {
    @FXML
    private BorderPane bp;
    //.... recommend by
    @FXML private TextField howManyField;
    @FXML private Label recommendLabel;

    //........................ table Result
    @FXML private TableView<Order> tableResult;
    @FXML private TableColumn<Order, String> resultColumn;
    @FXML private TableColumn<Order, String> sumColumn;
    //....................... grid show all
    @FXML private GridPane dishGrid;

    //................
    private ObservableList<Dish> dishList;
    private ObservableList<Dish> dishListSearch;
    private ObservableList<Order> dishListRecommend;
    private ObservableList<Order> dishListOrder;

    private  List<Dish> soupList;
    private  List<Dish> saladList;
    private  List<Dish> mainList;
    private double check;
    //................
    @FXML private TextField searchTextField;
    //......@FXML private ChoiceBox<String> typeChoice;
    @FXML private ChoiceBox<String> typeChoice;
    //.....
    @FXML private TableColumn<Order, Void> actionRecommentColumn;

    private String[] optionS ={"By price", "By calo", "By time"};
    //............. table order
    @FXML private TableView<Order> tableOrder;
    @FXML private TableColumn<Order, String> orderColumn;
    @FXML private TableColumn<Order, Void> actionOrderColumn;

    //private List<Dish> orderList;
    @FXML private Label timeTTLabel;
    @FXML private Label caloTTLabel;
    @FXML private Label priceTTLabel;
    @FXML private Label caloDefaultLabel;
    @FXML private Label caloNeedLabel;
    private ObservableList<Meal> mealList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dishList = IO.readFile();
        mealList = IO.readFileMeal();
        caloDefaultLabel.setText(String.valueOf(historyController.defaultCalo));
        caloNeedLabel.setText(String.valueOf(caloToday()));

        dishListRecommend = FXCollections.observableArrayList();
        dishListOrder=FXCollections.observableArrayList();
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("myOrder"));
        sumColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));
        tableResult.setItems(dishListRecommend);
        searchTextField.setText("");
        //..............
        orderColumn.setCellValueFactory(new PropertyValueFactory<>("myOrder"));
        tableOrder.setItems(dishListOrder);
        //..................
        int temp=makeUp(dishList,0);
        //recommendLabel.setText("admin");
        //....
        typeChoice.getItems().addAll(optionS);
        typeChoice.setValue("By price");
        //...set up button
        actionRecommentColumn.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button orderButton = new Button("order");
            private final HBox pane = new HBox(orderButton);

            {
                orderButton.setOnAction(event -> {
                    Order getPatient = getTableView().getItems().get(getIndex());
                    dishListOrder.add(getPatient);
                    Notification();
                });

            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(empty ? null : pane);
            }
        });
        actionOrderColumn.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button deleteButton = new Button("delete");
            private final HBox pane = new HBox(deleteButton);

            {
                deleteButton.setOnAction(event -> {
                    Order getPatient = getTableView().getItems().get(getIndex());
                    dishListOrder.remove(getPatient);
                    Notification();
                });

            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(empty ? null : pane);
            }
        });


    }
    public double caloToday(){
        Calendar cale = Calendar.getInstance();
        Date date = cale.getTime();
        int a =date.getDate();
        double rs=0.0;
        for(Meal me:mealList){
            if(me.getD().getDate()==a){
                rs+=me.getCalo();
            }
        }
        return historyController.defaultCalo-rs;
    }
    private void Notification(){
        double ttTime=0.0, ttPrice=0.0,ttCalo=0.0;
        for(Order s:dishListOrder) {
            for(Dish d : s.getListDish()){
                ttTime+=d.getTime();
                ttCalo+=d.getTotalCalo();
                ttPrice+=d.getTotalTien();
            }
        }
        timeTTLabel.setText(String.valueOf(ttTime));
        caloTTLabel.setText(String.valueOf(ttCalo));
        priceTTLabel.setText(String.valueOf(ttPrice));
    }
    private int makeUp(ObservableList<Dish> dishList,int columns){
        int row=1;
        try{
            for (int i=0;i<dishList.size();i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("dish-item.fxml"));
                VBox box = fxmlLoader.load();
                DishItemController dishItemController = fxmlLoader.getController();

                dishItemController.setDishItem(dishList.get(i));
                Button btn= new Button("Order");
                btn.setPrefWidth(85);
                btn.setPrefHeight(20);
                btn.setStyle("-fx-font:Bold 18 Cambria; -fx-background-radius: 30;-fx-background-color:#D68162;");
                List<Dish> re=new ArrayList();
                re.add(dishList.get(i));
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        dishListOrder.add(new Order(re,1));
                        Notification();
                    }
                });
                box.getChildren().add(btn);
                dishGrid.add(box, columns++, row);
                GridPane.setMargin(box, new Insets(30));
            }
        }catch (IOException e){
            e.printStackTrace ();
        }
        return columns;
    }
    public void searchBox(ActionEvent event){
        dishListSearch=FXCollections.observableArrayList();
        for (int i=0;i<dishList.size();i++) {
            if(dishList.get(i).getNameDish().contains(searchTextField.getText())){
                dishListSearch.add(dishList.get(i));
            }
        }
        int temp=makeUp(dishListSearch,0);
        int temp2=makeUp(dishList,temp);
    }

    public void changeSceneDishAdd(ActionEvent event) throws IOException {
        changeScene(event,"home-view-add.fxml");
    }
    public void changeSceneDishHistory(ActionEvent event) throws IOException {
        changeScene(event,"home-view-history.fxml");
    }
    public void randomSelection(ActionEvent event) {
        check = Double.parseDouble(howManyField.getText());
        //howManyField.setText("");
        soupList = new ArrayList<>();
        saladList = new ArrayList<>();
        mainList = new ArrayList<>();
        dishList = readFile();
        for (int i = 0; i < dishList.size(); i++) {
            if (dishList.get(i).getType().equals("soup")) {
                soupList.add(dishList.get(i));
            } else if (dishList.get(i).getType().equals("salad")) {
                saladList.add(dishList.get(i));
            } else {
                mainList.add(dishList.get(i));
            }
        }
        recommendLabel.setText(typeChoice.getValue()+": "+check);
        findTriplet(soupList,saladList,mainList,typeChoice.getValue(),check);
    }


    void findTriplet(List<Dish> soupList, List<Dish> saladList, List<Dish> mainList,
                     String byWhat,Double howMany)
    {
        List<Dish> oke;

        if(byWhat.equals("By price")) {
            dishListRecommend.removeAll(dishListRecommend);
            for (int i = 0; i < soupList.size(); i++) {
                for (int j = 0; j < saladList.size(); j++) {
                    for (int z = 0; z < mainList.size(); z++) {
                        if ((soupList.get(i).getTotalTien() + saladList.get(j).getTotalTien() + mainList.get(z).getTotalTien()) < howMany) {
                            oke=new ArrayList<>();
                            oke.add(soupList.get(i));
                            oke.add(saladList.get(j));
                            oke.add(mainList.get(z));
                            dishListRecommend.add(new Order(oke,1));

                        }
                    }
                }
            }
            typeChoice.setValue("By price");
        }else if (byWhat.equals("By calo")){
            dishListRecommend.removeAll(dishListRecommend);
            for (int i = 0; i < soupList.size(); i++) {
                for (int j = 0; j < saladList.size(); j++) {
                    for (int z = 0; z < mainList.size(); z++) {
                        if ((soupList.get(i).getTotalCalo() + saladList.get(j).getTotalCalo() + mainList.get(z).getTotalCalo()) < howMany) {
                            oke=new ArrayList<>();
                            oke.add(soupList.get(i));
                            oke.add(saladList.get(j));
                            oke.add(mainList.get(z));
                            dishListRecommend.add(new Order(oke,2));
                        }
                    }
                }
            }
            typeChoice.setValue("By calo");
        }else{
            dishListRecommend.removeAll(dishListRecommend);
            for (int i = 0; i < soupList.size(); i++) {
                for (int j = 0; j < saladList.size(); j++) {
                    for (int z = 0; z < mainList.size(); z++) {
                        if ((soupList.get(i).getTime() + saladList.get(j).getTime() + mainList.get(z).getTime()) < howMany) {
                            oke=new ArrayList<>();
                            oke.add(soupList.get(i));
                            oke.add(saladList.get(j));
                            oke.add(mainList.get(z));
                            dishListRecommend.add(new Order(oke,3));
                        }
                    }
                }
            }
            typeChoice.setValue("By time");
        }
    }
    public void close(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void confirmB(ActionEvent event) throws IOException {
        if(dishListOrder.size()==0){
            return;
        }
        ObservableList<Meal> mealList=IO.readFileMeal();
        Calendar cale = Calendar.getInstance();
        Date date = cale.getTime();
        Meal m = new Meal(dishListOrder,date);
        mealList.add(m);
        IO.writeFileMeal(mealList);
        dishListOrder.clear();
        Notification();
        changeScene(event,"home-view.fxml");
    }
    public void changeScene(ActionEvent event, String nameF) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(nameF));
        Parent add = loader.load();
        Scene scene = new Scene(add);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
    }
}
