package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.TableService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TableServiceImpl implements TableService {
    private static final String API_URL = "http://localhost:8000/api/tables";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper;

    public TableServiceImpl() {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public ApiResponse list(
            String keyword,
            String sortBy,
            String sortDirection,
            int page,
            int size,
            String statusFilter
    ) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL).newBuilder()
                    .addQueryParameter("keyword", keyword != null ? keyword : "")
                    .addQueryParameter("sortBy", sortBy != null ? sortBy : "id")
                    .addQueryParameter("sortDirection", sortDirection != null ? sortDirection : "asc")
                    .addQueryParameter("page", String.valueOf(page > 0 ? page : 1))
                    .addQueryParameter("size", String.valueOf(size > 0 ? size : 10))
                    .addQueryParameter("statusFilter", statusFilter != null ? statusFilter : "without_deleted");

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return ApiResponse.builder()
                            .status(response.code())
                            .message("Failed to fetch tables")
                            .errors(Map.of("error", "Unexpected response code: " + response.code()))
                            .build();
                }
                return objectMapper.readValue(response.body().string(), ApiResponse.class);
            }
        } catch (IOException e) {
            return ApiResponse.builder()
                    .status(500)
                    .message("Internal Server Error")
                    .errors(Map.of("exception", e.getMessage()))
                    .build();
        }
    }

    @Override
    public ApiResponse getTableById(long id) {
        try {
            HttpUrl url = HttpUrl.parse(API_URL + "/" + id).newBuilder().build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return ApiResponse.builder()
                            .status(response.code())
                            .message("Failed to fetch table")
                            .errors(Map.of("error", "Unexpected response code: " + response.code()))
                            .build();
                }
                return objectMapper.readValue(response.body().string(), ApiResponse.class);
            }
        } catch (IOException e) {
            return ApiResponse.builder()
                    .status(500)
                    .message("Internal Server Error")
                    .errors(Map.of("exception", e.getMessage()))
                    .build();
        }
    }

    @Override
    public ApiResponse createTable(Table table) {
        try {
            HttpUrl url = HttpUrl.parse(API_URL).newBuilder().build();

            String jsonBody = objectMapper.writeValueAsString(table);
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
            System.out.println(jsonBody);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return objectMapper.readValue(response.body().string(), ApiResponse.class);
            }

        } catch (IOException e) {
            return ApiResponse.builder()
                    .status(500)
                    .message("Internal Server Error")
                    .errors(Map.of("exception", e.getMessage()))
                    .build();
        }
    }


	@Override
	public ApiResponse updateTable(Table table){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse removeTable(long id){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse removeTables(List<Long> ids){
		// TODO Auto-generated method stub
		return null;
	}
}
