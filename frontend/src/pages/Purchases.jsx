import { useEffect, useState } from "react";
import api, { clean, errMsg, num, fmtDate } from "../api/api.js";
import { useAuth } from "../context/AuthContext.jsx";
import useLookups from "../hooks/useLookups.js";
import FilterBar from "../components/FilterBar.jsx";
import DataTable from "../components/DataTable.jsx";
import Pagination from "../components/Pagination.jsx";
import Alert from "../components/Alert.jsx";

const INITIAL = { baseId: "", equipmentTypeId: "", from: "", to: "" };
const EMPTY_FORM = { baseId: "", equipmentTypeId: "", quantity: "", purchaseDate: "", vendor: "" };

export default function Purchases() {
  const { user } = useAuth();
  const { bases, types } = useLookups();
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
    api.get("/purchases", { params: clean({ ...filters, page, size: 10 }) })
      .then((r) => live && setData(r.data))
      .catch((e) => live && setMsg({ type: "error", text: errMsg(e) }))
      .finally(() => live && setLoading(false));
    return () => { live = false; };
  }, [filters, page, reload]);

  const changeFilters = (f) => { setFilters(f); setPage(0); };

  const submit = async (e) => {
    e.preventDefault();
    setMsg(null);
    try {
      await api.post("/purchases", clean({
        baseId: isAdmin ? num(form.baseId) : undefined,
        equipmentTypeId: num(form.equipmentTypeId),
        quantity: num(form.quantity),
        purchaseDate: form.purchaseDate,
        vendor: form.vendor,
      }));
      setMsg({ type: "ok", text: "Purchase recorded" });
      setForm({ ...EMPTY_FORM, baseId: form.baseId });
      setPage(0);
      setReload((n) => n + 1);
    } catch (err) {
      setMsg({ type: "error", text: errMsg(err) });
    }
  };

  const columns = [
    { key: "purchaseDate", label: "Date", render: (r) => fmtDate(r.purchaseDate) },
    { key: "baseName", label: "Base" },
    { key: "equipmentType", label: "Equipment" },
    { key: "quantity", label: "Qty" },
    { key: "vendor", label: "Vendor", render: (r) => r.vendor || "—" },
    { key: "createdBy", label: "Recorded by" },
  ];

  return (
    <>
      <h2>Purchases</h2>
      <Alert msg={msg} />

      {canCreate && (
        <form className="card form-grid" onSubmit={submit}>
          <h3>Record purchase</h3>
          {isAdmin && (
            <label>Base
              <select required value={form.baseId} onChange={(e) => setForm({ ...form, baseId: e.target.value })}>
                <option value="">Select base</option>
                {bases.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
              </select>
            </label>
          )}
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
          <label>Purchase date
            <input type="date" value={form.purchaseDate}
                   onChange={(e) => setForm({ ...form, purchaseDate: e.target.value })} />
          </label>
          <label>Vendor
            <input value={form.vendor} onChange={(e) => setForm({ ...form, vendor: e.target.value })} />
          </label>
          <button className="btn primary">Add purchase</button>
        </form>
      )}

      <h3>Purchase history</h3>
      <FilterBar filters={filters} onChange={changeFilters} initial={INITIAL}
                 bases={bases} types={types} showBase={isAdmin} />
      {loading ? <p className="muted">Loading...</p> : <DataTable columns={columns} rows={data.content} />}
      <Pagination page={page} totalPages={data.totalPages} onPage={setPage} />
    </>
  );
}