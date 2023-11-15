package com.sidpatchy.secretclaire.MessageComponents;

import com.sidpatchy.Robin.Discord.ParseCommands;
import com.sidpatchy.secretclaire.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

public class HelpEmbed {

    private static final ParseCommands commands = new ParseCommands(Main.getCommandsFile());
    public static EmbedBuilder getHelp(String commandName, String userID) throws FileNotFoundException {
        List<String> commandsList = Main.getCommandList();

        // Create HashMaps for help command
        HashMap<String, HashMap<String, String>> allCommands = new HashMap<String, HashMap<String, String>>();
        HashMap<String, HashMap<String, String>> regularCommands = new HashMap<String, HashMap<String, String>>();

        for (String s : commandsList) {
            regularCommands.put(s, commands.get(s));
        }

        allCommands.putAll(regularCommands);

        // Commands list
        if (commandName.equalsIgnoreCase("help")) {
            StringBuilder glob = new StringBuilder("```");
            for (String s : commandsList) {
                if (glob.toString().equalsIgnoreCase("```")) {
                    glob.append(commands.getCommandName(s));
                } else {
                    glob.append(", ")
                            .append(commands.getCommandName(s));
                }
            }
            glob.append("```");

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Main.getColor())
                    .addField("Commands", glob.toString(), false);

            return embed;
        }
        // Command details
        else {
            if (allCommands.get(commandName) == null) {
                String errorCode = Main.getErrorCode("help_command");
                Main.getLogger().error("Unable to locate command \"" + commandName + "\" for help command. Error code: " + errorCode);
                return ErrorEmbed.getError(errorCode);
            } else {
                return new EmbedBuilder()
                        .setColor(Main.getColor())
                        .setAuthor(commandName.toUpperCase())
                        .setDescription(allCommands.get(commandName).get("help"))
                        .addField("Command", "Usage\n" + "```" + allCommands.get(commandName).get("usage") + "```");
            }
        }
    }
}