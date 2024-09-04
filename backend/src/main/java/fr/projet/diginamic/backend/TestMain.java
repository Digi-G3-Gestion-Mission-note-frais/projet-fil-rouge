package fr.projet.diginamic.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.repositories.MissionRepository;
import fr.projet.diginamic.backend.scheduling.MissionUpdater;

@EnableScheduling
@SpringBootApplication
public class TestMain {

    @Autowired
     MissionRepository missionRepository;

    @Autowired
     MissionUpdater missionUpdater;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TestMain.class, args);
        // SpringApplication.run(TestMain.class, args);
        TestMain testMain = context.getBean(TestMain.class);
        testMain.run();
    }

    public void run() {
        // Fetch the mission with ID 3
        Mission mission = missionRepository.findById(3L).orElseThrow(() -> new RuntimeException("Mission not found"));

        // Call the method to test
        missionUpdater.emailManagerAboutPendingMission(mission);
    }
}

