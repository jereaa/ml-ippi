package com.jereaa;

import com.google.common.net.InetAddresses;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@DisplayName("com.jereaa.Ippi class Tests")
class IppiTest {

    String[] testIpsLong = {
            "",
            "016.016.016.016",
            "016.016.016",
            "016.016",
            "016",
            "000.000.000.000",
            "000",
            "0x0a.0x0a.0x0a.0x0a",
            "0x0a.0x0a.0x0a",
            "0x0a.0x0a",
            "0x0a",
            "42.42.42.42.42",
            "42.42.42",
            "42.42",
            "42",
            "42..42.42",
            "42..42.42.42",
            "42.42.42.42.",
            "42.42.42.42...",
            ".42.42.42.42",
            "...42.42.42.42",
            "42.42.42.-0",
            "42.42.42.+0",
            ".",
            "...",
            "bogus",
            "bogus.com",
            "192.168.0.1.com",
            "12345.67899.-54321.-98765",
            "257.0.0.0",
            "42.42.42.-42",
            "3ffe::1.net",
            "3ffe::1::1",
            "1::2::3::4:5",
            "::7:6:5:4:3:2:", // should end with ":0"
            ":6:5:4:3:2:1::", // should begin with "0:"
            "2001::db:::1",
            "FEDC:9878",
            "+1.+2.+3.4",
            "1.2.3.4e0",
            "::7:6:5:4:3:2:1:0", // too many parts
            "7:6:5:4:3:2:1:0::", // too many parts
            "9:8:7:6:5:4:3::2:1", // too many parts
            "0:1:2:3::4:5:6:7", // :: must remove at least one 0.
            "3ffe:0:0:0:0:0:0:0:1", // too many parts (9 instead of 8)
            "3ffe::10000", // hextet exceeds 16 bits
            "3ffe::goog",
            "3ffe::-0",
            "3ffe::+0",
            "3ffe::-1",
            ":",
            ":::",
            "::1.2.3",
            "::1.2.3.4.5",
            "::1.2.3.4:",
            "1.2.3.4::",
            "2001:db8::1:",
            ":2001:db8::1",
            ":1:2:3:4:5:6:7",
            "1:2:3:4:5:6:7:",
            ":1:2:3:4:5:6:",
            "216.218.222.14",
            "209.141.45.212",
            "204.8.156.142",
            "199.249.223.69",
            "195.123.221.122",
            "192.42.116.17",
            "185.220.101.21",
            "178.32.147.150",
            "173.163.61.189",
            "163.172.41.228",
            "149.202.238.204",
            "136.63.186.225",
            "109.70.100.19",
            "104.58.144.185",
            "100.2.209.68",
            "97.106.65.94",
            "93.115.86.8",
            "85.114.142.205",
            "77.247.181.163"
    };
    String[] testIpsShort = {
            "...",
            "bogus",
            "bogus.com",
            "192.168.0.1.com",
            "12345.67899.-54321.-98765",
            "257.0.0.0",
            "42.42.42.-42",
            "3ffe::1.net",
            "1::2::3::4:5",
            "::7:6:5:4:3:2:",
            ":6:5:4:3:2:1::",
            "216.218.222.14",
            "209.141.45.212",
            "204.8.156.142",
            "199.249.223.69",
            "195.123.221.122",
            "192.42.116.17",
            "185.220.101.21",
            "178.32.147.150",
            "173.163.61.189",
            "163.172.41.228",
            "149.202.238.204",
            "136.63.186.225",
            "109.70.100.19",
            "104.58.144.185",
            "100.2.209.68",
            "97.106.65.94",
            "93.115.86.8",
            "85.114.142.205",
            "77.247.181.163"
    };


    @Test
    @DisplayName("Valid IPs Test")
    void ValidIPTest() {
        Ippi ippi = new Ippi();

        for (String bogusInput : testIpsLong) {
            try {
                InetAddresses.forString(bogusInput);
                Assertions.assertTrue(ippi.isValidIp(bogusInput));
            } catch (IllegalArgumentException expected) {
                Assertions.assertFalse(ippi.isValidIp(bogusInput));
            }
        }
    }

    @Test
    @DisplayName("Result from IP investigation Test")
    void InvestigateIPTest() {
        Ippi ippi = new Ippi();

        JSONObject result;
        for (String testIp : testIpsShort) {
            result = ippi.investigateIp(testIp);
            if (!ippi.isValidIp(testIp)) {
                Assertions.assertNull(result);
            } else {
                Assertions.assertTrue(result.has("ip"));
                Assertions.assertTrue(result.has("countryCode"));
                Assertions.assertTrue(result.has("countryName"));
                Assertions.assertTrue(result.has("languages"));
                Assertions.assertTrue(result.has("times"));
                Assertions.assertTrue(result.has("distance"));
                Assertions.assertTrue(result.has("currencyCode"));
                Assertions.assertTrue(result.has(result.getString("currencyCode") + "_USD"));
                Assertions.assertTrue(result.has("USD_" + result.getString("currencyCode")));
            }
        }
    }

    @Test
    @DisplayName("Country from IP Request Test")
    void getCountryFromIPTest() {
        Ippi ippi = new Ippi();

        for (String testIp : testIpsShort) {
            if (!ippi.isValidIp(testIp)) {
                Assertions.assertThrows(IOException.class, () -> ippi.getCountryFromIP(testIp, null));
            } else {
                Assertions.assertDoesNotThrow(() -> ippi.getCountryFromIP(testIp, null));
            }
        }
    }

    @Test
    @DisplayName("Country Info Request Test")
    void getCountryInfoTest() {
        Ippi ippi = new Ippi();

        Assertions.assertDoesNotThrow(() -> ippi.getCountryInfo("arg", null));
        Assertions.assertDoesNotThrow(() -> ippi.getCountryInfo("nl", null));
        Assertions.assertDoesNotThrow(() -> ippi.getCountryInfo("ury", null));
        Assertions.assertDoesNotThrow(() -> ippi.getCountryInfo("US", null));
        Assertions.assertDoesNotThrow(() -> ippi.getCountryInfo("JPN", null));
        Assertions.assertDoesNotThrow(() -> ippi.getCountryInfo("es", null));
        Assertions.assertThrows(IOException.class, () -> ippi.getCountryInfo("xx", null));
        Assertions.assertThrows(IOException.class, () -> ippi.getCountryInfo("XXX", null));
        Assertions.assertThrows(IOException.class, () -> ippi.getCountryInfo("asdasd", null));
        Assertions.assertThrows(IOException.class, () -> ippi.getCountryInfo("", null));
    }

    @Test
    @DisplayName("Currency conversion Request Test")
    void getCurrencyConversionTest() {
        Ippi ippi = new Ippi();

        String[] currencies = {
                "ARS",
                "usd",
                "jpy",
                "EUR",
                "gbp",
                "ARGS",
                "xxx",
                "asdasd",
                "XX",
                "JPN"
        };

        JSONObject result;
        for (int i = 0; i < currencies.length; i++) {
            try {
                result = ippi.getCurrencyInUSD(currencies[i], null);
                String currencyCodeUpper = currencies[i].toUpperCase();
                if (i < currencies.length / 2) {
                    Assertions.assertTrue(result.has("USD_" + currencyCodeUpper));
                    Assertions.assertTrue(result.has(currencyCodeUpper + "_USD"));
                } else {
                    Assertions.assertFalse(result.has("USD_" + currencyCodeUpper));
                    Assertions.assertFalse(result.has(currencyCodeUpper + "_USD"));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
