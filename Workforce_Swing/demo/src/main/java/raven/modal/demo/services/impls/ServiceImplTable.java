package raven.modal.demo.services.impls;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import raven.modal.demo.models.ModelTable;
import raven.modal.demo.services.ServiceTable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ServiceImplTable implements ServiceTable{
    private static final String API_URL = "http://localhost:8000/api/tables";
    private static final String API_URL_ID = API_URL + "/";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ServiceImplTable() {
        // Khởi tạo OkHttpClient
        this.client = new OkHttpClient();

        // Cấu hình ObjectMapper với PropertyNamingStrategies và JavaTimeModule
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.objectMapper.registerModule(new JavaTimeModule()); // Đăng ký module để hỗ trợ LocalDateTime
    }

    public List<ModelTable> getAllTables() throws IOException {
        // Tạo request HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Chuyển JSON thành danh sách ModelTable
            ModelTable[] tables = objectMapper.readValue(response.body().string(), ModelTable[].class);
            return Arrays.asList(tables);
        }
    }

    @Override
    public ModelTable getTableById(Long id) throws IOException {
        // Tạo URL endpoint cho table ID
        String url = API_URL_ID + id;

        // Tạo request HTTP GET
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Thực hiện request và xử lý response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Chuyển JSON response thành ModelTable object
            return objectMapper.readValue(response.body().string(), ModelTable.class);
        }
    }

	@Override
	public void createTable(ModelTable table) throws IOException {
	    // Tạo JSON body với tham số name
	    String jsonBody = objectMapper.writeValueAsString(table);

	    // Tạo request HTTP POST
	    Request request = new Request.Builder()
	            .url(API_URL) // Không cần truyền name qua URL nữa
	            .post(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"))) // Gửi body chứa name
	            .build();

	    try (Response response = client.newCall(request).execute()) {
	        if (!response.isSuccessful()) {
	            throw new IOException("Unexpected code " + response);
	        }
	        // Không cần trả về giá trị nữa, chỉ cần quăng lỗi nếu không thành công
	    }
	}

	@Override
	public void updateTable(ModelTable modelTable) throws IOException {
	    // Chuyển đổi đối tượng modelTable thành JSON
	    String jsonBody = objectMapper.writeValueAsString(modelTable);
	    System.out.println(jsonBody);
	    // Tạo request HTTP PUT
	    Request request = new Request.Builder()
	            .url(API_URL_ID + modelTable.getId()) // API endpoint để cập nhật bảng
	            .put(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"))) // Gửi JSON body
	            .build();
	    
	    // Thực hiện request
	    try (Response response = client.newCall(request).execute()) {
	        if (!response.isSuccessful()) {
	            throw new IOException("Unexpected code " + response);
	        }
	    }
	}
	@Override
	public void removeTable(long id) throws IOException {
	    // Tạo URL endpoint cho API xóa
	    String url = API_URL_ID + id;

	    // Tạo request HTTP DELETE
	    Request request = new Request.Builder()
	            .url(url)
	            .delete() // Phương thức DELETE
	            .build();

	    // Thực hiện request và xử lý response
	    try (Response response = client.newCall(request).execute()) {
	        if (!response.isSuccessful()) {
	            throw new IOException("Failed to delete table with ID: " + id + ". Unexpected code: " + response.code());
	        }
	    }
	}
	@Override
	public void removeTables(List<Long> ids) throws IOException {
	    // Tạo URL endpoint cho API xóa
	    String url = API_URL;  // Giả sử API URL của bạn là "/remove"

	    // Tạo JSON body chứa mảng các ID cần xóa
	    String json = "{\"ids\": " + ids.toString() + "}";  // Sử dụng toString() của List<Long> để chuyển thành chuỗi

	    // Tạo request HTTP DELETE
	    RequestBody body = RequestBody.create(
	            json, MediaType.parse("application/json; charset=utf-8"));

	    Request request = new Request.Builder()
	            .url(url)
	            .delete(body)  // Phương thức DELETE (đúng là DELETE, không phải POST)
	            .build();

	    // Thực hiện request và xử lý response
	    try (Response response = client.newCall(request).execute()) {
	        if (!response.isSuccessful()) {
	            throw new IOException("Failed to delete tables. Unexpected code: " + response.code());
	        }
	    }
	}
	@Override
	public List<ModelTable> searchTables(String search) throws IOException {
	    // Tạo URL endpoint cho API tìm kiếm với tham số q
	    String url = API_URL + "/search?q=" + search;

	    // Tạo request HTTP GET
	    Request request = new Request.Builder()
	            .url(url)
	            .build();

	    // Thực hiện request và xử lý response
	    try (Response response = client.newCall(request).execute()) {
	        if (!response.isSuccessful()) {
	            throw new IOException("Unexpected code " + response);
	        }

	        // Chuyển JSON response thành danh sách ModelTable
	        ModelTable[] tables = objectMapper.readValue(response.body().string(), ModelTable[].class);
	        return Arrays.asList(tables);
	    }
	}

}
