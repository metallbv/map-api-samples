package by.googlemapid.sample;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Maps;

public class GeodecodingSample extends AbstractSample {

    public static void main(final String[] args) throws IOException, JSONException {

        ArrayList<String> langagesCities = new ArrayList();
        langagesCities.add("de");
        langagesCities.add("en");
        langagesCities.add("ru");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("C:\\Project\\allCountries1.txt"), Charset.forName("UTF-8")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] subStr;
                subStr = line.split("\\t"); // Разделения строки str с помощью метода split()
                for (int i = 0; i < langagesCities.size(); i++) {
                    TimeUnit.SECONDS.sleep(1);
                    getCity(subStr[9], subStr[10], langagesCities.get(i));
                }
                System.out.println();

            }
        } catch (IOException e) {
            // log error
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // log warning
                }
            }
        }

        //getCity("-26.0833", "-65.263", "ru");

        /*final String baseUrl = "http://maps.googleapis.com/maps/api/geocode/json";// путь к Geocoding API по HTTP
        final Map<String, String> params = Maps.newHashMap();
        params.put("language", "en");// язык данные на котором мы хочем получить
        params.put("sensor", "false");// исходит ли запрос на геокодирование от устройства с датчиком местоположения
        // текстовое значение широты/долготы, для которого следует получить ближайший понятный человеку адрес, догота и
        // широта разделяется запятой, берем из предыдущего примера
        params.put("latlng", "42.5667,1.4833");
        final String url = baseUrl + '?' + encodeParams(params);// генерируем путь с параметрами
        System.out.println(url);// Путь, что бы можно было посмотреть в браузере ответ службы
        final JSONObject response = JsonReader.read(url);// делаем запрос к вебсервису и получаем от него ответ
        // как правило наиболее подходящий ответ первый и данные о адресе можно получить по пути
        // //results[0]/formatted_address
        final JSONObject location = response.getJSONArray("results").getJSONObject(0);
        final String formattedAddress = location.getString("formatted_address");
        System.out.println(formattedAddress);// итоговый адрес*/
    }

    private static void getCity(String s, String s1, String lang) throws IOException, JSONException {

        String locality = null;
        String administrative_area_level_2 = null;
        String administrative_area_level_1 = null;

        JSONObject location;
        final String baseUrl = "http://maps.googleapis.com/maps/api/geocode/json";// путь к Geocoding API по HTTP
        final Map<String, String> params = Maps.newHashMap();
        params.put("language", lang);// язык данные на котором мы хочем получить
        params.put("sensor", "false");// исходит ли запрос на геокодирование от устройства с датчиком местоположения
        // текстовое значение широты/долготы, для которого следует получить ближайший понятный человеку адрес, догота и
        // широта разделяется запятой, берем из предыдущего примера
        params.put("latlng", s + "," + s1);
        final String url = baseUrl + '?' + encodeParams(params);// генерируем путь с параметрами
        System.out.println(url);// Путь, что бы можно было посмотреть в браузере ответ службы
        final JSONObject response = JsonReader.read(url);// делаем запрос к вебсервису и получаем от него ответ
        JSONArray resultArray = response.getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
            location = resultArray.getJSONObject(i);
            String typesAdress = location.get("types").toString();
            JSONArray arrayTypeAdress = (JSONArray) location.get("types");
            for (int k = 0; k < arrayTypeAdress.length(); k++) {
                String s2 = (String) arrayTypeAdress.get(k);
                if (s2.equals("locality")) {
                    JSONArray jsonArray = (JSONArray) location.get("address_components");
                    locality = getPartOfAdress(jsonArray, "locality");
                    break;
                } else if (s2.equals("administrative_area_level_2")) {
                    JSONArray jsonArray = (JSONArray) location.get("address_components");
                    administrative_area_level_2 = getPartOfAdress(jsonArray, "administrative_area_level_2");
                } else if (s2.equals("administrative_area_level_1")) {
                    JSONArray jsonArray = (JSONArray) location.get("address_components");
                    administrative_area_level_1 = getPartOfAdress(jsonArray, "administrative_area_level_1");
                }
            }
        }

        if (locality != null) {
            System.out.print("lang - " + lang + "; city - " + locality + " | "); // city
        } else if (administrative_area_level_2 != null) {
            System.out.print("lang - " + lang + "; adm_area2 - " + administrative_area_level_2 + " | "); // city
        } else if (administrative_area_level_1 != null) {
            System.out.print("lang - " + lang + "; adm_area1 - " + administrative_area_level_1 + " | "); // city
        }

    }

    public static String getPartOfAdress(JSONArray jsonArray, String kindOfAdress) throws JSONException {

        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject ditails = (JSONObject) jsonArray.get(j);
            String check = ditails.toString();
            if (check.contains(kindOfAdress)) {
                return (String) ditails.opt("long_name");
            }
        }

        return "";
    }
}
