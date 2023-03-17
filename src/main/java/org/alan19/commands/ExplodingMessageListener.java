package org.alan19.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.concurrent.TimeUnit;

public class ExplodingMessageListener implements MessageCreateListener {
    private int count = 0;
    private long messageId;
    private long channelId;

    public ExplodingMessageListener(DiscordApi api, long messageId, long channelId) {
        this.messageId = messageId;
        this.channelId = channelId;
        api.getThreadPool().getScheduler().schedule(() -> api.getTextChannelById(channelId).map(textChannel -> api.getMessageById(messageId, textChannel).thenAccept(message -> message.delete("This exploding message was deleted after %d seconds has passed".formatted(count)))), Config.getSecondsBeforeDeletion(), TimeUnit.SECONDS);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        DiscordApi api = event.getApi();
        if (channelId == event.getChannel().getId()) {
            count++;
        }
        if (count >= Config.getMessagesBeforeDeletion()) {
            api.getTextChannelById(channelId).map(textChannel -> api.getMessageById(messageId, textChannel).thenAccept(message -> message.delete("This exploding message was deleted after %d messages was sent after it".formatted(count))));
        }
    }
}
