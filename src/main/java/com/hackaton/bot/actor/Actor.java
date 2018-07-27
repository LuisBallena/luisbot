package com.hackaton.bot.actor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

/**
 * Actor.
 *
 * @author Luis Alonso Ballena Garcia
 */
@Slf4j
@Getter
@Setter
public abstract class Actor {

    protected String botUsername;

    protected SendMessage sendMessage;

    public Actor(String botUsername){
        this.botUsername = botUsername;
    }

    public abstract boolean activateSteps(Update update);

}
