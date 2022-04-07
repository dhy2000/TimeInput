package oo.util;

import java.io.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeInput implements Runnable {

    private final InputStream inputStream;

    private final PipedOutputStream pipedOutputStream = new PipedOutputStream();
    private final PipedInputStream pipedInputStream = new PipedInputStream();

    public TimeInput(InputStream inputStream) {
        this.inputStream = inputStream;
        try {
            pipedOutputStream.connect(pipedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    public InputStream getTimedInputStream() {
        return pipedInputStream;
    }

    public static void main(String[] args) throws IOException {
        InputStream in = new TimeInput(System.in).getTimedInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            System.out.flush();
        }
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Timer timer = new Timer();
        long now = System.currentTimeMillis();
        long maxMillis = 0;
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                TimedContent content = TimedContent.fromLine(line);
                if (content == null) {
                    continue;
                }
                long millis = content.getMillis();
                maxMillis = Math.max(maxMillis, millis);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            pipedOutputStream.write(content.getContent().getBytes());
                            pipedOutputStream.write('\n');
                            pipedOutputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Date(now + millis));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    pipedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        }, maxMillis + 1);
    }

    private static class TimedContent {
        private static final String PATTERN = "\\[(?<time>\\d+(\\.\\d+)?)](?<text>.*)"; // example: [1.0]1-FROM-1-TO-2

        private final long millis;
        private final String content;

        private TimedContent(long millis, String content) {
            this.millis = millis;
            this.content = content;
        }

        public long getMillis() {
            return millis;
        }

        public String getContent() {
            return content;
        }

        public static TimedContent fromLine(String line) {
            Matcher matcher = Pattern.compile(PATTERN).matcher(line);
            if (!matcher.find()) {
                return null;
            }
            String time = matcher.group("time");
            String text = matcher.group("text");
            long millis = (int)(Double.parseDouble(time) * 1000);
            return new TimedContent(millis, text);
        }
    }
}
