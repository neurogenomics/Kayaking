export function getEarliestTime(dates: Date[]): Date {
  return dates.reduce((earliest, current) =>
    current < earliest ? current : earliest,
  );
}

export const differenceInSeconds = (earlier: Date, later: Date): number => {
  return (later.getTime() - earlier.getTime()) / 1000;
};
