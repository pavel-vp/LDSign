package com.elewise.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.util.Callback;

import java.io.IOException;

public class PasswordDialog extends Dialog<String> {
    private PasswordField passwordField;

    public PasswordDialog() {
        super();
        setContent();
        final ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        passwordField = (PasswordField) getDialogPane().lookup("#passwordField");
        setResultConverter(new Callback<ButtonType, String>() {
            @Override
            public String call(ButtonType param) {
                if (param == okButtonType)
                    return passwordField.getText();
                return null;
            }
        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                passwordField.requestFocus();
            }
        });
    }

    private void setContent() {
        try {
            Node node = FXMLLoader.load(getClass().getResource("/ui/password_prompt.fxml"));
            getDialogPane().setContent(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}