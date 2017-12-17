package vn.busmap.busstation.Utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by dangkhoa on 9/26/17.
 */

public class AsyncHttp {
    private static String host = "https://api.busmap.vn/v1";
    static AsyncHttpClient client = new AsyncHttpClient();

    private static void config() {
        client.setResponseTimeout(10000);
    }

    private static String getAbsoluteUrl(String route) {
        return host + route;
    }

    public static void GET(String route, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        client.get(getAbsoluteUrl(route), params, asyncHttpResponseHandler);
    }

    public static void POST(String route, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        client.post(getAbsoluteUrl(route), params, asyncHttpResponseHandler);
    }

    public static void PATCH(String route, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        client.patch(getAbsoluteUrl(route), params, asyncHttpResponseHandler);
    }

    public static void DELETE(String route, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        client.delete(getAbsoluteUrl(route), params, asyncHttpResponseHandler);
    }
}
