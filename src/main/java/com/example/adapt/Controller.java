package com.example.adapt;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.Calendar;

public class Controller {

    @FXML
    private TableView table_schedule;

    @FXML
    private Button btn_upload;

    public Controller() {

    }

    @FXML
    protected void btnUpload() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(new Stage());
        System.out.println(file.getName());
    }

    @FXML
    protected void loadSchedule() {
        Integer num_month_days = YearMonth.now().lengthOfMonth();
        //this.table_schedule.set
    }

    @FXML
    protected void btn_Insert_Single_Schedule() {

    }

    @FXML
    protected void btn_Insert_Multiple_Schedule() {

    }

    @FXML
    protected void btn_Amend_Schedule() {

    }

    @FXML
    protected void btn_Insert_Single_Tasking() {

    }

    @FXML
    protected void btn_Insert_Multiple_Tasking() {

    }

    @FXML
    protected void btn_Amend_Tasking() {

    }
}