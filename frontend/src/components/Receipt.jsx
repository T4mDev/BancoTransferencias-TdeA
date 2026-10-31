import { formatDate, formatMoney } from '../format.js';

const ownerOf = (accounts, number) => accounts.find((a) => a.accountNumber === number)?.ownerName ?? '';

/** Comprobante de la última transferencia con su código QR. */
export default function Receipt({ result, accounts }) {
  if (!result) {
    return (
      <aside className="receipt-slot" aria-live="polite">
        <div className="receipt-placeholder">
          <p>El comprobante de tu próxima transferencia aparecerá aquí, con un código QR que la identifica.</p>
        </div>
      </aside>
    );
  }

  const { transaction: tx, qrCode } = result;

  return (
    <aside className="receipt-slot" aria-live="polite">
      <article className="receipt" key={tx.reference}>
        <header className="receipt-head">
          <p className="receipt-bank">Banco TdeA</p>
          <h2>Transferencia exitosa</h2>
        </header>

        <p className="receipt-amount">{formatMoney(tx.amount)}</p>

        <dl className="receipt-data">
          <div><dt>Desde</dt><dd>{tx.sourceAccount}<small>{ownerOf(accounts, tx.sourceAccount)}</small></dd></div>
          <div><dt>Hacia</dt><dd>{tx.targetAccount}<small>{ownerOf(accounts, tx.targetAccount)}</small></dd></div>
          <div><dt>Fecha</dt><dd>{formatDate(tx.createdAt)}</dd></div>
          {tx.description && <div><dt>Descripción</dt><dd>{tx.description}</dd></div>}
          <div><dt>Referencia</dt><dd className="ref">{tx.reference}</dd></div>
        </dl>

        <figure className="receipt-qr">
          <img src={qrCode} alt={`Código QR de la transacción ${tx.reference}`} width="168" height="168" />
          <figcaption>Escanéalo para ver los datos de la transacción</figcaption>
        </figure>

        <a className="link-button" href={qrCode} download={`transferencia-${tx.reference}.png`}>Descargar QR</a>
      </article>
    </aside>
  );
}
