package com.example.adapt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

public class Controller {

    private final ObservableList<ActivityDateTime> scheduleData = FXCollections.observableArrayList();
    private final ObservableList<Location> locationData = FXCollections.observableArrayList();
    private final ObservableList<Location> locationPossibilityData = FXCollections.observableArrayList();

    protected final int numCapability = 4;
    protected final int numRows = numCapability+1;
    protected final String[] CategoryList = {"Category A", "Category B", "Category C", "Category D"};

    protected String data_directory_string = "src/main/resources/com/example/adapt/";
    protected String schedule_filename_string = "Schedule_OCT_2021.csv";
    protected Path schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);

    protected String locations_filename_string = "List_Target.csv";
    protected Path locations_filepath = Paths.get(data_directory_string+ locations_filename_string);

    // calculate date month
    protected String month = YearMonth.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    protected Integer num_month_days = YearMonth.now().lengthOfMonth();

    // table views in titled panes
    @FXML private TreeTableView table_schedule;
    @FXML private TableView table_locations;
    @FXML private TableView table_taskings;

    // upload buttons
    @FXML private Button btn_upload_schedule;
    @FXML private Button btn_upload_locations;

    // insert new schedule
    @FXML private DatePicker sch_insert_new_date;
    @FXML private TextField sch_insert_new_category;
    @FXML private TextField sch_insert_new_timeStart;
    @FXML private TextField sch_insert_new_timeEnd;
    @FXML private Button btn_schedule_insert;

    // delete schedule
    @FXML private DatePicker sch_delete_date;
    @FXML private TextField sch_delete_category;
    @FXML private TextField sch_delete_start_time;
    @FXML private Button btn_schedule_delete;

    // insert new location
    @FXML private TextField loc_insert_name;
    @FXML private TextField loc_insert_priority;
    @FXML private TextField loc_insert_catA;
    @FXML private TextField loc_insert_catB;
    @FXML private TextField loc_insert_catC;
    @FXML private TextField loc_insert_catD;
    @FXML private Button btn_location_insert;

    //delete location
    @FXML private TextField loc_delete_name;
    @FXML private Button btn_location_delete;

    // pick month
    @FXML private DatePicker picker_month_year;
    @FXML private Button btn_picker;

    public Controller() {

    }

    private int getCategoryId(String category_name) {
        // -1 means that the category cannot be found
        int category_id = -1;
        for (int i=0; i<CategoryList.length; i++) {
            if (CategoryList[i].toLowerCase().equals(category_name.toLowerCase())) {
                category_id = i;
                break;
            }
        }
        return category_id;
    }

    private String month_numToString(int month_int) {
        String  month_short = "";
        if (month_int == 1) {
            month_short = "JAN";
        } else if (month_int == 2) {
            month_short = "FEB";
        } else if (month_int == 3) {
            month_short = "MAR";
        } else if (month_int == 4) {
            month_short = "APR";
        } else if (month_int == 5) {
            month_short = "MAY";
        } else if (month_int == 6) {
            month_short = "JUN";
        } else if (month_int == 7) {
            month_short = "JUL";
        } else if (month_int == 8) {
            month_short = "AUG";
        } else if (month_int == 9) {
            month_short = "SEP";
        } else if (month_int == 10) {
            month_short = "OCT";
        } else if (month_int == 11) {
            month_short = "NOV";
        } else if (month_int == 12) {
            month_short = "DEC";
        }
        return month_short;
    }

    private int month_stringToNum(String month_short) {
        month_short = month_short.toUpperCase();

        int month_int = 0;
        if (month_short.equals("JAN")) {
            month_int = 1;
        } else if (month_short.equals("FEB")) {
            month_int = 2;
        } else if (month_short.equals("MAR")) {
            month_int = 3;
        } else if (month_short.equals("APR")) {
            month_int = 4;
        } else if (month_short.equals("MAY")) {
            month_int = 5;
        } else if (month_short.equals("JUN")) {
            month_int = 6;
        } else if (month_short.equals("JUL")) {
            month_int = 7;
        } else if (month_short.equals("AUG")) {
            month_int = 8;
        } else if (month_short.equals("SEP")) {
            month_int = 9;
        } else if (month_short.equals("OCT")) {
            month_int = 10;
        } else if (month_short.equals("NOV")) {
            month_int = 11;
        } else if (month_short.equals("DEC")){
            month_int = 12;
        }

        return month_int;
    }

    private File findCSV(String month_short, String year) {
        schedule_filename_string = "Schedule_"+month_short+"_"+year+".csv";
        schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);

        File f = new File(schedule_filepath.toString());
        if (f.exists()) {
            return f;
        } else {
            return null;
        }
    }

    private File createNewScheduleCSV(String month_short, String year) {

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

    private boolean writeNewScheduleCSV(File newCSV, String month_short, String year) {
        int month_int = month_stringToNum(month_short);
        try {
            BufferedWriter writer = Files.newBufferedWriter(newCSV.toPath());
            int num_days_month = YearMonth.of(Integer.parseInt(year), month_int).lengthOfMonth();

            String ColumnA = "";
            String otherColumns = "";
            for (int i=0; i<num_days_month; i++) {
                ColumnA += (i+1)+"-"+month_short+",";
                otherColumns += "NIL,";
            }
            ColumnA = ColumnA.substring(0,ColumnA.length()-1);
            otherColumns = otherColumns.substring(0, otherColumns.length()-1);

            writer.write(ColumnA);
            writer.newLine();
            for (int i=0; i<numCapability; i++) {
                writer.write(otherColumns);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing to new file "+newCSV.toPath());
            System.out.println(e);
            return false;
        }
        return true;
    }

    @FXML
    protected void pickMonthYear() {
        LocalDate selected = this.picker_month_year.getValue();
        if (selected != null) {
            // create variables
            String month = selected.getMonth().toString();
            String month_short = month.substring(0,3);
            String year = Integer.toString(selected.getYear());
            int month_int = month_stringToNum(month_short);
            // create file if cannot find file
            if (findCSV(month_short, year) == null) {
                File newCSV = createNewScheduleCSV(month_short, year);
                writeNewScheduleCSV(newCSV, month_short, year);
            }
            schedule_filename_string = "Schedule_"+month_short+"_"+year+".csv";
            schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);
            // load fields
            loadSchedule();
            loadTaskings();
        }
    }

    private ArrayList<String[]> readCSVData(File file) {
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

    private ArrayList<String[]> writeScheduleData(File file, String category, String date_day, String timeStart, String timeEnd) {

        try {
            int date_day_int = Integer.parseInt(date_day);
            int category_id = getCategoryId(category);

            // -1 means that the category cannot be found
            if (category_id == -1) {
                return null;
            }
            // include timing into array
            ArrayList<String[]> existingScheduleData = readCSVData(file);
            String[] arrayData = existingScheduleData.get(category_id+1);
            if (arrayData[date_day_int-1].equals("NIL")) {
                arrayData[date_day_int-1] = timeStart+"H-"+timeEnd+'H';
            } else {
                arrayData[date_day_int-1] = arrayData[date_day_int-1]+" "+timeStart+"H-"+timeEnd+'H';
            }
            existingScheduleData.set(category_id+1, arrayData);
            return existingScheduleData;

        } catch(Exception e) {
            return null;
        }
    }

    private void debugScheduleData(ArrayList<String[]> existingScheduleData) {
        System.out.println("ArrayList.size: "+existingScheduleData.size());
        for (int i=0; i<existingScheduleData.size(); i++) {
            System.out.println(i);
            for (int j=0; j<existingScheduleData.get(i).length; j++) {
                System.out.println(existingScheduleData.get(i)[j]);
            }
        }
    }

    private boolean writeScheduleFile(ArrayList<String[]> existingScheduleData, String month, String year) {
        if(existingScheduleData == null) {
            System.out.println("no existing data, create file first");
            return false;
        }

        String month_short = month.substring(0, 3).toUpperCase();
        int month_int = month_stringToNum(month_short);
        // set current file name and file path
        schedule_filename_string = "Schedule_" + month_short + "_" + year + ".csv";
        schedule_filepath = Paths.get(data_directory_string + schedule_filename_string);
        // create new file if it does not exist
        if (findCSV(month_short, year) == null) {
            File newCSV = createNewScheduleCSV(month_short, year);
            writeNewScheduleCSV(newCSV, month_short, year);
        }

        try {
            // convert to ArrayList to String
            String newData = "";
            for (int i = 0; i < existingScheduleData.size(); i++) {
                int num_days_month = YearMonth.of(Integer.parseInt(year), month_int).lengthOfMonth();
                String[] currentArray = existingScheduleData.get(i);
                String currentString = "";
                for (int j = 0; j < num_days_month; j++) {
                    if (j == num_days_month - 1) {
                        currentString = currentString + currentArray[j];
                    } else {
                        currentString = currentString + currentArray[j] + ",";

                    }
                }
                newData = newData + currentString + "\n";
            }

            // overwrite new dataset into file
            File file = new File(schedule_filepath.toString());
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(newData);
            writer.close();

            return true;
        } catch (Exception e) {
            return false;
        }
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

        // check array length
        schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);
        ArrayList<String[]> schedule_file_data = readCSVData(new File(schedule_filepath.toString()));
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
            String date_month = schedule_filename_string.split("_")[1];
            String date_year = schedule_filename_string.split("_")[2].split("/.")[0];
            TreeItem root = new TreeItem(new ActivityDateTime(date_month+" "+date_year,
                    "...", "...", "...", "..."));
            for (int i=0; i<num_month_days; i++) {
                String date = schedule_file_data.get(0)[i];
                String[] capability_time_A = schedule_file_data.get(1)[i].split(" ");
                String[] capability_time_B = schedule_file_data.get(2)[i].split(" ");
                String[] capability_time_C = schedule_file_data.get(3)[i].split(" ");
                String[] capability_time_D = schedule_file_data.get(4)[i].split(" ");

                TreeItem date_day = null;
                if (capability_time_A.length > 1 || capability_time_B.length > 1 ||
                        capability_time_C.length > 1 || capability_time_D.length > 1) {
                    // create new root node
                    date_day = new TreeItem(new ActivityDateTime(date, capability_time_A[0],
                            capability_time_B[0], capability_time_C[0], capability_time_D[0]));
                    // find the largest string array
                    int largest_size = capability_time_A.length;
                    if (capability_time_B.length > largest_size) {
                        largest_size = capability_time_B.length;
                    }
                    if (capability_time_C.length > largest_size) {
                        largest_size = capability_time_C.length;
                    }
                    if (capability_time_D.length > largest_size) {
                        largest_size = capability_time_D.length;
                    }
                    // loop through all string array
                    for (int j=1; j<largest_size; j++) {
                        String newA = "NIL";
                        if (j < capability_time_A.length) {
                            newA = capability_time_A[j];
                        }
                        String newB = "NIL";
                        if (j < capability_time_B.length) {
                            newB = capability_time_B[j];
                        }
                        String newC = "NIL";
                        if (j < capability_time_C.length) {
                            newC = capability_time_C[j];
                        }
                        String newD = "NIL";
                        if (j < capability_time_D.length) {
                            newD = capability_time_D[j];
                        }
                        TreeItem newNode = new TreeItem(new ActivityDateTime("...", newA, newB, newC, newD));
                        date_day.getChildren().add(newNode);
                    }

                } else {
                    date_day = new TreeItem(new ActivityDateTime(date, capability_time_A[0],
                            capability_time_B[0], capability_time_C[0], capability_time_D[0]));
                }
                root.getChildren().add(date_day);
            }

            // create columns
            TreeTableColumn DateColumn = new TreeTableColumn("Date");
            DateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<ActivityDateTime, String>("Date"));
            DateColumn.setText("Date");

            TreeTableColumn CapabilityA = new TreeTableColumn("CapabilityA");
            CapabilityA.setCellValueFactory(new TreeItemPropertyValueFactory<ActivityDateTime, String>("CapabilityA"));
            CapabilityA.setText("Capability A");

            TreeTableColumn CapabilityB = new TreeTableColumn("CapabilityB");
            CapabilityB.setCellValueFactory(new TreeItemPropertyValueFactory<ActivityDateTime, String>("CapabilityB"));
            CapabilityB.setText("Capability B");

            TreeTableColumn CapabilityC = new TreeTableColumn("CapabilityC");
            CapabilityC.setCellValueFactory(new TreeItemPropertyValueFactory<ActivityDateTime, String>("CapabilityC"));
            CapabilityC.setText("Capability C");

            TreeTableColumn CapabilityD = new TreeTableColumn("CapabilityD");
            CapabilityD.setCellValueFactory(new TreeItemPropertyValueFactory<ActivityDateTime, String>("CapabilityD"));
            CapabilityD.setText("Capability D");

            // load data into TableView
            this.table_schedule.setEditable(false);
            this.table_schedule.getColumns().addAll(DateColumn, CapabilityA, CapabilityB, CapabilityC, CapabilityD);
            this.table_schedule.setRoot(root);
            this.table_schedule.setShowRoot(false);
            root.setExpanded(true);
        }
    }

    @FXML
    protected void btnUploadLocations() {
        try {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            chooser.getExtensionFilters().add(extFilter);
            File file = chooser.showOpenDialog(new Stage());
            System.out.println(file.getName());

            Files.copy(file.toPath(), locations_filepath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Please select a file..");
            return;
        }
        loadLocations();
    }

    @FXML
    protected void loadLocations() {

        this.table_locations.getColumns().clear();
        this.table_locations.getItems().clear();

        locations_filepath = Paths.get(data_directory_string+ locations_filename_string);
        ArrayList<String[]> locations_file_data = readCSVData(new File(locations_filepath.toString()));

        // load location data
        for (int i = 0; i < locations_file_data.size(); i++) {
            String location = locations_file_data.get(i)[0];
            String loc_revisit = locations_file_data.get(i)[1];
            String capability_ability_A = locations_file_data.get(i)[2];
            String capability_ability_B = locations_file_data.get(i)[3];
            String capability_ability_C = locations_file_data.get(i)[4];
            String capability_ability_D = locations_file_data.get(i)[5];

            Location newLocation = new Location(location, loc_revisit, capability_ability_A, capability_ability_B, capability_ability_C, capability_ability_D);
            locationData.add(newLocation);
        }

        // create columns
        TableColumn LocationColumn = new TableColumn("Location");
        LocationColumn.setCellValueFactory(new PropertyValueFactory<Location, String>("Location"));
        LocationColumn.setText("Location");

        TableColumn LocRevisitColumn = new TableColumn("Revisit");
        LocRevisitColumn.setCellValueFactory(new PropertyValueFactory<Location, Integer>("Revisit"));
        LocRevisitColumn.setText("Revisit (days)");

        TableColumn CapabilityA = new TableColumn("CapabilityA");
        CapabilityA.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("CapabilityA"));
        CapabilityA.setText("Capability A");

        TableColumn CapabilityB = new TableColumn("CapabilityB");
        CapabilityB.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("CapabilityB"));
        CapabilityB.setText("Capability B");

        TableColumn CapabilityC = new TableColumn("CapabilityC");
        CapabilityC.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("CapabilityC"));
        CapabilityC.setText("Capability C");

        TableColumn CapabilityD = new TableColumn("CapabilityD");
        CapabilityD.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("CapabilityD"));
        CapabilityD.setText("Capability D");

        // load data into TableView
        this.table_locations.setEditable(false);
        this.table_locations.getColumns().addAll(LocationColumn, LocRevisitColumn, CapabilityA, CapabilityB, CapabilityC, CapabilityD);
        this.table_locations.setItems(locationData);
    }

    private ArrayList<String[]> getPossibleTaskings(int date_day, ArrayList<String[]> existingScheduleData,
                                                    ArrayList<String[]> existingLocationData) {
        // get date schedule data
        String[] dateSchedule = new String[numCapability];
        for (int i=1; i<existingScheduleData.size()-1; i++) {
            dateSchedule[i] = existingScheduleData.get(i)[date_day-1];
        }
        // loop through locations row data
        ArrayList<String[]> taskingsData = new ArrayList<String[]>();
        for (int i=0; i<existingLocationData.size(); i++) {
            String[] currentLocation = existingLocationData.get(i);
            int numAdditionalRows = 2;
            String[] newData = new String[numAdditionalRows+numCapability];
            newData[0] = currentLocation[0]; // location name
            newData[1] = currentLocation[1]; // location revisit
            for (int j=numAdditionalRows; j<currentLocation.length-numAdditionalRows; j++) {
                if (currentLocation[j].equals("1") && !dateSchedule[j-1].equals("NIL")) {
                    newData[j] = "1";
                } else {
                    newData[j] = "0";
                }
            }
            taskingsData.add(newData);
        }
        return taskingsData;
    }

    @FXML
    public void loadTaskings () {
        this.table_taskings.getColumns().clear();
        this.table_taskings.getItems().clear();

        // get date selected
        LocalDate selected = this.picker_month_year.getValue();
        if (selected == null) {
            System.out.println("tasking date not selected");
            return;
        }
        // get date
        String month = selected.getMonth().toString();
        String month_short = month.substring(0,3);
        String year = Integer.toString(selected.getYear());
        int date_day = selected.getDayOfMonth();
        int month_int = month_stringToNum(month_short);
        // get schedule data file
        schedule_filename_string = "Schedule_"+month_short+"_"+year+".csv";
        schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);
        File scheduleFile = new File(schedule_filepath.toString());
        // get location data file
        locations_filepath = Paths.get(data_directory_string+locations_filename_string);
        File locationsFile = new File(locations_filepath.toString());
        // check if all required data are available
        if (!scheduleFile.exists() || !locationsFile.exists()) {
            System.out.println("required files are not available");
            return;
        }
        // get required file data
        ArrayList<String[]> existingScheduleData = readCSVData(scheduleFile);
        ArrayList<String[]> existingLocationData = readCSVData(locationsFile);
        // generate possible taskings
        ArrayList<String[]> possibleTaskings = getPossibleTaskings(date_day, existingScheduleData, existingLocationData);
        // load data into table
        for (int i = 0; i < possibleTaskings.size(); i++) {
            String location = possibleTaskings.get(i)[0];
            String loc_revisit = possibleTaskings.get(i)[1];
            String capability_ability_A = possibleTaskings.get(i)[2];
            String capability_ability_B = possibleTaskings.get(i)[3];
            String capability_ability_C = possibleTaskings.get(i)[4];
            String capability_ability_D = possibleTaskings.get(i)[5];

            Location newLocation = new Location(location, loc_revisit, capability_ability_A, capability_ability_B, capability_ability_C, capability_ability_D);
            locationPossibilityData.add(newLocation);
        }

        // create columns
        TableColumn LocationColumn = new TableColumn("Location");
        LocationColumn.setCellValueFactory(new PropertyValueFactory<Location, String>("Location"));
        LocationColumn.setText("Location");

        TableColumn LocRevisitColumn = new TableColumn("Revisit");
        LocRevisitColumn.setCellValueFactory(new PropertyValueFactory<Location, Integer>("Revisit"));
        LocRevisitColumn.setText("Revisit (days)");

        TableColumn CapabilityA = new TableColumn("CapabilityA");
        CapabilityA.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("CapabilityA"));
        CapabilityA.setText("Capability A");

        TableColumn CapabilityB = new TableColumn("CapabilityB");
        CapabilityB.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("CapabilityB"));
        CapabilityB.setText("Capability B");

        TableColumn CapabilityC = new TableColumn("CapabilityC");
        CapabilityC.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("CapabilityC"));
        CapabilityC.setText("Capability C");

        TableColumn CapabilityD = new TableColumn("CapabilityD");
        CapabilityD.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("CapabilityD"));
        CapabilityD.setText("Capability D");

        // load data into TableView
        this.table_taskings.setEditable(false);
        this.table_taskings.getColumns().addAll(LocationColumn, LocRevisitColumn, CapabilityA, CapabilityB, CapabilityC, CapabilityD);
        this.table_taskings.setItems(locationPossibilityData);
    }

    @FXML
    public void btnScheduleInsert() {
        LocalDate date = sch_insert_new_date.getValue();
        String newCategory = sch_insert_new_category.getText();
        String newTimeStart = sch_insert_new_timeStart.getText();
        String newTimeEnd = sch_insert_new_timeEnd.getText();
        // Button btn_schedule_insert

        if (date == null || newCategory.isEmpty() || newTimeStart.isEmpty() || newTimeEnd.isEmpty()) {
            return; // stop execution when one field is empty
        } else {
            String[] date_split = date.toString().split("-");
            String date_year = String.valueOf(date_split[0]);
            String date_month = month_numToString(Integer.parseInt(date_split[1]));
            String date_day = String.valueOf(date_split[2]);
            String date_string = String.valueOf(date_split[2]) + "-" + month_numToString(Integer.parseInt(date_split[1]));

            // check if file exists. if not, create new file
            File currentFile = findCSV(date_month, date_year);
            if (currentFile == null) {
                currentFile = createNewScheduleCSV(date_month, date_year);
                writeNewScheduleCSV(currentFile, date_month, date_year);
            }

            // insert data into file
            ArrayList<String[]> newData = writeScheduleData(currentFile, newCategory, date_day, newTimeStart, newTimeEnd);
            //debugScheduleData(newData);
            boolean isSuccess = writeScheduleFile(newData, date_month, date_year);
            if (isSuccess) {
                System.out.println("Wrote to file successfully");
            } else {
                System.out.println("Not successful in writing to file");
            }

        }
        loadSchedule();
    }

    @FXML
    public void btnScheduleDelete() {
        LocalDate date = sch_delete_date.getValue();
        String category = sch_delete_category.getText();
        String startTime = sch_delete_start_time.getText();
        if (date == null || category.isEmpty()|| startTime.isEmpty()) {
            System.out.println("one of the delete fields is invalid or empty");
            return;
        }
        // Button btn_schedule_delete
        String[] date_split = date.toString().split("-");
        String date_year = String.valueOf(date_split[0]);
        String date_month = month_numToString(Integer.parseInt(date_split[1]));
        String date_day = String.valueOf(date_split[2]);
        String date_string = String.valueOf(date_split[2]) + "-" + month_numToString(Integer.parseInt(date_split[1]));
        // check if file exists
        File dataFile = findCSV(date_month,date_year);
        if (dataFile != null){
            int category_id = getCategoryId(category);
            ArrayList<String[]> currentScheduleData = readCSVData(dataFile);
            // check if start time exists
            int date_day_int = Integer.parseInt(date_day);
            String dayData = currentScheduleData.get(category_id+1)[date_day_int-1];
            // get all timings
            String[] dataTimings = dayData.split(" ");
            ArrayList<String> allTimings = new ArrayList<String>();
            if (!(dataTimings.length == 1 && dataTimings[0].equals("NIL"))) {
                for (int i=0; i<dataTimings.length; i++) {
                    String timeStart = dataTimings[i].split("-")[0];
                    if(!timeStart.equals(startTime+"H")) {
                        allTimings.add(dataTimings[i]);
                    }
                }
            }
            // set data in array
            String[] arrayData = currentScheduleData.get(category_id+1);
            if (allTimings.size() == 0) {
                // set time to NIL
                arrayData[date_day_int-1] = "NIL";
            } else {
                String newData = "";
                for (int i=0; i<allTimings.size(); i++) {
                    if (i == allTimings.size()-1) {
                        newData = newData + allTimings.get(i);
                    } else {
                        newData = newData + allTimings.get(i) + " ";
                    }
                }
                arrayData[date_day_int-1] = newData;
                System.out.println(newData);
            }
            currentScheduleData.set(category_id+1, arrayData);
            writeScheduleFile(currentScheduleData, date_month, date_year);
        } else {
            System.out.println("Invalid month or year provided");
        }
        loadSchedule();
    }

    @FXML
    public void btnLocationInsert() {
        String locationName = loc_insert_name.getText();
        String locationPriority = loc_insert_priority.getText();
        String categoryName_A = loc_insert_catA.getText();
        String categoryName_B = loc_insert_catB.getText();
        String categoryName_C = loc_insert_catC.getText();
        String categoryName_D = loc_insert_catD.getText();
        // Button btn_location_insert
        if (locationName.isEmpty() || locationPriority.isEmpty() || categoryName_A.isEmpty() ||
                categoryName_B.isEmpty() || categoryName_C.isEmpty() || categoryName_D.isEmpty()) {
            System.out.println("One of the fields is empty");
            return;
        }

        File dataFile = new File(locations_filepath.toString());

        if (dataFile.exists()) {
            ArrayList<String[]> csvData = readCSVData(dataFile);
            String[] newLocation = {locationName, categoryName_A, categoryName_B, categoryName_C, categoryName_D};
            csvData.add(newLocation);
            // create String to be added into file
            String stringData = "";
            for (int i=0; i<csvData.size(); i++) {
                String[] currentRow = csvData.get(i);
                for (int j=0; j<currentRow.length; j++) {
                    if (j != currentRow.length-1) {
                        stringData = stringData + currentRow[j] + ",";
                    } else {
                        stringData = stringData + currentRow[j];
                    }
                }
                stringData = stringData + "\n";
            }
            try {
                // overwrite new dataset into file
                BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
                writer.write(stringData);
                writer.close();
                System.out.println("Successfully wrote to file");
            } catch (Exception e) {
                System.out.println("Unable to write to file");
            }
        }
        loadLocations();
    }

    @FXML
    public void btnLocationDelete() {
        String locationName = loc_delete_name.getText();
        // Button btn_location_delete
        if (locationName.isEmpty()) {
            System.out.println("Location name field is empty");
            return;
        }

        File dataFile = new File(locations_filepath.toString());

        if (dataFile.exists()) {
            ArrayList<String[]> csvData = readCSVData(dataFile);
            int locationIndex = -1;
            for (int i=0; i<csvData.size(); i++) {
                if(csvData.get(i)[0].toLowerCase().equals(locationName.toLowerCase())) {
                    locationIndex = i;
                    break;
                }
            }
            if(locationIndex == -1) {
                System.out.println("Location name not found");
                return;
            } else {
                csvData.remove(locationIndex);
                System.out.println("Successfully deleted from list");
                // create String to be added into file
                String stringData = "";
                for (int i=0; i<csvData.size(); i++) {
                    String[] currentRow = csvData.get(i);
                    for (int j=0; j<currentRow.length; j++) {
                        if (j != currentRow.length-1) {
                            stringData = stringData + currentRow[j] + ",";
                        } else {
                            stringData = stringData + currentRow[j];
                        }
                    }
                    stringData = stringData + "\n";
                }
                try {
                    // overwrite new dataset into file
                    BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
                    writer.write(stringData);
                    writer.close();
                    System.out.println("Successfully wrote to file");
                } catch (Exception e) {
                    System.out.println("Unable to write to file");
                }
            }
        }
        loadLocations();
    }
}