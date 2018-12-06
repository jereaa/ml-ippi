import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainApplication {

    public static void main(String... args) {

        if (args.length != 1) {
            System.err.println("Incorrect arguments. Please provide the IP address to investigate as the only argument.");
            return;
        }

        String ip = args[0];
        Ippi ippi = new Ippi();

        if (!ippi.isValidIp(ip)) {
            System.err.println(String.format("Invalid IP. The IP %s is not a valid IP address. Please provide a valid IP address.", ip));
            return;
        }

        JSONObject result = ippi.investigateIp(ip);
        if (result != null) {
            printResult(result);
        }
    }

    /**
     * Prints JSON obtained from IP investigation into the console.
     *
     * @param result JSON obtained from our IP investigation.
     */
    private static void printResult(JSONObject result) {
        if (result == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        // IP to investigate
        sb.append("IP to investigate: ");
        sb.append(result.getString("ip"));

        // Current time
        sb.append("\nCurrent local time: ");
        sb.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // IP's Country info
        sb.append("\nCountry: ");
        sb.append(result.getString("countryName"));
        sb.append("\nISO Code: ");
        sb.append(result.getString("countryCode"));

        if (result.has("languages")) {

            // Languages
            sb.append("\nLanguages: ");
            JSONArray languages = result.getJSONArray("languages");

            for (int i = 0; i < languages.length(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                JSONObject lang = languages.getJSONObject(i);
                sb.append(lang.getString("name"));
                sb.append(" (");
                sb.append(lang.getString("code"));
                sb.append(')');
            }

            // Local times
            sb.append("\nLocal times: ");
            JSONArray times = result.getJSONArray("times");

            for (int i = 0; i < times.length(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(times.getString(i));
            }

            // Estimated distance from Argentina
            sb.append("\nEstimated distance from Argentina: ");
            sb.append(result.getDouble("distance"));
            sb.append(" km");

            // Currency
            String currencyCode = result.getString("currencyCode");
            sb.append("\nCurrency: ");
            sb.append(currencyCode);

            String fromUSD = "USD_" + currencyCode;
            String toUSD = currencyCode + "_USD";
            if (result.has(fromUSD)) {
                sb.append(" (1 ");
                sb.append(currencyCode);
                sb.append(" = ");
                sb.append(result.getDouble(toUSD));
                sb.append(" --- 1 USD = ");
                sb.append(result.getDouble(fromUSD));
                sb.append(' ');
                sb.append(currencyCode);
                sb.append(')');
            }
        }

        sb.append('\n');
        System.out.println(sb.toString());
    }
}
