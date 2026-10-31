import { useCallback, useEffect, useState } from 'react';
import bankService, { errorMessage } from './services/bankService.js';
import AccountsPanel from './components/AccountsPanel.jsx';
import TransferForm from './components/TransferForm.jsx';
import Receipt from './components/Receipt.jsx';
import TransactionHistory from './components/TransactionHistory.jsx';
import Toast from './components/Toast.jsx';

export default function App() {
  const [accounts, setAccounts] = useState([]);
  const [history, setHistory] = useState([]);
  const [filter, setFilter] = useState('');
  const [receipt, setReceipt] = useState(null);
  const [source, setSource] = useState('');
  const [toast, setToast] = useState(null);

  const notify = (message, kind = 'error') => setToast({ message, kind, id: Date.now() });

  const loadAccounts = useCallback(async () => {
    try {
      setAccounts(await bankService.getAccounts());
    } catch (e) {
      notify(errorMessage(e));
    }
  }, []);

  const loadHistory = useCallback(async () => {
    try {
      setHistory(await bankService.getHistory(filter));
    } catch (e) {
      notify(errorMessage(e));
    }
  }, [filter]);

  useEffect(() => { loadAccounts(); }, [loadAccounts]);
  useEffect(() => { loadHistory(); }, [loadHistory]);

  const handleTransfer = async (data) => {
    try {
      const result = await bankService.transfer(data);
      setReceipt(result);
      notify('Transferencia realizada.', 'success');
      return true;
    } catch (e) {
      notify(errorMessage(e));
      return false;
    } finally {
      loadAccounts();
      loadHistory();
    }
  };

  const handleCreateAccount = async (data) => {
    try {
      await bankService.createAccount(data);
      notify('Cuenta creada.', 'success');
      loadAccounts();
      return true;
    } catch (e) {
      notify(errorMessage(e));
      return false;
    }
  };

  return (
    <div className="app">
      <header className="masthead">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true" />
          <span>Banco TdeA</span>
        </div>
        <p className="masthead-note">Transferencias entre cuentas</p>
      </header>

      <main>
        <section className="workspace">
          <div className="column">
            <AccountsPanel accounts={accounts} selected={source} onSelect={setSource} onCreate={handleCreateAccount} />
            <TransferForm accounts={accounts} source={source} onSourceChange={setSource} onSubmit={handleTransfer} />
          </div>
          <Receipt result={receipt} accounts={accounts} />
        </section>

        <TransactionHistory
          history={history}
          accounts={accounts}
          filter={filter}
          onFilterChange={setFilter}
        />
      </main>

      {toast && <Toast key={toast.id} {...toast} onClose={() => setToast(null)} />}
    </div>
  );
}
