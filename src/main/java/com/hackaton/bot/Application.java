package com.hackaton.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Application.
 *
 * @author lballena.
 */
@Slf4j
public class Application {

  public static void main(String args[]) {
    log.info("Iniciando telegram");
    ApiContextInitializer.init();
    TelegramBotsApi botsApi = new TelegramBotsApi();

    try {
      botsApi.registerBot(new TrinityBot());
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

}
