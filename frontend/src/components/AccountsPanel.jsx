import { useState } from 'react';
import { formatMoney } from '../format.js';

const emptyAccount = { accountNumber: '', ownerName: '', initialBalance: '' };

/** Lista de cuentas con saldo; al elegir una se usa como cuenta origen. */
export default function AccountsPanel({ accounts, selected, onSelect, onCreate }) {
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(emptyAccount);

  const submit = async (e) => {
    e.preventDefault();
    const ok = await onCreate({ ...form, initialBalance: Number(form.initialBalance) });
    if (ok) {
      setForm(emptyAccount);
      setOpen(false);
    }
  };

  return (
    <section className="panel">
      <div className="panel-head">
        <h2>Cuentas</h2>
        <button type="button" className="link-button" onClick={() => setOpen((v) => !v)} aria-expanded={open}>
          {open ? 'Cerrar' : 'Abrir cuenta'}
        </button>
      </div>

      {open && (
        <form className="new-account" onSubmit={submit}>
          <label>
            Número de cuenta
            <input required inputMode="numeric" pattern="\d{6,20}" title="Entre 6 y 20 dígitos"
              value={form.accountNumber} onChange={(e) => setForm({ ...form, accountNumber: e.target.value })} />
          </label>
          <label>
            Titular
            <input required maxLength={120}
              value={form.ownerName} onChange={(e) => setForm({ ...form, ownerName: e.target.value })} />
          </label>
          <label>
            Saldo inicial
            <input required type="number" min="0" step="0.01"
              value={form.initialBalance} onChange={(e) => setForm({ ...form, initialBalance: e.target.value })} />
          </label>
          <button type="submit" className="button secondary">Crear cuenta</button>
        </form>
      )}

      {accounts.length === 0 ? (
        <p className="empty">Todavía no hay cuentas. Abre la primera para empezar a transferir.</p>
      ) : (
        <ul className="accounts">
          {accounts.map((a) => (
            <li key={a.id}>
              <button type="button" className={`account ${selected === a.accountNumber ? 'is-selected' : ''}`}
                onClick={() => onSelect(a.accountNumber)} aria-pressed={selected === a.accountNumber}>
                <span className="account-owner">{a.ownerName}</span>
                <span className="account-number">{a.accountNumber}</span>
                <span className="account-balance">{formatMoney(a.balance)}</span>
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
