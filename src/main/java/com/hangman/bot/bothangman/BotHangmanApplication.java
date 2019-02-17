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

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@LineMessageHandler
public class BotHangmanApplication extends SpringBootServletInitializer {
    private static boolean game_on = false;
    private static int lives = 8;
    private static String[] category = new String[]{"fruits", "countries"};
    private static String[] fruits = new String[]{"apple", "apricots", "avocado", "banana",
                                    "blueberries", "breadfruit", "cantaloupe",
                                    "cherries", "clementine", "coconut",
                                    "cranberries", "durian", "elderberries",
                                    "grapefruit", "grapes", "guava", "jackfruit",
                                    "jujube", "kiwi", "lemon", "lychee",
                                    "mandarin", "mango", "olives", "orange",
                                    "papaya", "passion", "peach", "pear",
                                    "persimmon", "pineapple", "plums", "pomegranate",
                                    "prunes", "raspberries", "sapodilla", "strawberries",
                                    "tangerine", "watermelon"};
    private static String[] countries = new String[]{"afghanistan", "albania", "algeria", "andorra",
                                       "angola", "argentina", "armenia", "australia",
                                       "austria", "azerbaijan", "bahamas", "bahrain",
                                       "bangladesh", "barbados", "belarus", "belgium",
                                       "belize", "benin", "bhutan", "bolivia", "botswana",
                                       "brazil", "brunei", "bulgaria", "burundi", "cabo verde",
                                       "cambodia", "cameroon", "canada", "chile", "china",
                                       "colombia", "congo", "costa rica", "croatia", "cuba",
                                       "czech republic", "denmark", "dominica", "ecuador",
                                       "egypt", "el savador", "estonia", "ethiopia", "fiji",
                                       "finland", "france", "germany", "greece", "haiti",
                                       "hungary", "iceland", "indonesia", "iran", "iraq",
                                       "ireland", "israel", "italy", "jamaica", "japan",
                                       "kenya", "kiribati", "kuwait", "laos", "lebanon",
                                       "liberia", "libya", "lithuania", "luxembourg",
                                       "madagascar", "malaysia", "mexico", "monaco",
                                       "mongolia", "morocco", "mozambique", "myanmar",
                                       "nepal", "netherlands", "new zealand", "nicaragua",
                                       "nigeria", "north korea", "norway", "oman", "pakistan",
                                       "panama", "paraguay", "peru", "philippines", "poland",
                                       "portugal", "qatar", "romania", "russia", "saudi arabia",
                                       "serbia", "sierra leone", "singapore", "slovakia",
                                       "somalia", "south africa", "south korea", "spain",
                                       "sri lanka", "sudan", "sweden", "switzerland", "syria",
                                       "thailand", "tunisia", "turkey", "uganda", "ukraine",
                                       "united arab emirates", "united kingdom",
                                       "united states of america", "uruguay", "venezuela",
                                       "vietnam", "yemen", "zimbabwe"};
    private static int Neff = 0;
    private static char[] answer_arr = new char[50];
    private static String current_cat = "";
    private static String quiz = "";
    private static String answer = "";

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
        if(msg.equals("/help")){
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Type /start to start the game.\nType /stop to stop the game.");
        } else if(msg.equals("/start") && !game_on){
            Arrays.fill(answer_arr, '*');
            game_on = true;
            current_cat = "";
            quiz = "";
            current_cat = getCategory(category);
            if(current_cat.equals("fruits")){
                quiz = getQuiz(fruits);
            } else if(current_cat.equals("countries")){
                quiz = getQuiz(countries);
            }
            Neff = quiz.length();
            answer = fillAnswer(answer_arr);
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Starting the game.\nQuiz category will be randomized.\n" +
                                         "Category: " + current_cat + ".\nLives: " + lives + ".\nAnswer: " +
                                         answer);
        } else if(msg.equals("/stop") && game_on) {
            game_on = false;
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Stopping the game.\nThank you for playing!");
        } else if(game_on){
            if(msg.length() == 1){
                boolean exist = false;
                for(int i = 0; i < Neff; i++){
                    if(msg.charAt(0) == quiz.charAt(i)){
                        answer_arr[i] = msg.charAt(0);
                        exist = true;
                    }
                }
                if(exist){
                    answer = fillAnswer(answer_arr);
                    String replyToken = messageEvent.getReplyToken();
                    BotMessage(replyToken, "Correct!\nCategory: " + current_cat + ".\nLives: " +
                            lives + ".\nAnswer: " + answer);
                    if(checkWin(answer, quiz)) {
                        replyToken = messageEvent.getReplyToken();
                        BotMessage(replyToken, "Congratulations! You won the game!");
                    }
                } else {
                    lives--;
                    String replyToken = messageEvent.getReplyToken();
                    BotMessage(replyToken, "Too bad, wrong answer.\nCategory: " + current_cat + ".\nLives: " +
                            lives + ".\nAnswer: " + answer);
                }
            } else{
                lives--;
                String replyToken = messageEvent.getReplyToken();
                BotMessage(replyToken, "You can only type one character at a time.\nCategory: " + current_cat + ".\nLives: " +
                        lives + ".\nAnswer: " + answer);
            }
        } else{
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Incorrect command");
        }
        /*if(msg.equals("/help")){
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Type /start to start the game.\nType /stop to stop the game.");
        } else if(msg.equals("/start") && !game_on){
            game_on = true;
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Starting the game.\nPick between rock, paper, \nor scissor by typing it!");
        } else if(msg.equals("/stop") && game_on) {
            game_on = false;
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Stopping the game.\nThank you for playing!");
        } else if(game_on && (msg.equals("scissor") || msg.equals("paper") || msg.equals("rock"))){
            String replyToken = messageEvent.getReplyToken();
            String answer = Answer();
            if(answer.equals("Paper") && msg.equals("scissor")){
                BotMessage(replyToken, answer + ". You win!");
            } else if(answer.equals("Paper") && msg.equals("rock")){
                BotMessage(replyToken, answer + ". You lose!");
            } else if(answer.equals("Paper") && msg.equals("paper")){
                BotMessage(replyToken, answer + ". Draw!");
            } else if(answer.equals("Scissor") && msg.equals("scissor")){
                BotMessage(replyToken, answer + ". Draw!");
            } else if(answer.equals("Scissor") && msg.equals("rock")){
                BotMessage(replyToken, answer + ". You win!");
            } else if(answer.equals("Scissor") && msg.equals("paper")){
                BotMessage(replyToken, answer + ". You lose!");
            } else if(answer.equals("Rock") && msg.equals("scissor")){
                BotMessage(replyToken, answer + ". You lose!");
            } else if(answer.equals("Rock") && msg.equals("rock")){
                BotMessage(replyToken, answer + ". Draw!");
            } else if(answer.equals("Rock") && msg.equals("paper")){
                BotMessage(replyToken, answer + ". You win!");
            }
        } else{
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Incorrect command");
        }*/
    }

    /*private String Answer(){
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
    }*/

    private void BotMessage(String replyToken, String reply){
        TextMessage replyText = new TextMessage(reply);
        try {
            lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, replyText))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error when replying user's chat");
        }
    }

    private String getCategory(String[] category){
        int random = new Random().nextInt(category.length);
        return category[random];
    }

    private String getQuiz(String[] ans){
        int random = new Random().nextInt(ans.length);
        return ans[random];
    }

    private String fillAnswer(char[] ans){
        String answ = "";
        for(int i = 0; i < Neff; i++){
            answ += ans[i];
        }
        return answ;
    }

    private boolean checkWin(String ans, String qz){
        return ans.equals(qz);
    }
}