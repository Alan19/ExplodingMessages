package org.alan19.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.concurrent.TimeUnit;

public class ExplodingMessageListener implements MessageCreateListener {
    private int count = 0;
    private final long messageId;
    private final long channelId;
    private final int messagesBeforeDeletion;

    public ExplodingMessageListener(DiscordApi api, long messageId, long channelId, int secondsBeforeDeletion, int messagesBeforeDeletion) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.messagesBeforeDeletion = messagesBeforeDeletion;
        api.getThreadPool().getScheduler().schedule(() -> api.getTextChannelById(channelId).map(textChannel -> api.getMessageById(messageId, textChannel).thenAccept(message -> message.delete("This exploding message was deleted after %d seconds has passed".formatted(count)))), secondsBeforeDeletion, TimeUnit.SECONDS);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        DiscordApi api = event.getApi();
        if (channelId == event.getChannel().getId()) {
            count++;
        }
        if (count >= messagesBeforeDeletion) {
            api.getTextChannelById(channelId).map(textChannel -> api.getMessageById(messageId, textChannel).thenAccept(message -> message.delete("This exploding message was deleted after %d messages was sent after it".formatted(count))));
        }
    }
}
