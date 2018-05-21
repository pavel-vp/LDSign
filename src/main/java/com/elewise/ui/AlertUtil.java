package com.elewise.ui;

import com.elewise.messages.Messages;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Locale;

public class AlertUtil {
    private final Messages messages;
    private static AlertUtil alertUtil = getInstance();
    private final Node errorDialog;

    private AlertUtil() throws IOException {
        Locale locale = Locale.getDefault();
        messages = Messages.getInstance(locale);
        errorDialog = FXMLLoader.load(getClass().getResource("/ui/error_dialog.fxml"));
    }

    private static AlertUtil getInstance() {
        if (alertUtil == null) {
            synchronized (AlertUtil.class) {
                if (alertUtil == null)
                    try {
                        alertUtil = new AlertUtil();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return alertUtil;
    }

    static void showMessage(String contentCode) {
        String content = alertUtil.messages.getMessage(contentCode);
        showMessage(AlertType.INFORMATION, content, content, null);

    }

    static void showMessage(String titleCode, String contentCode) {
        showMessage(AlertType.INFORMATION, titleCode, contentCode);
    }

    static void showMessage(AlertType alertType, String titleCode, String contentCode) {
        AlertUtil alertUtil = getInstance();
        String content = alertUtil.messages.getMessage(contentCode);
        String title = alertUtil.messages.getMessage(titleCode);
        showMessage(alertType, title, content, null);
    }

    static void showMessageWithParams(AlertType alertType, String titleCode, String contentCode, Object... params) {
        AlertUtil alertUtil = getInstance();
        String content = alertUtil.messages.getMessage(contentCode);
        content = MessageFormat.format(content, params);
        String title = alertUtil.messages.getMessage(titleCode);
        showMessage(alertType, title, content, null);
    }

    static void showExceptionDialog(String titleCode, String contentCode, Throwable throwable) {
        String title;
        if (titleCode != null)
            title = alertUtil.messages.getMessage(titleCode);
        else
            title = alertUtil.messages.getMessage("ERROR");
        String content;
        if (contentCode != null)
            content = alertUtil.messages.getMessage(contentCode);
        else
            content = throwable.getMessage();
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setExpandableContent(alertUtil.errorDialog);
        alert.setTitle(title);
        alert.setContentText(content);
        TextArea textArea = (TextArea) alert.getDialogPane().lookup("#errorMessageField");
        textArea.setText(getStackTrace(throwable));
        alert.showAndWait();
    }

    static void showMessage(AlertType alertType, String title, String content, String header) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
