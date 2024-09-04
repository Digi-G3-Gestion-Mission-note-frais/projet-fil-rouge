package fr.projet.diginamic.backend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** The type of transport */
@AllArgsConstructor
@Getter
public enum TransportEnum {
    NONE("Aucun"),
    AIRPLANE("Air Plane"),
    CARPOOLING("Carpooling"),
    TRAIN("Train"),
    SERVICE_CAR("Service Car");

    // Attibutes
    /** The type of transport */
    private final String type;

}
