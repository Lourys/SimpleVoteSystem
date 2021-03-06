package fr.keke142.simplevotesystem.utils;

import fr.keke142.simplevotesystem.SimpleVoteSystemPlugin;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class ReadersUtil {
    private ReadersUtil() {
        throw new UnsupportedOperationException();
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (IOException ex) {
            SimpleVoteSystemPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to read: " + url, ex);
            return null;
        }
    }

    public static String readFromUrl(String url) {
        try (InputStream in = new URL(url).openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            SimpleVoteSystemPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to read: " + url, e);
            return null;
        }
    }

    public static JSONObject readJsonFromUrlThrow(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

}
