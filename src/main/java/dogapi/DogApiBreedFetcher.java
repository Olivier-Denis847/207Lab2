package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();
    private final String API_URL = "https://dog.ceo/api/breed";

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        Request request = new Request.Builder()
                .url(String.format("%s/%s/list", API_URL, breed))
                .addHeader("Content-Type", "application/json")
                .build();

        ArrayList<String> list = new ArrayList<>();
        try {
            Response response = client.newCall(request).execute();
            JSONObject content =  new JSONObject(response.body().string());

            if (content.getString("status").equals("success")) {
                JSONArray breeds = content.getJSONArray("message");
                for (int i = 0; i < breeds.length(); i++) {
                    list.add(breeds.getString(i));
                }
                return list;
            }
            else {
                throw new BreedNotFoundException(breed);
            }
        }
        catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}