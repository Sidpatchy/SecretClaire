package com.sidpatchy.secretclaire.Listener;

import com.sidpatchy.Robin.Discord.ParseCommands;
import com.sidpatchy.secretclaire.MessageComponents.ErrorEmbed;
import com.sidpatchy.secretclaire.MessageComponents.HelpEmbed;
import com.sidpatchy.secretclaire.MessageComponents.SantaEmbed;
import com.sidpatchy.secretclaire.Main;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.io.FileNotFoundException;
import java.util.Objects;

public class SlashCommandCreate implements SlashCommandCreateListener {
    static ParseCommands parseCommands = new ParseCommands(Main.getCommandsFile());
    Logger logger = Main.getLogger();

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();
        User author = slashCommandInteraction.getUser();
        User user = slashCommandInteraction.getArgumentUserValueByName("user").orElse(author);

        if (commandName.equalsIgnoreCase(parseCommands.getCommandName("help"))) {
            String command = slashCommandInteraction.getArgumentStringValueByIndex(0).orElse("help");

            try {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(HelpEmbed.getHelp(command, user.getIdAsString()))
                        .respond();
            } catch (FileNotFoundException e) {
                Main.getLogger().error(e);
                Main.getLogger().error("There was an issue locating the commands file at some point in the chain while the help command was running, good luck!");
            }
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("santa"))) {
            Role role = slashCommandInteraction.getArgumentRoleValueByName("role").orElse(null);

            if (role == null) {
                slashCommandInteraction.createImmediateResponder().addEmbed(
                        ErrorEmbed.getError(Main.getErrorCode("RoleMissing"))
                ).respond();
                return;
            }

            if (!author.canManageRole(role)) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(ErrorEmbed.getLackingPermissions("Sorry! You don't have the permission to run this " +
                                "command. You must be able to manage the role " + role.getMentionTag() + "."))
                        .respond();
                return;
            }

            slashCommandInteraction.createImmediateResponder().addEmbed(
                    SantaEmbed.getConfirmationEmbed(author)
            ).respond();

            SantaEmbed.getHostMessage(role, author, "", "").send(author);
        }
    }
}
