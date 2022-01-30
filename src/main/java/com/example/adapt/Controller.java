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

    protected final int dailyStartTime_Hours = 0; // tasking daily start time in hours, max num is 24

    protected String data_directory_string = "src/main/resources/com/example/adapt/";
    protected String schedule_filename_string = "Schedule_OCT_2021.csv";
    protected Path schedule_filepath = Paths.get(data_directory_string+schedule_filename_string);

    protected String locations_filename_string = "List_Target.csv";
    protected Path locations_filepath = Paths.get(data_directory_string+locations_filename_string);

    protected String tasking_filename_string = "Tasking_Schedule_2022.csv";
    protected Path tasking_filepath = Paths.get(data_directory_string+tasking_filename_string);

    // calculate date month
    protected String month = YearMonth.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    protected Integer num_month_days = YearMonth.now().lengthOfMonth();

    // table views in titled panes
    @FXML private TreeTableView table_schedule;
    @FXML private TableView table_locations;
    @FXML private TreeTableView table_tasking;

    // upload buttons
    @FXML private Button btn_upload_schedule;
    @FXML private Button btn_upload_locations;
    @FXML private Button btn_upload_tasking;

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
    @FXML private TextField loc_insert_revisit;
    @FXML private TextField loc_insert_catA;
    @FXML private TextField loc_insert_catB;
    @FXML private TextField loc_insert_catC;
    @FXML private TextField loc_insert_catD;
    @FXML private Button btn_location_insert;

    // delete location
    @FXML private TextField loc_delete_name;
    @FXML private Button btn_location_delete;

    // delete tasking
    @FXML private TextField tasking_capability;
    @FXML private TextField tasking_date;
    @FXML private TextField tasking_start_time;
    @FXML private Button btn_tasking_delete;

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

    private ArrayList<String[]> readNumCSVData(String[] file_paths) {
        ArrayList<String[]> compiledData = new ArrayList<String[]>();
        // loop through each file path
        for (int i=0; i<file_paths.length; i++) {
            File currentFile = new File(file_paths[i]);
            if (!currentFile.exists()) {
                System.out.println(file_paths[i] + " does not exist.");
                continue;
            }
            // get data of each file
            ArrayList<String[]> currentData = readCSVData(currentFile);
            if (compiledData.size() < 1) {
                compiledData = currentData;
            } else {
                // copy original and new data into single arraylist
                for (int j=0; j<compiledData.size(); j++) {
                    int newLength = compiledData.get(j).length + currentData.get(j).length;
                    String[] currentArray = new String[newLength];
                    for (int k=0; k<compiledData.get(j).length; k++) {
                        currentArray[k] = compiledData.get(j)[k];
                    }
                    for (int k=0; k<currentData.get(j).length; k++) {
                        currentArray[compiledData.get(j).length+k] = currentData.get(j)[k];
                    }
                    compiledData.set(j, currentArray);
                }
            }
        }
        return compiledData;
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
            String loc_priority = locations_file_data.get(i)[1];
            String loc_revisit = locations_file_data.get(i)[2];
            String capability_ability_A = locations_file_data.get(i)[3];
            String capability_ability_B = locations_file_data.get(i)[4];
            String capability_ability_C = locations_file_data.get(i)[5];
            String capability_ability_D = locations_file_data.get(i)[6];

            Location newLocation = new Location(location, loc_priority, loc_revisit, capability_ability_A, capability_ability_B, capability_ability_C, capability_ability_D);
            locationData.add(newLocation);
        }

        // create columns
        TableColumn LocationColumn = new TableColumn("Location");
        LocationColumn.setCellValueFactory(new PropertyValueFactory<Location, String>("Location"));
        LocationColumn.setText("Location");

        TableColumn LocPriorityColumn = new TableColumn("Priority");
        LocPriorityColumn.setCellValueFactory(new PropertyValueFactory<Location, Integer>("Priority"));
        LocPriorityColumn.setText("Priority");

        TableColumn LocRevisitColumn = new TableColumn("Revisit");
        LocRevisitColumn.setCellValueFactory(new PropertyValueFactory<Location, Integer>("Revisit"));
        LocRevisitColumn.setText("Revisit (hours)");

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
        this.table_locations.getColumns().addAll(LocationColumn, LocPriorityColumn, LocRevisitColumn, CapabilityA, CapabilityB, CapabilityC, CapabilityD);
        this.table_locations.setItems(locationData);
    }

    private String convertTimeToString(int currentTime) {

        int currentHours = currentTime/60;
        int currentMinutes = currentTime-(currentHours*60);
        String currentMinutes_String = String.valueOf(currentMinutes);
        if (currentMinutes < 10) {
            currentMinutes_String = "0"+currentMinutes_String;
        }
        String currentTime_String = String.valueOf(currentHours)+currentMinutes_String;
        if (currentTime_String.length() < 4) { // change to 4 digit timings
            int addedLength = 4 - currentTime_String.length();
            currentTime_String = ("0".repeat(addedLength)) + currentTime_String;
        }
        //System.out.println(currentTime+" minutes "+ currentTime_String);
        return currentTime_String;
    }

    private String[] getLastCapDateTimings(ArrayList<String[]> existingScheduleData, int numCapability) {
        // get the last tasking date and time for each capability
        String[] lastTasking = new String[numCapability];
        String[] lastDates = new String[numCapability];
        for (int i=0; i<numCapability; i++) {
            lastTasking[i] = "0000"; // fill empty null array with values
            lastDates[i] = existingScheduleData.get(0)[0];
        }
        for (int i=1; i<existingScheduleData.size(); i++) {
            for (int j = 0; j < existingScheduleData.get(i).length; j++) {
                String currentDate = existingScheduleData.get(0)[j];
                String current_tasking_timings = existingScheduleData.get(i)[j];
                if (!current_tasking_timings.equals("NIL")) {
                    int tasking_last = 0;
                    // get the latest timing from timing(s)
                    String[] tasking_dataset = current_tasking_timings.split(" ");
                    for (int k = 0; k < tasking_dataset.length; k++) {
                        int last_timing = Integer.parseInt(tasking_dataset[k].split("-")[1].substring(0,4));
                        tasking_last = Integer.max(last_timing, tasking_last);
                    }

                    int current_timing = Integer.parseInt(lastTasking[i-1]);
                    lastTasking[i-1] = String.valueOf(Integer.max(current_timing, tasking_last));
                    if (Integer.max(current_timing, tasking_last) == tasking_last) {
                        lastDates[i-1] = currentDate;
                    }
                }
            }
            lastTasking[i-1] = convertTimeToString(Integer.parseInt(lastTasking[i-1]));
        }
        // return date and time as one array
        String[] newArray = new String[numCapability];
        for (int i=0; i<numCapability; i++) {
            newArray[i] = lastDates[i] + " " + lastTasking[i];
        }
        return newArray;
    }

    private String[] findLocationRow(ArrayList<String[]> existingLocationData, String LocationName) {
        // get entire row data with provided location name
        int rowSize = existingLocationData.size();
        String[] selectedLocRow = new String[rowSize];
        for (int i=0; i<existingLocationData.size(); i++) {
            String currentLocName = existingLocationData.get(i)[0];
            if (currentLocName.equals(LocationName)) {
                selectedLocRow = existingLocationData.get(i);
            }
        }
        return selectedLocRow;
    }

    private String[] getAvailableCapByDate(ArrayList<String[]> existingScheduleData, String date) {
        // return capability availability time based on date provided
        // returns null if date does not match
        int foundIndex = -1;
        for (int k=0; k<existingScheduleData.get(0).length; k++) {
            if(existingScheduleData.get(0)[k].equals(date)) {
                foundIndex = k;
                break;
            }
        }
        if (foundIndex == -1) {
            return null;
        } else {
            String[] wantedData = new String[existingScheduleData.size()];
            for (int k=0; k<existingScheduleData.size(); k++) {
                wantedData[k] = existingScheduleData.get(k)[foundIndex];
            }
            return wantedData;
        }
    }

    private int[] getStartEndTime_Minutes(String timeRange) {
        // "0900H-1100H" to String{"540", "660"}
        int[] startEndTime = new int[2];
        String startTime = timeRange.substring(0,4);
        int startTime_Hours = Integer.parseInt(startTime.substring(0,2))*60;
        int startTime_Minutes = startTime_Hours + Integer.parseInt(startTime.substring(2,4));
        startEndTime[0] = startTime_Minutes;
        String endTime = timeRange.substring(7,11);
        int endTime_Hours = Integer.parseInt(endTime.substring(0,2))*60;
        int endTime_Minutes = endTime_Hours + Integer.parseInt(endTime.substring(2,4));
        startEndTime[1] = endTime_Minutes;
        return startEndTime;
    }

    private ArrayList<String[]> getTaskings(String year) {
        String tasking_filename_string = "Tasking_Schedule_"+year+".csv";
        Path tasking_filepath = Paths.get(data_directory_string+tasking_filename_string);
        File taskingFile = new File(tasking_filepath.toString());
        return readCSVData(taskingFile);
    }

    private ArrayList<String[]> getMultipleYears_Taskings(String[] years) {
        ArrayList<String[]> compiledYearTasking = new ArrayList<String[]>();
        for (int i=0; i<years.length; i++) {
            ArrayList<String[]> current = getTaskings(years[i]);
            for (int j=0; j< current.size(); j++) {
                compiledYearTasking.add(current.get(j));
            }
        }
        return compiledYearTasking;
    }

    private ArrayList<String[]> getLocationData() {
        locations_filepath = Paths.get(data_directory_string+locations_filename_string);
        File locationsFile = new File(locations_filepath.toString());
        ArrayList<String[]> locationData = readCSVData(locationsFile);
        return locationData;
    }

    @FXML
    public void btnUploadTasking() {

    }

    @FXML
    public void loadTaskings () {
        // get date selected
        LocalDate selected = this.picker_month_year.getValue();
        if (selected == null) {
            System.out.println("tasking date not selected");
            return;
        }
        // clear treetableview
        this.table_tasking.getColumns().clear();
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
        // get required schedule file data
        int numMonths = 2;
        String[] scheduleDataFilepath = new String[numMonths];
        for (int k=0; k<numMonths; k++) { // generate file path with input month and year
            int nextFewMonths = month_int + k;
            if (nextFewMonths > 12) {
                nextFewMonths = nextFewMonths - 12;
            }
            System.out.println("Getting "+nextFewMonths+"_"+year + " filepath and data");
            scheduleDataFilepath[k] = data_directory_string+"Schedule_"+month_numToString(nextFewMonths)+"_"+year+".csv";
        }
        ArrayList<String[]> existingScheduleData = readNumCSVData(scheduleDataFilepath);
        System.out.println("Compiled file paths data");
        // get required location data
        ArrayList<String[]> existingLocationData = readCSVData(locationsFile);
        // generate required taskings
        LocalDate today = LocalDate.now();
        String today_year = today.toString().split("-")[0];
        String today_day = today.toString().split("-")[2];
        String today_month = today.toString().split("-")[1];
        String today_short = today_day+"-"+today_month;
        // set starting date
        Date date_tomorrow = new Date();
        date_tomorrow.setHours(dailyStartTime_Hours);
        date_tomorrow.setMinutes(0);
        date_tomorrow.setDate(date_tomorrow.getDate()+1);

        ArrayList<Tasking> compiledTasking = new ArrayList<Tasking>();
        String[] years = {String.valueOf(Integer.parseInt(today_year)-1), today_year, String.valueOf(Integer.parseInt(today_year)+1)};
        ArrayList<String[]> taskings = getMultipleYears_Taskings(years);
        for(int i=0; i<taskings.size(); i++) {
            String current_date = taskings.get(i)[0];
            String current_time = taskings.get(i)[1];
            String current_StartTime = taskings.get(i)[1].split("-")[0];
            String current_EndTime = taskings.get(i)[1].split("-")[1];
            String current_capability = taskings.get(i)[2];
            String[] multiple_locations = taskings.get(i)[3].split(";");
            for (int j=0; j<multiple_locations.length; j++) {
                String current_location = multiple_locations[j];
                String[] locationRow = findLocationRow(existingLocationData,current_location);
                if (locationRow[0] == null) continue;
                int priority = Integer.parseInt(locationRow[1]);
                int revisit_rate = Integer.parseInt(locationRow[2]);
                Tasking new_tasking = new Tasking(current_date,current_time,priority,revisit_rate,current_capability, current_location);
                compiledTasking.add(new_tasking);
            }
        }
        System.out.println("Compiled tasking data..");

        TreeTableColumn<Tasking, String> dateColumn = new TreeTableColumn<Tasking, String>("Date_short");
        dateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Tasking, String>("Date_short"));
        dateColumn.setText("Date");
        TreeTableColumn<Tasking, String> timeColumn = new TreeTableColumn<Tasking, String>("Time");
        timeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Tasking, String>("Time"));
        timeColumn.setText("Time");
        TreeTableColumn<Tasking, String> priorityColumn = new TreeTableColumn<Tasking, String>("Priority");
        priorityColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Tasking, String>("Priority"));
        priorityColumn.setText("Priority");
        TreeTableColumn<Tasking, String> revisitRateColumn = new TreeTableColumn<Tasking, String>("Revisit_Hours");
        revisitRateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Tasking, String>("Revisit_Hours"));
        revisitRateColumn.setText("Revisit (Hours)");
        TreeTableColumn<Tasking, String> capabilityColumn = new TreeTableColumn<Tasking, String>("CapabilityType");
        capabilityColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Tasking, String>("CapabilityType"));
        capabilityColumn.setText("Capability");
        TreeTableColumn<Tasking, String> locationColumn = new TreeTableColumn<Tasking, String>("Location");
        locationColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Tasking, String>("Location"));
        locationColumn.setText("Location");

        TreeItem root = new TreeItem(new Tasking("...","...",0,0,"...","..."));
        for (int i=0; i<compiledTasking.size(); i++) {
            TreeItem current = new TreeItem(compiledTasking.get(i));
            root.getChildren().add(current);
        }
        this.table_tasking.setEditable(false);
        this.table_tasking.getColumns().addAll(dateColumn, timeColumn, priorityColumn, revisitRateColumn, capabilityColumn, locationColumn);
        this.table_tasking.setRoot(root);
        this.table_tasking.setShowRoot(false);
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
        String locationRevisit = loc_insert_revisit.getText();
        String categoryName_A = loc_insert_catA.getText();
        String categoryName_B = loc_insert_catB.getText();
        String categoryName_C = loc_insert_catC.getText();
        String categoryName_D = loc_insert_catD.getText();
        // Button btn_location_insert
        if (locationName.isEmpty() || locationPriority.isEmpty() || locationRevisit.isEmpty() || categoryName_A.isEmpty() ||
                categoryName_B.isEmpty() || categoryName_C.isEmpty() || categoryName_D.isEmpty()) {
            System.out.println("One of the fields is empty");
            return;
        }

        File dataFile = new File(locations_filepath.toString());

        if (dataFile.exists()) {
            ArrayList<String[]> csvData = readCSVData(dataFile);
            String[] newLocation = {locationName, locationPriority, locationRevisit, categoryName_A, categoryName_B, categoryName_C, categoryName_D};
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

    @FXML
    public void btnTaskingDelete() {

    }
}