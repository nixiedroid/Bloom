package com.nixiedroid.bloomlwp.weather.owm;


import com.nixiedroid.bloomlwp.weather.owm.enums.ErrorResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;

public class HttpUrlConnector {
    public static final URL API_URL = Util.constantURL("https://api.openweathermap.org/data/2.5/weather");

    private static HttpURLConnection createUrlConnection(URL url) throws OWMConnectorException {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setUseCaches(false);
            return connection;
        } catch (IOException ignored) {
            throw new OWMConnectorException(ErrorResult.INTERNAL_INVALID_URL);
        }
    }

    public static OWMWeatherCode getWeatherCode(String APIKey, double lat, double lon) throws OWMConnectorException {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<OWMWeatherCode> cod = exec.submit(new Callable<OWMWeatherCode>() {
            @Override
            public OWMWeatherCode call() throws OWMConnectorException {
                //lat={lat}&lon={lon}&appid={API key}
                URL url = Util.concatenateURL(API_URL, "lat=" + lat);
                url = Util.concatenateURL(url, "lon=" + lon);
                url = Util.concatenateURL(url, "appid=" + APIKey);
                HttpURLConnection connection =  createUrlConnection(url);
                int weatherId = Util.getWeatherIdFromJson(readInputStream(connection));
                return new OWMWeatherCode(weatherId);
            }
        });
        OWMWeatherCode responseCode;
        try {
            responseCode = cod.get();
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            throw new OWMConnectorException(e.getMessage());
        }
        return responseCode;
    }

    private static String readInputStream(final HttpURLConnection connection) throws OWMConnectorException {
        InputStream inputStream = null;
        try {
            final int status = connection.getResponseCode();
            if (status < 400) {
                inputStream = connection.getInputStream();
                final String result = Util.toString(inputStream, StandardCharsets.UTF_8);
                if (result.isEmpty()) {
                    throw new OWMConnectorException(ErrorResult.NO_INTERNET);
                }
                return result;
            } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED || status == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new OWMConnectorException(ErrorResult.INVALID_API_KEY);
            } else if (status == 429) {
                throw new OWMConnectorException(ErrorResult.INTERNAL_TOO_MUCH_REQUESTS);
            } else if (status >= 500) {
                throw new OWMConnectorException(ErrorResult.INTERNAL_SERVER_ERROR);
            }
            throw new OWMConnectorException(ErrorResult.UNKNOWN);
        } catch (final IOException e) {
            throw new OWMConnectorException(ErrorResult.NO_INTERNET);
        } finally {
            Util.closeQuietly(inputStream);
        }
    }
}
