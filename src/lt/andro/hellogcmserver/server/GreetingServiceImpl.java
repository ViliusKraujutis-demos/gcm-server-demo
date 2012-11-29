
package lt.andro.hellogcmserver.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lt.andro.hellogcmserver.client.GreetingService;
import lt.andro.hellogcmserver.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.android.gcm.server.*;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

    public String greetServer(String input) throws IllegalArgumentException {
        // Verify that the input is valid.
        if (!FieldVerifier.isValidName(input)) {
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException("Name must be at least 4 characters long");
        }

        String serverInfo = getServletContext().getServerInfo();
        String userAgent = getThreadLocalRequest().getHeader("User-Agent");

        // Escape data from the client to avoid cross-site script vulnerabilities.
        input = escapeHtml(input);
        userAgent = escapeHtml(userAgent);

        sendMessageToDevice(input);
        return "Hello, " + input + "!<br><br>I am running " + serverInfo
                + ".<br><br>It looks like you are using:<br>" + userAgent;
    }

    private static final String API_KEY = "AIzaSyBgm6Wy37KMJNGbRWXPTP6sdU8sJBxaAsc";

    /**
     * @param input
     */
    private void sendMessageToDevice(String input) {
        Sender sender = new Sender(API_KEY);
        Message message = new Message.Builder().addData("NAME", input).build();
        List<String> devices = new ArrayList<String>();
        devices.add("APA91bGWo68Z7vBeTPTBmSvaw0p4soJm1AeZlxXufHrXozAr_t8RUEOXC-wP-p0b50UW5sLsjs7o0oBuWN6eb9p-tVs7ueu2jBQPZEYLup99Nvg4MUx8m8L6DElQhtxymr3AEotlectLtIavWQnS64NNVRHRo6Fn1A");
        MulticastResult result = null;
        try {
            result = sender.send(message, devices, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Result = " + result);
    }

    /**
     * Escape an html string. Escaping data received from the client helps to prevent cross-site
     * script vulnerabilities.
     * 
     * @param html the html string to escape
     * @return the escaped string
     */
    private String escapeHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
