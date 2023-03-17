package org.alan19;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.alan19.commands.Config;
import org.alan19.commands.SelfDestructCommand;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import pw.mihou.velen.interfaces.Velen;
import pw.mihou.velen.internals.observer.VelenObserver;
import pw.mihou.velen.internals.observer.modes.ObserverMode;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

public class Main {
    // TODO Add storage for commands when bot shuts down
    public static void main(String[] args) {
        Velen velen = Velen.builder().setDefaultPrefix("~").setDefaultCooldownTime(Duration.ZERO).build();
        new DiscordApiBuilder()
                .setToken(Config.getToken())
                .addIntents(Intent.MESSAGE_CONTENT)
                .addListener(velen)
                .login().thenAccept(api -> {
                    System.out.println(api.createBotInvite());
                    SelfDestructCommand.registerCommand(velen);
                    velen.registerAllSlashCommands(api);
                    new VelenObserver(api, ObserverMode.MASTER).observeAllServers(velen, api);
                    velen.index(true, api).join();
                });
    }
}