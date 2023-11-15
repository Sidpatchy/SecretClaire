package com.sidpatchy.secretclaire;

import com.sidpatchy.Robin.Discord.ParseCommands;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RegisterSlashCommands {
    private static final ParseCommands parseCommands = new ParseCommands(Main.getCommandsFile());

    public static void DeleteSlashCommands (DiscordApi api) {
        api.bulkOverwriteGlobalApplicationCommands(Set.of()).join();
    }

    /**
     * Register slash commands while feeling like you're doing it wrong no matter how you do it!
     * <p>
     * Only called on startup.
     *
     * @param api pass API into function
     */
    public static void RegisterSlashCommand(DiscordApi api) {

        // Create the command list in the help command without repeating the same thing 50 million times.
        ArrayList<SlashCommandOptionChoice> helpCommandOptions = new ArrayList<>();
        for (String s : Main.commandList) {
            helpCommandOptions.add(SlashCommandOptionChoice.create(parseCommands.getCommandName(s), parseCommands.getCommandName(s)));
        }

        Set<SlashCommandBuilder> commandsList = new HashSet<>(Arrays.asList(
                new SlashCommandBuilder().setName(parseCommands.getCommandName("help")).setDescription(parseCommands.getCommandHelp("help"))
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "command-name", "Command to get more info on", false, helpCommandOptions)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("santa")).setDescription(parseCommands.getCommandOverview("santa"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.ROLE, "Role", "Role to get users from", true))
        ));

        api.bulkOverwriteGlobalApplicationCommands(commandsList).join();
    }
}
