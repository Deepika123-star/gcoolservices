package com.smartwebarts.acrepair.retrofit;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.smartwebarts.acrepair.dashboard.ui.home.SliderImageData;
import com.smartwebarts.acrepair.models.AddWishListResponse;
import com.smartwebarts.acrepair.models.AddressModel;
import com.smartwebarts.acrepair.models.AmountModel;
import com.smartwebarts.acrepair.models.CategoryModel;
import com.smartwebarts.acrepair.models.CouponModels;
import com.smartwebarts.acrepair.models.HomeProductsModel;
import com.smartwebarts.acrepair.models.MessageModel;
import com.smartwebarts.acrepair.models.MyOrderModel;
import com.smartwebarts.acrepair.models.OTPModel;
import com.smartwebarts.acrepair.models.OrderDetailModel;
import com.smartwebarts.acrepair.models.OrderIdModel;
import com.smartwebarts.acrepair.models.OrderListModel;
import com.smartwebarts.acrepair.models.OrderUpdateModel;
import com.smartwebarts.acrepair.models.OrderedResponse;
import com.smartwebarts.acrepair.models.ProductDetailImagesModel;
import com.smartwebarts.acrepair.models.ProductDetailModel;
import com.smartwebarts.acrepair.models.ProductModel;
import com.smartwebarts.acrepair.models.RateCardModel;
import com.smartwebarts.acrepair.models.RegisterSocialModel;
import com.smartwebarts.acrepair.models.SignUpModel;
import com.smartwebarts.acrepair.models.SocialDataCheckModel;
import com.smartwebarts.acrepair.models.SubCategoryModel;
import com.smartwebarts.acrepair.models.SubSubCategoryModel;
import com.smartwebarts.acrepair.models.VendorDeliveryChargesModel;
import com.smartwebarts.acrepair.models.VendorModel;
import com.smartwebarts.acrepair.shared_preference.LoginData;

public interface EndPointInterface {

    @POST("API/api_login")
    @FormUrlEncoded
    Call<LoginData> login(@Field("mobile") String mobile,
                          @Field("password") String password);

    @POST("api.php")
    @FormUrlEncoded
    Call<OTPModel> otp(@Field("mobile") String mobile,
                       @Field("sms") String sms);


    @POST("api.php")
    @FormUrlEncoded
    Call<OrderedResponse> order(@Field("id") String id,
                                @Field("qty") String qty,
                                @Field("proId") String proId,
                                @Field("amount") String amount,
                                @Field("name") String name,
                                @Field("unit") String unit,
                                @Field("unit_in") String unit_in,
                                @Field("thumbnail") String thumbnail,
                                @Field("mobile") String mobile,
                                @Field("orderid") String orderid,
                                @Field("checkout") String checkout,
                                @Field("paymentmethod") String paymentmethod,
                                @Field("address") String address,
                                @Field("landmark") String landmark,
                                @Field("pincode") String pincode,
                                @Field("userdate") String userdate,
                                @Field("usertime") String usertime,
                                @Field("totalamount") String totalamount,
                                @Field("discount") String discount,
                                @Field("vendorid") String vendorid);

    @POST("api.php")
    @FormUrlEncoded
    Call<OrderIdModel> orderid(@Field("getorderid") String getorderid);

    @POST("API/User_Signup")
    @FormUrlEncoded
    Call<SignUpModel> signup(@Field("fullname") String fullname,
                             @Field("email") String email,
                             @Field("mobile") String mobile,
                             @Field("password") String password);

    @POST("api.php")
    @FormUrlEncoded
    Call<RegisterSocialModel> signupsocialuser(@Field("socialregister") String socialregister,
                                               @Field("email") String email,
                                               @Field("mobile") String mobile,
                                               @Field("name") String name);

    @POST("api.php")
    @FormUrlEncoded
    Call<SocialDataCheckModel> checkssocialuser(@Field("socialcheck") String socialcheck,
                                                @Field("email") String email);

    @GET("API/api_historyorders/{id}")
    Call<List<OrderListModel>> orderhistory(@Path("id") String id);


    @POST("api.php")
    @FormUrlEncoded
    Call<List<MyOrderModel>> TodayOrder(@Field("homepage") String homepage,
                                        @Field("id") String userid);


    @POST("api.php")
    @FormUrlEncoded
    Call<List<OrderDetailModel>> OrderDetails(@Field("orderdetails") String orderdetails,
                                              @Field("orderid") String orderid);

    @POST("api.php")
    @FormUrlEncoded
    Call<OrderUpdateModel> OrderUpdate(@Field("orderupdate") String orderupdate,
                                       @Field("orderid") String orderid);

    @POST("api.php")
    @FormUrlEncoded
    Call<List<MyOrderModel>> OrderHistory(@Field("orderhistory") String homepage,
                                          @Field("id") String userid);

    @GET("api.php")
    Call<MessageModel> addWallet(@Field("addwallet") String addwallet,
                                 @Field("amount") String amount,
                                 @Field("rpayid") String rpayid,
                                 @Field("id") String userid);

    @POST("api.php")
    @FormUrlEncoded
    Call<AddWishListResponse> addtowishlist(@Field("productid") String productid,
                                                  @Field("userid") String userid,
                                                  @Field("unitin") String unitin,
                                                  @Field("unit") String unit,
                                                  @Field("wishlist") String wishlist);

