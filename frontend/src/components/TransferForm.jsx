import { useState } from 'react';
import { formatMoney } from '../format.js';

/** Formulario para trasladar dinero entre dos cuentas. Todos los campos excepto la descripción son obligatorios. */
export default function TransferForm({ accounts, source, onSourceChange, onSubmit }) {
  const [target, setTarget] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [sending, setSending] = useState(false);

  const sourceAccount = accounts.find((a) => a.accountNumber === source);
  const overdraft = sourceAccount && amount && Number(amount) > Number(sourceAccount.balance);

  const submit = async (e) => {
    e.preventDefault();
    setSending(true);
    const ok = await onSubmit({
      sourceAccount: source,
      targetAccount: target,
      amount: Number(amount),
      description: description.trim() || null,
    });
    setSending(false);
    if (ok) {
      setAmount('');
      setDescription('');
    }
  };

  return (
    <section className="panel">
      <h2>Nueva transferencia</h2>
      <form className="transfer" onSubmit={submit}>
        <label>
          Desde
          <select required value={source} onChange={(e) => onSourceChange(e.target.value)}>
            <option value="">Elige la cuenta origen</option>
            {accounts.map((a) => (
              <option key={a.id} value={a.accountNumber}>{a.accountNumber} · {a.ownerName}</option>
            ))}
          </select>
        </label>
        <label>
          Hacia
          <select required value={target} onChange={(e) => setTarget(e.target.value)}>
            <option value="">Elige la cuenta destino</option>
            {accounts.filter((a) => a.accountNumber !== source).map((a) => (
              <option key={a.id} value={a.accountNumber}>{a.accountNumber} · {a.ownerName}</option>
            ))}
          </select>
        </label>
        <label>
          Monto (COP)
          <input required type="number" min="0.01" step="0.01" inputMode="decimal"
            value={amount} onChange={(e) => setAmount(e.target.value)} aria-describedby="amount-hint" />
        </label>
        <p id="amount-hint" className={`hint ${overdraft ? 'is-warning' : ''}`}>
          {sourceAccount
            ? overdraft
              ? `Supera el saldo disponible de ${formatMoney(sourceAccount.balance)}. El banco la rechazará.`
              : `Saldo disponible: ${formatMoney(sourceAccount.balance)}`
            : 'Elige una cuenta origen para ver su saldo.'}
        </p>
        <label>
          <span>Descripción <span className="optional">(opcional)</span></span>
          <input maxLength={200} value={description} onChange={(e) => setDescription(e.target.value)} />
        </label>
        <button type="submit" className="button" disabled={sending}>
          {sending ? 'Transfiriendo…' : 'Transferir'}
        </button>
      </form>
    </section>
  );
}
