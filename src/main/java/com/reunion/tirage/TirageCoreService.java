/**
 * 
 */
package com.reunion.tirage;

/**
 * @author pmekeze
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import com.reunion.tirage.data.CreateTirageRequest;
import com.reunion.tirage.data.NotifyUserResource;
import com.reunion.tirage.data.NotifyUserResponse;
import com.reunion.tirage.data.UserResource;
import com.reunion.tirage.data.UserResponse;
import com.reunion.tirage.data.UserTirageResponse;
import com.reunion.tirage.entity.Tirage;
import com.reunion.tirage.entity.TirageParameter;
import com.reunion.tirage.entity.User;
import com.reunion.tirage.persist.ParameterBDService;
import com.reunion.tirage.persist.TirageDBService;
import com.reunion.tirage.persist.UserBDService;
import com.reunion.tirage.persist.exception.PersistenceManagerException;

public class TirageCoreService {

	private static final Logger LOG = Logger.getLogger(TirageCoreService.class
			.getName());
	
	private static TirageCoreService instance;
	private UserBDService userBDService = UserBDService.getInstance();
	private TirageDBService tirageBDService = TirageDBService.getInstance();
	private ParameterBDService parameterBDService = ParameterBDService
			.getInstance();

	public static TirageCoreService getInstance() {

		if (instance == null) {
			instance = new TirageCoreService();
		}

		return instance;
	}

	public List<UserResponse> initListMembre(CreateTirageRequest createTirage) throws Exception {
  
		List<UserResource> userResourceList = createTirage.getUserListRequest();

		 List<User> admins = new ArrayList<User>();
		for (UserResource userDto : userResourceList) {
			if(userDto.getEmail()!=null && userDto.getSecureCode()!=null){
				User user = new User(userDto.getEmail(), userDto.getSecureCode(), createTirage.getCompany());
				Date date = new Date();
				user.setCreateTimestamp(date);
				if(userDto.isAdmin()){
				    admins.add(user);
				}
				try {
					userBDService.persist(user);
					LOG.log(Level.INFO, "user '"+userDto.getEmail()+"' and secureCode '"+userDto.getSecureCode() +"' for company '" + createTirage.getCompany() + "' persist with success" );
				} catch (PersistenceManagerException ex) {
					LOG.log(Level.SEVERE, "error caught when tring to persist user '"+userDto.getEmail() + "' and secureCode '"+userDto.getSecureCode() +"' for company '" + createTirage.getCompany(), ex.getCause());
					throw new Exception( ex.getCause());
				}
			}

		}
		
		List<UserResponse> listUser = initTirage(createTirage.getCompany(), createTirage.isNotificationEnabled(), admins);
		
		if(createTirage.isNotificationEnabled()){
		    for(UserResponse userResponse : listUser){
		        userBDService.updateUser(userResponse.getEmail(), createTirage.getCompany(), userResponse.isNotificationSend());
		    }
		}
		
		return listUser;
	}
	
	public List<UserResponse> initTirage(String compagnie, boolean notificationEnabled, List<User> admins) throws Exception {
		List<UserResponse> result = new ArrayList<UserResponse>();
		
		try {
			List<User> allUser = userBDService.getAllUser(compagnie);
			
			if (allUser != null && allUser.size() != 0) {
				List<String> listeNumero = new ArrayList<String>();
				List<String> listTire = new ArrayList<String>();
				for (int i = 1; i <= allUser.size(); i++) {
					listeNumero.add(String.valueOf(i));
				}
				TirageParameter tirageParams = new TirageParameter(
						allUser.size(), listTire, listeNumero, compagnie, admins);
				Date date = new Date();
				tirageParams.setUpdateTimestamp(date);
				parameterBDService.persist(tirageParams);
				
				if( notificationEnabled){
					return sendMailToUsers(allUser, compagnie);
				}else{
					for(User user : allUser){
						result.add(new UserResponse(user.getEmail(), user.getCodeSecurite(), false));
					}
					
					return result;
				}
			}
		} catch (PersistenceManagerException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			throw new Exception( ex.getCause());
		}
		return result;
	}

	public List<User> getListMembreFromDB(String company) throws PersistenceManagerException {
		List<User> allUser = userBDService.getAllUser(company);
		return allUser;
	}

	public int doTirage(String name, String company) throws Exception {

		Tirage tirageUser = verifyUserAlreadyDoTirage(name, company);
		
		if (tirageUser != null) {
			return tirageUser.getNumeroTire();
		}

		Integer numeroTire = 0;
		TirageParameter parameter = null;
		try {

			parameter = parameterBDService.find(company);
			List<String> listRestante = parameter.getListRestante();
			List<String> listTire = parameter.getListTire();

			Random rand = new Random();
			int numberTake = rand.nextInt(listRestante.size());
			numeroTire = Integer.valueOf(listRestante.get(numberTake));
			listRestante.remove(numberTake);
			listTire.add(String.valueOf(numeroTire));

			parameter.setListRestante(listRestante);
			parameter.setListTire(listTire);

			parameter.setUpdateTimestamp(new Date());
			parameterBDService.update(parameter);

			tirageUser = new Tirage(name, numeroTire, company);
			Date date = new Date();
			tirageUser.setCreateTimestamp(date);
			tirageBDService.persist(tirageUser);

		} catch (PersistenceManagerException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			throw new Exception( ex.getCause());
		}

		return numeroTire;

	}

	public boolean authenticateByDB(String email, String criteria, String company)
			throws Exception {
		boolean autenticate = false;
		try {
			autenticate = tirageBDService.authenticateUser(email, criteria, company);
		} catch (PersistenceManagerException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			throw new Exception( ex.getCause());
		}
		return autenticate;
	}

	public boolean authenticateAdminByDB(String email, String criteria, String company)
            throws Exception {
        boolean autenticate = false;
        TirageParameter tirageParameter = null;
        try {
            tirageParameter = parameterBDService.find(company);
        } catch (PersistenceManagerException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex.getCause());
            throw new Exception( ex.getCause());
        }
        
        if(tirageParameter!=null){
            List<User> adminList = tirageParameter.getAdminList();
            for(User admin : adminList){
                
                if(admin.getEmail().equals(email)){
                    if(admin.getCodeSecurite().equals(criteria)){
                        autenticate = true;
                    }else{
                        LOG.log(Level.WARNING, "bad admin password provided: provide = " + criteria + " and expected = " + admin.getCodeSecurite() );
                    }
                }
            }
        }
        
        return autenticate;
    }
	
	public List<UserTirageResponse> getResultList(String company) throws Exception {

		List<UserTirageResponse> result = null;
		try {
			result = tirageBDService.getTirageResult(company);
		} catch (PersistenceManagerException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			throw new Exception( ex.getCause());
		}

		Collections.sort(result, new Comparator<UserTirageResponse>() {
			public int compare(UserTirageResponse result1, UserTirageResponse result2) {
				Integer integer1 = new Integer(result1.getNumero());
				Integer integer2 = new Integer(result2.getNumero());
				return integer1.compareTo(integer2);
			}
		});
		List<User> allUser = userBDService.getAllUser(company);
		List<UserTirageResponse> remaindUser = new ArrayList<UserTirageResponse>();
		
		List<String> allUserResultEmail = new ArrayList<String>();
		
		for(UserTirageResponse user : result){
			allUserResultEmail.add(user.getEmail());
		}
		
		for(User user : allUser ){
			
			if(!allUserResultEmail.contains(user.getEmail())){
				remaindUser.add(new UserTirageResponse(user.getEmail(), 0,company ));
			}
			
		}
		result.addAll(remaindUser);
		return result;
	}

	public Tirage verifyUserAlreadyDoTirage(String email, String company) throws Exception {
		Tirage tirage = null;
		try {
			tirage = tirageBDService.findByEmailAndConpany(email, company);
		} catch (PersistenceManagerException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			throw new Exception( ex.getCause());
		}
		return tirage;
	}
	
	public TirageParameter verifyCompanyAlreadyExist(String company) throws Exception {
		TirageParameter tirage = null;
		try {
			tirage = parameterBDService.find(company);
		} catch (PersistenceManagerException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			throw new Exception( ex.getCause());
		}
		return tirage;
	}
	
	private List<UserResponse> sendMailToUsers(List<User> allUser, String company){
		List<UserResponse> resultList = new ArrayList<UserResponse>();
		MailService instance = MailService.getInstance();
		for(User user : allUser){
			if(user.getEmail().contains("@")){
				try{
					instance.sendMail(user.getEmail(), company, user.getCodeSecurite());
					resultList.add(new UserResponse(user.getEmail(), user.getCodeSecurite(),true));
				}catch(MessagingException e){
					resultList.add(new UserResponse(user.getEmail(), user.getCodeSecurite(),false));	
				}
			}else{
				resultList.add(new UserResponse(user.getEmail(), user.getCodeSecurite(),false));
			}
		}
		return resultList;
	}
	
	public NotifyUserResponse notifyUser(NotifyUserResource userResource){
		
		NotifyUserResponse userResponse = null;
		MailService instance = MailService.getInstance();
		try{
			User user = userBDService.find(userResource.getEmail(), userResource.getCompany());
			instance.sendMail(userResource.getEmail(), userResource.getCompany(), user.getCodeSecurite());
			userResponse = new NotifyUserResponse(userResource.getEmail(),userResource.getCompany(), true);
		}catch(MessagingException  | PersistenceManagerException e){
			userResponse = new NotifyUserResponse(userResource.getEmail(),userResource.getCompany(), false, e.getMessage());	
		}
		
		return userResponse;
	}
}
