package com.project1.softwaresoluitons.xyz;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamojo.android.Instamojo;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class amount extends AppCompatActivity {

    private static final HashMap<String, String> env_options = new HashMap<>();

    static {
        env_options.put("Test", "https://test.instamojo.com/");
        env_options.put("Production", "https://api.instamojo.com/");
    }

    private ProgressDialog dialog;
    private TextView amountBox;
    private String currentEnv = null;
    private String accessToken = null;
    public String price,paymentID,training_id;
    public RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);
        Button button = (Button) findViewById(R.id.pay);
        Intent i=getIntent();
        price=i.getStringExtra("price");
        training_id=i.getStringExtra("training_id");
        queue=Volley.newRequestQueue(getApplicationContext());
        amountBox = (TextView) findViewById(R.id.amount);
        amountBox.setText(price);
        final ArrayList<String> envs = new ArrayList<>(env_options.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, envs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("please wait...");
        dialog.setCancelable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchTokenAndTransactionID();
            }
        });
        Instamojo.setBaseUrl("https://api.instamojo.com/");

        //let's set the log level to debug
        Instamojo.setLogLevel(Log.DEBUG);
    }

    // this is for the market place
    // you should have created the order from your backend and pass back the order id to app for the payment
    private void fetchOrder(String accessToken, String orderID){
        // Good time to show dialog
        Request request = new Request(accessToken, orderID, new OrderRequestCallBack() {
            @Override
            public void onFinish(final Order order, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                showToast("No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                showToast("Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                showToast("Access token is invalid or expired. Please Update the token!!");
                            } else {
                                showToast(error.toString());
                            }
                            return;
                        }

                        startPreCreatedUI(order);
                    }
                });

            }
        });

        request.execute();
    }

    private void createOrder(String accessToken, String transactionID) {
        SharedPreferences sh=getSharedPreferences("user",MODE_PRIVATE);
        String name = sh.getString("name",null);
        final String email = sh.getString("email",null);
        String phone = sh.getString("contact",null);
        String amount = amountBox.getText().toString();
        String description = "no description";

        //Create the Order
        Order order = new Order(accessToken, transactionID, name, email, phone, amount, description);

        //set webhook

        //Validate the Order
        if (!order.isValid()) {
            //oops order validation failed. Pinpoint the issue(s).

            if (!order.isValidEmail()) {
                showToast("registered email is invalid");
            }

            if (!order.isValidPhone()) {
                showToast("registered contact is invalid");
            }

            if (!order.isValidAmount()) {
                amountBox.setError("Amount is invalid or has more than two decimal places");
            }

            if (!order.isValidDescription()) {
                showToast("description is invalid");
            }

            if (!order.isValidTransactionID()) {
                showToast("Transaction is Invalid");
            }

            if (!order.isValidRedirectURL()) {
                showToast("Redirection URL is invalid");
            }

            if (!order.isValidWebhook()) {
                showToast("Webhook URL is invalid");
            }

            return;
        }

        //Validation is successful. Proceed
        dialog.show();
        Log.i("url","url");
        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(final Order order, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("url1","url1");
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                showToast("No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                showToast("Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                showToast("Access token is invalid or expired. Please Update the token!!");
                            } else if (error instanceof Errors.ValidationError) {
                                // Cast object to validation to pinpoint the issue
                                Errors.ValidationError validationError = (Errors.ValidationError) error;

                                if (!validationError.isValidTransactionID()) {
                                    showToast("Transaction ID is not Unique");
                                    return;
                                }

                                if (!validationError.isValidRedirectURL()) {
                                    showToast("Redirect url is invalid");
                                    return;
                                }

                                if (!validationError.isValidWebhook()) {
                                    showToast("Webhook url is invalid");
                                    return;
                                }

                                if (!validationError.isValidPhone()) {
                                    showToast("Buyer's Phone Number is invalid/empty");
                                    return;
                                }

                                if (!validationError.isValidEmail()) {
                                   showToast("Buyer's Email is invalid/empty");
                                    return;
                                }

                                if (!validationError.isValidAmount()) {
                                    amountBox.setError("Amount is either less than Rs.9 or has more than two decimal places");
                                    return;
                                }

                                if (!validationError.isValidName()) {
                                    showToast("Buyer's Name is required");
                                    return;
                                }
                            } else {
                                showToast(error.getMessage());
                            }
                            return;
                        }

                        startPreCreatedUI(order);
                    }
                });
            }
        });

        request.execute();
    }

    private void startPreCreatedUI(Order order) {
        //Using Pre created UI
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void startCustomUI(Order order) {
        //Custom UI Implementation
        Intent intent = new Intent(getBaseContext(), CustomUIActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     *
     * Fetch Access token and unique transactionID from developers server
     */
    private void fetchTokenAndTransactionID() {
        if (!dialog.isShowing()) {
            dialog.show();
        }

        OkHttpClient client = new OkHttpClient();
        HttpUrl url = getHttpURLBuilder()
                .addPathSegment("create")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("env", "Production".toLowerCase())
                .add("client_id","05G825EiIZqFD7qEMoNsV4f6euWIKXKAuwp3UO2M")
                .add("client_secrert","4pD1opFWzTc5thrD3jbzwBqv0i9wLiTLbQWdaVuA6uL8qWph0VpDgyJcVkKOwYb8vRy8x06Hw546smZd2un8vsnLp4hOG4FfrmfFabEkUKVsgJQ2YavjHRiObeLyRDdD")
                .add("grant_type","client_credentials")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        showToast("Failed to fetch the Order Tokens");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString;
                String errorMessage = null;
                String transactionID = null;
                responseString = response.body().string();
                response.body().close();
                Log.i("response",responseString);
                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    if (responseObject.has("error")) {
                        errorMessage = responseObject.getString("error");
                    } else {
                        accessToken = responseObject.getString("access_token");
                        transactionID = responseObject.getString("transaction_id");
                    }
                } catch (JSONException e) {
                    errorMessage = "Failed to fetch Order tokens";
                }

                final String finalErrorMessage = errorMessage;
                final String finalTransactionID = transactionID;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (finalErrorMessage != null) {
                            showToast(finalErrorMessage);
                            return;
                        }

                        createOrder(accessToken, finalTransactionID);
                    }
                });

            }
        });

    }

    /**
     * Will check for the transaction status of a particular Transaction
     *
     * @param transactionID Unique identifier of a transaction ID
     */
    private void checkPaymentStatus(final String transactionID, final String orderID) {
        if (accessToken == null || (transactionID == null && orderID == null)) {
            return;
        }

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        showToast("checking transaction status");
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder builder = getHttpURLBuilder();
        builder.addPathSegment("status");
        if (transactionID != null){
            builder.addQueryParameter("transaction_id", transactionID);
        } else {
            builder.addQueryParameter("id", orderID);
        }
        builder.addQueryParameter("env", "Production".toLowerCase());
        HttpUrl url = builder.build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        showToast("Failed to fetch the Transaction status");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                response.body().close();
                String status = null;
                paymentID = null;
                String amount = null;
                String errorMessage = null;

                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    JSONObject payment = responseObject.getJSONArray("payments").getJSONObject(0);
                    status = payment.getString("status");
                    paymentID = payment.getString("id");
                    amount = responseObject.getString("amount");

                } catch (JSONException e) {
                    errorMessage = "Failed to fetch the Transaction status";
                }

                final String finalStatus = status;
                final String finalErrorMessage = errorMessage;
                final String finalPaymentID = paymentID;
                final String finalAmount = amount;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (finalStatus == null) {
                            showToast(finalErrorMessage);
                            return;
                        }

                        if (!finalStatus.equalsIgnoreCase("successful")) {
                            showToast("Transaction still pending");
                            return;
                        }

                       // showToast("Transaction Successful for id - " + finalPaymentID);
                        refundTheAmount(transactionID, finalAmount);
                        register();

                    }
                });
            }
        });

    }

    public void register(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, URLs.URL_ROOT,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.i("response",response);
                        finish();
                        Toast.makeText(amount.this,"Transaction successful! Your have registered for the training",Toast.LENGTH_LONG).show();

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finish();
                        Toast.makeText(amount.this,"Please try again! Transaction unsuccessful",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences sh=getSharedPreferences("user",MODE_PRIVATE);
                params.put("type","register_training_user");
                   /* params.put("name",sh.getString("name",null));
                    params.put("email",sh.getString("email",null));
                    params.put("contact",sh.getString("contact",null));  */
                params.put("payment_ID",paymentID);
                params.put("amount",price);
                params.put("user_id",sh.getInt("usr_id",0)+"");
                params.put("training_id",training_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    /**
     * Will initiate a refund for a given transaction with given amount
     *
     * @param transactionID Unique identifier for the transaction
     * @param amount    amount to be refunded
     */
    private void refundTheAmount(String transactionID, String amount) {
        if (accessToken == null || transactionID == null || amount == null) {
            return;
        }

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        showToast("Initiating a refund for - " + amount);
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = getHttpURLBuilder()
                .addPathSegment("refund")
                .addPathSegment("")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("env", currentEnv.toLowerCase())
                .add("transaction_id", transactionID)
                .add("amount", amount)
                .add("type", "PTH")
                .add("body", "Refund the Amount")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        showToast("Failed to Initiate a refund");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        String message;

                        if (response.isSuccessful()) {
                            message = "Refund intiated successfully";
                        } else {
                            message = "Failed to Initiate a refund";
                        }

                        showToast(message);
                    }
                });
            }
        });
    }

    private HttpUrl.Builder getHttpURLBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("sample-sdk-server.instamojo.com");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (transactionID != null || paymentID != null) {
                checkPaymentStatus(transactionID, orderID);
            } else {
                showToast("Oops!! Payment was cancelled");
            }
        }
    }
}
