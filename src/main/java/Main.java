import me.app.opr.Forecast;


public class Main {
    public static void main(String[] args) {
        boolean isPredict = false; // false -> train; true -> predict and gen upload file
        Forecast forecast = new Forecast(isPredict);

        forecast.run();
    }
}
