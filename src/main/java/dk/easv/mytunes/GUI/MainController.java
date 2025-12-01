package dk.easv.mytunes.GUI;

import dk.easv.mytunes.BLL.MusicException;
import dk.easv.mytunes.Be.Playlist;
import javafx.event.ActionEvent;
import dk.easv.mytunes.Be.Song;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import java.util.Optional;

public class MainController {

    @FXML
    private TableView<Playlist> TvPlaylists;
    @FXML
    private Button btnEditSong;
    @FXML
    private TableColumn tblCoPLName;
    @FXML
    private TableColumn tblCoPLSongs;
    @FXML
    private TableColumn tblCoPLTime;
    @FXML
    private TableView tvSongs;
    @FXML
    private TableColumn tblCoTitle;
    @FXML
    private TableColumn tblCoArtist;
    @FXML
    private TableColumn tblCoTitle1;
    @FXML
    private TableColumn tblCoTime;
    @FXML
    private Button btnEditPL;

    private Model model;

    {
        try {
            model = new Model();
        } catch (MusicException e) {
            displayError(e);
        }
    }

    private Song selectedSong;

    public MainController() {
    }

    public void initialize() {
        loadSongs();
        loadPlaylists();
        btnEditPL.setOnAction(this::onEditPlaylist);

        tvSongs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedSong = (Song) newValue;
                btnEditSong.setDisable(false);
            }
        });
    }

    private void loadSongs() {
        tblCoTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tblCoArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        tblCoTitle1.setCellValueFactory(new PropertyValueFactory<>("category"));
        tblCoTime.setCellValueFactory(new PropertyValueFactory<>("formattedTime"));

        try {
            tvSongs.setItems(model.loadSongs());
        } catch (MusicException e) {
            displayError(e);
        }
    }

    private void loadPlaylists() {
        // Tells the table which properties of the playlist to show in which columns
        tblCoPLName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCoPLSongs.setCellValueFactory(new PropertyValueFactory<>("numberOfSongs"));
        tblCoPLTime.setCellValueFactory(new PropertyValueFactory<>("playTime"));
        try {
            // Gets the data from model
            TvPlaylists.setItems(model.loadPlaylists());
        } catch (MusicException e) {
            displayError(e);
        }
    }


    @FXML
    private void editSong(ActionEvent actionEvent) {
        try {
            openSongWindow("edit", selectedSong, actionEvent);
        } catch (MusicException | IOException e) {
            displayError(e);
        }
    }

    public void openSongWindow(String windowType, Song song, ActionEvent actionEvent) throws MusicException, IOException {
        // Loads the new fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/dk/easv/mytunes/NewEditSong.fxml"));
        Scene scene = new Scene(loader.load());

        // Set this controller as a parent controller for the new controller
        SongController songController = loader.getController();
        songController.setParent(this);

        // If the window is used to edit a song, then setup editmode and load the data for the song
        if (windowType.equals("edit") && song != null) {
            songController.setEditMode();
            songController.init(song);
        }

        Stage stage = new Stage();
        stage.setScene(scene);

        // Locks the old window while the new window is open
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(((Node) actionEvent.getSource()).getScene().getWindow());
        stage.setResizable(false);

        stage.show();
    }


    @FXML
    private void onDeletePlaylist(ActionEvent actionEvent) {
        Playlist playlist = TvPlaylists.getSelectionModel().getSelectedItem();
        if (playlist != null) {
            if (conformationMassage("conformation massage", "do you want to delete playlist " + playlist.getName())) {
                try {
                    model.deletePlaylist(playlist);
                } catch (Exception e) {
                    displayError(e);
                }
            }
        }
    }

    /**
     * a dialog to confirm something
     *
     * @param title   the title
     * @param message the message
     * @return true for yes and false for cancel
     */
    private boolean conformationMassage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    public void updateSong(Song song) {
        try {
            int placement = model.updateSong(song);
            tvSongs.getSelectionModel().select(placement);
        } catch (Exception e) {
            displayError(e);
        }
    }

    public void onDeleteSong(ActionEvent actionEvent) {
        if (selectedSong != null) {
            if (conformationMassage("conformation massage", "do you want to delete song " + selectedSong.getTitle())) {
                try {
                    model.deleteSong(selectedSong);
                } catch (Exception e) {
                    displayError(e);
                }
            }
        }
    }

    @FXML
    private void onEditPlaylist(ActionEvent actionEvent) {
        Playlist selected = TvPlaylists.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a playlist to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/NewEditPlaylist.fxml"));
            Scene scene = new Scene(loader.load());

            PlaylistController controller = loader.getController();
            controller.setParent(this);
            controller.init(selected);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Edit Playlist");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node) actionEvent.getSource()).getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

            // Refresh playlists table after dialog closes
            loadPlaylists();

        } catch (IOException e) {
            displayError(e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updatePlaylist(Playlist playlist) {
        try {
            model.updatePlaylist(playlist);
        } catch (Exception e) {
            displayError(e);
        }
    }

    public void createSong(Song newSong) {
        try {
            model.createSong(newSong);
            loadSongs();
            tvSongs.getSelectionModel().selectLast();
        } catch (Exception e)  {
            displayError(e);
        }
    }

    @FXML
    private void onNewSong(ActionEvent actionEvent) throws MusicException,  IOException {
        try {
            openSongWindow("new", null,actionEvent);
        }
        catch (Exception e) {
            displayError(e);
        }
    }
}

