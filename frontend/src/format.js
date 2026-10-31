const money = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 2 });
const dateTime = new Intl.DateTimeFormat('es-CO', { dateStyle: 'medium', timeStyle: 'short' });

export const formatMoney = (value) => money.format(Number(value));
export const formatDate = (value) => dateTime.format(new Date(value));
export const shortRef = (reference) => reference.slice(0, 8).toUpperCase();
