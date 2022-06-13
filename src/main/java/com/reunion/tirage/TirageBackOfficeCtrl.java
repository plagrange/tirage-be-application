package com.reunion.tirage;

import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.reunion.tirage.data.CompanyExisted;
import com.reunion.tirage.data.CreateTirageRequest;
import com.reunion.tirage.data.CreateTirageResponse;
import com.reunion.tirage.data.AdminResourceRequest;
import com.reunion.tirage.data.NotifyUserResource;
import com.reunion.tirage.data.NotifyUserResponse;
import com.reunion.tirage.data.UserResponse;
import com.reunion.tirage.data.UserTirageResourceList;
import com.reunion.tirage.data.UserTirageResponse;
import com.reunion.tirage.data.UsersResponse;
import com.reunion.tirage.entity.TirageParameter;
import com.reunion.tirage.entity.User;



/**
 * Root resource (exposed at "tirageresource" path)
 */
@Api
@Path("backoffice")
public class TirageBackOfficeCtrl {
	
	private static Logger LOG = Logger.getLogger(TirageBackOfficeCtrl.class.getName());
	private TirageCoreService tirageService = TirageCoreService.getInstance();

	@Path("/results/{company}")
    @GET
    @Deprecated
    public Response getResult(@PathParam(value = "company") String company) throws Exception {
		LOG.info("retrieving results of tirage for company : " + company);
    	List<UserTirageResponse> resultList = tirageService.getResultList(company);
        Gson gson = new Gson(); 
        String jsonResult = gson.toJson(resultList);
        LOG.info("retrieving results of tirage for company : " + company + " end with success.  result list is : " + jsonResult);
        return Response.ok(jsonResult).build();
    }
    
    @GET
    @Path("/result/{email}/{securecode}/{company}")
    @Deprecated
    public Response getResult(@PathParam("email") String email, @PathParam("securecode") String criteria, @PathParam("company") String company) throws Exception {
    	LOG.info("retrieving result of tirage for user : "+ email + " and company : " + company);
    	if(tirageService.authenticateByDB(email, criteria, company) ){
    		int number = tirageService.doTirage(email,company);
    		UserTirageResponse resultDto = new UserTirageResponse(email, number, company);
    		Gson gson = new Gson(); 
            String jsonResult = gson.toJson(resultDto);
            LOG.info("retrieving result of tirage for user : "+ email + " and company : " + company + " end with success. result is : " + jsonResult);
            return Response.ok(jsonResult).build();
            
		}else{
			LOG.info("retrieving result of tirage for user : "+ email + " and company : " + company +" is forbiden due to bad criteria provided : " + criteria);
			return Response.status(401).build();
		}
    	
    }
    
    @Path("/results")
    @POST
    public UserTirageResourceList getResults(AdminResourceRequest adminresource) throws Exception {
        LOG.info("retrieving results of tirage for company : " + adminresource.getCompany());
        
        UserTirageResourceList userTirageResourceList = new UserTirageResourceList();
        
        if(!tirageService.authenticateAdminByDB(adminresource.getEmail(), adminresource.getSecureCode(), adminresource.getCompany())){
            userTirageResourceList.setMessage("Admin authentication failed!");
            return userTirageResourceList;
        }
        
        List<UserTirageResponse> resultList = tirageService.getResultList(adminresource.getCompany());
        userTirageResourceList.setUserResourceList(resultList);
        userTirageResourceList.setCompany(adminresource.getCompany());
        
        LOG.info("retrieving results of tirage for company : " + adminresource.getCompany() + " end with success.  result list is : " + userTirageResourceList);
        return userTirageResourceList;
    }
    
    @POST
    @Path("/result")
    public Response getResultUser(@FormParam("email") String email, @FormParam("secureCode") String criteria, @FormParam("company") String company) throws Exception {
    	LOG.info("retrieving result of tirage for user : "+ email + " and company : " + company);
    	if(tirageService.authenticateByDB(email, criteria, company) ){
    		int number = tirageService.doTirage(email,company);
    		UserTirageResponse resultDto = new UserTirageResponse(email, number, company);
    		Gson gson = new Gson(); 
            String jsonResult = gson.toJson(resultDto);
            LOG.info("retrieving result of tirage for user : "+ email + " and company : " + company + " end with success. result is : " + jsonResult);
            return Response.ok(jsonResult).build();
            
		}else{
			LOG.info("retrieving result of tirage for user : "+ email + " and company : " + company +" is forbiden due to bad criteria provided : " + criteria);
			return Response.status(401).build();
		}
    	
    }

    
    @Path("/createtirage")
    @POST
    public Response createtirage(@FormParam("createtirage") String createTirage) {
    	LOG.info("create a new tirage with resource : " + createTirage);
         Gson gson = new Gson();
       //convert the json string back to object
         CreateTirageRequest createTirageRequest = gson.fromJson(createTirage, CreateTirageRequest.class);
         List<UserResponse> userList = null;
         if(createTirageRequest.getCompany()==null || createTirageRequest.getCompany().length()==0 || createTirageRequest.getUserListRequest()==null || createTirageRequest.getUserListRequest().size()==0){
        	 
        	 return Response.serverError().status(400).build();
         }
		try {
		
			userList = tirageService.initListMembre(createTirageRequest);
		
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "create a new tirage failed", e);
			return Response.serverError().status(400).build();
		}
		
