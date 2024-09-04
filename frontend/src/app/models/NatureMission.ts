export interface NatureMission {
    id: number;
    label: string;
    adr: number;
    isBilled: boolean;
    isInvoiced: boolean; 
    startDate: Date;
    endDate: Date;
    bountyRate: number;
    isEligibleToBounty: boolean;
  }