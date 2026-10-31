import { useEffect } from 'react';

/** Aviso temporal para confirmar acciones o mostrar errores del backend. */
export default function Toast({ message, kind, onClose }) {
  useEffect(() => {
    const timer = setTimeout(onClose, kind === 'error' ? 6000 : 3500);
    return () => clearTimeout(timer);
  }, [kind, onClose]);

  return (
    <div className={`toast toast-${kind}`} role={kind === 'error' ? 'alert' : 'status'}>
      <p>{message}</p>
      <button type="button" onClick={onClose} aria-label="Cerrar aviso">×</button>
    </div>
  );
}
