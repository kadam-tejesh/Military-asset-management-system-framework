import { useEffect, useState } from "react";
import api, { clean, errMsg, num, fmtDate } from "../api/api.js";
import { useAuth } from "../context/AuthContext.jsx";
import useLookups from "../hooks/useLookups.js";
import FilterBar from "../components/FilterBar.jsx";
import DataTable from "../components/DataTable.jsx";
import Pagination from "../components/Pagination.jsx";
import Alert from "../components/Alert.jsx";
import Badge from "../components/Badge.jsx";
import Modal from "../components/Modal.jsx";

const INITIAL = { baseId: "", equipmentTypeId: "", status: "", from: "", to: "" };
const EMPTY_FORM = { baseId: "", equipmentTypeId: "", personnelName: "", quantity: "", assignedDate: "" };

export default function Assignments() {
  const { user } = useAuth();
  const { bases, types } = useLookups();
  const isAdmin = user.role === "ADMIN";

  const [filters, setFilters] = useState(INITIAL);
  const [page, setPage] = useState(0);
  const [data, setData] = useState({ content: [], totalPages: 0 });
  const [loading, setLoading] = useState(true);
  const [reload, setReload] = useState(0);
  const [form, setForm] = useState(EMPTY_FORM);
  const [msg, setMsg] = useState(null);
  const [expendTarget, setExpendTarget] = useState(null);
  const [reason, setReason] = useState("");

  useEffect(() => {
    let live = true;
    setLoading(true);
    api.get("/assignments", { params: clean({ ...filters, page, size: 10 }) })
      .then((r) => live && setData(r.data))
      .catch((e) => live && setMsg({ type: "error", text: errMsg(e) }))
      .finally(() => live && setLoading(false));
    return () => { live = false; };
  }, [filters, page, reload]);

  const changeFilters = (f) => { setFilters(f); setPage(0); };
  const refresh = () => setReload((n) => n + 1);

  const submit = async (e) => {
    e.preventDefault();
    setMsg(null);
    try {
      await api.post("/assignments", clean({
        baseId: isAdmin ? num(form.baseId) : undefined,
        equipmentTypeId: num(form.equipmentTypeId),
        personnelName: form.personnelName,
        quantity: num(form.quantity),
        assignedDate: form.assignedDate,
      }));
      setMsg({ type: "ok", text: "Asset assigned" });
      setForm({ ...EMPTY_FORM, baseId: form.baseId });
      setPage(0);
      refresh();
    } catch (err) {
      setMsg({ type: "error", text: errMsg(err) });
    }
  };

  const expend = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/assignments/${expendTarget.id}/expend`, { reason });
      setMsg({ type: "ok", text: "Marked as expended" });
      setExpendTarget(null);
      setReason("");
      refresh();
    } catch (err) {
      setMsg({ type: "error", text: errMsg(err) });
      setExpendTarget(null);
    }
  };

  const giveBack = async (row) => {
    if (!window.confirm(`Return ${row.quantity} × ${row.equipmentType} from ${row.personnelName}?`)) return;
    try {
      await api.post(`/assignments/${row.id}/return`);
      setMsg({ type: "ok", text: "Asset returned to stock" });
      refresh();
    } catch (err) {
      setMsg({ type: "error", text: errMsg(err) });
    }
  };

  const columns = [
    { key: "assignedDate", label: "Assigned", render: (r) => fmtDate(r.assignedDate) },
    { key: "personnelName", label: "Personnel" },
    { key: "baseName", label: "Base" },
    { key: "equipmentType", label: "Equipment" },
    { key: "quantity", label: "Qty" },
    { key: "status", label: "Status", render: (r) => <Badge value={r.status} /> },
    { key: "expended", label: "Expended", render: (r) => r.expendedDate ? `${r.expendedDate}${r.expendedReason ? " · " + r.expendedReason : ""}` : "—" },
    {
      key: "actions", label: "Actions",
      render: (r) => r.status === "ASSIGNED" ? (
        <div className="row-actions">
          <button className="btn small danger" onClick={() => setExpendTarget(r)}>Expend</button>
          <button className="btn small ghost" onClick={() => giveBack(r)}>Return</button>
        </div>
      ) : "—",
    },
  ];

  return (
    <>
      <h2>Assignments &amp; Expenditures</h2>
      <Alert msg={msg} />

      <form className="card form-grid" onSubmit={submit}>
        <h3>Assign asset to personnel</h3>
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
        <label>Personnel name
          <input required value={form.personnelName} onChange={(e) => setForm({ ...form, personnelName: e.target.value })} />
        </label>
        <label>Quantity
          <input type="number" min="1" required value={form.quantity}
                 onChange={(e) => setForm({ ...form, quantity: e.target.value })} />
        </label>
        <label>Date
          <input type="date" value={form.assignedDate} onChange={(e) => setForm({ ...form, assignedDate: e.target.value })} />
        </label>
        <button className="btn primary">Assign</button>
      </form>

      <h3>Assignment records</h3>
      <FilterBar filters={filters} onChange={changeFilters} initial={INITIAL}
                 bases={bases} types={types} showBase={isAdmin}>
        <label>Status
          <select value={filters.status} onChange={(e) => changeFilters({ ...filters, status: e.target.value })}>
            <option value="">All</option>
            <option value="ASSIGNED">Assigned</option>
            <option value="EXPENDED">Expended</option>
            <option value="RETURNED">Returned</option>
          </select>
        </label>
      </FilterBar>
      {loading ? <p className="muted">Loading...</p> : <DataTable columns={columns} rows={data.content} />}
      <Pagination page={page} totalPages={data.totalPages} onPage={setPage} />

      {expendTarget && (
        <Modal title="Record expenditure" onClose={() => setExpendTarget(null)}>
          <form onSubmit={expend} className="stack">
            <p>{expendTarget.quantity} × {expendTarget.equipmentType} assigned to <b>{expendTarget.personnelName}</b></p>
            <label>Reason
              <input value={reason} onChange={(e) => setReason(e.target.value)} placeholder="e.g. Training exercise" />
            </label>
            <button className="btn danger">Confirm expended</button>
          </form>
        </Modal>
      )}
    </>
  );
}