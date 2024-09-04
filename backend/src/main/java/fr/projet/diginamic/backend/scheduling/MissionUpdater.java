package fr.projet.diginamic.backend.scheduling;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.entities.UserEntity;

import fr.projet.diginamic.backend.enums.StatusEnum;
import fr.projet.diginamic.backend.repositories.MissionRepository;
import fr.projet.diginamic.backend.specs.MissionSpecifications;
import fr.projet.diginamic.backend.utils.CalculateMissionPricing;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;

// import com.sun.jersey.api.client.Client;
// import com.sun.jersey.api.client.ClientResponse;
// import com.sun.jersey.api.client.WebResource;
// import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
// import com.sun.jersey.core.util.MultivaluedMapImpl;

// import javax.ws.rs.core.MediaType;

@Service

public class MissionUpdater {

    @Autowired
    MissionRepository missionRepository;

    @Value("${mailgun.api.key}")
    private String apiKey;

     /**
     * Scheduled method to update mission statuses. 
     * This method runs at midnight every day.
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    protected void updateMissionStatus(){
        System.out.println("[CRON: updateMissionStatus] STARTING...");
        List<Mission> initialMissions = missionRepository.getAllByStatus(StatusEnum.INITIAL);
        System.out.println("[CRON: updateMissionStatus] FOUND " + initialMissions.size() +  "MISSIONS TO UPDATE TO WAITING STATUS");
        
        initialMissions.forEach(mission -> {
            try {
                mission.setStatus(StatusEnum.WAITING);
                System.out.println("[CRON: updateMissionStatus] Updated mission with ID: " + mission.getId());
            } catch (Exception e) {
                System.err.println("[CRON: updateMissionStatus] Error updating mission with ID: " + mission.getId() + " :" + e);
            }
        });

        missionRepository.saveAll(initialMissions);
    } 

    /**
	 * Calculate the bounty amount and the total price of the missions if they are completed and eligible for a bounty.
     * This method runs at midnight every day.
	 */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    protected void updateMissionPricing(){  
        System.out.println("[CRON: updateMissionPricing]STARTING... ");
        List<Mission> missions = missionRepository.findAll(MissionSpecifications.missionEligibleForBountyToCalculate());
        System.out.println("[CRON: updateMissionPricing]FOUND " + missions.size() + "MISSIONS TO UPDATE");
        
        missions.forEach(m -> {
            try {
                CalculateMissionPricing.calculateBounty(m);
                System.out.println("[CRON: updateMissionPricing] Updated mission with ID: " + m.getId());
            } catch (Exception e) {
                System.err.println("[CRON: updateMissionPricing] ERROR: FAILED TO UPDATE MISSION WITH ID: " + m.getId() + " :" + e);
            }
        });
        missionRepository.saveAll(missions);
        System.out.println("[CRON: updateMissionPricing] FINISHED.");
    }

    // @Scheduled(cron = "* * * * * ?")
    // @Scheduled(fixedRate = 1)

    // public void testCron(){
    //     System.out.println("TEST CRON:::::::::::");
    // }

    @Transactional(readOnly = true)
    public JsonNode emailManagerAboutPendingMission(Mission mission) throws UnirestException {
        System.out.println("::::::::::emailManagerAboutPendingMission::::::::::");

        try{
            UserEntity manager = mission.getUser().getManager();

        if(manager == null){
             System.err.println("[emailManagerAboutPendingMission]Error: Manager not found for mission with ID " + mission.getId());
             return new JsonNode("{ \"error\": \"Manager not found\" }");
        }
        String sandboxDomain = "sandboxbb425306c22f423385328c0cffab49e7.mailgun.org";
       		HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + sandboxDomain + "/messages")
  			.basicAuth("api", apiKey)
  			.queryString("from", "Excited User <USER@sandboxbb425306c22f423385328c0cffab49e7.mailgun.org>")
  			.queryString("to", "don.lysiane@gmail.com")
  			.queryString("subject", "hello")
  			.queryString("text", "CA MAARCHE !!!!!!!!!!! ")
  			.asJson();
              System.out.println("::::::::::email envoyé !!!!!!!!!!!::::::::::" + request.getBody());
  		return request.getBody();
         
        }catch (UnirestException e) {
            System.err.println("[emailManagerAboutPendingMission] ERROR: " + e);
            return new JsonNode("{ \"error\": \"Exception occurred\" }"); 

        }catch (Exception e) {
            System.err.println("[emailManagerAboutPendingMission] ERROR: " + e);
            return new JsonNode("{ \"error\": \"General exception occurred\" }");
        }
        
    }
    //  @Transactional(readOnly = true)
    // public JsonNode emailManagerAboutPendingMission(Mission mission) throws Exception {
    //     System.out.println("::::::::::emailManagerAboutPendingMission::::::::::");

    //     UserEntity manager = mission.getUser().getManager();
    //     if (manager == null) {
    //         System.err.println("[emailManagerAboutPendingMission] Error: Manager not found for mission with ID " + mission.getId());
    //         return new JsonNode("{ \"error\": \"Manager not found\" }");
    //     }

    //     String from = "Mailgun Sandbox <postmaster@" + domain + ">";
    //     String to = manager.getEmail();
    //     String subject = "Pending Missions";
    //     String variables = "{\"first_name\": \"" + manager.getFirstName() + "\", \"number_of_missions\": 1, \"login_link\": \"" + loginLink + "\"}";

    //     Client client = Client.create();
    //     client.addFilter(new HTTPBasicAuthFilter("api", apiKey));
    //     WebResource webResource = client.resource("https://api.mailgun.net/v3/" + domain + "/messages");

    //     MultivaluedMapImpl formData = new MultivaluedMapImpl();
    //     formData.add("from", from);
    //     formData.add("to", to);
    //     formData.add("subject", subject);
    //     formData.add("template", "emailManagerAboutPendingMission");
    //     formData.add("h:X-Mailgun-Variables", variables);

    //     ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
    //     String responseBody = response.getEntity(String.class);
    //     System.out.println("::::::::::email envoyé !!!!!!!!!!!::::::::::" + responseBody);
    //     return new JsonNode(responseBody);
    // }

}
