package com.hangman.bot.bothangman;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.Random;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@LineMessageHandler
public class BotHangmanApplication extends SpringBootServletInitializer {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BotHangmanApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BotHangmanApplication.class, args);
    }

    @EventMapping
    public void handleTextEvent(MessageEvent<TextMessageContent> messageEvent){
        String msg = messageEvent.getMessage().getText().toLowerCase();
        if(msg.equals("/start")){
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Starting the game!");
        } else if(msg.equals("/stop")){
            System.out.println("Permainan berhenti.");
        }
    }

    private void BotMessage(String replyToken, String reply){
        TextMessage jawabanDalamBentukTextMessage = new TextMessage(reply);
        try {
            lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, jawabanDalamBentukTextMessage))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Ada error saat ingin membalas chat");
        }
    }

}