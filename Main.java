import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final String IN_FILE_TXT = "src\\inFile.txt";
    private static final String OUT_FILE_TXT = "src\\outFile.txt";
    private static final String PATH_TO_MUSIC = "src\\music\\music";
    private static final int COUNT_MUSIC = 10;
    public static final String DATA_URL = "\\s*(?<=data-url\\s?=\\s?\")[^>]*\\/*(?=\")";

    public static void main(String[] args) {
        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
             BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_TXT))) {
            extractMusicURLs(inFile, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader musicFile = new BufferedReader(new FileReader(OUT_FILE_TXT))) {
            download(musicFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
        FileOutputStream stream = new FileOutputStream(file);
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        stream.close();
        byteChannel.close();
    }

    private static void extractMusicURLs(BufferedReader inFile, BufferedWriter outFile) {
        String Url;
        try {
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);
                String result = fetchHost(url);
                Pattern email_pattern = Pattern.compile(DATA_URL);
                Matcher matcher = email_pattern.matcher(result);
                int i = 0;
                while (matcher.find() && i < COUNT_MUSIC) {
                    outFile.write(matcher.group() + "\r\n");
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String fetchHost(URL url) throws IOException {
        String result;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            result = bufferedReader.lines().collect(Collectors.joining("\n"));
        }
        return result;
    }

    private static void download(BufferedReader musicFile) {
        try {
            String music;
            int count = 0;
            while ((music = musicFile.readLine()) != null) {
                downloadUsingNIO(music, PATH_TO_MUSIC + String.valueOf(count) + ".mp3");
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}