/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;

public class CasoPractico {

    private static final String API_KEY = "a661ed7dedc81a176ae9b4de4e98261d4eccefaaf70b280cacacf0feca856833";
    private static final String API_URL = "https://gender-api.com/v2/gender";

    public static void main(String[] args) {

        Gson gson = new Gson();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese un nombre: ");
        String nombre = scanner.nextLine();

        try {
            String generoIngles = obtenerGenero(nombre);
            String generoEspanol = traducirGenero(generoIngles);
            System.out.println("El nombre " + nombre + " es : " + generoEspanol);
        } catch (IOException e) {
            System.err.println("Error al realizar la solicitud a la API: " + e.getMessage());
        }

        Persona persona = new Persona(nombre);

        String json = gson.toJson(persona);
        System.out.println("JSON: " + json);

        Persona personaDesdeJson = gson.fromJson(json, Persona.class);
        System.out.println("Nombre: " + personaDesdeJson.getNombre());
    }

    private static String obtenerGenero(String nombre) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JsonObject json = new JsonObject();
        json.addProperty("first_name", nombre);

        RequestBody body = RequestBody.create(
                json.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseData = response.body().string();
            JsonObject responseJson = new Gson().fromJson(responseData, JsonObject.class);
            boolean resultFound = responseJson.get("result_found").getAsBoolean();

            if (resultFound) {
                return responseJson.get("gender").getAsString();
            } else {
                return "unknown";
            }
        }
    }

    private static String traducirGenero(String generoIngles) {
        switch (generoIngles.toLowerCase()) {
            case "male":
                return "masculino";
            case "female":
                return "femenino";
            default:
                return "desconocido";
        }
    }
}
