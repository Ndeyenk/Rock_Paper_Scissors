package rps;

import java.util.Random;

public class RandomStrategy implements Strategy {

    private Random rand = new Random();

    @Override
    public String getMove(String playerMove) {
        int choice = rand.nextInt(3);
        switch (choice) {
            case 0: return "R";
            case 1: return "P";
            case 2: return "S";
            default: return "R";
        }
    }
}