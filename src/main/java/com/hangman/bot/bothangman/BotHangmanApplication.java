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
    private static String[] category = new String[]{"fruits", "countries", "colors", "animals"};
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
                                       "america", "uruguay", "venezuela",
                                       "vietnam", "yemen", "zimbabwe"};
    private static String[] colors = new String[]{"yellow", "peach", "sepia", "orange", "gold",
                                                  "tangerine", "apricot", "bronze", "clay", "amber",
                                                  "red", "scarlet", "maroon", "crimson", "pink",
                                                  "ruby", "magenta", "lavender", "taffy", "violet",
                                                  "orchid", "plum", "blue", "sky", "navy", "sapphire",
                                                  "azure", "denim", "green", "olive", "jade", "moss",
                                                  "mint", "emerald", "brown", "cedar", "cinnamon",
                                                  "brunette", "mocha", "chocolate", "caramel", "walnut",
                                                  "pecan", "hickory", "tawny", "gray", "ash"};
    private static String[] animals = new String[]{"otter", "elephant", "fish", "lion", "lynx", "porcupine",
                                                   "bull", "dog", "cat", "snake", "albatross", "alpaca",
                                                   "parrot", "possum", "alligator", "beaver", "badger", "bison",
                                                   "sheep", "buffalo", "crow", "goose", "ant", "anteater",
                                                   "squirrel", "rat", "fox", "hare", "rabbit", "armadillo",
                                                   "bat", "tortoise", "turtle", "turkey", "magpie", "pelican",
                                                   "baboon", "eagle", "bird", "whale", "mongoose", "deer",
                                                   "gecko", "bear", "rhino", "swan", "spider", "monkey",
                                                   "crane", "flamingo", "kangaroo", "ferret", "oyster", "duck",
                                                   "peacock", "shark", "bobcat", "boar", "butterfly", "capybara",
                                                   "cheetah", "catfish", "chameleon", "chimpanzee", "chipmunnk",
                                                   "cobra", "iguana", "seal", "wolf", "zebra", "crab", "coyote",
                                                   "crocodile", "cow", "dolphin", "dove", "woodpecker", "emu",
                                                   "falcon", "pigeon", "frog", "gazelle", "giraffe", "goat",
                                                   "glider", "goose", "gull", "hawk", "hedgehog", "hen", "cock",
                                                   "chicken", "hippo", "hornbill", "herring", "hyena",
                                                   "hummingbird", "impala", "jaguar", "kingfisher", "koala",
                                                   "lark", "lemur", "leopard", "lizard", "meerkat", "marmot",
                                                   "moose", "ocelot", "orca", "ostrich", "owl", "ox",
                                                   "penguin", "platypus", "puma", "python", "quail",
                                                   "raccoon", "raven", "dragonfly", "antelope", "salmon",
                                                   "skunk", "sloth", "starfish", "tarantula", "tiger",
                                                   "vulture", "wallaby", "weaver"};

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
            BotMessage(replyToken, "Type /start to start the game.\nType /stop to stop the game.\n" +
                                         "When playing, you can only type one character at a time.");
        } else if(msg.equals("/start") && !game_on){
            initializeGame();
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
                int contains = answer.indexOf(msg.charAt(0));
                if(contains == -1){
                    for(int i = 0; i < Neff; i++){ /*search character in quiz answer*/
                        if(msg.charAt(0) == quiz.charAt(i)){
                            answer_arr[i] = msg.charAt(0);
                            exist = true;
                        }
                    }
                    if(exist){ /*If character exist in quiz answer*/
                        answer = fillAnswer(answer_arr); /*update answer string*/
                        boolean win = checkWin(answer, quiz); /*check win condition*/
                        if(win) {
                            String replyToken = messageEvent.getReplyToken();
                            BotMessage(replyToken, "Correct!\nCategory: " + current_cat + ".\nLives: " +
                                    lives + ".\nAnswer: " + answer + ".\nCongratulations! You won the game!" +
                                    "\n\nType /start to play again.");
                            game_on = false;
                        } else{
                            String replyToken = messageEvent.getReplyToken();
                            BotMessage(replyToken, "Correct!\nCategory: " + current_cat + ".\nLives: " +
                                    lives + ".\nAnswer: " + answer);
                        }
                    } else { /*If character isn't in quiz answer*/
                        lives--; /*decrement live*/
                        if(lives == 0){
                            String replyToken = messageEvent.getReplyToken();
                            BotMessage(replyToken, "Too bad, wrong answer.\nCategory: " + current_cat + ".\nLives: " +
                                    lives + ".\nAnswer: " + answer + ".\n\nSorry, you lost :(\nThe answer is " + quiz +
                                    "\n\nType /start to play again.");
                            game_on = false;
                        } else{
                            String replyToken = messageEvent.getReplyToken();
                            BotMessage(replyToken, "Too bad, wrong answer.\nCategory: " + current_cat + ".\nLives: " +
                                    lives + ".\nAnswer: " + answer);
                        }
                    }
                } else{ /*If character already in string answer*/
                    String replyToken = messageEvent.getReplyToken();
                    BotMessage(replyToken, "You have already used that character.");
                }
            } else{ /*If user type in more than one character*/
                String replyToken = messageEvent.getReplyToken();
                BotMessage(replyToken, "You can only type one character at a time.\nCategory: " + current_cat + ".\nLives: " +
                        lives + ".\nAnswer: " + answer);
            }
        } else{
            String replyToken = messageEvent.getReplyToken();
            BotMessage(replyToken, "Incorrect command");
        }
    }

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

    private void initializeGame(){
        Arrays.fill(answer_arr, '*');
        lives = 8;
        game_on = true;
        current_cat = "";
        quiz = "";
        current_cat = getCategory(category);
        switch(current_cat){
            case "fruits": quiz = getQuiz(fruits); break;
            case "countries": quiz = getQuiz(countries); break;
            case "colors": quiz = getQuiz(colors); break;
            case "animals": quiz = getQuiz(animals); break;
        }
        Neff = quiz.length();
        answer = fillAnswer(answer_arr);
    }
}