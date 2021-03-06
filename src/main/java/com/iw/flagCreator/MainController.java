package com.iw.flagCreator;

import com.twelvemonkeys.image.ResampleOp;
import flag_templates.GenericFlagTemplate;
import flag_templates.hoi4FlagSpecs;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.*;
import java.net.URL;

public class MainController
{

    @FXML
    protected ImageView patreonIcon;

    @FXML
    protected AnchorPane mainWindowElement;

    @FXML
    protected Button hoi4GameIcon;

    @FXML
    protected Button hoi3GameIcon;

    @FXML
    protected Button eu4GameIcon;

    @FXML
    protected Button eu3GameIcon;

    @FXML
    protected ImageView flagPathFolderIcon;

    @FXML
    protected ImageView outputFolderPathIcon;

    @FXML
    protected TextField flagFilePathTextField;

    @FXML
    protected TextField outputFolderTextField;

    @FXML
    protected TextField flagTagTextField;

    @FXML
    protected TextField flagSuffixTextField;

    @FXML
    protected HBox flagPreviewElement;

    @FXML
    protected HBox gameSelectionElement;

    @FXML
    protected Button createFlagButton;

    @FXML
    protected Button previewFlagButton;

    @FXML
    protected CheckBox openOutputFolderCheckBox;

    @FXML
    protected Label flagCreatorVersionLabel;

    final String flagCreatorVersion = "1.2";
    File imageToConvert;
    GenericFlagTemplate newFlagSpecs = new GenericFlagTemplate(93,64);

