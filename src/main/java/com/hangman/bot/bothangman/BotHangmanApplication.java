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
            BotMessage(replyToken, "Starting the game!\nPick between rock, paper, or scissor!");
        } else{
            String replyToken = messageEvent.getReplyToken();
            String answer = Answer();
            BotMessage(replyToken, answer);
            if(answer.equals("Paper") && msg.equals("scissor")){
                BotMessage(replyToken, "You win!");
            } else if(answer.equals("Paper") && msg.equals("rock")){
                BotMessage(replyToken, "You lose!");
            } else if(answer.equals("Paper") && msg.equals("paper")){
                BotMessage(replyToken, "Draw!");
            } else if(answer.equals("Scissor") && msg.equals("scissor")){
                BotMessage(replyToken, "Draw!");
            } else if(answer.equals("Scissor") && msg.equals("rock")){
                BotMessage(replyToken, "You win!");
            } else if(answer.equals("Scissor") && msg.equals("paper")){
                BotMessage(replyToken, "You lose!");
            } else if(answer.equals("Rock") && msg.equals("scissor")){
                BotMessage(replyToken, "You lose!");
            } else if(answer.equals("Rock") && msg.equals("rock")){
                BotMessage(replyToken, "Draw!");
            } else if(answer.equals("Rock") && msg.equals("paper")){
                BotMessage(replyToken, "You win!");
            }
        }
    }

    private String Answer(){
        String answer = "";
        int random = new Random().nextInt();
        if(random % 3 == 0){
            answer = "Paper";
        } else if(random % 3 == 1){
            answer = "Scissor";
        } else{
            answer = "Rock";
        }
        return answer;
    }

    private void BotMessage(String replyToken, String reply){
        TextMessage jawabanDalamBentukTextMessage = new TextMessage(reply);
        try {
            lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, jawabanDalamBentukTextMessage))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error when replying user's chat");
        }
    }

}