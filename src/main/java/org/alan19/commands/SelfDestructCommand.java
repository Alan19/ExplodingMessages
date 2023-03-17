package org.alan19.commands;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.util.logging.ExceptionLogger;
import pw.mihou.velen.interfaces.Velen;
import pw.mihou.velen.interfaces.VelenCommand;
import pw.mihou.velen.interfaces.VelenHybridHandler;
import pw.mihou.velen.interfaces.hybrid.event.VelenGeneralEvent;
import pw.mihou.velen.interfaces.hybrid.objects.VelenHybridArguments;
import pw.mihou.velen.interfaces.hybrid.objects.VelenOption;
import pw.mihou.velen.interfaces.hybrid.responder.VelenGeneralResponder;

public class SelfDestructCommand implements VelenHybridHandler {
    public static void registerCommand(Velen velen) {
        VelenCommand.ofHybrid("selfdestruct", "Deletes the input message after a while", velen, new SelfDestructCommand())
                .addShortcuts("sd", "killerqueen")
                .addOption(SlashCommandOption.createStringOption("message-link", "the link to the message to delete", true))
                .addFormats("selfdestruct :[message-link:of(string)]")
                .attach();
    }
    // TODO Add ephemeral messages by either switching to old handler style or fixing the responder
    @Override
    public void onEvent(VelenGeneralEvent event, VelenGeneralResponder responder, User user, VelenHybridArguments args) {
        boolean canUseCommand = user.getRoles(event.getServer().orElseThrow()).stream().anyMatch(role -> role.getAllowedPermissions().contains(PermissionType.MANAGE_MESSAGES));
        if (canUseCommand) {
            DiscordApi api = user.getApi();
            args.withName("message-link").flatMap(VelenOption::asString).ifPresent(s -> api.getMessageByLink(s).map(messageCompletableFuture -> messageCompletableFuture.thenAccept(message -> {
                message.addReaction(EmojiParser.parseToUnicode(":bomb:")).exceptionally(ExceptionLogger.get());
                responder.setContent("This message will be deleted after %d messages have been sent after it, or in %d seconds".formatted(Config.getMessagesBeforeDeletion(), Config.getSecondsBeforeDeletion())).setFlags(MessageFlag.EPHEMERAL).respond()
                        .thenAccept((message1) -> api.addListener(new ExplodingMessageListener(api, message.getId(), message.getChannel().getId(), Config.getSecondsBeforeDeletion(), Config.getMessagesBeforeDeletion())));
                ;
            })));
        } else {
            responder.setContent("You cannot use this command as you cannot delete others' messages").setFlags(MessageFlag.EPHEMERAL).respond();
        }
    }
}
