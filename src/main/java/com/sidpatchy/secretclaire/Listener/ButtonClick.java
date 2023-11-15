package com.sidpatchy.secretclaire.Listener;

import com.sidpatchy.secretclaire.Main;
import com.sidpatchy.secretclaire.MessageComponents.SantaEmbed;
import com.sidpatchy.secretclaire.MessageComponents.SantaModal;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedField;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ButtonClick implements ButtonClickListener {
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction buttonInteraction = event.getButtonInteraction();

        String buttonID = buttonInteraction.getCustomId().toLowerCase();
        User buttonAuthor = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();

        Embed embed = buttonInteraction.getMessage().getEmbeds().get(0);
        EmbedFooter footer = embed.getFooter().orElse(null);

        // Extract data from embed fields
        String rules = "";
        String theme = "";
        List<User> givers = new ArrayList<>();
        List<User> receivers = new ArrayList<>();
        for (EmbedField field : embed.getFields()) {
            String name = field.getName();
            String value = field.getValue();

            if (name.equalsIgnoreCase("rules")) {
                rules = value;
            }
            else if (name.equalsIgnoreCase("theme")) {
                theme = value;
            }
            else {
                value = value.replace("<@!", "");
                value = value.replace(">", "");
                value = value.replace(" → ", "");
                value = value.replace(name, "");

                givers.add(Main.getApi().getUserById(name).join());
                receivers.add(Main.getApi().getUserById(value).join());
            }
        }

        assert footer != null;
        HashMap<String, String> santaID = parseSantaID(footer.getText().orElse(null));

        Server server = Main.getApi().getServerById(santaID.get("serverID")).orElse(null);
        User author = Main.getApi().getUserById(santaID.get("authorID")).join();

        switch (buttonID) {
            case "rules":
                buttonInteraction.respondWithModal(message.getIdAsString(), "Update Rules",
                        SantaModal.getRulesRow()
                );

                break;
            case "theme":
                buttonInteraction.respondWithModal(message.getIdAsString(), "Update Theme",
                        SantaModal.getThemeRow()
                );

                break;
            case "send":
                for (int i = 0; i < givers.size(); i++) {
                    SantaEmbed.getSantaMessage(server, author, givers.get(i), receivers.get(i), rules, theme).send(givers.get(i));
                }

                break;
            case "test":
                buttonInteraction.acknowledge();
                SantaEmbed.getSantaMessage(server, author, givers.get(0), receivers.get(0), rules, theme).send(buttonAuthor);

                break;
            case "randomize":
                buttonInteraction.acknowledge();
                Role role = Main.getApi().getRoleById(santaID.get("roleID")).orElse(null);
                buttonInteraction.getMessage().delete();
                SantaEmbed.getHostMessage(role, buttonAuthor, rules, theme).send(buttonAuthor);

                break;
        }
    }

    public static String getSantaID(String serverID, String authorID, String roleID) {
        return serverID + ":" + authorID + ":" + roleID;
    }

    public static HashMap<String, String> parseSantaID(String id) {
        List<String> entries = Arrays.asList(StringUtils.splitPreserveAllTokens(id, ":"));

        return new HashMap<>() {{
            put("serverID", entries.get(0));
            put("authorID", entries.get(1));
            put("roleID", entries.get(2));
        }};
    }
}
