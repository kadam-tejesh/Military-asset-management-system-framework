import { useEffect, useState } from "react";
import api, { clean, errMsg, num, fmtDateTime } from "../api/api.js";
import { useAuth } from "../context/AuthContext.jsx";
import useLookups from "../hooks/useLookups.js";
import FilterBar from "../components/FilterBar.jsx";
import DataTable from "../components/DataTable.jsx";
import Pagination from "../components/Pagination.jsx";
import Alert from "../components/Alert.jsx";
import Badge from "../components/Badge.jsx";

const INITIAL = { baseId: "", equipmentTypeId: "", from: "", to: "" };
const EMPTY_FORM = { sourceBaseId: "", destinationBaseId: "", equipmentTypeId: "", quantity: "" };

export default function Transfers() {
  const { user } = useAuth();
  const { bases, allBases, types } = useLookups();
  const isAdmin = user.role === "ADMIN";
  const canCreate = isAdmin || user.role === "LOGISTICS_OFFICER";

  const [filters, setFilters] = useState(INITIAL);
  const [page, setPage] = useState(0);
  const [data, setData] = useState({ content: [], totalPages: 0 });
  const [loading, setLoading] = useState(true);
  const [reload, setReload] = useState(0);
  const [form, setForm] = useState(EMPTY_FORM);
  const [msg, setMsg] = useState(null);

  useEffect(() => {
    let live = true;
    setLoading(true);
    api.get("/transfers", { params: clean({ ...filters, page, size: 10 }) })
      .then((r) => live && setData(r.data))
      .catch((e) => live && setMsg({ type: "error", text: errMsg(e) }))
      .finally(() => live && setLoading(false));
    return () => { live = false; };
  }, [filters, page, reload]);

  const changeFilters = (f) => { setFilters(f); setPage(0); };
  const sourceId = isAdmin ? form.sourceBaseId : String(user.baseId);

  const submit = async (e) => {
    e.preventDefault();
    setMsg(null);
    if (sourceId === form.destinationBaseId) {
      setMsg({ type: "error", text: "Source and destination base cannot be the same" });
      return;
    }
    try {
      await api.post("/transfers", clean({
        sourceBaseId: isAdmin ? num(form.sourceBaseId) : undefined,
        destinationBaseId: num(form.destinationBaseId),
        equipmentTypeId: num(form.equipmentTypeId),
        quantity: num(form.quantity),
      }));
      setMsg({ type: "ok", text: "Transfer completed" });
      setForm({ ...EMPTY_FORM, sourceBaseId: form.sourceBaseId });
      setPage(0);
      setReload((n) => n + 1);
    } catch (err) {
      setMsg({ type: "error", text: errMsg(err) });
    }
  };

  const columns = [
    { key: "transferDate", label: "Timestamp", render: (r) => fmtDateTime(r.transferDate) },
    { key: "equipmentType", label: "Equipment" },
    { key: "quantity", label: "Qty" },
    { key: "sourceBase", label: "From" },
    { key: "destinationBase", label: "To" },
    { key: "status", label: "Status", render: (r) => <Badge value={r.status} /> },
    { key: "createdBy", label: "By" },
  ];

  return (
    <>
      <h2>Transfers</h2>
      <Alert msg={msg} />

      {canCreate && (
        <form className="card form-grid" onSubmit={submit}>
          <h3>New transfer</h3>
          {isAdmin ? (
            <label>From base
              <select required value={form.sourceBaseId} onChange={(e) => setForm({ ...form, sourceBaseId: e.target.value })}>
                <option value="">Select source</option>
                {allBases.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
              </select>
            </label>
          ) : (
            <label>From base<input value={user.baseName || ""} disabled /></label>
          )}
          <label>To base
            <select required value={form.destinationBaseId} onChange={(e) => setForm({ ...form, destinationBaseId: e.target.value })}>
              <option value="">Select destination</option>
              {allBases.filter((b) => String(b.id) !== sourceId).map((b) => (
                <option key={b.id} value={b.id}>{b.name}</option>
              ))}
            </select>
          </label>
          <label>Equipment type
            <select required value={form.equipmentTypeId} onChange={(e) => setForm({ ...form, equipmentTypeId: e.target.value })}>
              <option value="">Select type</option>
              {types.map((t) => <option key={t.id} value={t.id}>{t.name}</option>)}
            </select>
          </label>
          <label>Quantity
            <input type="number" min="1" required value={form.quantity}
                   onChange={(e) => setForm({ ...form, quantity: e.target.value })} />
          </label>
          <button className="btn primary">Transfer</button>
        </form>
      )}

      <h3>Transfer history</h3>
      <FilterBar filters={filters} onChange={changeFilters} initial={INITIAL}
                 bases={bases} types={types} showBase={isAdmin} />
      {loading ? <p className="muted">Loading...</p> : <DataTable columns={columns} rows={data.content} />}
      <Pagination page={page} totalPages={data.totalPages} onPage={setPage} />
    </>
  );
}