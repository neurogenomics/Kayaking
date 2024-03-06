export type Vector = {
  u: number;
  v: number;
};

export function normalVector(angle: number): Vector {
  const radianAngle = angle * (Math.PI / 180);

  const u = Math.cos(radianAngle + Math.PI / 2);
  const v = Math.sin(radianAngle + Math.PI / 2);

  return { u, v };
}
