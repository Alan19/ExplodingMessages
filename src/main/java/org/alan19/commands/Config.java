package org.alan19.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import io.vavr.control.Try;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.NoSuchElementException;

public class Config {
    public static final String CONFIG_PATH = "config.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Expose
    private String token = "";
    @Expose
    private int secondsBeforeDeletion = 60;
    @Expose
    private int messagesBeforeDeletion = 5;

    private static Try<Config> readWriteConfig() {
        return Try.of(() -> gson.fromJson(new FileReader(CONFIG_PATH), Config.class))
                .onFailure(throwable -> Try.run(() -> {
                            FileWriter writer = new FileWriter(CONFIG_PATH);
                            gson.toJson(new Config(), writer);
                            writer.flush();
                            writer.close();
                        }).onSuccess(config -> {
                            System.out.println("You do not have a config.json file, so it has now been generated for you. Please add your Discord token and restart this program");
                            System.exit(1);
                        })
                        .onFailure(Throwable::printStackTrace));
    }

    public static String getToken() throws NoSuchElementException {
        return readWriteConfig()
                .map(config -> config.token)
                .get();
    }

    public static int getSecondsBeforeDeletion() {
        return readWriteConfig()
                .map(config -> config.secondsBeforeDeletion)
                .getOrElse(60);
    }

    public static int getMessagesBeforeDeletion() {
        return readWriteConfig()
                .map(config -> config.messagesBeforeDeletion)
                .getOrElse(5);
    }

}
