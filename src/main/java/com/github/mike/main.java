package com.github.mike;

import com.github.mike.database.Connect;
import com.github.mike.database.DBModify;
import com.github.mike.init.Startup;
import com.google.gson.Gson;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

import java.io.*;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new Gson();
        String[] test = gson.fromJson(new FileReader("settings.json"), String[].class);
        DiscordApi api = new DiscordApiBuilder().setToken(test[0]).setAllIntentsExcept(Intent.GUILD_PRESENCES).login().join();
        Connect conn = new Connect(test);
        DBModify mod = new DBModify(conn);
        Startup startup = new Startup(api, mod);

        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
        System.out.println("Ready.");
        Scanner sc = new Scanner(new BufferedInputStream(System.in));
        if(sc.hasNext()){
            if(sc.nextLine().equalsIgnoreCase("stop")){
                System.exit(0);
            }
        }
    }
}
