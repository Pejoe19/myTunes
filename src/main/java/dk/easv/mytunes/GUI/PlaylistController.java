package dk.easv.mytunes.GUI;

import dk.easv.mytunes.Be.Playlist;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlaylistController {

    @FXML private TextField txtNewPLName;

    private Playlist playlistToEdit;
    private MainController parent;
    private boolean editMode = false;

    public void init(Playlist playlist) {
        if (playlist != null) {
            this.playlistToEdit = playlist;
            editMode = true;
            txtNewPLName.setText(playlist.getName());
        }
    }

    public void setParent(MainController parent) {
        this.parent = parent;
    }

    public void setEditMode() {
        editMode = true;
    }

    @FXML
    private void onClickSave(ActionEvent event) {
        String newName = txtNewPLName.getText().trim();
        if (newName.isEmpty()) {
            showAlert("Playlist name cannot be empty");
            return;
        }

        if (editMode) {
            playlistToEdit.setName(newName);
            parent.updatePlaylist(playlistToEdit);
        } else {
            Playlist playlist = new Playlist(newName);
            parent.createPlaylist(playlist);
        }
        closeWindow();
    }

    @FXML
    private void onClickCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtNewPLName.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