    @FXML
    public void initialize(){
        flagCreatorVersionLabel.setText("Version: " + flagCreatorVersion);
        openAboutPopup();
        /// Listener to flag source text field to prevent generating preview when no file is loaded
        flagFilePathTextField.textProperty().addListener((ObservableValue<? extends String> observable,
                                                          String oldValue, String newValue) -> {
            // Enable preview & create button if a flag file is loaded
            if (!(newValue.isEmpty()) && checkFlagSourceCorrectness(newValue)) {
                imageToConvert = new File(flagFilePathTextField.getText());
                previewFlagButton.setDisable(false);
            }
            // Disable preview if no flag is loaded
            if (newValue.isEmpty() || (!checkFlagSourceCorrectness(newValue))) {
                previewFlagButton.setDisable(true);
            }
            /// Downloads an image from the web and creates a preview file in the source folder
            if(newValue.startsWith("https://") && checkFlagSourceCorrectness(newValue)) {
                try {
                    URL flagURL = new URL(flagFilePathTextField.getText());
                    BufferedImage img = ImageIO.read(flagURL);
                    ImageIO.write(img, "PNG", imageToConvert = new File("flagFilePreview.png"));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            if(newValue.endsWith("svg") && newValue.contains("wikipedia.org") && newValue.contains("File:")){
                flagFilePathTextField.clear();
                Alert fileNotSupportedPopup = new Alert(Alert.AlertType.ERROR);
                fileNotSupportedPopup.setHeight(600);
                fileNotSupportedPopup.setTitle("Bad Image File Link");
                fileNotSupportedPopup.setHeaderText("Please provide direct link to SVG file");
                fileNotSupportedPopup.setContentText("It looks like you are trying to use a flag file from Wikipedia.\n" +
                        "see 'Help' -> 'How to use' " +
                        "on how to retrieve flags from Wikipedia.");
                fileNotSupportedPopup.showAndWait();
            }
        });
        /// Listener to output folder text field to unlock create flag button
        outputFolderTextField.textProperty().addListener((ObservableValue<? extends String> observable,
                                                          String oldValue, String newValue) -> {
            // Enable preview & create button if a flag file is loaded
            if (!(newValue.isEmpty())) {
                createFlagButton.setDisable(false);
            }
            // Disable preview & create button if no flag is loaded
            if (newValue.isEmpty()) {
                createFlagButton.setDisable(true);
            }
        });
    }

    public void openFileNameChanger(){
        try {
            Stage flagNameStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("flag-name-changer.fxml"));
            Scene flagNameChangerStage = new Scene(fxmlLoader.load(), 600, 500);
            flagNameStage.setTitle("Flag Name Changer");
            flagNameStage.setScene(flagNameChangerStage);
            flagNameStage.setResizable(false);
            flagNameStage.show();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /// Methods to open web pages from top menu bar, code duplication needs to be reduced
    @FXML
    public void openYouTubeInfoVideo(){
        try {
            Desktop.getDesktop().browse(new URL("https://youtu.be/0a8xXvN_ygk").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void openPatreonPage(){
        try {
            Desktop.getDesktop().browse(new URL("https://www.patreon.com/playerhoi").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openGitHubPage(){
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/PlayerHOI/IWFC").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openDiscordPage(){
        try {
            Desktop.getDesktop().browse(new URL("https://discord.gg/sSCU3WS").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openKnownIssuesPage(){
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/PlayerHOI/IWFC/issues/1").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFlagCreatorReleasesPage(){
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/PlayerHOI/IWFC/releases").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openAboutPopup(){
        Alert aboutPopup = new Alert(Alert.AlertType.INFORMATION);
        aboutPopup.setTitle("Iron Workshop Flag Creator " + flagCreatorVersion);
        aboutPopup.setHeaderText("Created by PlayerHOI\nA product of the Iron Workshop\nironworkshopbiz@gmail.com\n");
        aboutPopup.setContentText("Use this software at your own discretion, any consequence of using this software" +
                " is the sole responsibility of the user.\n\nClick on 'Help' -> 'How to use' section for" +
                " instructions on how " + "to use the flag creator.\n\nSupported file formats:" +
                " JPEG, JPG, TGA, PNG ,BMP and SVG.");
        aboutPopup.setWidth(200);
        aboutPopup.setHeight(500);
        aboutPopup.showAndWait();
    }

    /// Methods to create instances of flags
    @FXML
    public void createFlagHoi4(){
        gameIconClickEffect();
        hoi4GameIcon.getStyleClass().add("gameIconClickedClass");
        /// HOI4 needs an instance created to make sure that flag preview generates 3 flags instead of one
        newFlagSpecs = new hoi4FlagSpecs(82,52);
    }

    @FXML
    public void createFlagVic2(){
        gameIconClickEffect();
        hoi3GameIcon.getStyleClass().add("gameIconClickedClass");
        newFlagSpecs = new GenericFlagTemplate(93,64);
    }

    @FXML
    public void createFlagEU4(){
        gameIconClickEffect();
        eu4GameIcon.getStyleClass().add("gameIconClickedClass");
        newFlagSpecs = new GenericFlagTemplate(128,128);
    }

    @FXML
    public void createFlagEU3(){
        gameIconClickEffect();
        eu3GameIcon.getStyleClass().add("gameIconClickedClass");
        newFlagSpecs = new GenericFlagTemplate(64,64);
    }

    /// Methods to increase/reduce opacity of game icons, code duplication should be reduced.
    @FXML
    public void gameIconHoverEffectHOI4(){
        hoi4GameIcon.getStyleClass().add("gameIconHoverClass");
    }

    @FXML
    public void gameIconHoverEffectHOI3(){
        hoi3GameIcon.getStyleClass().add("gameIconHoverClass");
    }

    @FXML
    public void gameIconHoverEffectEU3(){
        eu3GameIcon.getStyleClass().add("gameIconHoverClass");
    }

    @FXML
    public void gameIconHoverEffectEU4(){
        eu4GameIcon.getStyleClass().add("gameIconHoverClass");
    }

    @FXML
    public void gameIconRemoveHoverEffectHOI4(){
        hoi4GameIcon.getStyleClass().remove("gameIconHoverClass");
    }

    @FXML
    public void gameIconRemoveHoverEffectHOI3(){
        hoi3GameIcon.getStyleClass().remove("gameIconHoverClass");
    }

    @FXML
    public void gameIconRemoveHoverEffectEU3(){
        eu3GameIcon.getStyleClass().remove("gameIconHoverClass");
    }

    @FXML
    public void gameIconRemoveHoverEffectEU4(){
        eu4GameIcon.getStyleClass().remove("gameIconHoverClass");
    }

    public void gameIconClickEffect(){
        for (Node child : gameSelectionElement.getChildren())
        {
            child.getStyleClass().remove("gameIconClickedClass");
        }
    }

    @FXML
    /// Executed when user clicks on "Create Flag" button
    public void createFlagButtonClick(){
        checkIfNotificationNeeded();
        setFlagCreationData();
        newFlagSpecs.createFlagFolders();
        try
        {
            newFlagSpecs.createFlag();
            if(openOutputFolderCheckBox.isSelected()){
                Desktop.getDesktop().open(new File(outputFolderTextField.getText()));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /// Updates new flag data in case user changed some settings
    public void setFlagCreationData(){
        if(flagFilePathTextField.getText().startsWith("https://")){
            newFlagSpecs.setSourceFlagLocation(new File("flagFilePreview.png"));
        }else{
            newFlagSpecs.setSourceFlagLocation(new File(flagFilePathTextField.getText()));
        }
        newFlagSpecs.setOutputFolderLocation(new File(outputFolderTextField.getText()));
        newFlagSpecs.setOutputFileFolderPath(outputFolderTextField.getText());
        if(flagTagTextField.getText().isEmpty()){
            newFlagSpecs.setFlagName(imageToConvert.getName());
        }else {
            newFlagSpecs.setFlagName(flagTagTextField.getText());
        }
        newFlagSpecs.setFlagNameSuffix(flagSuffixTextField.getText());
    }

    /// Checks if a notification is needed to notify player about a problem
    public void checkIfNotificationNeeded(){
        if(outputFolderTextField.getText().isEmpty()){
            createWarningPopup("No output folder set", "Make sure an output folder has been selected.");
        }
        if(newFlagSpecs==null){
            createWarningPopup("No Game Selected", "Click on a game icon before creating the flag");
        }
        if(newFlagSpecs instanceof hoi4FlagSpecs && !(outputFolderTextField.getText().isEmpty())){
            createWarningPopup("Important", "Hearts of Iron 4 flags files " +
                    "are created upside down due " +
                    "to an ongoing issue, the flags will appear properly in-game.");
        }
    }

    /// Standard method to create pop up messages
    public void createWarningPopup(String alertTitle ,String alertText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(alertTitle);
        alert.setHeaderText(null);
        alert.setContentText(alertText);
        alert.showAndWait();
    }

    /// Method to check if source file URL/path is a supported file format
    public boolean checkFlagSourceCorrectness(String flagPath){
        return flagPath.endsWith("jpg") || flagPath.endsWith("jpeg")
                || flagPath.endsWith("png") || flagPath.endsWith("bmp")
                || flagPath.endsWith("tga") || flagPath.endsWith("svg");
    }

    /// Method to create flag previews
    public void generateFlagPreview(){
        if(newFlagSpecs==null){
            createWarningPopup("No Game Selected", "No game selected, click on a game icon before " +
                    "generating a preview.");
        }
        if(newFlagSpecs instanceof hoi4FlagSpecs){
            generateThreeFlagPreview();
            System.out.println("Generating 3 flag preview");
        }else {
            generateSingleFlagPreview();
            gameIconClickEffect();
        }
    }

    /// Generates a 3 flag preview for HOI4 flags
    public void generateThreeFlagPreview(){
        /// Removes all flags from Node to prevent buildup of too many flags
        flagPreviewElement.getChildren().removeAll(flagPreviewElement.getChildren());
        /// Resets imageToConvert to prevent conflict and writes flag from URL
        try {
            flagPreviewElement.setSpacing(20);
            // First Flag
            BufferedImage flagPreviewLarge = ImageIO.read(imageToConvert);
            flagPreviewLarge = resizeImage(flagPreviewLarge,82,52);
            flagPreviewElement.getChildren().add(convertImageToNode(flagPreviewLarge));
            // Second Flag
            BufferedImage flagPreviewMedium = ImageIO.read(imageToConvert);
            flagPreviewMedium = resizeImage(flagPreviewMedium,41,26);
            flagPreviewElement.getChildren().add(convertImageToNode(flagPreviewMedium));
            // Third Flag
            BufferedImage flagPreviewSmall = ImageIO.read(imageToConvert);
            flagPreviewSmall = resizeImage(flagPreviewSmall,10,7);
            flagPreviewElement.getChildren().add(convertImageToNode(flagPreviewSmall));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /// Generates a single flag preview
    public void generateSingleFlagPreview(){
        /// Removes all flags from Node to prevent buildup of too many flags
        flagPreviewElement.getChildren().removeAll(flagPreviewElement.getChildren());
        /// Resets imageToConvert to prevent conflict and writes flag from URL
        try {
            BufferedImage flagPreviewLarge = ImageIO.read(imageToConvert);
            flagPreviewLarge = resizeImage(flagPreviewLarge,newFlagSpecs.getBaseFlagWidth(),newFlagSpecs.getBaseFlagHeight());
            flagPreviewElement.getChildren().add(convertImageToNode(flagPreviewLarge));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /// Converts flag image to Node to allow showing in preview Node
    public ImageView convertImageToNode(BufferedImage imageToConvert){
        BufferedImage bf = imageToConvert;
        WritableImage wr = null;
        if (bf != null) {
            wr = new WritableImage(bf.getWidth(), bf.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bf.getWidth(); x++) {
                for (int y = 0; y < bf.getHeight(); y++) {
                    pw.setArgb(x, y, bf.getRGB(x, y));
                }
            }
        }
        return new ImageView(wr);
    }

    /// Opens source flag locator window
    @FXML
    public void openFlagFolderLocator(){
        FileChooser flagFileChooser = new FileChooser();
        File selectedDirectory = flagFileChooser.showOpenDialog(null);
        if(selectedDirectory != null){
            flagFilePathTextField.setText(String.valueOf(selectedDirectory));
        }
    }

    /// Opens output flag folder locator
    public void openOutputFolderLocator(){
        DirectoryChooser outputFolderChooser = new DirectoryChooser();
        File selectedDirectory = outputFolderChooser.showDialog(null);
        if(selectedDirectory != null){
            outputFolderTextField.setText(String.valueOf(selectedDirectory));
        }
    }

    //// Resize method is used to generate previews, should stay here
    public static BufferedImage resizeImage(BufferedImage imageToResize, int width, int height)
    {
        BufferedImageOp resampledImage = new ResampleOp(width, height, ResampleOp.FILTER_LANCZOS); // A good default filter, see class documentation for more info
        return resampledImage.filter(imageToResize, null);
    }
}