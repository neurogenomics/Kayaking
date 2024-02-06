export type Vector = {
  u: number;
  v: number;
};

export const vectorToString = (vector: Vector): string => {
  return `Speed in x direction (u): ${vector.u},\nSpeed in y direction (v): ${vector.v}`;
};
