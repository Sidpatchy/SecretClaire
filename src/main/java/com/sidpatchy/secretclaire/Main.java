package com.sidpatchy.secretclaire;

import com.sidpatchy.Robin.Discord.ParseCommands;
import com.sidpatchy.Robin.Exception.InvalidConfigurationException;
import com.sidpatchy.Robin.File.ResourceLoader;
import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.secretclaire.Listener.ButtonClick;
import com.sidpatchy.secretclaire.Listener.ModalSubmit;
import com.sidpatchy.secretclaire.Listener.SlashCommandCreate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * BaseBot
 * Copyright (C) 2023  Sidpatchy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * @since November 2021
 * @version 2.0.0
 * @author Sidpatchy
 */
public class Main {

    // Discord API
    private static DiscordApi api;

    private static final long startMillis = System.currentTimeMillis();

    // Related to ClaireData API
    private static String apiPath;
    private static String apiUser;
    private static String apiPassword;

    // Default values for users and guilds when creating them
    private static Map<String, Object> userDefaults;
    private static Map<String, Object> guildDefaults;

    // Various parameters extracted from config files
    private static String botName;
    private static String color;
    private static String errorColor;

    // Commands
    private static final Logger logger = LogManager.getLogger(Main.class);

    // Related to configuration files
    private static final String configFile = "config.yml";
    private static final String commandsFile = "commands.yml";
    private static RobinConfiguration config;
    private static ParseCommands commands;

    public static List<String> commandList = Arrays.asList("help", "santa");

    public static void main(String[] args) throws InvalidConfigurationException {
        logger.info("Starting...");

        // Make sure required resources are loaded
        ResourceLoader loader = new ResourceLoader();
        loader.saveResource(configFile, false);
        loader.saveResource(commandsFile, false);

        // Init config handlers
        config = new RobinConfiguration("config/" + configFile);
        commands = new ParseCommands("config/" + commandsFile);

        config.load();

        // Read data from config file
        String token = config.getString("token");
        Integer current_shard = config.getInt("current_shard");
        Integer total_shards = config.getInt("total_shards");
        String video_url = config.getString("video_url");

        extractParametersFromConfig(true);

        api = DiscordLogin(token, current_shard, total_shards);

        if (api == null) {
            System.exit(2);
        }
        else {
            logger.info("Successfully connected to Discord on shard " + current_shard + " with a total shard count of " + total_shards);
        }

        // Set the bot's activity
        api.updateActivity("SecretClaire v2.0.0", video_url);

        // Register slash commands
        registerSlashCommands();

        // Register Command-related listeners
        api.addSlashCommandCreateListener(new SlashCommandCreate());
        api.addButtonClickListener(new ButtonClick());
        api.addModalSubmitListener(new ModalSubmit());

        logger.info("Done loading! (" + (System.currentTimeMillis() - startMillis) + "ms)");
    }

    // Connect to Discord and create an API object
    private static DiscordApi DiscordLogin(String token, Integer current_shard, Integer total_shards) {
        if (token == null || token.isEmpty()) {
            logger.fatal("Token can't be null or empty. Check your config file!");
            System.exit(1);
        }
        else if (current_shard == null || total_shards == null) {
            logger.fatal("Shard config is empty, check your config file!");
            System.exit(3);
        }

        try {
            // Connect to Discord
            logger.info("Attempting discord login");
            return new DiscordApiBuilder()
                    .setToken(token)
                    .setAllIntents()
                    .setCurrentShard(current_shard)
                    .setTotalShards(total_shards)
                    .login().join();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.fatal(e.toString());
            logger.fatal("Unable to log in to Discord. Aborting startup!");
        }
        return null;
    }

    // Extract parameters from the config.yml file, update the config if applicable.
    @SuppressWarnings("unchecked")
    public static void extractParametersFromConfig(boolean updateOutdatedConfigs) {
        logger.info("Loading configuration files...");

        try {
            botName = config.getString("botName");
            color = config.getString("color");
            errorColor = config.getString("errorColor");
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("There was an error while extracting parameters from the config. This isn't fatal but there's a good chance things will be very broken.");
        }

    }

    // Handle the registry of slash commands and any errors associated.
    public static void registerSlashCommands() {
        try {
            RegisterSlashCommands.RegisterSlashCommand(api);
            logger.info("Slash commands registered successfully!");
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            logger.fatal("There was an error while registering slash commands. There's a pretty good chance it's related to an uncaught issue with the commands.yml file, trying to read all commands and printing out results.");
            for (String s : Main.commandList) {
                logger.fatal(commands.getCommandName(s));
            }
            logger.fatal("If the above list looks incomplete or generates another error, check your commands.yml file!");
            System.exit(4);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.fatal("There was a fatal error while registering slash commands.");
            System.exit(5);
        }
    }

    // Getters
    public static Color getColor() { return Color.decode(color); }

    public static Color getErrorColor() { return Color.decode(errorColor); }

    public static String getConfigFile() { return configFile; }

    public static String getCommandsFile() { return "config/" + commandsFile; }

    public static Logger getLogger() { return logger; }

    public static String getErrorCode(String descriptor) {
        return descriptor + ":" + api.getCurrentShard() + ":" + api.getTotalShards() + ":" + api.getClientId() + ":" + System.currentTimeMillis() / 1000L;
    }

    public static DiscordApi getApi() { return api; }

    public static long getStartMillis() { return startMillis; }

    public static List<String> getCommandList() {
        return commandList;
    }
}
