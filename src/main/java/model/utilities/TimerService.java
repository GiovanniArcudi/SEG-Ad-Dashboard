package model.utilities;

public class TimerService {
    long startTime;
    long endTime;
    String message;

    public TimerService(String message) {
        this.message = message;
    }

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println(message + " in " + (duration / 1000000) + " milliseconds");
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
