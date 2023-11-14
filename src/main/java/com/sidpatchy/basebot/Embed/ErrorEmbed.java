package com.sidpatchy.basebot.Embed;

import com.sidpatchy.basebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ErrorEmbed {

    public static EmbedBuilder getError(String errorCode) {

        return new EmbedBuilder()
                .setColor(Main.getErrorColor())
                .setAuthor("ERROR")
                .setDescription("It appears that I've encountered an error, oops! Please try running the command once more and if that doesn't work, join my [Discord server](https://example.com/) and let us know about the issue."
                        + "\n\nPlease include the following error code: " + errorCode);
    }

    public static EmbedBuilder getError(String errorCode, String customMessage) {
        return getError(errorCode).setDescription(customMessage + "\n\nPlease try running the command once more and if that doesn't work, join my [Discord server](https://example.com/) and let us know about the issue."
                + "\n\nPlease include the following error code: " + errorCode);
    }

    public static EmbedBuilder getCustomError(String errorCode, String message) {
        return getError(errorCode).setDescription(message);
    }

    public static EmbedBuilder getLackingPermissions(String message) {
        return getCustomError(Main.getErrorCode(Main.getErrorCode("noPerms")), message);
    }
}
