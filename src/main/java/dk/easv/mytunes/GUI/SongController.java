package dk.easv.mytunes.GUI;

import dk.easv.mytunes.Be.Song;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SongController {

    @FXML private TextField txtFTitle;
    @FXML private TextField txtFArtist;
    @FXML private ComboBox cbCategory;
    @FXML private TextField txtFTime;
    @FXML private TextField txtFFile;

    private MainController parent;
    private boolean editMode = false;
    private int editId;

    public void init(Song song) {
        if(editMode){
            txtFTitle.setText(song.getTitle());
            txtFArtist.setText(song.getArtist());
            cbCategory.setValue(song.getCategory());
            txtFFile.setText(song.getFilePath());
            txtFTime.setText(song.getFormattedTime());
            editId = song.getId();
        }
    }

    public void setParent(MainController mainController) {
        this.parent = mainController;
    }

    public void setEditMode() {
        editMode = true;
    }

    public void onCancel(ActionEvent actionEvent) {
        closeWindow(actionEvent);
    }

    public void onSave(ActionEvent actionEvent) {
        if(editMode){
            // Edit Mode.
            String timeAsString = txtFTime.getText();
            String[] timeParts = timeAsString.split("\\.");
            int timeInSeconds = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
            Song song = new Song(editId, txtFTitle.getText(), txtFArtist.getText(), cbCategory.getValue().toString(), timeInSeconds, txtFFile.getText());
            parent.updateSong(song);
        } else {
            // Create new song.
            String timeAsString = txtFTime.getText();
            String[] timeParts = timeAsString.split("\\.");
            int timeInSeconds = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);

            Song newSong = new Song(
                    txtFTitle.getText(),
                    txtFArtist.getText(),
                    cbCategory.getValue().toString(),
                    timeInSeconds,
                    txtFFile.getText()
            );
            parent.createSong(newSong);
        }
        closeWindow(actionEvent);
    }

    public void closeWindow(ActionEvent actionEvent){
        Button btn = (Button) actionEvent.getSource();
        Stage window = (Stage) btn.getScene().getWindow();
        window.close();
    }
}
