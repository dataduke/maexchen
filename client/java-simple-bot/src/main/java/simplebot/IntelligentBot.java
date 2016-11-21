package simplebot;

import udphelper.MessageListener;
import udphelper.MessageSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class IntelligentBot implements MessageListener {

    private final MessageSender messageSender;
    private final String name;
    private final List<String> dices = Arrays.asList("2,1", "6,6", "5,5", "4,4", "3,3", "2,2", "1,1", "6,5", "6,4",
            "6,3", "6,2", "6,1", "5,4", "5,3", "5,2", "5,1",
            "4,3", "4,2", "4,1",
            "3,2", "3,1");
    String lastDice = "";
    String forLastDice = "";

    public IntelligentBot(String name, MessageSender messageSender) {
        Collections.reverse(dices);
        this.messageSender = messageSender;
        this.name = name;
        tryToSend("REGISTER;" + name);
    }

    private void tryToSend(String message) {
        try {
            System.out.println("---> " + message);
            messageSender.send(message);
        } catch (IOException e) {
            System.err.println("Failed to send " + message + ": " + e.getMessage());
        }
    }

    @Override
    public void onMessage(String message) {
        System.out.println("<--- " + message);

        String[] parts = message.split(";");
        String command = parts[0];

        if (parts[0].equals("ROUND STARTING")) {
            tryToSend("JOIN;" + parts[1]);
        } else if (parts[0].equals("YOUR TURN")) {
            if (lastDice.equals("2,1")) {
                System.out.println("SEE wegen maxien");
                tryToSend("SEE;" + parts[1]);
            } else if (lastDice.equals("6,6")) {
                System.out.println("SEE wegen 6,6");
                tryToSend("SEE;" + parts[1]);
            }
            else if (dices.indexOf(lastDice) == dices.indexOf(forLastDice) + 1){
                System.out.println("SEE wegen nur eins abstand");
                tryToSend("SEE;" + parts[1]);
            } else {
                System.out.println("Last Dice:" + lastDice);
                tryToSend("ROLL;" + parts[1]);
            }
        } else if (parts[0].equals("ROLLED")) {
            String dice = parts[1];
            int indexOf = dices.indexOf(dice);
            int indexOfLast = dices.indexOf(lastDice);
            if (indexOf > indexOfLast) {
                System.out.println("real");
                tryToSend("ANNOUNCE;" + parts[1] + ";" + parts[2]);
            } else {
                int fake = new Random().nextInt(3) + 1;
                if (indexOfLast + fake >= dices.size() - 1) {
                    fake = 1;
                }
                System.out.println("fake");
                tryToSend("ANNOUNCE;" + dices.get(indexOfLast + fake) + ";" + parts[2]);
            }
        } else if (command.equals("ANNOUNCED")) {
            forLastDice = lastDice;
            lastDice = parts[2];
        } else if (command.equals("PLAYER LOST")) {
            lastDice = "";
            forLastDice = "";
        }
    }

    @Override
    public void onStop() {
        tryToSend("UNREGISTER");
    }

}
