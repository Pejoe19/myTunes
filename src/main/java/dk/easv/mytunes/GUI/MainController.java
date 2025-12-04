package dk.easv.mytunes.GUI;

import dk.easv.mytunes.BLL.MusicException;
import dk.easv.mytunes.Be.IndexSong;
import dk.easv.mytunes.Be.Playlist;
import dk.easv.mytunes.Be.Song;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class MainController {

    @FXML private FontIcon iconMute;
    @FXML private Slider sliderVolume;
    @FXML private Button btnDeleteSong;
    @FXML private TableView<Playlist> TvPlaylists;
    @FXML private Button btnEditSong;
    @FXML private TableColumn tblCoPLName;
    @FXML private TableColumn tblCoPLSongs;
    @FXML private TableColumn tblCoPLTime;
    @FXML private TableColumn tblCoTitle;
    @FXML private TableColumn tblCoArtist;
    @FXML private TableColumn tblCoTitle1;
    @FXML private TableColumn tblCoTime;
    @FXML private TableView<IndexSong> tvSongsOnPlaylist;
    @FXML private TableColumn<IndexSong,String> tblCoPLTitle;
    @FXML private Button btnEditPL;
    @FXML private Label lbDisplay;
    @FXML private Button btnPlay;
    @FXML private TableView<Song> tvSongs;
    @FXML private TextField txfFilterSearchBar;

    private Song selectedSong;
    private Song currentSong;
    private Playlist selectedPlaylist;
    private Model model;
    private MediaPlayer mediaPlayer;

    {
        try {
            model = new Model();
        } catch (MusicException e) {
            displayError(e);
        }
    }


    public MainController() {
    }

    public void initialize(){
        loadSongs();
        loadPlaylists();
        initializeActivePlaylist();

        btnEditPL.setOnAction(this::onEditPlaylist);
        btnPlay.setOnAction(event -> onPlay());

        tvSongs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedSong = newValue;
                btnEditSong.setDisable(false);
                btnDeleteSong.setDisable(false);

                tvSongsOnPlaylist.getSelectionModel().clearSelection();
            }
        });

        tvSongsOnPlaylist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getSong() != null) {
                selectedSong = newValue.getSong();

                tvSongs.getSelectionModel().clearSelection();
            }
        });

        sliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double newVolume = (double) newValue/100;
                setVolume(newVolume);
            }
        });
    }

    private void loadSongs() {
        tblCoTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tblCoArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        tblCoTitle1.setCellValueFactory(new PropertyValueFactory<>("category"));
        tblCoTime.setCellValueFactory(new PropertyValueFactory<>("formattedTime"));

        try {
            FilteredList<Song> filteredList = new FilteredList<>(model.loadSongs());
            txfFilterSearchBar.textProperty().addListener((observableValue, oldValue, newValue) -> {
                filteredList.setPredicate((song) -> {
                    if(newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if(song.getTitle().toLowerCase().contains(lowerCaseFilter) || song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    else {return false;}
                });
            });
            SortedList<Song> sortedSong = new SortedList<>(filteredList);
            sortedSong.comparatorProperty().bind(tvSongs.comparatorProperty());
            tvSongs.setItems(sortedSong);
        } catch (MusicException e) {
            displayError(e);
        }
    }

    private void loadPlaylists(){
        // Tells the table which properties of the playlist to show in which columns
        tblCoPLName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCoPLSongs.setCellValueFactory(new PropertyValueFactory<>("numberOfSongs"));
        tblCoPLTime.setCellValueFactory(new PropertyValueFactory<>("formattedTime"));
        try {
            // Gets the data from model
            TvPlaylists.setItems(model.loadPlaylists());
        } catch (MusicException e) {
            displayError(e);
        }
        TvPlaylists.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) ->{
            if(newValue != null) {
                try{
                    selectedPlaylist = newValue;
                    model.displayPlaylist(selectedPlaylist);
                }
                catch (Exception e){
                    displayError(e);
                }

            }
            else {
                model.clearActivePlaylist();
            }
        });
    }
    private void initializeActivePlaylist() {
        tblCoPLTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tvSongsOnPlaylist.setItems(model.initializeActivePlayList());
    }

    @FXML
    private void onNewSong(ActionEvent actionEvent) {
        try {
            openSongWindow("new", null, actionEvent);
        } catch (MusicException | IOException e) {
            displayError(e);
        }
    }

    public void createSong(Song newSong) {
        try {
            model.createSong(newSong);
            loadSongs();
            tvSongs.getSelectionModel().selectLast();
        } catch (Exception e) {
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
        if (windowType.equals("edit") && song != null){
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
        if(playlist != null) {
            if(conformationMassage("conformation massage", "do you want to delete playlist "+playlist.getName())){
                try{
                    model.deletePlaylist(playlist);
                } catch (Exception e) {
                    displayError(e);
                }
            }
        }
    }

    /**
     * a dialog to confirm something
     * @param title the title
     * @param message the message
     * @return true for yes and false for cancel
     */
    private boolean conformationMassage(String title, String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void displayError(Throwable t)
    {
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
        if(selectedSong != null) {
            if(conformationMassage("conformation massage", "do you want to delete song "+ selectedSong.getTitle())){
                try{
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

    private void onPlay() {
        if (selectedSong != null) {
            model.setCurrentlyPlayingSong(selectedSong);
            lbDisplay.setText("Now playing: " + selectedSong.getTitle() + " - " + selectedSong.getArtist());
            playMedia(selectedSong);
        } else {
            lbDisplay.setText("No song selected to play.");
        }
    }

    @FXML
    private void onClickPLSDelete() {
        Playlist selectedPlaylist = TvPlaylists.getSelectionModel().getSelectedItem();
        IndexSong selectedIndexSong = tvSongsOnPlaylist.getSelectionModel().getSelectedItem();

        if (selectedPlaylist != null && selectedIndexSong != null && selectedIndexSong.getSong() != null) {
            if (conformationMassage("Remove Song",
                    "Do you want to remove \"" + selectedIndexSong.getSong().getTitle() +
                            "\" from playlist \"" + selectedPlaylist.getName() + "\"?")) {

                try {

                    model.removeSongFromPlaylist(selectedPlaylist, selectedIndexSong);

                    model.displayPlaylist(selectedPlaylist);
                } catch (Exception e) {
                    displayError(e);
                }
            }
        } else {
            showAlert("Please select a playlist and a song to remove.");
        }
    }

    public void onSongUp(ActionEvent actionEvent) {
        moveIndex("up");
    }

    public void onSongDown(ActionEvent actionEvent) {
        moveIndex("down");
    }

    private void moveIndex(String direction) {
        // If a song on a playlist is selected in tableview
        if(!(tvSongsOnPlaylist.getSelectionModel().isEmpty())) {
            int selectedIndex = tvSongsOnPlaylist.getSelectionModel().getSelectedIndex();

            int moveToIndex;
            boolean canBeMoved;
            if (direction.equals("down")) {
                moveToIndex = selectedIndex + 1;
                canBeMoved = selectedIndex < tvSongsOnPlaylist.getItems().size() - 1;
            } else {
                moveToIndex = selectedIndex - 1;
                canBeMoved = selectedIndex > 0;
            }

            if (canBeMoved) {
                try {
                    model.switchPlaylistOrder(selectedPlaylist, selectedIndex, moveToIndex);
                } catch (MusicException e) {
                    displayError(e);
                }
                tvSongsOnPlaylist.getSelectionModel().select(moveToIndex);
            }
        }
    }

    @FXML
    private void onCloseApp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit MyTunes");
        alert.setHeaderText("Are you sure you want to close the application?");
        alert.setContentText("Any unsaved changes will be lost.");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.YES) {
            // Close the stage (the main window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void onNextS(ActionEvent event) {
        try {
            Song next = model.getNextSong();
            if (next != null) {
                model.setCurrentlyPlayingSong(next);
                lbDisplay.setText("Now playing: " + next.getTitle() + " - " + next.getArtist());
                playMedia(next);
            } else {
                lbDisplay.setText("Reached end of playlist.");
            }
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void onPrevS(ActionEvent event) {
        try {
            Song prev = model.getPreviousSong();
            if (prev != null) {
                model.setCurrentlyPlayingSong(prev);
                lbDisplay.setText("Now playing: " + prev.getTitle() + " - " + prev.getArtist());
                playMedia(prev);
            } else {
                lbDisplay.setText("At the start of the playlist.");
            }
        } catch (Exception e) {
            displayError(e);
        }
    }

    private void playMedia(Song song) {
        try {
            model.loadSongFile(song);
            if(selectedSong.getFile() != null){
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }
                Media media = new Media(song.getFile().toURL().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
                mediaPlayer.setOnEndOfMedia(() ->{ onNextS(new ActionEvent());});
            }
            else {
                onNextS(new ActionEvent());
            }
        }
        catch (Exception e) {
            displayError(e);
        }
    }

    public void onAddSongToPlaylist(ActionEvent actionEvent) {
        if(selectedSong != null && selectedPlaylist != null){
            model.addSongToPlaylist(selectedPlaylist, selectedSong);
        } else {
            showAlert("Please select a playlist and a song to add a song to the playlist.");
        }
    }

    private void setVolume(Number newValue) {
        if(mediaPlayer != null) {
            mediaPlayer.setVolume((Double) newValue);
            model.setVolume(newValue);
        }
    }

    @FXML private void onMute(ActionEvent actionEvent) {
        if(mediaPlayer != null){
            mediaPlayer.setMute(model.toogleMute());
        }
        if(model.getMute())
            iconMute.setIconLiteral("fas-volume-mute");
        else
            iconMute.setIconLiteral("fas-volume-up");
    }
}
