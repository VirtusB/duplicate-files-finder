import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class HomeFX {
    public TableView<File> duplicateFilesTableView;
    public Button browseSourceDirectoryBtn;
    public Button browseDestinationDirectoryBtn;
    public Button findDuplicatesBtn;
    public TextField sourceDirTextField;
    public TextField destinationDirTextField;

    private File sourceDir = null;
    private File destinationDir = null;
    public ObservableList<File> duplicateFiles = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss");

    @FXML
    protected void initialize() {
        this.createDuplicateFilesTable();
        this.addBrowseSourceBtnListener();
        this.addDestinationSourceBtnListener();
        this.addFindDuplicatesBtnListener();
    }

    private void addFindDuplicatesBtnListener() {
        this.findDuplicatesBtn.setOnAction(event -> {
            if (this.sourceDir == null || this.destinationDir == null) {
                return;
            }

            Collection<File> sourceFiles = FileUtils.listFiles(
                    this.sourceDir,
                    new RegexFileFilter("^(.*?)"),
                    DirectoryFileFilter.DIRECTORY
            );

            Collection<File> destinationFiles = FileUtils.listFiles(
                    this.destinationDir,
                    new RegexFileFilter("^(.*?)"),
                    DirectoryFileFilter.DIRECTORY
            );


            Set<String> sourceFileNames = sourceFiles.stream().map(File::getName).collect(toSet());
            Set<String> destinationFileNames = destinationFiles.stream().map(File::getName).collect(toSet());

            sourceFileNames.forEach(fName -> {
                if (destinationFileNames.contains(fName)) {
                    List<File> filesWithSameName = sourceFiles.stream().filter(file -> fName.equals(file.getName())).collect(Collectors.toList());
                    this.duplicateFiles.addAll(filesWithSameName);
                }
            });

        });
    }

    private void addBrowseSourceBtnListener() {
        this.browseSourceDirectoryBtn.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File sourceDirectory = directoryChooser.showDialog(this.browseSourceDirectoryBtn.getScene().getWindow());

            if (sourceDirectory == null) {
                return;
            }

            this.sourceDir = sourceDirectory;
            this.sourceDirTextField.setText(sourceDirectory.getAbsolutePath());
        });
    }

    private void addDestinationSourceBtnListener() {
        this.browseDestinationDirectoryBtn.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File destinationDirectory = directoryChooser.showDialog(this.browseDestinationDirectoryBtn.getScene().getWindow());

            if (destinationDirectory == null) {
                return;
            }

            this.destinationDir = destinationDirectory;
            this.destinationDirTextField.setText(destinationDirectory.getAbsolutePath());
        });
    }

    private void createDuplicateFilesTable() {
        TableColumn<File, String> nameColumn = new TableColumn<>("Name");
        TableColumn<File, Number> sizeColumn = new TableColumn<>("Size");
        TableColumn<File, String> createdColumn = new TableColumn<>("Created");

        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
        sizeColumn.setCellValueFactory(cellData -> new ReadOnlyLongWrapper(cellData.getValue().length()));
        createdColumn.setCellValueFactory(cellData -> {
            Path path = Paths.get(cellData.getValue().getAbsolutePath());
            FileTime fileTime = null;
            try {
                BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
                fileTime = attr.creationTime();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ReadOnlyStringWrapper(fileTimeToString(fileTime));
        });

        this.duplicateFilesTableView.setItems(this.duplicateFiles);
        this.duplicateFilesTableView.getColumns().addAll(nameColumn, sizeColumn, createdColumn);
        this.duplicateFilesTableView.setMaxHeight(450);
        this.duplicateFilesTableView.setMaxWidth(750);
        this.duplicateFilesTableView.setPrefWidth(750);
        this.duplicateFilesTableView.setPrefHeight(450);
    }

    public static String fileTimeToString(FileTime fileTime) {
        String s = parseToString(
                fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return s;
    }

    public static FileTime fileTimeFromString(String dateTimeString) {
        LocalDateTime localDateTime = parseFromString(dateTimeString);
        return FileTime.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String parseToString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_FORMATTER_WITH_TIME);
    }

    public static LocalDateTime parseFromString(String date) {
        return LocalDateTime.parse(date, DATE_FORMATTER_WITH_TIME);
    }

}
