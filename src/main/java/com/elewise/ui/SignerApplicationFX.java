package com.elewise.ui;

import com.elewise.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static com.elewise.ui.AlertUtil.showMessage;

public class SignerApplicationFX extends Application {
    private static final Logger logger = Logger.getLogger(SignerApplicationFX.class.getName());
    private final SystemTray tray = SystemTray.getSystemTray();
    private Stage stage;
    private String tsaURL;
    private int maxFileSize;
    private Server server;
    private TrayIcon trayIcon;

    public static void main(String[] args) throws Exception {
       // SignKalkanProvider.loadProvider();
        launch(args);
    }

    public SignerApplicationFX() {
        super();
        Locale locale = Locale.getDefault();
    }

    @Override
    public void init() throws Exception {
        super.init();
        List<String> unnamed = getParameters().getUnnamed();
        if (!unnamed.isEmpty())
            tsaURL = unnamed.get(0);
        if (tsaURL == null)
            tsaURL = "http://tsp.pki.gov.kz";
        if (unnamed.size() > 1)
            this.maxFileSize = Integer.parseInt(unnamed.get(1));
        else
            this.maxFileSize = 10240;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.server = startServer();
        getParameters();
        this.stage = primaryStage;
        Platform.setImplicitExit(false);
        decorateStage(primaryStage);
        SwingUtilities.invokeLater(this::addAppToTray);
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/application.fxml"));
        //Parent root = loader.load();
        //Scene scene = new Scene(root, 750, 200);
        //primaryStage.setScene(scene);
        showMessage("APPL_NAME", "GREETING");
    }

    private void decorateStage(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Подписание ЭЦП");
        try (InputStream stream = getClass().getResourceAsStream("/images/key.png")) {
            primaryStage.getIcons().add(new Image(stream));
        }
    }

    private Server startServer() {
        Server server = new Server();
        if (!server.start()) {
            showMessage(AlertType.WARNING, "APP_ALREADY_RUNNING", "APP_ALREADY_RUNNING");
            System.exit(0);
        }
        return server;
    }

    private void addAppToTray() {
        try {
            Toolkit.getDefaultToolkit();
            BufferedImage appIcon = ImageIO.read(getClass().getResource("/images/key.png"));
            this.trayIcon = new TrayIcon(appIcon);
            trayIcon.setImageAutoSize(true);
            //trayIcon.addActionListener(e -> Platform.runLater(this::showStage));
            buildMenu(trayIcon);
            tray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStage() {
        if (!stage.isShowing()) {
            stage.show();
            stage.toFront();
        } else
            stage.hide();
    }

    private void buildMenu(TrayIcon trayIcon) {
        // Create a popup menu components
        MenuItem aboutItem = new MenuItem("О программе");
        MenuItem exitItem = new MenuItem("Выход");
        PopupMenu popup = new PopupMenu();
        popup.add(aboutItem);
        popup.add(exitItem);
        aboutItem.addActionListener(e -> Platform.runLater(() -> showMessage("ABOUT_APP", "ABOUT_APP_CONTENT")));
        exitItem.addActionListener(e -> Platform.exit());
        trayIcon.setPopupMenu(popup);
    }

    @Override
    public void stop() throws Exception {
        server.stop();
        tray.remove(trayIcon);
        super.stop();
    }
}
