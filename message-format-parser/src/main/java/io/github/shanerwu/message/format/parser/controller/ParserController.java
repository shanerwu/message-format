package io.github.shanerwu.message.format.parser.controller;

import io.github.shanerwu.message.format.parser.message.AbstractMessageSupport;
import io.github.shanerwu.message.format.parser.message.MessageData;
import io.github.shanerwu.message.format.parser.utils.MessageClassScanner;
import io.github.shanerwu.message.format.parser.utils.MessageParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParserController {

    private TreeItem<MessageData> root;

    private Map<String, String> defaultMessages = new HashMap<>();

    @FXML
    private ComboBox<String> messageTypes;

    @FXML
    private ListView<String> messageDetails;

    @FXML
    private TextArea textArea;

    @FXML
    private TreeTableView<MessageData> treeTable;

    @FXML
    private TreeTableColumn<MessageData, String> descriptionColumn;

    @FXML
    private TreeTableColumn<MessageData, String> messageColumn;

    @FXML
    public void initialize() {
        createMessageTypeComboBox();
        createTreeTableView();
        initializeDefaultMessages();

        messageDetails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleMessageDetailsSelection(newValue);
        });
    }

    private void handleMessageDetailsSelection(String detailName) {
        if (detailName != null) {
            if (defaultMessages.containsKey(detailName)) {
                textArea.setText(defaultMessages.get(detailName));
            } else {
                textArea.clear();
            }
        }
    }

    @FXML
    public void handleComboBoxOnAction() {
        String type = messageTypes.getSelectionModel().getSelectedItem();
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(MessageClassScanner.getDetails(type));
        messageDetails.setItems(list);
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void parseInput() {
        root.getChildren().clear();
        String message = textArea.getText();
        String type = messageTypes.getSelectionModel().getSelectedItem();
        String detailName = messageDetails.getSelectionModel().getSelectedItem();
        Class<? extends AbstractMessageSupport> clazz =
                (Class<? extends AbstractMessageSupport>) MessageClassScanner.getDetailClasses(type, detailName);
        try {
            List<MessageData> messageDataList = MessageParser.parse(clazz, message);
            messageDataList.forEach(messageData -> {
                if (messageData.getDetails() != null) {
                    TreeItem<MessageData> treeItem = new TreeItem<>(messageData);
                    setDetailTreeItem(messageData.getDetails(), treeItem);
                    root.getChildren().add(treeItem);
                } else {
                    root.getChildren().add(new TreeItem<>(messageData));
                }
            });
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private void setDetailTreeItem(List<MessageData> details, TreeItem<MessageData> treeItem) {
        details.forEach(detail -> {
            treeItem.setExpanded(true);
            if (detail.getDetails() != null) {
                TreeItem<MessageData> detailTreeItem = new TreeItem<>(detail);
                setDetailTreeItem(detail.getDetails(), detailTreeItem);
                treeItem.getChildren().add(detailTreeItem);
            } else {
                treeItem.getChildren().add(new TreeItem<>(detail));
            }
        });
    }

    private void createMessageTypeComboBox() {
        MessageClassScanner.getTypes()
                .forEach(type -> messageTypes.getItems().add(type));
    }

    private void createTreeTableView() {
        root = new TreeItem<>(new MessageData(StringUtils.EMPTY, StringUtils.EMPTY));
        treeTable.setRoot(root);
        treeTable.setShowRoot(false);

        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        messageColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("message"));
        treeTable.setEditable(true);

        messageColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
    }

    private void initializeDefaultMessages() {
        defaultMessages.put("Sample101 測試電文 Request", "TESTCODE" + "20250420203000" + "00247" +
                                                         "S" + "002" +
                                                         "0530a219-9ef1-4eaa-96f4-59793dedda34" + "75c63a5c-5198-4b44-bbfb-b070d814382f" + "622ad2cf-14ab-429c-b6cf-d4101c8a2ff9" +
                                                         "a6ef45ba-0901-4691-9c9c-dc2be8380d6c" + "ed1d1a92-6c60-45bc-97e1-d123c00441f8" + "f150e0ee-1306-42c8-bec8-8f7937efcf43");
        defaultMessages.put("Sample101 測試電文 Response", "0001" + "20250420203000" + "00027" +
                                                          "1" + "001");
        defaultMessages.put("Sample201 測試電文",          "0001" + "20250420203000" + "0147" +
                                                          "02" +
                                                          "20250401" + "02" +
                                                          "01" + "0530a219-9ef1-4eaa-96f4-59793dedda34" +
                                                          "02" + "75c63a5c-5198-4b44-bbfb-b070d814382f" +
                                                          "0" +
                                                          "20250402" + "03" +
                                                          "01" + "622ad2cf-14ab-429c-b6cf-d4101c8a2ff9" +
                                                          "02" + "a6ef45ba-0901-4691-9c9c-dc2be8380d6c" +
                                                          "03" + "ed1d1a92-6c60-45bc-97e1-d123c00441f8" +
                                                          "1"
        );
    }

    private void showErrorDialog(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("錯誤");
        alert.setHeaderText("發生錯誤啦ヾ( ⁰ д ⁰)ﾉ");

        Label label = new Label("Exception message:");

        TextArea errorMessageArea = new TextArea(e.getMessage());
        errorMessageArea.setEditable(false);
        errorMessageArea.setWrapText(true);

        errorMessageArea.setMaxWidth(Double.MAX_VALUE);
        errorMessageArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(errorMessageArea, Priority.ALWAYS);
        GridPane.setHgrow(errorMessageArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(errorMessageArea, 0, 1);

        alert.getDialogPane().setContent(expContent);
        alert.showAndWait();
    }

}
