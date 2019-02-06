package okhttp;

import com.google.gson.Gson;

import java.io.IOException;

import dialog.AlertDialog;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpService {
    private static HttpService mInstance = null;
    private Gson gson;

    private HttpService() {
        gson = new Gson();
    }

    public static HttpService getInstance() {
        if (mInstance == null) {
            synchronized (HttpService.class) {
                if (mInstance == null)
                    mInstance = new HttpService();
            }
        }
        return mInstance;
    }

    public <ResultObj> void requestData(String url, FormBody formBody,
                                        Class<ResultObj> objClass, HttpBack callBackInterface) {
        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI here.
                        AlertDialog.display("Post Failed", e.toString(), "知道了");
                        callBackInterface.loadFailed(e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        callBackInterface.loadSucceed(gson.fromJson(res,
                                objClass));
                    }
                });
            }
        });
    }

    public <ResultObj> void requestGetData(String url, Class<ResultObj> objClass, HttpBack callBackInterface) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI here.
                        AlertDialog.display("Post Failed", e.toString(), "知道了");
                        callBackInterface.loadFailed(e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        callBackInterface.loadSucceed(gson.fromJson(res,
                                objClass));
                    }
                });
            }
        });
    }
}
