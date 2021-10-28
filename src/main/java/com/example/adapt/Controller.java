package com.example.adapt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

import static java.nio.file.Files.list;

public class Controller {

    private final ObservableList<ActivityDateTime> scheduleData = FXCollections.observableArrayList();

    protected final int numCapability = 4;
    protected final int numRows = numCapability+1;

    protected String data_directory_string = "src/main/resources/com/example/adapt/";
    protected String schedule_filename_string = "Schedule_OCT_2021.csv";
    protected Path schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);

    protected String configuration_filename_string = "Configuration.csv";
    protected Path configuration_filepath = Paths.get(data_directory_string+configuration_filename_string);

    // calculate date month
    protected String month = YearMonth.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    protected Integer num_month_days = YearMonth.now().lengthOfMonth();

    @FXML
    private TableView table_schedule;

    @FXML
    private Button btn_upload_schedule;

    @FXML
    private Button btn_upload_config;

    @FXML
    private DatePicker picker_month_year;

    @FXML Button btn_picker;

    public Controller() {

    }

    private File createNewCSV(String month_short, String year) {

        schedule_filename_string = "Schedule_"+month_short+"_"+year+".csv";
        schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);
        File newCSV = new File(schedule_filepath.toString());
        try {
            newCSV.createNewFile();
            System.out.println("New file "+newCSV+" created.");
        } catch (Exception e) {
            System.out.println("Error creating or writing to new file.");
        }


        return newCSV;
    }

    @FXML
    protected void pickMonthYear() {
        if (this.picker_month_year != null) {

            LocalDate selected = this.picker_month_year.getValue();
            String month = selected.getMonth().toString();
            String month_short = month.substring(0,3);
            Integer month_int = 0;
            if (month_short == "JAN") {
                month_int = 1;
            } else if (month_short == "FEB") {
                month_int = 2;
            } else if (month_short == "MAR") {
                month_int = 3;
            } else if (month_short == "APR") {
                month_int = 4;
            } else if (month_short == "MAY") {
                month_int = 5;
            } else if (month_short == "JUN") {
                month_int = 6;
            } else if (month_short == "JUL") {
                month_int = 7;
            } else if (month_short == "AUG") {
                month_int = 8;
            } else if (month_short == "SEP") {
                month_int = 9;
            } else if (month_short == "OCT") {
                month_int = 10;
            } else if (month_short == "NOV") {
                month_int = 11;
            } else {
                month_int = 12;
            }
            String year = Integer.toString(selected.getYear());

            File f = new File(data_directory_string);
            String[] pathnames = f.list();
            for (String pathname : pathnames) {
                if (pathname.contains(month) && pathname.contains(year)) {
                    schedule_filename_string = pathname;
                    break;
                }
            }
            if (!(schedule_filename_string.contains(month_short) && schedule_filename_string.contains(year))) {
                File newCSV = createNewCSV(month_short, year);
                try {
                    BufferedWriter writer = Files.newBufferedWriter(newCSV.toPath());
                    int num_days_month = YearMonth.of(Integer.parseInt(year), month_int).lengthOfMonth();

                    String ColumnA = "";
                    String otherColumns = "";
                    for (int i=0; i<num_days_month; i++) {
                        ColumnA += (i+1)+"-"+month_short+",";
                        otherColumns += ",";
                    }
                    ColumnA = ColumnA.substring(0,ColumnA.length()-1);
                    otherColumns = otherColumns.substring(0, otherColumns.length()-1);
                    System.out.println(ColumnA);

                    writer.write(ColumnA);
                    writer.newLine();
                    for (int i=0; i<numCapability; i++) {
                        writer.write(otherColumns);
                        writer.newLine();
                    }
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error writing to new file "+newCSV.toPath());
                }
            }
            schedule_filename_string = "Schedule_"+month_short+"_"+year+".csv";
            schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);
            loadSchedule();
        }
    }

    private ArrayList<String[]> readFileData(File file) {
        ArrayList<String[]> compiled_data = new ArrayList<String[]>();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String[] newline = sc.nextLine().split(",");
                compiled_data.add(newline);
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error reading or loading file..");
        }
        return compiled_data;
    }



    @FXML
    protected void btnUploadSchedule() {
        try {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            chooser.getExtensionFilters().add(extFilter);
            File file = chooser.showOpenDialog(new Stage());
            System.out.println(file.getName());

            Files.copy(file.toPath(), schedule_filepath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Please select a file..");
            return;
        }
        loadSchedule();
    }

    @FXML
    protected void loadSchedule() {

        this.table_schedule.getColumns().clear();
        this.table_schedule.getItems().clear();

        // check array length
        schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);
        ArrayList<String[]> schedule_file_data = readFileData(new File(schedule_filepath.toString()));
        for (int i=0; i<schedule_file_data.size(); i++) {
            String[] current = schedule_file_data.get(i);

            if (current.length < num_month_days) {
                String[] new_list = new String[num_month_days];
                // remove all null values
                for (int j=0; j<current.length; j++) {
                    if (current[j] == null || current[j] == "") {
                        new_list[j] = "NIL";
                    } else {
                        new_list[j] = current[j];
                    }
                }
                for (int j=0; j< new_list.length; j++) {
                    if (new_list[j] == null || new_list[j] == "") {
                        new_list[j] = "NIL";
                    }
                }
                schedule_file_data.set(i, new_list);
            }
        }

        if (schedule_file_data.size() == numRows) {
            // load schedule data
            for (int i=0; i<num_month_days; i++) {
                String date = schedule_file_data.get(0)[i];
                String capability_time_A = schedule_file_data.get(1)[i];
                String capability_time_B = schedule_file_data.get(2)[i];
                String capability_time_C = schedule_file_data.get(3)[i];
                String capability_time_D = schedule_file_data.get(4)[i];

                ActivityDateTime newDay = new ActivityDateTime(date, capability_time_A, capability_time_B, capability_time_C, capability_time_D);
                scheduleData.add(newDay);
            }

            // create columns
            TableColumn DateColumn = new TableColumn("Date");
            DateColumn.setCellValueFactory(new PropertyValueFactory<ActivityDateTime, String>("Date"));
            DateColumn.setText("Date");

            TableColumn CapabilityA = new TableColumn("CapabilityA");
            CapabilityA.setCellValueFactory(new PropertyValueFactory<ActivityDateTime, String>("CapabilityA"));
            CapabilityA.setText("Capability A");

            TableColumn CapabilityB = new TableColumn("CapabilityB");
            CapabilityB.setCellValueFactory(new PropertyValueFactory<ActivityDateTime, String>("CapabilityB"));
            CapabilityB.setText("Capability B");

            TableColumn CapabilityC = new TableColumn("CapabilityC");
            CapabilityC.setCellValueFactory(new PropertyValueFactory<ActivityDateTime, String>("CapabilityC"));
            CapabilityC.setText("Capability C");

            TableColumn CapabilityD = new TableColumn("CapabilityD");
            CapabilityD.setCellValueFactory(new PropertyValueFactory<ActivityDateTime, String>("CapabilityD"));
            CapabilityD.setText("Capability D");

            // load data into TableView
            this.table_schedule.setEditable(false);
            this.table_schedule.getColumns().addAll(DateColumn, CapabilityA, CapabilityB, CapabilityC, CapabilityD);
            this.table_schedule.setItems(scheduleData);
        }
    }

    @FXML
    protected void btnUploadConfig() {
        try {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            chooser.getExtensionFilters().add(extFilter);
            File file = chooser.showOpenDialog(new Stage());
            System.out.println(file.getName());

            Files.copy(file.toPath(), configuration_filepath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Please select a file..");
            return;
        }
        loadConfiguration();
    }

    @FXML
    protected void loadConfiguration() {

    }
}