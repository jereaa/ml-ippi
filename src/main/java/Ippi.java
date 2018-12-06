import com.google.common.net.InetAddresses;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Ippi {

    private static final int Lat = -34;
    private static final int Lng = -64;

    public Ippi() {
        Unirest.setTimeouts(5000, 0);
    }

    /**
     * Tries to request all the information possible on the specified IP address
     *
     * @param ip {@code String} containing an IPv4 or IPv6 string literal, e.g. {@code "192.168.0.1"} or {@code "2001:db8::1"}
     * @return {@link JSONObject} with all the information obtained from the IP address
     */
    public JSONObject investigateIp(String ip) {
        if(!isValidIp(ip)) {
            return null;
        }

        JSONObject result = new JSONObject();
        result.put("ip", ip);

        try {
            getCountryFromIP(ip, result);
            String countryCode = result.getString("countryCode");

            getCountryInfo(countryCode, result);
            String currencyCode = result.getString("currencyCode");

            getCurrencyInUSD(currencyCode, result);

            return result;

        } catch (Exception e) {
            if (e.getCause() instanceof ConnectTimeoutException) {
                System.err.println("Request timed out and couldn't get all the required data.");
            } else if (e.getCause() instanceof UnknownHostException) {
                System.err.println("Couldn't make the request to the server. Make sure you have internet access.");
            } else if (e instanceof IOException) {
                System.err.println(e.getMessage());
            } else {
                System.err.println("Unhandled exception: " + e.toString());
            }

            // If we could get at least some info on the IP, we return it
            if (result.has("countryCode")) {
                return result;
            }
        }
        return null;
    }

    /**
     * Checks if a certain IP Address is a valid IP Address (both IPv4 and IPv6)
     *
     * @param ip {@code String} containing an IPv4 or IPv6 string literal, e.g. {@code "192.168.0.1"} or {@code "2001:db8::1"}
     * @return {@code true} if IP Address is valid, {@code false} otherwise
     */
    public boolean isValidIp(String ip) {
        return InetAddresses.isInetAddress(ip);
    }

    /**
     * Requests country associated with a certain IP address
     *
     * @param ip {@code String} containing an IPv4 or IPv6 string literal, e.g. {@code "192.168.0.1"} or {@code "2001:db8::1"}
     * @param result {@link JSONObject} to which we would like to add the information we need
     * @return {@link JSONObject} obtained from the request made.
     * @throws UnirestException in case of connection timeouts, no internet access or response parsing errors
     * @throws IOException in case a bad request was made (invalid IP)
     */
    public JSONObject getCountryFromIP(String ip, JSONObject result) throws UnirestException, IOException {
        JSONObject ipCountry = getData("https://api.ip2country.info/ip?" + ip);

        if (result != null) {
            result.put("countryCode", ipCountry.getString("countryCode3"));
            result.put("countryName", ipCountry.getString("countryName"));
        }
        return ipCountry;
    }

    /**
     * Requests all info about a certain country
     *
     * @param countryCode {@code String} containing a country's code, either 2 or 3 lettered code
     * @param result {@link JSONObject} to which we would like to add the information we need
     * @return {@link JSONObject} obtained from the request made. JSON format here: https://restcountries.eu/#api-endpoints-response-example
     * @throws UnirestException in case of connection timeouts, no internet access or response parsing errors
     * @throws IOException in case a bad request was made (invalid country code)
     */
    public JSONObject getCountryInfo(String countryCode, JSONObject result) throws UnirestException, IOException {
        JSONObject countryInfo = getData("https://restcountries.eu/rest/v2/alpha/" + countryCode);

        if (result != null) {
            JSONArray languages = new JSONArray();
            countryInfo.getJSONArray("languages").forEach(lang -> {
                JSONObject langJson = (JSONObject) lang;
                JSONObject language = new JSONObject();
                language.put("name", langJson.getString("name"));
                language.put("code", langJson.getString("iso639_1"));
                languages.put(language);
            });
            result.put("languages", languages);

            JSONArray times = new JSONArray();
            countryInfo.getJSONArray("timezones").forEach(tz -> {
                String timezone = (String) tz;
                ZonedDateTime time = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.of(timezone.substring(3)));
                times.put(time.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + String.format("(%s)", timezone));
            });
            result.put("times", times);

            JSONArray latlng = countryInfo.getJSONArray("latlng");
            double dist = DistanceCalculator.distance(Lat, Lng, latlng.getDouble(0), latlng.getDouble(1), "K");
            result.put("distance", Math.round(dist * 100) / (double) 100);

            result.put("currencyCode", countryInfo.getJSONArray("currencies").getJSONObject(0).getString("code"));
        }

        return countryInfo;
    }

    /**
     * Requests currency rates from and to USD
     *
     * @param currencyCode {@code String} containing the currency code representing the currency we want to find exchange rates for
     * @param result {@link JSONObject} to which we would like to add the information we need
     * @return {@link JSONObject} obtained from the request made. JSON format here: https://www.currencyconverterapi.com/docs
     * @throws UnirestException in case of connection timeouts, no internet access or response parsing errors
     * @throws IOException in case a bad request was made (invalid country code)
     */
    public JSONObject getCurrencyInUSD(String currencyCode, JSONObject result) throws UnirestException, IOException {
        String currencyCodeUpper = currencyCode.toUpperCase();
        String fromUSD = "USD_" + currencyCodeUpper;
        String toUSD = currencyCodeUpper + "_USD";

        JSONObject currencyInfo = getData(String.format(
                "https://free.currencyconverterapi.com/api/v6/convert?q=%s,%s&compact=ultra", fromUSD, toUSD));

        // If currency was invalid, we get an empty JSON in return
        // so we have to check it
        if (result != null && currencyInfo.has(fromUSD)) {
            result.put(fromUSD, currencyInfo.getDouble(fromUSD));
            result.put(toUSD, currencyInfo.getDouble(toUSD));
        }

        return currencyInfo;
    }

    /**
     * Makes a request to a certain URL and returns the result as a {@link JSONObject}
     *
     * @param url {@code String} containing the URL we will be requesting
     * @return {@link JSONObject} parsed from the body of the response we got
     * @throws UnirestException in case of connection timeouts, no internet access or response parsing errors
     * @throws IOException in case we got a response with status greater than or equal to 400
     */
    private JSONObject getData(String url) throws UnirestException, IOException {
        HttpResponse<String> response = Unirest.get(url).asString();

        // If we receive a bad response code, then we throw an exception
        if (response.getStatus() >= 400) {
            throw new IOException("Server responded with error. Check if URL is correct. URL: " + url);
        }
        return new JSONObject(response.getBody());
    }
}