		CreateTirageResponse response = new CreateTirageResponse(userList, createTirageRequest.getCompany());
		
		LOG.info("create a new tirage end with success, return response is :  " + gson.toJson(response));
    	return Response.ok(gson.toJson(response)).build();
    }
    
    @Path("/createtirages")
    @POST
    public CreateTirageResponse createtirages(CreateTirageRequest createTirageRequest) {
        Gson gson = new Gson();
        LOG.info("create a new tirage with resource : " + gson.toJson(createTirageRequest));
         CreateTirageResponse response = new CreateTirageResponse();
       //convert the json string back to object
         List<UserResponse> userList = null;
         if(createTirageRequest.getCompany()==null || createTirageRequest.getCompany().length()==0 || createTirageRequest.getUserListRequest()==null || createTirageRequest.getUserListRequest().size()==0){
             response.setMessage("One of required parameter for this request is null");
             return response;
         }
        try {
        
            userList = tirageService.initListMembre(createTirageRequest);
        
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "create a new tirage failed", e);
            response.setMessage("An error occured : cause = " + e.getMessage());
            return response;
        }
        
        response.setCompany(createTirageRequest.getCompany());
        response.setUserResponseList(userList);
        
        LOG.info("create a new tirage end with success, return response is :  " + gson.toJson(response));
        return response;
    }
    
    @Path("/verifycompany/{company}")
    @GET
    public Response verifyCompany(@PathParam("company") String company) throws Exception {
    	LOG.info("verifing if the company ' "+company +"' exist");
    	TirageParameter parameter = tirageService.verifyCompanyAlreadyExist(company);
    	CompanyExisted status = new CompanyExisted();;
    	if(parameter!=null){
    		status.setCompanyExisted(true);
    	}
    	Gson gson = new Gson();
    	LOG.info("verifing if the company ' "+company +"' exist end with success and result : "+ gson.toJson(status));
    	return Response.ok(gson.toJson(status)).build();
    }
    
    @Path("/getuser/{company}/{email}/{secureCode}")
    @GET
    public Response getUsers(@PathParam("company") String company,@PathParam("email") String email, @PathParam("secureCode") String secureCode) throws Exception {
    	LOG.info("Getting all users registre for company : " + company);
    	
    	if(!tirageService.authenticateAdminByDB(email, secureCode, company)){
    	    return Response.status(401).build();
    	}
    	List<User> users = tirageService.getListMembreFromDB(company);
    	Gson gson = new Gson();
    	String jsonUsers  = gson.toJson(users);
    	LOG.info("Getting all users registre for company : " + company + " end with success and result : " + jsonUsers);
    	return Response.ok(jsonUsers).build();
    }
    
    @Path("/getusers")
    @POST
    public UsersResponse getUser(AdminResourceRequest adminresource) throws Exception {
        Gson gson = new Gson();
        LOG.info("Getting all users registre for company : " + adminresource.getCompany() + " with adminresource request : " + gson.toJson(adminresource));
        
        if(!tirageService.authenticateAdminByDB(adminresource.getEmail(), adminresource.getSecureCode(), adminresource.getCompany())){
            UsersResponse usersResponse = new UsersResponse();
            usersResponse.setMessage("Admin authentication failed!");
            return usersResponse;
        }
        List<User> users = tirageService.getListMembreFromDB(adminresource.getCompany());
        List<UserResponse> userResponseList = formUserToUserResponce(users);
        
        UsersResponse usersResponse = new UsersResponse(userResponseList, adminresource.getCompany());
        LOG.info("Getting all users registre for company : " + adminresource.getCompany() + " end with success and result : " + new Gson().toJson(usersResponse));
        return usersResponse;
    }
    
    
    @Path("/notifyuser")
    @POST
    public Response notifyUser(@FormParam("notifyUserResource") String notifyUserResource) throws Exception {
    	LOG.info("Notifing user for doing tirage au sort : user resource : " + notifyUserResource);
         Gson gson = new Gson();
       //convert the json string back to object
         NotifyUserResource notifyUser = gson.fromJson(notifyUserResource, NotifyUserResource.class);
         NotifyUserResponse notifyUserResponse = tirageService.notifyUser(notifyUser);
         
         LOG.info("Notifing user for doing tirage au sort end with success and result : " + gson.toJson(notifyUserResponse));
    	return Response.ok(gson.toJson(notifyUserResponse)).build();
    }
    
    private UserResponse formUserToUserResponce(User user){
        
        return new UserResponse(user.getEmail(), user.getCodeSecurite(), user.isNotificationSend());
    }
    
    private List<UserResponse> formUserToUserResponce(List<User> users){
        List<UserResponse> result = new ArrayList<UserResponse>();
        for(User user: users){
            result.add(formUserToUserResponce(user));
        }
        return result;
    }
}
