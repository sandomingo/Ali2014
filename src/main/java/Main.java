import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import me.app.base.Consts;
import me.app.opr.BrandStatistics;
import me.app.opr.Forecast;
import me.app.opr.UserStatistics;


public class Main {
    public static void main(String[] args) {
        boolean isPredict = false; // false -> train; true -> predict and gen upload file
        Forecast forecast = new Forecast();

        if (isPredict) {
            forecast.tmpForecast();
        } else {
            forecast.curForecast();
        }
    }
}
