package com.hackaton.bot;

import com.hackaton.bot.actor.Actor;
import com.hackaton.bot.actor.ClientActor;
import com.hackaton.bot.business.BotBusiness;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * MyAmazingBot.
 *
 * @author lballena.
 */
@Slf4j
public class TrinityBot extends TelegramLongPollingBot {

  protected BotBusiness botBusiness = new BotBusiness();

  private Map<Long, BotMemory> idChats = new HashMap();

  private final Actor clientActor;

  public TrinityBot() {
    clientActor = new ClientActor(getBotUsername(),botBusiness,idChats);
  }

  @Override
  public void onUpdateReceived(Update update) {
    if(update.hasMessage()){
      if(idChats.get(update.getMessage().getChatId()) == null){
        hello(update);
      }else {
        if(clientActor.activateSteps(update)){
          executeMessage(clientActor.getSendMessage());
        }
      }
    }
  }

  @Override
  public String getBotUsername() {
    return "Luis21Bot";
  }

  @Override
  public String getBotToken() {
    return "680599289:AAF9mRhmMiSWxyFjZqQIaREljaiWwtcDsXE";
  }

  private void executeMessage(SendMessage message) {
    try {
      execute(message); // Call method to send the message
    } catch (TelegramApiException e) {
      log.error("Ocurrio un error al enviar el mensaje : {}",e.getMessage(),e);
    }
  }

  private void hello(Update update){
    log.info("ClientActor.activateSteps : ChatId : {}",update.getMessage().getChatId());
    idChats.put(update.getMessage().getChatId(), new BotMemory());
    SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId())
            .setText(botBusiness.saludar(update, getBotUsername()));
    executeMessage(sendMessage);
  }

}
