package simplebot;

import java.io.IOException;

import udphelper.MessageListener;
import udphelper.MessageSender;
import udphelper.UdpCommunicator;

public class Main {

	private enum BotTypes {
		simple,
		random,
		intelligent
	}
	
	public static void main(String[] args) throws IOException {

		String serverHost = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String clientName = args[2];
		BotTypes botType = BotTypes.intelligent;
		if (args.length > 3) {
			botType = BotTypes.valueOf(args[3]);
		}
		
		UdpCommunicator communicator = new UdpCommunicator(serverHost, serverPort);
		MessageListener bot = createBot(botType, clientName, communicator.getMessageSender());
        addShutdownHook(communicator);
		communicator.addMessageListener(bot);
		communicator.listenForMessages();
	}

    private static void addShutdownHook(final UdpCommunicator communicator) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                communicator.stop();
            }
		});
    }

	private static MessageListener createBot(BotTypes botType, String clientName, MessageSender messageSender) {
		if (botType == BotTypes.random) {
			return new RandomBot(clientName, messageSender);
		} else if (botType == BotTypes.simple) {
			return new SimpleBot(clientName, messageSender);
		}
		return  new IntelligentBot(clientName, messageSender);
	}

}
