package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FreeCADComponentManager extends Application {

    private ObservableList<ComponenteCAD> componentiList = FXCollections.observableArrayList();
    
    private TextField nomeFileField;
    private TextArea descrizioneArea;
    private TextField tipoField;
    private ComboBox<String> formatoCombo;
    private TextField linkImmagineField;
    private TextField percorsoArchivioField;
    
    private ImageView imagePreview;
    private Label fileInfoLabel;
    private Label statsLabel;
    private Label dimensionWarningLabel;
    
    private TableView<ComponenteCAD> tableView;
    private int editingIndex = -1;
    
    private static final String STORAGE_FILE = "componenti_cad.dat";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("FreeCAD Component Manager - Brunetti 2026");
        primaryStage.getIcons().add(new Image("file:C:/___JavaFX/CadComponetManager/JAVA.png"));
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #3498db);");
        mainLayout.setPadding(new Insets(15));

        // Header
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Center content - Vertical split
        VBox centerContent = new VBox(15);
        centerContent.setPadding(new Insets(15, 0, 0, 0));

        // Top row - Form and Preview side by side
        HBox topRow = new HBox(15);
        topRow.setAlignment(Pos.TOP_CENTER);
        
        // Form card (left side)
        VBox formCard = createFormCard();
        formCard.setPrefWidth(650);
        
        // Preview card (right side)
        VBox previewCard = createPreviewCard();
        previewCard.setPrefWidth(400);
        
        topRow.getChildren().addAll(formCard, previewCard);
        HBox.setHgrow(formCard, Priority.ALWAYS);
        
        // Bottom - Table full width
        VBox tableCard = createTableCard();
        
        centerContent.getChildren().addAll(topRow, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        
        mainLayout.setCenter(centerContent);

        // Load data
        loadFromFile();

        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1300);
        primaryStage.setMinHeight(700);
        primaryStage.show();

        updateStats();
        
        // Carica dati di esempio se non ci sono dati
        if (componentiList.isEmpty()) {
            caricaDatiEsempio();
        }
    }

    private VBox createHeader() {
        VBox header = new VBox(3);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
            "-fx-background-radius: 10;"
        );

        Label title = new Label("📦 FreeCAD Component Manager");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Gestione completa con anteprime - Brunetti 2026 -");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#f0f0f0"));

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createFormCard() {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label title = new Label("📝 Inserisci Componente");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        nomeFileField = createTextField("es: motore_elettrico", true);
        descrizioneArea = createTextArea("Descrizione dettagliata...", 2);
        
        HBox rowBox = new HBox(8);
        tipoField = createTextField("es: Motore", false);
        formatoCombo = createFormatoCombo();
        rowBox.getChildren().addAll(tipoField, formatoCombo);
        HBox.setHgrow(tipoField, Priority.ALWAYS);
        HBox.setHgrow(formatoCombo, Priority.ALWAYS);

        linkImmagineField = createTextField("https://... oppure C:\\percorso\\immagine.png", false);
        percorsoArchivioField = createTextField("C:\\CAD\\Archivio\\", false);

        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        Button aggiungiBtn = createStyledButton("➕ Aggiungi", "#667eea");
        Button nuovoBtn = createStyledButton("🔄 Nuovo", "#ecc94b");
        Button salvaBtn = createStyledButton("💾 Salva CSV", "#48bb78");
        Button caricaBtn = createStyledButton("📂 Carica CSV", "#95a5a6");

        aggiungiBtn.setOnAction(e -> aggiungiComponente());
        nuovoBtn.setOnAction(e -> resetForm());
        salvaBtn.setOnAction(e -> salvaCSV());
        caricaBtn.setOnAction(e -> caricaCSV());

        buttonBox.getChildren().addAll(aggiungiBtn, nuovoBtn, salvaBtn, caricaBtn);

        card.getChildren().addAll(
            title,
            createLabeledField("Nome File *", nomeFileField),
            createLabeledField("Descrizione *", descrizioneArea),
            createLabeledField("Tipo", tipoField),
            createLabeledField("Formato", formatoCombo),
            createLabeledField("Link Immagine", linkImmagineField),
            createLabeledField("Percorso Archivio", percorsoArchivioField),
            buttonBox
        );

        return card;
    }

    private VBox createPreviewCard() {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label title = new Label("🖼️ Anteprima Immagine");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(350, 350);
        imageContainer.setMaxSize(350, 350);
        imageContainer.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        imagePreview = new ImageView();
        imagePreview.setFitWidth(330);
        imagePreview.setFitHeight(330);
        imagePreview.setPreserveRatio(true);

        Label placeholder = new Label("🖼️\nNessuna immagine\nInserisci un link o percorso per l'anteprima");
        placeholder.setFont(Font.font("Arial", 12));
        placeholder.setTextFill(Color.web("#a0aec0"));
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setLineSpacing(5);

        imageContainer.getChildren().addAll(imagePreview, placeholder);
        imagePreview.setVisible(false);

        dimensionWarningLabel = new Label();
        dimensionWarningLabel.setStyle(
            "-fx-background-color: #fff3cd;" +
            "-fx-text-fill: #856404;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 4;" +
            "-fx-font-size: 11;"
        );
        dimensionWarningLabel.setVisible(false);

        fileInfoLabel = new Label("File: -\nFormato: -\nTipo: -\nPercorso: -");
        fileInfoLabel.setFont(Font.font("Arial", 12));
        fileInfoLabel.setStyle("-fx-padding: 8; -fx-background-color: #f8fafc; -fx-background-radius: 5;");

        // Aggiungi listener per cambiamenti nel campo link immagine
        linkImmagineField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                caricaImmagine(newVal);
            }
        });

        card.getChildren().addAll(title, imageContainer, dimensionWarningLabel, fileInfoLabel);
        return card;
    }

    private VBox createTableCard() {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📋 Catalogo Componenti");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        statsLabel = new Label("0");
        statsLabel.setStyle(
            "-fx-background-color: #4299e1;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 3 12;" +
            "-fx-background-radius: 15;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(title, statsLabel, spacer);

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(350);

        // Create columns with proper property names
        TableColumn<ComponenteCAD, String> colNome = new TableColumn<>("Nome File");
        colNome.setCellValueFactory(cellData -> cellData.getValue().nomeFileProperty());
        colNome.setPrefWidth(130);

        TableColumn<ComponenteCAD, String> colDescrizione = new TableColumn<>("Descrizione");
        colDescrizione.setCellValueFactory(cellData -> cellData.getValue().descrizioneProperty());
        colDescrizione.setPrefWidth(250);
        colDescrizione.setCellFactory(tc -> new TableCell<ComponenteCAD, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.length() > 50 ? item.substring(0, 47) + "..." : item);
                    setTooltip(new Tooltip(item));
                }
            }
        });

        TableColumn<ComponenteCAD, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        colTipo.setPrefWidth(100);

        TableColumn<ComponenteCAD, String> colFormato = new TableColumn<>("Formato");
        colFormato.setCellValueFactory(cellData -> cellData.getValue().formatoProperty());
        colFormato.setPrefWidth(80);
        colFormato.setCellFactory(tc -> new TableCell<ComponenteCAD, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: " + getFormatColor(item) + "; -fx-text-fill: white; -fx-padding: 3 6; -fx-background-radius: 10;");
                }
            }
        });

        TableColumn<ComponenteCAD, String> colImmagine = new TableColumn<>("Img");
        colImmagine.setCellValueFactory(cellData -> cellData.getValue().linkImmagineProperty());
        colImmagine.setPrefWidth(50);
        colImmagine.setCellFactory(tc -> new TableCell<ComponenteCAD, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null || item.isEmpty() ? "❌" : "✅");
            }
        });

        TableColumn<ComponenteCAD, String> colPercorso = new TableColumn<>("Percorso");
        colPercorso.setCellValueFactory(cellData -> cellData.getValue().percorsoArchivioProperty());
        colPercorso.setPrefWidth(150);
        colPercorso.setCellFactory(tc -> new TableCell<ComponenteCAD, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.length() > 20 ? "..." + item.substring(item.length() - 17) : item);
                    setTooltip(new Tooltip(item));
                }
            }
        });

        TableColumn<ComponenteCAD, Void> colAzioni = new TableColumn<>("Azioni");
        colAzioni.setPrefWidth(100);
        colAzioni.setCellFactory(param -> new TableCell<ComponenteCAD, Void>() {
            private final Button previewBtn = createIconButton("👁️", "#48bb78");
            private final Button editBtn = createIconButton("✏️", "#ecc94b");
            private final Button deleteBtn = createIconButton("🗑️", "#f56565");
            private final HBox pane = new HBox(3, previewBtn, editBtn, deleteBtn);

            {
                previewBtn.setOnAction(event -> {
                    ComponenteCAD comp = getTableView().getItems().get(getIndex());
                    anteprimaComponente(comp);
                });
                
                editBtn.setOnAction(event -> {
                    ComponenteCAD comp = getTableView().getItems().get(getIndex());
                    modificaComponente(comp, getIndex());
                });
                
                deleteBtn.setOnAction(event -> {
                    ComponenteCAD comp = getTableView().getItems().get(getIndex());
                    eliminaComponente(comp, getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableView.getColumns().addAll(colNome, colDescrizione, colTipo, colFormato, colImmagine, colPercorso, colAzioni);
        tableView.setItems(componentiList);

        card.getChildren().addAll(headerBox, tableView);
        return card;
    }

    private String getFormatColor(String formato) {
        switch (formato) {
            case "FCStd": return "#9f7aea";
            case "STEP": return "#48bb78";
            case "DWG": return "#4299e1";
            case "DXF": return "#ed8936";
            case "IGES": return "#ed64a6";
            default: return "#a0aec0";
        }
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 12;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;"
        );
        return btn;
    }

    private Button createIconButton(String icon, String color) {
        Button btn = new Button(icon);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 3 6;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 11;"
        );
        return btn;
    }

    private TextField createTextField(String prompt, boolean required) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
            "-fx-padding: 6 10;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #e1e8ed;" +
            "-fx-border-width: 1;" +
            "-fx-background-color: #f8fafc;" +
            "-fx-font-size: 12;"
        );
        return field;
    }

    private TextArea createTextArea(String prompt, int rows) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setPrefRowCount(rows);
        area.setWrapText(true);
        area.setStyle(
            "-fx-padding: 6 10;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #e1e8ed;" +
            "-fx-border-width: 1;" +
            "-fx-background-color: #f8fafc;" +
            "-fx-font-size: 12;"
        );
        return area;
    }

    private ComboBox<String> createFormatoCombo() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("FCStd", "STEP", "DWG", "DXF", "IGES", "SAT");
        combo.setPromptText("Seleziona");
        combo.setStyle(
            "-fx-padding: 3 6;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #e1e8ed;" +
            "-fx-border-width: 1;" +
            "-fx-background-color: #f8fafc;" +
            "-fx-font-size: 12;"
        );
        return combo;
    }

    private VBox createLabeledField(String labelText, Control field) {
        VBox box = new VBox(2);
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 11));
        label.setTextFill(Color.web("#34495e"));
        box.getChildren().addAll(label, field);
        return box;
    }

    private void caricaImmagine(String percorso) {
        try {
            Image img = null;
            
            // Prova come URL prima
            if (percorso.startsWith("http://") || percorso.startsWith("https://")) {
                img = new Image(percorso, true);
            } else {
                // Altrimenti prova come percorso locale
                percorso = percorso.replace("\\", "/");
                if (!percorso.startsWith("file:")) {
                    percorso = "file:///" + percorso;
                }
                img = new Image(percorso, true);
            }
            
            imagePreview.setImage(img);
            imagePreview.setVisible(true);
            
            // Mostra il placeholder finché non è caricata
            final Image finalImg = img;
            img.progressProperty().addListener((obs, old, newValue) -> {
                if (newValue.doubleValue() == 1.0 && finalImg.getWidth() > 0) {
                    imagePreview.setVisible(true);
                    if (finalImg.getWidth() != 1080 || finalImg.getHeight() != 1080) {
                        dimensionWarningLabel.setText("⚠️ Dimensioni: " + (int)finalImg.getWidth() + "x" + (int)finalImg.getHeight() + " (ottimale 1080x1080)");
                        dimensionWarningLabel.setVisible(true);
                    } else {
                        dimensionWarningLabel.setVisible(false);
                    }
                }
            });
            
            img.errorProperty().addListener((obs, old, newValue) -> {
                if (newValue) {
                    imagePreview.setVisible(false);
                    dimensionWarningLabel.setText("❌ Errore nel caricamento dell'immagine");
                    dimensionWarningLabel.setVisible(true);
                }
            });
            
        } catch (Exception e) {
            imagePreview.setVisible(false);
            dimensionWarningLabel.setText("❌ Errore: " + e.getMessage());
            dimensionWarningLabel.setVisible(true);
        }
    }

    private void anteprimaComponente(ComponenteCAD comp) {
        fileInfoLabel.setText(
            "File: " + comp.getNomeFile() + "\n" +
            "Formato: " + comp.getFormato() + "\n" +
            "Tipo: " + (comp.getTipo() != null ? comp.getTipo() : "-") + "\n" +
            "Percorso: " + (comp.getPercorsoArchivio() != null ? abbreviatePath(comp.getPercorsoArchivio()) : "-")
        );
        
        if (comp.getLinkImmagine() != null && !comp.getLinkImmagine().isEmpty()) {
            caricaImmagine(comp.getLinkImmagine());
        } else {
            imagePreview.setVisible(false);
            dimensionWarningLabel.setVisible(false);
        }
    }

    private String abbreviatePath(String path) {
        if (path == null || path.length() <= 30) return path;
        return "..." + path.substring(path.length() - 27);
    }

    private void modificaComponente(ComponenteCAD comp, int index) {
        nomeFileField.setText(comp.getNomeFile());
        descrizioneArea.setText(comp.getDescrizione());
        tipoField.setText(comp.getTipo());
        formatoCombo.setValue(comp.getFormato());
        linkImmagineField.setText(comp.getLinkImmagine());
        percorsoArchivioField.setText(comp.getPercorsoArchivio());
        
        editingIndex = index;
        anteprimaComponente(comp);
    }

    private void eliminaComponente(ComponenteCAD comp, int index) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText("Eliminare " + comp.getNomeFile() + "?");
        confirm.setContentText("Questa operazione non può essere annullata.");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            componentiList.remove(index);
            saveToFile();
            updateStats();
            if (editingIndex == index) resetForm();
        }
    }

    private void aggiungiComponente() {
        String nomeFile = nomeFileField.getText().trim();
        String descrizione = descrizioneArea.getText().trim();
        String tipo = tipoField.getText().trim();
        String formato = formatoCombo.getValue();
        String linkImmagine = linkImmagineField.getText().trim();
        String percorso = percorsoArchivioField.getText().trim();

        if (nomeFile.isEmpty() || descrizione.isEmpty() || formato == null) {
            showAlert("Errore", "Compila tutti i campi obbligatori (Nome File, Descrizione e Formato)!");
            return;
        }

        ComponenteCAD comp = new ComponenteCAD(
            nomeFile, descrizione, tipo, formato, 
            linkImmagine, percorso, LocalDateTime.now().toString()
        );

        if (editingIndex >= 0 && editingIndex < componentiList.size()) {
            componentiList.set(editingIndex, comp);
            editingIndex = -1;
        } else {
            componentiList.add(comp);
        }

        saveToFile();
        updateStats();
        resetForm();
        showAlert("Successo", "Componente salvato con successo!");
    }

    private void resetForm() {
        nomeFileField.clear();
        descrizioneArea.clear();
        tipoField.clear();
        formatoCombo.setValue(null);
        linkImmagineField.clear();
        percorsoArchivioField.clear();
        
        imagePreview.setImage(null);
        imagePreview.setVisible(false);
        dimensionWarningLabel.setVisible(false);
        
        fileInfoLabel.setText("File: -\nFormato: -\nTipo: -\nPercorso: -");
        editingIndex = -1;
    }

    private void updateStats() {
        statsLabel.setText(String.valueOf(componentiList.size()));
    }

    private void salvaCSV() {
        if (componentiList.isEmpty()) {
            showAlert("Attenzione", "Nessun componente da salvare!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva file CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        fileChooser.setInitialFileName("componenti_cad.csv");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                writer.println("Nome File,Descrizione,Tipo,Formato,Link Immagine,Percorso Archivio,Data Inserimento");
                
                for (ComponenteCAD comp : componentiList) {
                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        escapeCSV(comp.getNomeFile()),
                        escapeCSV(comp.getDescrizione()),
                        escapeCSV(comp.getTipo() != null ? comp.getTipo() : ""),
                        escapeCSV(comp.getFormato()),
                        escapeCSV(comp.getLinkImmagine() != null ? comp.getLinkImmagine() : ""),
                        escapeCSV(comp.getPercorsoArchivio() != null ? comp.getPercorsoArchivio() : ""),
                        escapeCSV(comp.getDataInserimento())
                    );
                }
                
                showAlert("Successo", "File CSV salvato con successo!\n" + componentiList.size() + " record esportati.");
            } catch (IOException e) {
                showAlert("Errore", "Errore durante il salvataggio del file!");
            }
        }
    }

    private void caricaCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Carica file CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            caricaDaFile(file);
        }
    }

    private void caricaDaFile(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            boolean firstLine = true;
            List<ComponenteCAD> nuovi = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                if (line.trim().isEmpty()) continue;
                
                String[] values = parseCSVLine(line);
                if (values.length >= 4) {
                    ComponenteCAD comp = new ComponenteCAD(
                        values[0], values[1], 
                        values.length > 2 ? values[2] : "",
                        values.length > 3 ? values[3] : "",
                        values.length > 4 ? values[4] : "",
                        values.length > 5 ? values[5] : "",
                        values.length > 6 ? values[6] : LocalDateTime.now().toString()
                    );
                    nuovi.add(comp);
                }
            }

            if (!nuovi.isEmpty()) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Caricamento CSV");
                confirm.setHeaderText("Trovati " + nuovi.size() + " record");
                confirm.setContentText("Premi OK per SOVRASCRIVERE, ANNULLA per ACCODARE");

                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    componentiList.setAll(nuovi);
                } else {
                    componentiList.addAll(nuovi);
                }

                saveToFile();
                updateStats();
                showAlert("Successo", "Caricati " + nuovi.size() + " record con successo!");
                
                // Mostra il primo componente in anteprima
                if (!componentiList.isEmpty()) {
                    anteprimaComponente(componentiList.get(0));
                }
            } else {
                showAlert("Attenzione", "Nessun record valido trovato nel file.");
            }

        } catch (IOException e) {
            showAlert("Errore", "Errore nella lettura del file!");
        }
    }

    private void caricaDatiEsempio() {
        List<ComponenteCAD> esempi = new ArrayList<>();
        esempi.add(new ComponenteCAD("PARTICOLARE-011G", "TUBO MECCANICO Ø51 x 10", "Tubo", "FCStd", "", "C:\\Doc\\Forni\\Disegni", LocalDateTime.now().toString()));
        esempi.add(new ComponenteCAD("PARTICOLARE-011H", "PIATTO 80x60 SPESSORE 15", "Piatto", "STEP", "", "C:\\Doc\\Forni\\Disegni", LocalDateTime.now().toString()));
        esempi.add(new ComponenteCAD("PARTICOLARE-011I", "PIATTO 80x185 SPESSORE 15", "Piatto", "DWG", "", "C:\\Doc\\Forni\\Disegni", LocalDateTime.now().toString()));
        esempi.add(new ComponenteCAD("PARTICOLARE-012", "PERNO FORCELLE Ø30 x 118", "Perno", "FCStd", "", "C:\\Doc\\Forni\\Disegni", LocalDateTime.now().toString()));
        esempi.add(new ComponenteCAD("PARTICOLARE-013", "FORCELLA SNODO CILINDRO", "Forcella", "STEP", "", "C:\\Doc\\Forni\\Disegni", LocalDateTime.now().toString()));
        
        // Aggiungi un esempio con immagine (usa un'immagine di placeholder online)
        esempi.add(new ComponenteCAD("ESEMPIO-CON-IMMAGINE", "Componente con anteprima", "Esempio", "FCStd", "https://via.placeholder.com/1080x1080/2563eb/ffffff?text=Anteprima+CAD", "C:\\Doc\\Forni\\Disegni", LocalDateTime.now().toString()));
        
        componentiList.addAll(esempi);
        saveToFile();
        updateStats();
    }

    private String escapeCSV(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_FILE))) {
            oos.writeObject(new ArrayList<>(componentiList));
        } catch (IOException e) {
            System.err.println("Errore salvataggio: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(STORAGE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<ComponenteCAD> loaded = (List<ComponenteCAD>) ois.readObject();
                componentiList.setAll(loaded);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Errore caricamento: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

// ComponenteCAD class with JavaFX properties
class ComponenteCAD implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String nomeFile;
    private final String descrizione;
    private final String tipo;
    private final String formato;
    private final String linkImmagine;
    private final String percorsoArchivio;
    private final String dataInserimento;

    public ComponenteCAD(String nomeFile, String descrizione, String tipo, String formato, 
                        String linkImmagine, String percorsoArchivio, String dataInserimento) {
        this.nomeFile = nomeFile;
        this.descrizione = descrizione;
        this.tipo = tipo;
        this.formato = formato;
        this.linkImmagine = linkImmagine;
        this.percorsoArchivio = percorsoArchivio;
        this.dataInserimento = dataInserimento;
    }

    public String getNomeFile() { return nomeFile; }
    public String getDescrizione() { return descrizione; }
    public String getTipo() { return tipo; }
    public String getFormato() { return formato; }
    public String getLinkImmagine() { return linkImmagine; }
    public String getPercorsoArchivio() { return percorsoArchivio; }
    public String getDataInserimento() { return dataInserimento; }
    
    // JavaFX property methods for TableView
    public javafx.beans.property.SimpleStringProperty nomeFileProperty() {
        return new javafx.beans.property.SimpleStringProperty(nomeFile);
    }
    
    public javafx.beans.property.SimpleStringProperty descrizioneProperty() {
        return new javafx.beans.property.SimpleStringProperty(descrizione);
    }
    
    public javafx.beans.property.SimpleStringProperty tipoProperty() {
        return new javafx.beans.property.SimpleStringProperty(tipo != null ? tipo : "");
    }
    
    public javafx.beans.property.SimpleStringProperty formatoProperty() {
        return new javafx.beans.property.SimpleStringProperty(formato);
    }
    
    public javafx.beans.property.SimpleStringProperty linkImmagineProperty() {
        return new javafx.beans.property.SimpleStringProperty(linkImmagine != null ? linkImmagine : "");
    }
    
    public javafx.beans.property.SimpleStringProperty percorsoArchivioProperty() {
        return new javafx.beans.property.SimpleStringProperty(percorsoArchivio != null ? percorsoArchivio : "");
    }
}