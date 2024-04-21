package com.sidpatchy.secretclaire.MessageComponents;

import com.sidpatchy.secretclaire.Listener.ButtonClick;
import com.sidpatchy.secretclaire.Main;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.*;

public class SantaEmbed {

    public static EmbedBuilder getConfirmationEmbed(User author) {
        return new EmbedBuilder()
                .setColor(Main.getErrorColor())
                .setAuthor("SecretClaire", "", "https://cdn.discordapp.com/attachments/595707737669894147/908187536587894844/santuelson.png")
                .setDescription("Confirmed! I've sent you a direct message. Please continue there.");
    }

    /**
     * Method for constructing the message to send to the host of the exchange.
     *
     * @param role The group of users participating
     * @param author Author of the command.
     * @param rules Rules for the exchange, seperated by \n.
     * @param theme Theme for the exchange.
     * @return Message with components
     */
    public static MessageBuilder getHostMessage(Role role, User author, String rules, String theme) {
        Set<User> users =  role.getUsers();
        Server server = role.getServer();

        MessageBuilder message = new MessageBuilder();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor())
                .setAuthor("SecretClaire", "", "https://cdn.discordapp.com/attachments/595707737669894147/908187536587894844/santuelson.png")
                .setFooter(ButtonClick.getSantaID(server.getIdAsString(), author.getIdAsString(), role.getIdAsString()), server.getIcon().orElse(null));

        if (!theme.isEmpty()) {
            embed.addField("Theme", theme, false);
        }

        if (!rules.isEmpty()) {
            embed.addField("Rules", rules, false);
        }

        HashMap<User, User> santaList = assignSecretSanta(users);

        for (Map.Entry<User, User> userPair : santaList.entrySet()) {
            User giver = userPair.getKey();
            User receiver = userPair.getValue();

            embed.addField(giver.getIdAsString(), giver.getNicknameMentionTag() + " → " + receiver.getNicknameMentionTag(), false);
        }

        ActionRow actionRow = ActionRow.of(
                Button.primary("rules", "Add rules"),
                Button.primary("theme", "Add a theme"),
                Button.danger("send", "Send messages"),
                Button.success("test", "Send sample"),
                Button.secondary("randomize", "Re-randomize"));

        message.addEmbed(embed);
        message.addComponents(actionRow);
        return message;
    }

    public static MessageBuilder getSantaMessage(Server server, User author, User giver, User receiver, String rules, String theme) {

        MessageBuilder message = new MessageBuilder();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor())
                .setAuthor("SecretClaire", "", "https://cdn.discordapp.com/attachments/595707737669894147/908187536587894844/santuelson.png")
                .setFooter("Sent by " + author.getName(), author.getAvatar());

        if (!theme.isEmpty()) {
            embed.addField("Theme", theme, false);
        }

        if (!rules.isEmpty()) {
            embed.addField("Rules", rules, false);
        }

        embed.setDescription("Ho! Ho! Ho! You have received **" + receiver.getNickname(server) + "** in the " + server.getName() + " Secret Santa!");

        message.addEmbed(embed);

        return message;
    }

    /**
     * Method implementing a simple selected-cycle approach for pairing users.
     *
     * @param participants the set of users participating in the exchange
     * @return A shuffled hashmap of paired up users
     */
    private static HashMap<User, User> assignSecretSanta(Set<User> participants) {
        List<User> userList = new ArrayList<>(participants);

        // Shuffle the list to ensure random assignment
        Collections.shuffle(userList);

        HashMap<User, User> users = new HashMap<>(); //giver, receiver

        // Creating a directed cycle
        for (int i = 0; i < userList.size(); i++) {
            User giver = userList.get(i);
            User receiver = userList.get((i + 1) % userList.size());

            // Assign the receiver to the giver here
            users.put(giver, receiver);
        }

        return users;
    }
}
