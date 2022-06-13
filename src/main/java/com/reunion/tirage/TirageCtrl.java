package com.reunion.tirage;

import io.swagger.annotations.Api;

import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.reunion.tirage.data.UserTirageRequest;
import com.reunion.tirage.data.UserTirageResponse;



/**
 * Root resource (exposed at "tirageresource" path)
 */
@Api
@Path("tirage")
public class TirageCtrl {
	
	private static Logger LOG = Logger.getLogger(TirageCtrl.class.getName());
	private TirageCoreService tirageService = TirageCoreService.getInstance();
    
	@Path("/tire")
    @POST
    public Response authenticateDoTirage(@FormParam("email") String email, @FormParam("secureCode") String criteria, @FormParam("company") String company) throws Exception {

    	if(tirageService.authenticateByDB(email, criteria, company) ){
    		 int  number = tirageService.doTirage(email,company);
    		UserTirageResponse resultDto = new UserTirageResponse(email, number, company);
    		Gson gson = new Gson(); 
            String jsonResult = gson.toJson(resultDto);
            LOG.info("email : " + email + "  and secureCode : " + criteria + "  for company : " + company + "; numero du tour  = " + number);
            return Response.ok(jsonResult).build();
            
		}else{
    		LOG.info("email : " + email + "  and secureCode : " + criteria + "  for company : " + company + " not matching");
    		return Response.status(401).build();
		}
    	
    }
    
    @Path("/dotire")
    @POST
    public Response DoTirage(@FormParam("dotire") String tirageRequest) throws Exception {

    	Gson gson = new Gson();
        //convert the json string back to object
    	UserTirageRequest userTirageRequest = gson.fromJson(tirageRequest, UserTirageRequest.class);
    	if(userTirageRequest.getEmail()==null ||userTirageRequest.getSecureCode()==null || userTirageRequest.getCompany()==null){
    		 return Response.ok().status(400).build();
    	}
    	if(tirageService.authenticateByDB(userTirageRequest.getEmail(), userTirageRequest.getSecureCode(), userTirageRequest.getCompany()) ){
    		 int  number = tirageService.doTirage(userTirageRequest.getEmail(),userTirageRequest.getCompany());
    		UserTirageResponse resultDto = new UserTirageResponse(userTirageRequest.getEmail(), number, userTirageRequest.getCompany());

    		String jsonResult = gson.toJson(resultDto);
            LOG.info("email : " + userTirageRequest.getEmail() + "  and secureCode : " + userTirageRequest.getSecureCode() + "  for company : " + userTirageRequest.getCompany() + "; numero du tour  = " + number);
            return Response.ok(jsonResult).build();
            
		}else{
    		LOG.info("email : " + userTirageRequest.getEmail() + "  and secureCode : " + userTirageRequest.getSecureCode() + "  for company : " + userTirageRequest.getCompany() + " not matching");
    		return Response.status(401).build();
		}
    	
    }
}
