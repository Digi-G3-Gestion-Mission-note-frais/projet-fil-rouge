package fr.projet.diginamic.backend.services;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.diginamic.backend.dtos.DisplayedMissionDTO;
import fr.projet.diginamic.backend.utils.CalculateMissionPricing;


@Service
public class CSVGenerationService {

    @Autowired
    CalculateMissionPricing calculateMissionPricing;

    /**
     * Generates a CSV report of bounties per month for a given list of missions.
     * 
     * @param missions     the list of displayed mission DTOs.
     * @param outputStream the output stream to write the CSV file to.
     * @throws IOException if there is an issue writing to the stream.
     */
    public void generateBountiesCsvReport(String titleCSV, List<DisplayedMissionDTO> missions, OutputStream outputStream)
            throws IOException {

        // Headers for the CSV file
        String[] headers = { "Mois", "Total des Primes" };

        try (OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                CSVPrinter csvPrinter = new CSVPrinter(streamWriter,
                        CSVFormat.DEFAULT.builder().setHeader(headers).setSkipHeaderRecord(false).build())) {

            // streamWriter.write(titleCSV);
            // streamWriter.write(System.lineSeparator()); 

            Map<String, Double> monthSum = calculateMissionPricing.summarizeBountiesPerMonth(missions);

            List<String> listMonths = Arrays.asList(
                "JANVIER", "FÉVRIER", "MARS", "AVRIL", "MAI", "JUIN",
                "JUILLET", "AOÛT", "SEPTEMBRE", "OCTOBRE", "NOVEMBRE", "DÉCEMBRE"
            );
            for (String month : listMonths) {
                double totalBounty = monthSum.getOrDefault(month, 0.0);
                csvPrinter.printRecord(month, totalBounty);
            }
            csvPrinter.flush();
           
        } catch (Exception e) {
            throw new IOException("Failed to generate CSV report", e);
        }
    }

    // /**
    //  * Summarizes bounties by month from a list of mission DTOs.
    //  * 
    //  * @param missions List of DisplayedMissionDTO
    //  * @return A map with month as key and sum of bounties as value.
    //  */
    // private Map<String, Double> summarizeBountiesPerMonth(List<DisplayedMissionDTO> missions) {
    //     Map<String, Double> monthSum = new HashMap<>();
    //     for (DisplayedMissionDTO mission : missions) {
    //         String monthKey = new SimpleDateFormat("MMMM", Locale.FRENCH).format(mission.getStartDate()).toUpperCase();
    //         if (monthSum.containsKey(monthKey)){
    //             Double bountiesSum = monthSum.get(monthKey) + mission.getBountyAmount() + 1;
    //             monthSum.put(monthKey, bountiesSum);       
    //         } else {
    //             monthSum.put(monthKey, mission.getBountyAmount());
    //         }
    //     }
    //     return monthSum;
    // }
}