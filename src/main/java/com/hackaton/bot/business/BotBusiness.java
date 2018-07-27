package com.hackaton.bot.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.bot.BotMemory;

import java.io.IOException;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

/**
 * BotBusiness.
 *
 * @author lballena.
 */
@Slf4j
public class BotBusiness {

    private ObjectMapper mapper = new ObjectMapper();

    private String urlHost = "http://192.168.43.188";

    public String saludar(Update update, String botUsername) {
        return String.format("Hola %s %s, soy %s y he venido a ayudarte con tus tasas.",
                update.getMessage().getChat().getFirstName(),
                update.getMessage().getChat().getLastName(),
                botUsername);
    }

    public SendMessage pedirPermsisoCelular(Update update) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Disculpa, para poder brindarte un mejor servicio, favor de brindarme " +
                        "permisos de tu celular");
        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton("Enviar mi número telefonico");
        keyboardButton.setRequestContact(true);
        row.add(keyboardButton);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        ArrayList<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);
        markup.setKeyboard(rows);
        sendMessage.setReplyMarkup(markup);
        return sendMessage;
    }

    public SendMessage solicitarDocumentoIdentidad(Update update) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Adicionalmente, será necesario que me indiques tu número de DNI por favor");
        return message;
    }

    public SendMessage validarDocumentoIdentidad(BotMemory botMemory, Update update) {
        SendMessage message;
        String dni = update.getMessage().getText();
        log.info("BotBusiness.validarDocumentoIdentidad: Se ha obtenido el siguiente DNI : {}",dni);
        String url = String.format("%s:8080/consultarDNI?dni=%s", urlHost, dni);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String bodyResponse = response.getBody();
        ConsultaDNI consultaDNI = jsonToClass(bodyResponse, ConsultaDNI.class);
        if (consultaDNI.getEstadoHTTP().equalsIgnoreCase("200")) {
            botMemory.setDni(true);
            message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText("Su DNI ha sido reconocido como existente. ¿De qué manera lo puedo ayudar el día de hoy?");
        } else {
            message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText("Su DNI no existe o fue ingresado incorrectamente. Por favor, volver a ingresarlo.");
        }
        return message;
    }

    private <T> T jsonToClass(String jsonValue, Class<T> t) {
        T value = null;
        try {
            value = mapper.readValue(jsonValue.getBytes(), t);
        } catch (IOException e) {
            log.error("Ocurrio un error al deserializar la data : {}", e.getMessage(), e);
        }
        return value;
    }

}