    @GET("API/api_categories")
    Call<List<CategoryModel>> categories();

    @GET("API/api_categories")
    Call<List<RateCardModel>> ratecard();

    @GET("API/api_offers/")
    Call<List<SliderImageData>> imageSlider();

    @GET("API/api_homeproducts/{id}")
    Call<List<HomeProductsModel>> homeProducts(@Path("id") String id);

    @GET("API/api_getwallet/{id}")
    Call<AmountModel> userWallet(@Path("id") String id);

    @GET("API/api_addwallet/")
    Call<MessageModel> addwallet(@Query("transactionid") String transactionid,
                                @Query("amount") String amount,
                                @Query("userid") String userid,
                                @Query("usermobile") String usermobile
                                );

    @GET("API/api_getwishlist/{id}")
    Call<List<ProductModel>> apiGetwishlist(@Path("id") String id);

    @GET("API/api_searchproducts/{s}")
    Call<List<SearchModel>> search(@Path("s") String s);

    @GET("API/api_subcategory/{id}")
    Call<List<SubCategoryModel>> subCategory(@Path("id") String id);


    @GET("API/api_subcategory/{id}")
    Call<List<SubCategoryModel>> ratecard(@Path("id") String id);



    @GET("API/api_subsubcategory/{id}/{subid}")
    Call<List<SubSubCategoryModel>> subsubCategory(@Path("id") String id,
                                                   @Path("subid") String subid);
//subCategory change Image
    @GET("API/api_subcategorywiseproduct/{catid}/{subcatid}")
    Call<List<ProductModel>> products(@Path("catid") String catid,
                                                   @Path("subcatid") String subcatid);

    @GET("API/api_vendorwiseproduct/{id}/")
    Call<List<ProductModel>> vendorwiseproduct(@Path("id") String id);

    @GET("API/api_gnewproduct/{id}/{subid}/{subsubid}")
    Call<List<ProductModel>> products(@Path("id") String id,
                                      @Path("subid") String subid,
                                      @Path("subsubid") String subsubid);

    @GET("API/api_specificproduct/{id}")
    Call<List<ProductDetailModel>> getProductDetails(@Path("id") String id);

    @GET("API/api_vendors/{lat}/{lng}")
    Call<List<VendorModel>> getVendors(@Path("lat") String lat,
                                       @Path("lng") String lng);

    @POST("API/api_timeslots")
    @FormUrlEncoded
    Call<List<TimeModel>> getTimeSlot(@Field("pincode") String pincode);

    @GET("API/api_deliverycharges2/{lat}/{lng}/{id}")
    Call<VendorDeliveryChargesModel> getDeliveryChargesAndVendorList(@Path("lat") String lat,
                                                                     @Path("lng") String lng,
                                                                     @Path("id") String id);

    @GET("API/api_distance/{lat}/{lng}/{id}")
    Call<VendorDeliveryChargesModel> api_distance(@Path("lat") String lat,
                                                                     @Path("lng") String lng,
                                                                     @Path("id") String id);

    @GET("API/api_productimages/{id}")
    Call<List<ProductDetailImagesModel>> getProductImages(@Path("id") String id);

    @GET("API/api_useraddress/{id}")
    Call<List<AddressModel>> getAddress(@Path("id") String userid);

    @GET("API/api_getcouponcodes/{id}")
    Call<List<CouponModels>> getCouponCodes(@Path("id") String userid);

    @POST("api.php")
    @FormUrlEncoded
    Call<OrderIdModel> cancelorder(@Field("cancel") String s,
                                   @Field("orderid") String orderId,
                                   @Field("reason") String reason,
                                   @Field("mobile") String mobile);

    /*for adding address api.php  params insertaddress=1,userid,house_number,landmark,city,pin_code,addr_type,*/

    @POST("api.php")
    @FormUrlEncoded
    Call<OrderIdModel> setAddress(@Field("insertaddress") String insertaddress,
                                  @Field("userid") String userid,
                                  @Field("house_number") String house_number,
                                  @Field("landmark") String landmark,
                                  @Field("city") String city,
                                  @Field("pin_code") String pin_code,
                                  @Field("addr_type") String addr_type);


    @GET("API/returnproductbyid/{id}")
    Call<MessageModel> returnProductByProId(@Path("id") String userid);


    @POST("api.php")
    @FormUrlEncoded
    Call<AddressModel> getaddress(@Field("getaddress") String getaddress,
                                  @Field("userid") String userid);


    @POST("api.php")
    @FormUrlEncoded
    Call<AddressModel> setAddress(@Field("insertaddress") String insertaddress,
                                  @Field("userid") String userid,
                                  @Field("address") String house_number,
                                  @Field("city") String city,
                                  @Field("pin_code") String pin_code);
    //API for Rate Card
    @GET("API/Getallproduct/{id}")
    Call<List<RateCardModel>>setServices(@Path("id") String id);

    @GET("API/updateAccessToken/{id}/{access_token}")
    Call<MessageModel> updateAccessToken(@Path("id") String id,
                                         @Path("access_token") String _token);
}
