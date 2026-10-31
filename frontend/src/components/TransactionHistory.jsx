import { useState } from 'react';
import bankService from '../services/bankService.js';
import { formatDate, formatMoney, shortRef } from '../format.js';
import QrModal from './QrModal.jsx';

const STATUS_LABEL = { COMPLETED: 'Completada', REJECTED: 'Rechazada' };

/** Tabla del histórico de transacciones con filtro por cuenta. */
export default function TransactionHistory({ history, accounts, filter, onFilterChange }) {
  const [qrFor, setQrFor] = useState(null);

  return (
    <section className="panel history">
      <div className="panel-head">
        <h2>Histórico de transacciones</h2>
        <label className="filter">
          <span className="visually-hidden">Filtrar por cuenta</span>
          <select value={filter} onChange={(e) => onFilterChange(e.target.value)}>
            <option value="">Todas las cuentas</option>
            {accounts.map((a) => (
              <option key={a.id} value={a.accountNumber}>{a.accountNumber} · {a.ownerName}</option>
            ))}
          </select>
        </label>
      </div>

      {history.length === 0 ? (
        <p className="empty">
          {filter ? 'Esta cuenta aún no tiene movimientos.' : 'Aún no hay transacciones. Haz la primera transferencia arriba.'}
        </p>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th scope="col">Referencia</th>
                <th scope="col">Fecha</th>
                <th scope="col">Desde</th>
                <th scope="col">Hacia</th>
                <th scope="col" className="num">Monto</th>
                <th scope="col">Estado</th>
                <th scope="col"><span className="visually-hidden">Acciones</span></th>
              </tr>
            </thead>
            <tbody>
              {history.map((tx) => (
                <tr key={tx.id}>
                  <td className="ref" title={tx.reference}>{shortRef(tx.reference)}</td>
                  <td>{formatDate(tx.createdAt)}</td>
                  <td>{tx.sourceAccount}</td>
                  <td>{tx.targetAccount}</td>
                  <td className={`num ${filter && tx.status === 'COMPLETED' ? (tx.sourceAccount === filter ? 'out' : 'in') : ''}`}>
                    {filter && tx.status === 'COMPLETED' ? (tx.sourceAccount === filter ? '− ' : '+ ') : ''}
                    {formatMoney(tx.amount)}
                  </td>
                  <td>
                    <span className={`status status-${tx.status.toLowerCase()}`} title={tx.failureReason ?? undefined}>
                      {STATUS_LABEL[tx.status]}
                    </span>
                    {tx.failureReason && <small className="reason">{tx.failureReason}</small>}
                  </td>
                  <td>
                    <button type="button" className="link-button" onClick={() => setQrFor(tx)}>Ver QR</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {qrFor && <QrModal transaction={qrFor} src={bankService.qrUrl(qrFor.reference)} onClose={() => setQrFor(null)} />}
    </section>
  );
}
