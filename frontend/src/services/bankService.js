import axios from 'axios';

/**
 * Cliente HTTP del backend. La URL base viene de la variable de entorno VITE_API_URL.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

/** Extrae un mensaje legible de un error del backend (ProblemDetail). */
export function errorMessage(error) {
  if (error.response?.data?.detail) return error.response.data.detail;
  if (error.code === 'ERR_NETWORK') return 'No hay conexión con el servidor. Verifica que el backend esté en ejecución.';
  return error.message ?? 'Ocurrió un error inesperado.';
}

/** Servicio con las operaciones bancarias del frontend. */
const bankService = {
  /** Lista todas las cuentas. */
  getAccounts: () => api.get('/cuentas').then((r) => r.data),

  /** Crea una cuenta nueva. */
  createAccount: (account) => api.post('/cuentas', account).then((r) => r.data),

  /** Realiza una transferencia. Devuelve { transaction, qrCode, qrContent }. */
  transfer: (transfer) => api.post('/transferencias', transfer).then((r) => r.data),

  /** Histórico de transacciones, opcionalmente filtrado por número de cuenta. */
  getHistory: (accountNumber) =>
    api.get('/transacciones', { params: accountNumber ? { cuenta: accountNumber } : {} }).then((r) => r.data),

  /** URL de la imagen PNG del código QR de una transacción. */
  qrUrl: (reference) => `${api.defaults.baseURL}/transacciones/${reference}/qr`,
};

export default bankService;
