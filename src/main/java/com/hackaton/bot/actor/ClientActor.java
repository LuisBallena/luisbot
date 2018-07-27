package com.hackaton.bot.actor;

import com.hackaton.bot.BotMemory;
import com.hackaton.bot.business.BotBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;

/**
 * ClientActor.
 *
 * @author Luis Alonso Ballena Garcia
 */
@Slf4j
public class ClientActor extends Actor {

    private final BotBusiness botBusiness;

    private final Map<Long, BotMemory> idChats;

    public ClientActor(String botUsername, BotBusiness botBusiness, Map<Long, BotMemory> idChats) {
        super(botUsername);
        this.botBusiness = botBusiness;
        this.idChats = idChats;
    }

    @Override
    public boolean activateSteps(Update update) {
        boolean activate = false;
        if (update.hasMessage() && update.getMessage().hasText()) {
            BotMemory botMemory = idChats.get(update.getMessage().getChatId());
            if (StringUtils.isEmpty(botMemory.getTelephone())) {
                activate = firstStep(update);
            } else if (!botMemory.isDni()) {
                sendMessage = botBusiness.validarDocumentoIdentidad(botMemory, update);
            } else {
                sendMessage = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText(update.getMessage().getText());
            }
        } else {
            activate = secondStep(update);
        }
        return activate;
    }


    /**
     * Te pide el acceso a tu telefono para obtener el nro de celular
     */
    private boolean firstStep(Update update) {
        log.info("ClientActor.activateSteps : Se procede a pedir el permiso del teléfono");
        sendMessage = botBusiness.pedirPermsisoCelular(update);
        return true;
    }

    /**
     * Se guarda la información del celular
     */
    private boolean secondStep(Update update) {
        log.info("ClientActor.activateSteps : Telephone : {}", update.getMessage().getContact()
                .getPhoneNumber());
        BotMemory botMemory = idChats.get(update.getMessage().getChatId());
        sendMessage = botBusiness.solicitarDocumentoIdentidad(update);
        if (botMemory != null) {
            botMemory.setTelephone(update.getMessage().getContact().getPhoneNumber());
        }
        sendMessage = removeKeyBoard(sendMessage,update);
        return true;
    }


    private SendMessage removeKeyBoard(SendMessage sendMessage, Update update) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(markup);
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }

}
