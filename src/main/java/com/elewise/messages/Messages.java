package com.elewise.messages;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.logging.Logger;

public class Messages {
    private static final Logger logger = Logger.getLogger(Messages.class.getName());
    private static final String messagesFilePrefix = "/properties/messages";
    private PropertyResourceBundle resourceBundle;
    private static Messages messages;

    public static Messages getInstance(Locale locale) {
        if (messages == null) {
            synchronized (Messages.class) {
                if (messages == null)
                    messages = new Messages(locale);
            }
        }
        return messages;
    }

    private Messages(Locale locale) {
        try {
            logger.info("searching resource: " + messagesFilePrefix + ".properties");
            InputStream is = this.getClass().getResourceAsStream(messagesFilePrefix + ".properties");
//            if (locale == null) {
//                logger.info("searching resource: " + messagesFilePrefix + ".properties");
//                is = this.getClass().getResourceAsStream(messagesFilePrefix + ".properties");
//            } else {
//                logger.info("searching resource: " + messagesFilePrefix + "_" + locale.getLanguage() + ".properties");
//                is = this.getClass().getResourceAsStream(messagesFilePrefix + "_" + locale.getLanguage() + ".properties");
//                if (is == null)
//                    is = this.getClass().getResourceAsStream(messagesFilePrefix + ".properties");
//            }
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            resourceBundle = new PropertyResourceBundle(reader);
        } catch (Exception e) {
            e.printStackTrace();
            resourceBundle = null;
        }
    }

    /**
     * Ф-я возвращает значение параметра по заданному ключу
     *
     * @param key Ключ параметра
     * @return Значение параметра
     */
    public String getMessage(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getMessageArray(String key) {
        try {
            return resourceBundle.getString(key).split(",");
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String formatServerErrorMsg(String msg) {
        String[] msgs = msg.split(",");
        String res = "";
        if (msgs.length == 1)
            res = getMessage(msg.trim());
        else
            for (int i = 0; i < msgs.length; i++)
                if (!"".equals(msgs[i]))
                    res += (i + 1) + ". " + getMessage(msgs[i].trim()) + "\n";
        return res;
    }
}
