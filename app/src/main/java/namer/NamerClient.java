package namer;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.gson.Gson;
import com.kamron.pogoiv.clipboardlogic.tokens.NamerModels.NameItem;

import org.jetbrains.annotations.NotNull;

public class NamerClient {
    public String GetRandomName(String gender){
        GetDataClient getDataClient = new GetDataClient();
       // String s = getDataClient.execute(" https://192.168.1.19:5001/api/naming/random/" + gender);
        final String[] result = {null};
        Fuel fuel = Fuel.INSTANCE;
        fuel.get("https://DESKTOP-AJ7V7AT:5001/api/naming/random/"+gender, null)
                .responseString(new Handler<String>() {
            @Override
            public void success(String s) {
                result[0] = new Gson().fromJson(s, NameItem.class).name;
            }

            @Override
            public void failure(@NotNull FuelError fuelError) {
            }
        });
        return result[0];
    }
}
