import { useEffect, useRef } from 'react';
import { formatMoney, shortRef } from '../format.js';

/** Ventana modal con el código QR de una transacción del histórico. */
export default function QrModal({ transaction, src, onClose }) {
  const dialogRef = useRef(null);

  useEffect(() => {
    // Sin cleanup: cerrar el diálogo en el cleanup dispararía onClose (React StrictMode monta dos veces).
    const dialog = dialogRef.current;
    if (!dialog.open) dialog.showModal();
  }, []);

  return (
    <dialog ref={dialogRef} className="qr-modal" onClose={onClose} onClick={(e) => e.target === dialogRef.current && onClose()}>
      <h3>Transacción {shortRef(transaction.reference)}</h3>
      <p>{formatMoney(transaction.amount)} · {transaction.sourceAccount} a {transaction.targetAccount}</p>
      <img src={src} alt={`Código QR de la transacción ${transaction.reference}`} width="220" height="220" />
      <button type="button" className="button secondary" onClick={onClose}>Cerrar</button>
    </dialog>
  );
}
