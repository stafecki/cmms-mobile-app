package com.example.cmms.data.remote;

import com.example.cmms.data.remote.models.DashboardResponse;
import com.example.cmms.data.remote.models.LoginRequest;
import com.example.cmms.data.remote.models.LoginResponse;
import com.example.cmms.data.remote.models.MachineResponse;
import com.example.cmms.data.remote.models.NotificationResponse;
import com.example.cmms.data.remote.models.UserResponse;
import com.example.cmms.data.remote.models.WorkOrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("auth/me")
    Call<UserResponse> getMe();

    @GET("dashboard")
    Call<DashboardResponse> getDashboard(@Query("period") String period);

    @GET("machines")
    Call<List<MachineResponse>> getMachines();

    @GET("machines/{id}")
    Call<MachineResponse> getMachineById(@Path("id") String id);

    @GET("work-orders")
    Call<List<WorkOrderResponse>> getWorkOrders();

    @GET("work-orders/{id}")
    Call<WorkOrderResponse> getWorkOrderById(@Path("id") String id);

    @GET("notifications")
    Call<List<NotificationResponse>> getNotifications();

    @POST("auth/logout")
    Call<Void> logout();
}