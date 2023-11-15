package com.sidpatchy.secretclaire.Listener;

import com.sidpatchy.secretclaire.Main;
import com.sidpatchy.secretclaire.MessageComponents.SantaEmbed;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedField;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.ModalInteraction;
import org.javacord.api.listener.interaction.ModalSubmitListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModalSubmit implements ModalSubmitListener {

    @Override
    public void onModalSubmit(ModalSubmitEvent event) {
        ModalInteraction modalInteraction = event.getModalInteraction();

        User user = modalInteraction.getUser();
        String modalID = modalInteraction.getCustomId();

        Main.getLogger().debug(modalID);

        modalInteraction.createImmediateResponder().respond();
        Message message = Main.getApi().getCachedMessageById(modalID).orElse(null);
        assert message != null;

        Embed embed = message.getEmbeds().get(0);
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
        HashMap<String, String> santaID = ButtonClick.parseSantaID(footer.getText().orElse(null));

        rules = modalInteraction.getTextInputValueByCustomId("rules-row").orElse(rules);
        theme = modalInteraction.getTextInputValueByCustomId("theme-row").orElse(theme);

        Role role = Main.getApi().getRoleById(santaID.get("roleID")).orElse(null);
        assert role != null;

        SantaEmbed.getHostMessage(role, user, rules, theme).send(user);
        message.delete();
    }
}
