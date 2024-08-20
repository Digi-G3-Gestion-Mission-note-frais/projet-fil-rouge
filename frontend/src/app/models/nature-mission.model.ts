// nature-mission.model.ts
export interface NatureMission {
  id: number;
  label: string;
  adr: number;
  isBilled: boolean;
  startDate: Date;
  endDate: Date;
  bountyRate: number;
  isEligibleToBounty: boolean;
}
