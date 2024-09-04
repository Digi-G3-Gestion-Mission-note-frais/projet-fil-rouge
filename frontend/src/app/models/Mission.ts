import { StatusEnum } from "../enums/StatusEnum";
import { TransportEnum } from "../enums/TransportEnum";
import { Expense } from "./Expense";

export interface Mission {
    /** The unique identifier of the mission */
    id: number;
  
    /** The label or name of the mission */
    label: string;
  
    /** The total price of the mission, calculated based on its duration and daily rate */
    totalPrice: number;
  
    /** The current status of the mission */
    status: string;
  
    /** The start date of the mission */
    startDate: Date;
  
    /** The end date of the mission */
    endDate: Date;
  
    /** The transport mode used for the mission */
    transport: string;
  
    /** The city from which the mission starts */
    departureCity: string;
  
    /** The city at which the mission ends */
    arrivalCity: string;
  
    /** The date when the bounty is assigned, if applicable */
    bountyDate?: Date;
  
    /** The amount of the bounty for the mission, if applicable */
    bountyAmount: number;
  
    /** The label of the nature of the mission */
    labelNatureMission: string;
  
    /** The unique identifier of the user assigned to the mission */
    userId: number;
  
    /** The unique identifier of the nature of the mission */
    natureMissionId: number;
  
    /** The unique identifier of the associated expense */
    expense: Expense;
  }
  