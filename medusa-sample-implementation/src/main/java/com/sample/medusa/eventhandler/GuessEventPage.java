package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.DomChanges;
import io.getmedusa.medusa.core.annotation.MEventPage;

import java.util.ArrayList;
import java.util.List;

@MEventPage(path = "/guess/{number}", file = "pages/guess")
public class GuessEventPage {
    static int ID = 1;
    int max = 5; // optional query parameter with a default value
    int number;  // form path variable, the conversion to a random number in setter
    int toGuess;
    int guesses;
    int guess;
    String message;
    List<Integer> numbers = new ArrayList<>();

    public GuessEventPage() {
        System.out.println("new GuessEventPage: " + ID ++);
        System.out.print("init=> ");
        init();
    }

    // optional method, magically called on init page
    @DomChanges({"message","winner"})
    public void init() {
        // optional method magically called
        System.out.println(this);
    }

    @DomChanges("message")
    public void guess(int guess) {
        guesses++;
        this.guess = guess;
        System.out.println(guess +  " == " + number);
    }

    public String getMessage() {
        message = "Guess the number between 1 and " + toGuess;
        if(guesses != 0) {
            if (isWinner()) {
                message = "We have a WINNER! You guessed the number " + toGuess  + " in " + guesses + " times.";
            } else {
                message = "Guess again, the number is " + highLow() + ", you can guess " + ( max - guesses ) + " more times." ;
            }
        }
        return message;
    }

    private String highLow() {
        return guess <  number ? "higher" : "lower";
    }

    public boolean isWinner() {
        return guess == number;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    // setter query parameter
    public void setMax(String max) {
        this.max = Integer.valueOf(max);
    }

    // setter path variable
    public void setNumber(String number) {
        toGuess = Integer.valueOf(number);
        this.number = ((int) (Math.random() * toGuess )) + 1;
        int i = 0;
        while (i <= toGuess) {
            numbers.add(i++);
        }
    }

    @Override
    public String toString() {
        return "GuessEventPage{" +
                "max=" + max +
                ", number=" + number +
                ", toGuess=" + toGuess +
                ", guesses=" + guesses +
                ", guess=" + guess +
                ", message='" + message + '\'' +
                ", numbers=" + numbers +
                '}';
    }
}
