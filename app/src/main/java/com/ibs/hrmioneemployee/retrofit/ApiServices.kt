package com.ibs.hrmioneemployee.retrofit

import com.ibs.hrmioneemployee.models.api_models.mail.MailResponse
import com.ibs.hrmioneemployee.models.api_models.attendance_check_in_out.PunchInOutResponse
import com.ibs.hrmioneemployee.models.api_models.attendance_history.AttendanceHistoryResponse
import com.ibs.hrmioneemployee.models.api_models.attendance_of_month.AttendanceOfMonthResponse
import com.ibs.hrmioneemployee.models.api_models.company_holidays_list.CompanyHolidaysListResponse
import com.ibs.hrmioneemployee.models.api_models.company_policy.CompanyPolicyResponse
import com.ibs.hrmioneemployee.models.api_models.create_leave.CreateLeaveModel
import com.ibs.hrmioneemployee.models.api_models.create_leave.CreateLeaveResponse
import com.ibs.hrmioneemployee.models.api_models.employees_list.EmployeesListResponse
import com.ibs.hrmioneemployee.models.api_models.generateOTPForResetPassword.otpForResetPasswordResponse
import com.ibs.hrmioneemployee.models.api_models.homepage.HomepageResponse
import com.ibs.hrmioneemployee.models.api_models.leave_type_list.LeaveTypeListResponse
import com.ibs.hrmioneemployee.models.api_models.login.LoginModel
import com.ibs.hrmioneemployee.models.api_models.login.LoginResponse
import com.ibs.hrmioneemployee.models.api_models.my_leave_balance.MyLeaveBalanceResponse
import com.ibs.hrmioneemployee.models.api_models.notification.DeleteAllNotificationResponse
import com.ibs.hrmioneemployee.models.api_models.notification.DeleteSingleNotificationResponse
import com.ibs.hrmioneemployee.models.api_models.notification.NotificationListResponse
import com.ibs.hrmioneemployee.models.api_models.payslip.PayslipResponse
import com.ibs.hrmioneemployee.models.api_models.reset_password.ResetPasswordModel
import com.ibs.hrmioneemployee.models.api_models.reset_password.ResetPasswordResponse
import com.ibs.hrmioneemployee.models.api_models.sign_up.SignUpModel
import com.ibs.hrmioneemployee.models.api_models.sign_up.SignUpResponse
import com.ibs.hrmioneemployee.models.api_models.signature.GetSignatureResponse
import com.ibs.hrmioneemployee.models.api_models.signature.PostSignatureResponse
import com.ibs.hrmioneemployee.models.api_models.signout.SignoutResponse
import com.ibs.hrmioneemployee.models.api_models.verify_otp.VerifyOtpModel
import com.ibs.hrmioneemployee.models.api_models.verify_otp.VerifyOtpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {

    @POST("user/signup")
    fun createUser(@Body signUpModel: SignUpModel) : Call<SignUpResponse>

    @POST("employee/verifyOTP")
    fun verifyOTP(@Body verifyOtpModel: VerifyOtpModel) : Call<VerifyOtpResponse>

    @POST("employee/signIn")
    fun loginUser(@Body loginModel: LoginModel) : Call<LoginResponse>

    @POST("employee/resetpassword")
    fun resetPassword(@Body resetPasswordModel: ResetPasswordModel) : Call<ResetPasswordResponse>

    @GET("employee/otp/generateOTPForResetPassword/{email}")
    fun generateOTPForResetPassword(@Path("email") email: String) : Call<otpForResetPasswordResponse>

    @Multipart
    @POST("employee/editprofile")
    fun editProfileWithImage(@Header("Authorization") Authorization: String,
                             @Part image: MultipartBody.Part,
                             @Part("userId") userId: Int,
                             @Part("firstName") firstName: RequestBody,
                             @Part("lastName") lastName: RequestBody,
                             @Part("dob") dob: RequestBody,
                             @Part("gender") gender: RequestBody,
                             @Part("maritalStatus") maritalStatus: RequestBody,
                             @Part("nationality") nationality: RequestBody,
                             @Part("userName") userName: RequestBody,
                             @Part("email") email: RequestBody,
                             @Part("countryCode") countryCode: Int,
                             @Part("mobileNumber") mobileNumber: RequestBody,
                             @Part("currentAddress") currentAddress: RequestBody,
                             @Part("degreeEducation") degreeEducation: RequestBody,
                             @Part("intermediate") intermediate: RequestBody,
                             @Part("highSchool") highSchool: RequestBody) : Call<LoginResponse>

    @FormUrlEncoded
    @POST("employee/editprofile")
    fun editProfileWithoutImage(@Header("Authorization") Authorization: String,
                                @Field("userId") userId: Int,
                                @Field("firstName") firstName: String,
                                @Field("lastName") lastName: String,
                                @Field("dob") dob: String,
                                @Field("gender") gender: String,
                                @Field("maritalStatus") maritalStatus: String,
                                @Field("nationality") nationality: String,
                                @Field("userName") userName: String,
                                @Field("email") email: String,
                                @Field("countryCode") countryCode: Int,
                                @Field("mobileNumber") mobileNumber: String,
                                @Field("currentAddress") currentAddress: String,
                                @Field("degreeEducation") degreeEducation: String,
                                @Field("intermediate") intermediate: String,
                                @Field("highSchool") highSchool: String) : Call<LoginResponse>

    @FormUrlEncoded
    @POST("employee/editprofile")
    fun editProfileBank(@Header("Authorization") Authorization: String,
                        @Field("userId") userId: Int,
                        @Field("bankName") bankName: String,
                        @Field("accountHolderName") accountHolderName: String,
                        @Field("accountNumber") accountNumber: String,
                        @Field("branchAddress") branchAddress: String,
                        @Field("ifscCode") ifscCode: String) : Call<LoginResponse>

    @FormUrlEncoded
    @POST("employee/getAttendaneHistory")
    fun attendanceHistoryWithFilter(@Header("Authorization") Authorization: String,
                                    @Field("employeeId") employeeId: Int,
                                    @Field("startDate") startDate: Long,
                                    @Field("endDate") endDate: Long,
                                    @Field("endDay") endDay: String) : Call<AttendanceHistoryResponse>

    @FormUrlEncoded
    @POST("employee/getAttendaneHistory")
    fun attendanceHistoryWithoutFilter(@Header("Authorization") Authorization: String,
                                       @Field("employeeId") employeeId: Int) : Call<AttendanceHistoryResponse>

    @FormUrlEncoded
    @POST("attendance")
    fun checkInCheckOutWFH(@Header("Authorization") Authorization: String,
                           @Field("employeeId") userId: Int,
                           @Field("workingLocation") workingLocation: String,
                           @Field("lat") lat: Double,
                           @Field("lng") lng: Double,
                           @Field("currentLocation") currentLocation: String) : Call<PunchInOutResponse>

    @FormUrlEncoded
    @POST("attendance")
    fun checkInCheckOutWFO(@Header("Authorization") Authorization: String,
                           @Field("employeeId") userId: Int,
                           @Field("workingLocation") workingLocation: String,
                           @Field("lat") lat: Double,
                           @Field("lng") lng: Double,
                           @Field("currentLocation") currentLocation: String) : Call<PunchInOutResponse>

    @FormUrlEncoded
    @POST("getAttendanceOfMonth")
    fun attendanceOfMonth(@Header("Authorization") Authorization: String,
                          @Field("userId") userId: Int,
                          @Field("month") month: String,
                          @Field("year") year: Int) : Call<AttendanceOfMonthResponse>

    @GET("employee/getEmployeesOfOrganization/{employeeId}")
    suspend fun getEmployeesList(@Header("Authorization") Authorization: String,
                         @Path("employeeId") employeeId: Int) : Response<EmployeesListResponse>

    @FormUrlEncoded
    @POST("employee/companyHolidaysList")
    fun companyHolidaysList(@Header("Authorization") Authorization: String,
                            @Field("employeeId") employeeId: Int,
                            @Field("year") year: Int) : Call<CompanyHolidaysListResponse>

    @GET("employee/getLeaveTypeList/{employeeId}")
    fun getLeaveTypeList(@Header("Authorization") Authorization: String,
                         @Path("employeeId") employeeId: Int) : Call<LeaveTypeListResponse>

    @POST("leave/createLeave")
    fun createLeave(@Header("Authorization") Authorization: String,
                    @Body createLeaveModel: CreateLeaveModel) : Call<CreateLeaveResponse>

    @GET("leave/myLeaveBalance/{employeeId}")
    fun myLeaveBalance(@Header("Authorization") Authorization: String,
                       @Path("employeeId") employeeId: Int) : Call<MyLeaveBalanceResponse>

    @GET("employee/signout/{employeeId}")
    fun signOut(@Header("Authorization") Authorization: String,
                @Path("employeeId") employeeId: Int) : Call<SignoutResponse>

    @FormUrlEncoded
    @POST("hr/employeePayslips")
    fun payslip(@Header("Authorization") Authorization: String,
                @Field("employeeId") employeeId: Int,
                @Field("year") year: Int) : Call<PayslipResponse>
    
    @GET("employee/homePage/{employeeId}")
    fun homepage(@Header("Authorization") Authorization: String,
                 @Path("employeeId") employeeId: Int) : Call<HomepageResponse>

    @GET("notification/getEmployeeNotifications/{employeeId}")
    fun notificationList(@Header("Authorization") Authorization: String,
                 @Path("employeeId") employeeId: Int) : Call<NotificationListResponse>

    @DELETE("notification/deleteEmployeeNotification/{notificationId}")
    fun deleteSingleNotification(@Header("Authorization") Authorization: String,
                         @Path("notificationId") notificationId: Int) : Call<DeleteSingleNotificationResponse>

    @DELETE("notification/deleteAllEmployeeNotification/{employeeId}")
    fun deleteAllNotification(@Header("Authorization") Authorization: String,
                              @Path("employeeId") employeeId: Int) : Call<DeleteAllNotificationResponse>

    @GET("employee/getPrivacyPolicy/{employeeId}")
    fun companyPolicy(@Header("Authorization") Authorization: String,
                      @Path("employeeId") employeeId: Int) : Call<CompanyPolicyResponse>

    @Multipart
    @POST("employee/signature")
    fun postSignature(@Header("Authorization") Authorization: String,
                      @Part file: MultipartBody.Part,
                      @Part("employeeId") employeeId: Int,
                      @Part("isAgree") isAgree: Int) : Call<PostSignatureResponse>

    @GET("employee/getSignature/{employeeId}")
    fun getSignature(@Header("Authorization") Authorization: String,
                     @Path("employeeId") employeeId: Int) : Call<GetSignatureResponse>

    //Mail Send
    @Multipart
    @POST("email/sendEmail")
    fun sendMail(@Header("Authorization") Authorization:String,
                 @Part("sender") sender:RequestBody,
                 @Part("receiver") receiver:RequestBody,
                 @Part("subject") subject:RequestBody,
                 @Part("message") message:RequestBody,
                 @Part file: ArrayList<MultipartBody.Part>): Call<MailResponse>

    @FormUrlEncoded
    @POST("email/sendEmail")
    fun sendMailWithoutFile(@Header("Authorization") Authorization:String,
                            @Field("sender") sender:String,
                            @Field("receiver") receiver:String,
                            @Field("subject") subject:String,
                            @Field("message") message:String) : Call<MailResponse>
}