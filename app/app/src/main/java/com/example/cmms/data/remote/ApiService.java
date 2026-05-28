package com.example.cmms.data.remote;

import com.example.cmms.data.remote.models.AddPartRequest;
import com.example.cmms.data.remote.models.AssignTechnicianRequest;
import com.example.cmms.data.remote.models.CreateMachineRequest;
import com.example.cmms.data.remote.models.CreateWorkOrderRequest;
import com.example.cmms.data.remote.models.DashboardResponse;
import com.example.cmms.data.remote.models.LocationResponse;
import com.example.cmms.data.remote.models.LoginRequest;
import com.example.cmms.data.remote.models.LoginResponse;
import com.example.cmms.data.remote.models.MachineResponse;
import com.example.cmms.data.remote.models.NotificationResponse;
import com.example.cmms.data.remote.models.PartResponse;
import com.example.cmms.data.remote.models.UpdateMachineRequest;
import com.example.cmms.data.remote.models.UpdateOperatingHoursRequest;
import com.example.cmms.data.remote.models.UpdateWorkOrderRequest;
import com.example.cmms.data.remote.models.UpdateWorkOrderStatusRequest;
import com.example.cmms.data.remote.models.UserResponse;
import com.example.cmms.data.remote.models.WorkOrderPartResponse;
import com.example.cmms.data.remote.models.WorkOrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
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

    @POST("machines")
    Call<MachineResponse> createMachine(@Body CreateMachineRequest request);

    @PATCH("machines/{id}")
    Call<MachineResponse> updateMachine(@Path("id") String id, @Body UpdateMachineRequest request);

    @DELETE("machines/{id}")
    Call<Void> deleteMachine(@Path("id") String id);

    @PATCH("machines/{id}/operating-hours")
    Call<MachineResponse> updateOperatingHours(@Path("id") String id, @Body UpdateOperatingHoursRequest request);

    @GET("work-orders")
    Call<List<WorkOrderResponse>> getWorkOrders();

    @GET("work-orders/{id}")
    Call<WorkOrderResponse> getWorkOrderById(@Path("id") String id);

    @POST("work-orders")
    Call<WorkOrderResponse> createWorkOrder(@Body CreateWorkOrderRequest request);

    @PATCH("work-orders/{id}")
    Call<WorkOrderResponse> updateWorkOrder(@Path("id") String id, @Body UpdateWorkOrderRequest request);

    @PATCH("work-orders/{id}/status")
    Call<WorkOrderResponse> updateWorkOrderStatus(@Path("id") String id, @Body UpdateWorkOrderStatusRequest request);

    @PATCH("work-orders/{id}/assign")
    Call<WorkOrderResponse> assignTechnician(@Path("id") String id, @Body AssignTechnicianRequest request);

    @GET("work-orders/{id}/parts")
    Call<List<WorkOrderPartResponse>> getWorkOrderParts(@Path("id") String id);

    @POST("work-orders/{id}/parts")
    Call<WorkOrderPartResponse> addPartToWorkOrder(@Path("id") String id, @Body AddPartRequest request);

    @GET("users")
    Call<okhttp3.ResponseBody> getUsers(@Query("role") String role);

    @GET("inventory/parts")
    Call<okhttp3.ResponseBody> getAllParts();

    @GET("locations")
    Call<List<LocationResponse>> getLocations();

    @GET("notifications")
    Call<List<NotificationResponse>> getNotifications();

    @PATCH("notifications/{id}/read")
    Call<Void> markNotificationRead(@Path("id") String id);

    @POST("auth/logout")
    Call<Void> logout();
}